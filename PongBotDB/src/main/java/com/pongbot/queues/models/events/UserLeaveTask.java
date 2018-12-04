package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.USER_LEFT;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class UserLeaveTask extends Task {
  private String userId;

  public UserLeaveTask(String userId) {
    super(USER_LEFT.name());
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public static UserLeaveTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String userId = attributeValues.get("userId").getStringValue();
    return new UserLeaveTask(userId);
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(USER_LEFT.name()));
    response.put("userId", new MessageAttributeValue().withDataType("String").withStringValue(userId));
    return response;
  }

  @Override
  public String toString() {
    return "UserLeaveTask{" +
        "userId='" + userId + '\'' +
        '}';
  }
}
