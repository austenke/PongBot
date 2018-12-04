package com.pongbot.workers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.BotMessage;

public class ChatWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final SlackApiClient slackApiClient;

  public ChatWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void handleChat(String userId, String message) {
    logger.log(String.format("Handling message %s from %s", message, userId));

    switch (message) {
      case "!rank":
        getRankingForUser(userId);
        break;
      case "!rules":
        sendRules(userId);
        break;
    }
  }

  private void getRankingForUser(String userId) {
    Player player = playerTableDao.get(userId);
    String rankString = player.getRanking() == -1? "you are currently unranked" : "you are currently ranked #" + player.getRanking();
    slackApiClient.sendMessage(userId, new BotMessage("Your current elo score is " + player.getElo() + ", " + rankString + ". Rankings are updated every 24 hours."));
  }

  private void sendRules(String userId) {
    BotMessage rules = new BotMessage("Community rules (this is more of a guideline, not heavily enforced):\n- " +
        "Matches are played to 21\n- Switch server after 5 points\n- You must win by 2 points\n- If a player is on match point the losing player serves until they lose or both players tie, in " +
        "which case the first person to reach match point serves (this cycle repeats into OT)");
    slackApiClient.sendMessage(userId, rules);
  }
}
