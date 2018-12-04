package com.pongbot.queues.dao.implementations;

import static com.pongbot.QueueConfig.EVENT_QUEUE_FUNCTION;
import static com.pongbot.QueueConfig.EVENT_QUEUE_TRIGGER_THRESHOLD;
import static com.pongbot.QueueConfig.EVENT_QUEUE_URL;

import java.util.Map;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.dao.EventQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.events.ActivityCheckTask;
import com.pongbot.queues.models.events.BuildLeaderboardTask;
import com.pongbot.queues.models.events.BuildTablesTask;
import com.pongbot.queues.models.events.ChatTask;
import com.pongbot.queues.models.events.EventTaskType;
import com.pongbot.queues.models.events.UserJoinTask;
import com.pongbot.queues.models.events.UserLeaveTask;

public class EventQueueDaoImpl extends AbstractQueueDaoImpl implements EventQueueDao {

  public EventQueueDaoImpl(AmazonSQS sqs) {
    super(sqs, EVENT_QUEUE_URL, EVENT_QUEUE_TRIGGER_THRESHOLD, EVENT_QUEUE_FUNCTION);
  }

  @Override
  Task convertMessageToTask(Message message) {
    Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
    EventTaskType eventTaskType = EventTaskType.valueOf(attributes.get("type").getStringValue());

    Task task = buildTask(eventTaskType, attributes);
    task.setSqsMessageId(message.getReceiptHandle());

    return task;
  }

  private Task buildTask(EventTaskType eventTaskType, Map<String, MessageAttributeValue> attributes) {
    switch (eventTaskType) {
      case CHAT:
        return ChatTask.fromAttributeValues(attributes);
      case USER_JOINED:
        return UserJoinTask.fromAttributeValues(attributes);
      case USER_LEFT:
        return UserLeaveTask.fromAttributeValues(attributes);
      case BUILD_LEADERBOARD:
        return new BuildLeaderboardTask();
      case BUILD_TABLES:
        return new BuildTablesTask();
      case ACTIVITY_CHECK:
        return new ActivityCheckTask();
      default:
        throw new IllegalArgumentException(String.format("No event task object for %s", eventTaskType));
    }
  }
}
