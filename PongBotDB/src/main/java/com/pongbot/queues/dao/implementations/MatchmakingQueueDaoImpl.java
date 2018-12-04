package com.pongbot.queues.dao.implementations;

import static com.pongbot.QueueConfig.MATCHMAKING_QUEUE_FUNCTION;
import static com.pongbot.QueueConfig.MATCHMAKING_QUEUE_TRIGGER_THRESHOLD;
import static com.pongbot.QueueConfig.MATCHMAKING_QUEUE_URL;

import java.util.Map;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.matchmaking.DequeueTask;
import com.pongbot.queues.models.matchmaking.EnqueueTask;
import com.pongbot.queues.models.matchmaking.MatchmakingTaskType;
import com.pongbot.queues.models.matchmaking.UpdateRankTask;

public class MatchmakingQueueDaoImpl extends AbstractQueueDaoImpl implements MatchmakingQueueDao {

  public MatchmakingQueueDaoImpl(AmazonSQS sqs) {
    super(sqs, MATCHMAKING_QUEUE_URL, MATCHMAKING_QUEUE_TRIGGER_THRESHOLD, MATCHMAKING_QUEUE_FUNCTION);
  }

  @Override
  Task convertMessageToTask(Message message) {
    Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
    MatchmakingTaskType matchmakingTaskType = MatchmakingTaskType.valueOf(attributes.get("type").getStringValue());

    Task task = buildTask(matchmakingTaskType, attributes);
    task.setSqsMessageId(message.getReceiptHandle());

    return task;
  }

  private Task buildTask(MatchmakingTaskType matchmakingTaskType, Map<String, MessageAttributeValue> attributes) {
    switch (matchmakingTaskType) {
      case ENQUEUE:
        return EnqueueTask.fromAttributeValues(attributes);
      case DEQUEUE:
        return DequeueTask.fromAttributeValues(attributes);
      case UPDATERANK:
        return UpdateRankTask.fromAttributeValues(attributes);
      default:
        throw new IllegalArgumentException(String.format("No event task object for %s", matchmakingTaskType));
    }
  }
}
