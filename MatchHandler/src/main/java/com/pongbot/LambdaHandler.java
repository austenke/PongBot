package com.pongbot;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.pongbot.queues.dao.MatchQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.matches.MatchResultTask;
import com.pongbot.workers.MatchWorker;

public class LambdaHandler implements RequestStreamHandler {
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
    LambdaLogger logger = context.getLogger();

    logger.log("Running matchHandler");

    MatchQueueDao matchQueueDao = QueueProvider.getMatchQueueDao();

    MatchWorker matchWorker = new MatchWorker(logger);
    List<Task> tasks = matchQueueDao.get(10);

    tasks.forEach(task -> {
      logger.log(String.format("Running on task %s", task));
      matchWorker.handleMatchResult((MatchResultTask) task);
      logger.log(String.format("Successfully finished task %s", task.getIdentifier()));
      matchQueueDao.delete(task);
    });
  }
}
