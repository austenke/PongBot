package com.pongbot.workers;

import static com.pongbot.Config.CHANNEL_ID;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.helpers.PlayerComparator;
import com.pongbot.models.BotMessage;

public class BuildLeaderboardWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final SlackApiClient slackApiClient;

  public BuildLeaderboardWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void buildRankings() {
    logger.log("Building rankings");

    List<Player> players = playerTableDao.getAllPlayers()
        .stream()
        .filter(player -> player.getMatchesPlayed() > 0 && player.isActive())
        .sorted(new PlayerComparator().reversed())
        .collect(Collectors.toList());

    Calendar cal = Calendar.getInstance();
    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH) + 1;

    StringBuilder topTen = new StringBuilder(String.format(":pingpong: Top 10 players (%s/%s) :pingpong:\n", month, dayOfMonth));

    int rankingCounter = 1;

    for (Player player : players) {
      player.setRanking(rankingCounter);
      playerTableDao.put(player);
      logger.log(String.format("Setting player %s to rank %s", player.getName(), player.getRanking()));

      if (rankingCounter <= 10) {
        topTen.append(String.format("%s: %s - elo: %s\n", rankingCounter, player.getName(), player.getElo()));
      }

      rankingCounter++;
    }

    topTen.append("\nPlayer rankings are updated every 24 hours. *To see your rank DM me with the message '!rank'*");

    logger.log("Rankings have been built, sending top ten to channel");
    slackApiClient.postToChannel(CHANNEL_ID, new BotMessage(topTen.toString()));
  }
}
