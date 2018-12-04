package com.pongbot.queues.models.matchmaking;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class DequeueTask extends Task {
  private String playerId;

  public DequeueTask(String playerId) {
    super(MatchmakingTaskType.DEQUEUE.name());
    this.playerId = playerId;
  }

  public String getPlayerId() {
    return playerId;
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(getIdentifier()));
    response.put("playerId", new MessageAttributeValue().withDataType("String").withStringValue(playerId));
    return response;
  }

  public static DequeueTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String playerId = attributeValues.get("playerId").getStringValue();
    return new DequeueTask(playerId);
  }

  @Override
  public String toString() {
    return "DequeueTask{" +
        "playerId='" + playerId + '\'' +
        '}';
  }
}
