package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.BUILD_TABLES;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class BuildTablesTask extends Task {

  public BuildTablesTask() {
    super(BUILD_TABLES.name());
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    return Collections.singletonMap("type", new MessageAttributeValue().withDataType("String").withStringValue(BUILD_TABLES.name()));
  }

  @Override
  public String toString() {
    return "BuildTablesTask{}";
  }
}
