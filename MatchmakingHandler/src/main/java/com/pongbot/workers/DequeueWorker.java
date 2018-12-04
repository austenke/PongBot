package com.pongbot.workers;

import static com.pongbot.Config.ADMIN_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.QueueProvider;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.MatchTableDao;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dao.QueueTableDao;
import com.pongbot.db.dynamo.models.Match;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.BotMessage;
import com.pongbot.queues.models.matchmaking.DequeueTask;
import com.pongbot.queues.models.matchmaking.EnqueueTask;

public class DequeueWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final MatchTableDao matchTableDao;
  private final QueueTableDao queue;
  private final SlackApiClient slackApiClient;

  public DequeueWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.matchTableDao = TableProvider.getMatchTableDao();
    this.queue = TableProvider.getQueueTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void dequeuePlayer(DequeueTask dequeueTask) {
    Player player = playerTableDao.get(dequeueTask.getPlayerId());

    logger.log(String.format("Setting player %s to inactive", player.getName()));

    player.setInactive();
    playerTableDao.put(player);

    logger.log(String.format("Removing %s from queue table", player.getName()));

    queue.removePlayer(player);

    List<String> activeMatches = new ArrayList<>(player.getActiveMatches());

    for (String opponentId : activeMatches) {
      Player opponent = playerTableDao.get(opponentId);

      logger.log(String.format("Cancelling match between %s and %s", player.getName(), opponent.getName()));

      player.cancelMatch(opponent);
      playerTableDao.put(player);

      opponent.cancelMatch(player);
      playerTableDao.put(opponent);

      Optional<Match> optionalMatch = matchTableDao.getIncompleteMatches(player.getId(), opponentId);

      if (optionalMatch.isPresent()) {
        Match match = optionalMatch.get();

        Map<String, String> playerMessageMap = match.getPlayers();
        slackApiClient.deleteMessage(player.getId(), playerMessageMap.get(player.getId()));
        slackApiClient.deleteMessage(opponentId, playerMessageMap.get(opponentId));

        match.setComplete(true);
        matchTableDao.putMatch(match);
      }

      if (!opponent.isActive()) continue;

      EnqueueTask enqueueTask = new EnqueueTask(opponentId, player.getId());
      QueueProvider.getMatchmakingQueueDao().put(enqueueTask);

      slackApiClient.sendMessage(opponentId, new BotMessage(String.format("Your match with %s has been cancelled. " +
          "You have been added back to the matchmaking queue, expect another opponent shortly!", player.getName())));
      slackApiClient.sendMessage(ADMIN_ID, new BotMessage(String.format("Cancelling match between %s and %s", player.getName(), opponent.getName())));
    }
  }
}
