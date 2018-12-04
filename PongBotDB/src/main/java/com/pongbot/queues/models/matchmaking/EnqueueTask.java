package com.pongbot.queues.models.matchmaking;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class EnqueueTask extends Task {
  private String playerId;
  private String ignorePlayer;

  public EnqueueTask(String playerId, String ignorePlayer) {
    super(MatchmakingTaskType.ENQUEUE.name());
    this.playerId = playerId;
    this.ignorePlayer = ignorePlayer;
  }

  public String getPlayerId() {
    return playerId;
  }

  public String getIgnorePlayer() {
    return ignorePlayer;
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(getIdentifier()));
    response.put("playerId", new MessageAttributeValue().withDataType("String").withStringValue(playerId));
    response.put("ignorePlayer", new MessageAttributeValue().withDataType("String").withStringValue(ignorePlayer));
    return response;
  }

  public static EnqueueTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String playerId = attributeValues.get("playerId").getStringValue();
    String ignorePlayer = attributeValues.get("ignorePlayer").getStringValue();
    return new EnqueueTask(playerId, ignorePlayer);
  }

  @Override
  public String toString() {
    return "EnqueueTask{" +
        "playerId='" + playerId + '\'' +
        ", ignorePlayer='" + ignorePlayer + '\'' +
        '}';
  }
}
