package com.pongbot;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.pongbot.queues.dao.EventQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.events.ChatTask;
import com.pongbot.queues.models.events.EventTaskType;
import com.pongbot.queues.models.events.UserJoinTask;
import com.pongbot.queues.models.events.UserLeaveTask;
import com.pongbot.workers.ActivityCheckWorker;
import com.pongbot.workers.BuildLeaderboardWorker;
import com.pongbot.workers.BuildTablesWorker;
import com.pongbot.workers.ChatWorker;
import com.pongbot.workers.UserJoinWorker;
import com.pongbot.workers.UserLeaveWorker;

public class LambdaHandler implements RequestStreamHandler {

  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
    LambdaLogger logger = context.getLogger();

    logger.log("Running eventHandler");

    EventQueueDao eventQueueDao = QueueProvider.getEventQueueDao();

    List<Task> tasks = eventQueueDao.get(10);
    tasks.forEach(task -> {
      logger.log(String.format("Executing task %s", task));
      executeTask(task, logger);
      logger.log(String.format("Successfully finished task %s", task));
      eventQueueDao.delete(task);
    });
  }

  private void executeTask(Task task, LambdaLogger lambdaLogger) {
    lambdaLogger.log(String.format("Running on task %s", task));

    EventTaskType eventTaskType = EventTaskType.valueOf(task.getIdentifier());

    switch (eventTaskType) {
      case CHAT:
        ChatTask chatTask = (ChatTask) task;
        new ChatWorker(lambdaLogger).handleChat(chatTask.getUserId(), chatTask.getMessage());
        break;
      case USER_JOINED:
        UserJoinTask userJoinTask = (UserJoinTask) task;
        new UserJoinWorker(lambdaLogger).createUser(userJoinTask.getUserId());
        break;
      case USER_LEFT:
        UserLeaveTask userLeaveTask = (UserLeaveTask) task;
        new UserLeaveWorker(lambdaLogger).setUserInactive(userLeaveTask.getUserId());
        break;
      case BUILD_LEADERBOARD:
        new BuildLeaderboardWorker(lambdaLogger).buildRankings();
        break;
      case BUILD_TABLES:
        new BuildTablesWorker(lambdaLogger).buildTables();
        break;
      case ACTIVITY_CHECK:
        new ActivityCheckWorker(lambdaLogger).checkPlayerActivity();
        break;
      default:
        throw new IllegalArgumentException(String.format("No case found for event task type %s", eventTaskType));
    }
  }
}
