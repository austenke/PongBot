package com.pongbot.workers;

import static com.pongbot.Config.ADMIN_ID;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.QueueProvider;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.BotMessage;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.models.matchmaking.DequeueTask;

public class UserLeaveWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final MatchmakingQueueDao matchmakingQueueDao;
  private final SlackApiClient slackApiClient;

  public UserLeaveWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.matchmakingQueueDao = QueueProvider.getMatchmakingQueueDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void setUserInactive(String playerId) {
    Player player = playerTableDao.get(playerId);

    if (player == null) {
      logger.log(String.format("Well that was easy, cannot make %s inactive because they are not a player", playerId));
      return;
    }

    DequeueTask dequeueTask = new DequeueTask(playerId);
    matchmakingQueueDao.put(dequeueTask);

    slackApiClient.sendMessage(ADMIN_ID, new BotMessage(String.format("<@%s> left the channel", player.getId())));
    slackApiClient.sendMessage(playerId, new BotMessage("Sorry to see you go! Your matches have been canceled, you can rejoin the channel any time to pick up from where you left off!"));
  }
}
