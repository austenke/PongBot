package com.pongbot.workers;

import static com.pongbot.Config.ADMIN_ID;

import java.util.Collections;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.QueueProvider;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.Action;
import com.pongbot.models.Attachment;
import com.pongbot.models.BotMessage;
import com.pongbot.queues.models.matchmaking.DequeueTask;

public class ActivityCheckWorker {

  private LambdaLogger logger;

  private PlayerTableDao playerTableDao;
  private SlackApiClient slackApiClient;

  public ActivityCheckWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.slackApiClient = new SlackApiClient();
  }

  public void checkPlayerActivity() {
    logger.log("Checking player activity");

    for (Player player : playerTableDao.getAllPlayers()) {
      if (!player.isActive() || player.getActiveMatches().isEmpty()) continue;

      player.incrementDaysInactive();
      player.setName(slackApiClient.getUsername(player.getId()));
      playerTableDao.put(player);

      logger.log(String.format("Looking at %s", player.getName()));

      if (player.getDaysInactive() == 8) {
        DequeueTask dequeueTask = new DequeueTask(player.getId());
        QueueProvider.getMatchmakingQueueDao().put(dequeueTask);

        Action joinButton = new Action(player.getId(), "Re-join", "button", "rejoin", "primary");
        Attachment attachment = new Attachment("", "", "rejoin", "#3AA3E3", "default", Collections.singletonList(joinButton));
        BotMessage message = new BotMessage("You have been set as inactive and your current matches have been cancelled. If you wish to play again, please click the re-join button",
            Collections.singletonList(attachment));

        slackApiClient.sendMessage(ADMIN_ID, new BotMessage(String.format("Kicked <@%s> for inactivity", player.getId())));
        slackApiClient.sendMessage(player.getId(), message);
      } else if (player.getDaysInactive() == 7) {
        logger.log(String.format("Sending final warning to %s", player.getName()));
        slackApiClient.sendMessage(ADMIN_ID, new BotMessage(String.format("Sent final warning to <@%s>", player.getId())));
        slackApiClient.sendMessage(player.getId(), new BotMessage("Last chance! Please play a match by the end of today or you will be set as inactive and your matches will be cancelled."));
      } else if (player.getDaysInactive() == 4) {
        logger.log(String.format("Sending initial warning to %s", player.getName()));
        slackApiClient.sendMessage(player.getId(), new BotMessage("Hi, I noticed that you have not played a match in the last four days. Please play a match in the next week or you" +
            " will be marked as inactive. If you would like to stop playing you can leave the #ping-pong-league channel and your pending matches will be cancelled."));
      }
    }
  }
}
