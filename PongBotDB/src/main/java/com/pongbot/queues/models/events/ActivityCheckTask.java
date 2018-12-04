package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.ACTIVITY_CHECK;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class ActivityCheckTask extends Task {

  public ActivityCheckTask() {
    super(ACTIVITY_CHECK.name());
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    return Collections.singletonMap("type", new MessageAttributeValue().withDataType("String").withStringValue(ACTIVITY_CHECK.name()));
  }

  @Override
  public String toString() {
    return "ActivityCheckTask{}";
  }
}
