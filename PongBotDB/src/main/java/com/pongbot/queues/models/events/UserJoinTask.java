package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.USER_JOINED;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class UserJoinTask extends Task {
  private String userId;

  public UserJoinTask(String userId) {
    super(USER_JOINED.name());
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public static UserJoinTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String userId = attributeValues.get("userId").getStringValue();
    return new UserJoinTask(userId);
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(USER_JOINED.name()));
    response.put("userId", new MessageAttributeValue().withDataType("String").withStringValue(userId));
    return response;
  }

  @Override
  public String toString() {
    return "UserJoinTask{" +
        "userId='" + userId + '\'' +
        '}';
  }
}
