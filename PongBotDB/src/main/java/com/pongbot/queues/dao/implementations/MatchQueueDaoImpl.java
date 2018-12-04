package com.pongbot.queues.dao.implementations;

import static com.pongbot.QueueConfig.MATCH_QUEUE_FUNCTION;
import static com.pongbot.QueueConfig.MATCH_QUEUE_TRIGGER_THRESHOLD;
import static com.pongbot.QueueConfig.MATCH_QUEUE_URL;

import java.util.Map;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.dao.MatchQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.matches.MatchResultTask;
import com.pongbot.queues.models.matches.MatchTaskType;

public class MatchQueueDaoImpl extends AbstractQueueDaoImpl implements MatchQueueDao {

  public MatchQueueDaoImpl(AmazonSQS sqs) {
    super(sqs, MATCH_QUEUE_URL, MATCH_QUEUE_TRIGGER_THRESHOLD, MATCH_QUEUE_FUNCTION);
  }

  @Override
  Task convertMessageToTask(Message message) {
    Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
    MatchTaskType matchTaskType = MatchTaskType.valueOf(attributes.get("type").getStringValue());

    Task task = buildTask(matchTaskType, attributes);
    task.setSqsMessageId(message.getReceiptHandle());

    return task;
  }

  private Task buildTask(MatchTaskType matchTaskType, Map<String, MessageAttributeValue> attributes) {
    switch (matchTaskType) {
      case RESULT:
        return MatchResultTask.fromAttributeValues(attributes);
      default:
        throw new IllegalArgumentException(String.format("No event task object for %s", matchTaskType));
    }
  }
}
