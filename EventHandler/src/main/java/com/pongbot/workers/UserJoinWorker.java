package com.pongbot.workers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.QueueProvider;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.BotMessage;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.models.matchmaking.EnqueueTask;

public class UserJoinWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final SlackApiClient slackApiClient;

  public UserJoinWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void createUser(String playerId) {
    Player player = playerTableDao.get(playerId);
    BotMessage welcomeMessage;

    if (player == null) {
      player = new Player(playerId, slackApiClient.getUsername(playerId));
      welcomeMessage = new BotMessage(buildWelcomeMessage(playerId));
    } else if (!player.isActive()) {
      player.setName(slackApiClient.getUsername(playerId));
      player.setActive(true);
      player.setDaysInactive(0);
      welcomeMessage = new BotMessage(buildReturnMessage(playerId, player.getElo()));
    } else {
      logger.log(String.format("Got joined event for player %s but they are already active", player.getName()));
      return;
    }

    logger.log(String.format("Creating player %s", player.getName()));
    playerTableDao.put(player);
    slackApiClient.sendMessage(playerId, welcomeMessage);

    EnqueueTask enqueueTask = new EnqueueTask(playerId, "none");
    MatchmakingQueueDao matchmakingQueueDao = QueueProvider.getMatchmakingQueueDao();

    matchmakingQueueDao.put(enqueueTask);
  }

  private String buildWelcomeMessage(String playerId) {
    return String.format("Welcome <@%s>! Your initial elo rating is 1000. You have been added to the matchmaking queue, matches will be " +
        "assigned as players become available. This bot is still very much in an alpha stage, so if you encounter any issues please message " +
        "akeene. To see the community rules type !rules", playerId);
  }

  private String buildReturnMessage(String playerId, long elo) {
    return String.format("Welcome back <@%s>! Your current elo rating is %s. You have been added back to the matchmaking queue.", playerId, elo);
  }
}
