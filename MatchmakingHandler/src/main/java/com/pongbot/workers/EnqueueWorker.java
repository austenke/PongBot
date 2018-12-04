package com.pongbot.workers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.json.Jackson;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.MatchTableDao;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dao.QueueTableDao;
import com.pongbot.db.dynamo.models.Match;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.Action;
import com.pongbot.models.Attachment;
import com.pongbot.models.BotMessage;
import com.pongbot.models.PostMessageResponse;
import com.pongbot.queues.models.matchmaking.EnqueueTask;

public class EnqueueWorker {

  private static final int MAX_RANK_DIFF = 4;
  private static final int MAX_MATCHES = 3;

  private final LambdaLogger logger;

  private final QueueTableDao queue;
  private final PlayerTableDao playerTableDao;
  private final MatchTableDao matchTableDao;

  private SlackApiClient slackApiClient;

  public EnqueueWorker(LambdaLogger logger) {
    this.logger = logger;
    this.queue = TableProvider.getQueueTableDao();
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.matchTableDao = TableProvider.getMatchTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void enqueuePlayer(EnqueueTask enqueueTask) {
    Player player = playerTableDao.get(enqueueTask.getPlayerId());

    // In cases (like cancellations) where someone is already in queue, be safe and wipe first
    wipeFromQueue(player);

    if (!player.isActive()) {
      throw new IllegalArgumentException(String.format("Cannot enqueue player %s, they are inactive", player.getName()));
    }

    int matchesNeeded = MAX_MATCHES - player.getActiveMatches().size();

    if (matchesNeeded == 0) {
      logger.log(String.format("Player %s does not need any more matches", player.getName()));
      return; // Fail silently
    }

    logger.log(String.format("Finding %s matches for %s", matchesNeeded, player.getName()));

    Set<String> potentialOpponents = queue.getOpponents(player, matchesNeeded, enqueueTask.getIgnorePlayer());
    Set<Player> opponents = findOpponents(player, potentialOpponents, matchesNeeded, enqueueTask.getIgnorePlayer());

    opponents.forEach(opponent -> {
      queue.decrementDesiredMatchCount(opponent);
      createNewMatch(player, opponent);
    });

    if (opponents.size() < matchesNeeded) {
      addToQueue(player, matchesNeeded - opponents.size());
    }
  }

  private Set<Player> findOpponents(Player player, Set<String> opponents, int desiredOpponents, String ignorePlayer) {
    logger.log(String.format("Found %s potential opponents in matchmaking queue", opponents));

    Set<Player> challengers = new HashSet<>();

    for (String opponentId : opponents) {
      if (isValidOpponentId(player, opponentId, ignorePlayer)) {
        Player opponent = playerTableDao.get(opponentId);

        logger.log(String.format("Examining potential opponent %s", opponent.getName()));

        if (!opponent.isActive()) {
          logger.log("Opponent is not active");
          continue;
        }

        if (Math.abs(player.getRanking() - opponent.getRanking()) > MAX_RANK_DIFF && opponent.getRanking() > 0 && player.getRanking() > 0) {
          logger.log(String.format("Opponent is rank %s while player is rank %s", opponent.getRanking(), player.getRanking()));
          continue;
        }

        logger.log(String.format("Found valid opponent %s", opponent.getName()));
        challengers.add(opponent);

        if (challengers.size() == desiredOpponents) break;
      }
    }

    return challengers;
  }

  private boolean isValidOpponentId(Player player, String opponent, String ignorePlayer) {
    if (player.getId().equals(opponent) || player.getActiveMatches().contains(opponent) || opponent.equals(ignorePlayer)) {
      logger.log(String.format("%s is an invalid opponent for %s", opponent, player.getName()));
      logger.log(String.format("Is player: %s", opponent.equals(player.getId())));
      logger.log(String.format("Already playing: %s", player.getActiveMatches().contains(opponent)));
      logger.log(String.format("Ignored player: %s", opponent.equals(ignorePlayer)));
      return false;
    } else {
      return true;
    }
  }

  private void createNewMatch(Player firstPlayer, Player secondPlayer) {
    logger.log(String.format("Creating match between %s and %s", firstPlayer.getName(), secondPlayer.getName()));

    firstPlayer.addMatch(secondPlayer);
    secondPlayer.addMatch(firstPlayer);

    playerTableDao.put(firstPlayer);
    playerTableDao.put(secondPlayer);

    String firstPlayerMessage = sendNewMatchConfirmationMessage(firstPlayer.getId(), secondPlayer.getId());
    String secondPlayerMessage = sendNewMatchConfirmationMessage(secondPlayer.getId(), firstPlayer.getId());

    Map<String, String> playerMap = new HashMap<>();
    playerMap.put(firstPlayer.getId(), firstPlayerMessage);
    playerMap.put(secondPlayer.getId(), secondPlayerMessage);

    Match match = new Match(System.currentTimeMillis(), playerMap, false);
    matchTableDao.putMatch(match);
    logger.log(String.format("Creating match %s", match));
  }

  private String sendNewMatchConfirmationMessage(String recipient, String opponent) {
    String text = String.format("A match has been scheduled between you and <@%s>. Please complete your match and report the winner.", opponent);

    Action winButton = new Action(opponent, "Win", "button", "win", "primary");
    Action loseButton = new Action(opponent, "Loss", "button", "loss", "danger");

    Attachment attachment = new Attachment("", "", "match_result", "#3AA3E3", "default", Arrays.asList(winButton, loseButton));

    BotMessage message = new BotMessage(text, Collections.singletonList(attachment));
    PostMessageResponse response = Jackson.fromJsonString(slackApiClient.sendMessage(recipient, message), PostMessageResponse.class);
    return response.getTs();
  }

  private void addToQueue(Player player, int count) {
    logger.log(String.format("Adding %s to the queue table %s times", player.getName(), count));
    queue.enqueuePlayer(player, count);
  }

  private void wipeFromQueue(Player player) {
    logger.log(String.format("Wiping %s from the queue table", player.getName()));
    queue.removePlayer(player);
  }
}
