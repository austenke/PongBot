package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.BUILD_LEADERBOARD;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class BuildLeaderboardTask extends Task {

  public BuildLeaderboardTask() {
    super(BUILD_LEADERBOARD.name());
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    return Collections.singletonMap("type", new MessageAttributeValue().withDataType("String").withStringValue(BUILD_LEADERBOARD.name()));
  }

  @Override
  public String toString() {
    return "BuildLeaderboardTask{}";
  }
}
