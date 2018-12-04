package com.pongbot;

import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.matchmaking.UpdateRankTask;
import com.pongbot.workers.DequeueWorker;
import com.pongbot.workers.EnqueueWorker;
import com.pongbot.queues.models.matchmaking.DequeueTask;
import com.pongbot.queues.models.matchmaking.EnqueueTask;
import com.pongbot.queues.models.matchmaking.MatchmakingTaskType;
import com.pongbot.workers.RankWorker;

public class LambdaHandler implements RequestStreamHandler {

  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
    LambdaLogger logger = context.getLogger();

    logger.log("MatchmakingHandler has started");

    MatchmakingQueueDao matchmakingQueueDao = QueueProvider.getMatchmakingQueueDao();

    matchmakingQueueDao.get(10).forEach(task -> {
      logger.log(String.format("Executing task %s", task));
      executeTask(task, logger);
      logger.log(String.format("Task %s has finished, removing from queue", task));
      matchmakingQueueDao.delete(task);
    });
  }

  private void executeTask(Task task, LambdaLogger logger) {
    MatchmakingTaskType matchmakingTaskType = MatchmakingTaskType.valueOf(task.getIdentifier());

    switch (matchmakingTaskType) {
      case ENQUEUE:
        EnqueueTask enqueueTask = (EnqueueTask) task;
        EnqueueWorker enqueueWorker = new EnqueueWorker(logger);
        enqueueWorker.enqueuePlayer(enqueueTask);
        break;
      case DEQUEUE:
        DequeueTask dequeueTask = (DequeueTask) task;
        DequeueWorker dequeueWorker = new DequeueWorker(logger);
        dequeueWorker.dequeuePlayer(dequeueTask);
        break;
      case UPDATERANK:
        UpdateRankTask updateRankTask = (UpdateRankTask) task;
        RankWorker rankWorker = new RankWorker(logger);
        rankWorker.adjustRank(updateRankTask);
    }
  }
}
