package com.pongbot.queues.models.matchmaking;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class UpdateRankTask extends Task {
  private String playerId;
  private boolean moveUp;

  public UpdateRankTask(String playerId, boolean moveUp) {
    super(MatchmakingTaskType.UPDATERANK.name());
    this.playerId = playerId;
    this.moveUp = moveUp;
  }

  public String getPlayerId() {
    return playerId;
  }

  public boolean getMoveUp() {
    return moveUp;
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(getIdentifier()));
    response.put("playerId", new MessageAttributeValue().withDataType("String").withStringValue(playerId));
    response.put("moveUp", new MessageAttributeValue().withDataType("String").withStringValue(String.valueOf(moveUp)));
    return response;
  }

  public static UpdateRankTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String playerId = attributeValues.get("playerId").getStringValue();
    boolean moveUp = Boolean.valueOf(attributeValues.get("moveUp").getStringValue());
    return new UpdateRankTask(playerId, moveUp);
  }

  @Override
  public String toString() {
    return "EnqueueTask{" +
        "playerId='" + playerId + '\'' +
        ", moveUp='" + moveUp + '\'' +
        '}';
  }
}
