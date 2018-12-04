package com.pongbot.queues.models.matches;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class MatchResultTask extends Task {
  private String subjectId;
  private String opponentId;
  private MatchResultType resultType;

  public MatchResultTask(String subjectId, String opponentId, MatchResultType resultType) {
    super(MatchTaskType.RESULT.name());
    this.subjectId = subjectId;
    this.opponentId = opponentId;
    this.resultType = resultType;
  }

  public String getSubjectId() {
    return subjectId;
  }

  public String getOpponentId() {
    return opponentId;
  }

  public MatchResultType getResultType() {
    return resultType;
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(MatchTaskType.RESULT.name()));
    response.put("subjectId", new MessageAttributeValue().withDataType("String").withStringValue(subjectId));
    response.put("opponentId", new MessageAttributeValue().withDataType("String").withStringValue(opponentId));
    response.put("resultType", new MessageAttributeValue().withDataType("String").withStringValue(resultType.name()));
    return response;
  }

  public static MatchResultTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String subjectId = attributeValues.get("subjectId").getStringValue();
    String opponentId = attributeValues.get("opponentId").getStringValue();
    MatchResultType resultType = MatchResultType.valueOf(attributeValues.get("resultType").getStringValue());
    return new MatchResultTask(subjectId, opponentId, resultType);
  }

  public enum MatchResultType {
    WIN_CLAIM,
    WIN_DENY,
    WIN_CONFIRMATION,
    LOSS
  }

  @Override
  public String toString() {
    return "MatchResultTask{" +
        "subjectId='" + subjectId + '\'' +
        ", opponentId='" + opponentId + '\'' +
        ", resultType=" + resultType +
        '}';
  }
}
