package com.pongbot.queues.models.events;

import static com.pongbot.queues.models.events.EventTaskType.CHAT;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.pongbot.queues.models.Task;

public class ChatTask extends Task {
  private String message;
  private String userId;
  private String channelId;

  public ChatTask(String message, String userId, String channelId) {
    super(CHAT.name());
    this.message = message;
    this.userId = userId;
    this.channelId = channelId;
  }

  public String getMessage() {
    return message;
  }

  public String getUserId() {
    return userId;
  }

  public String getChannelId() {
    return channelId;
  }

  public static ChatTask fromAttributeValues(Map<String, MessageAttributeValue> attributeValues) {
    String message = attributeValues.get("message").getStringValue();
    String userId = attributeValues.get("userId").getStringValue();
    String channelId = attributeValues.get("channelId").getStringValue();
    return new ChatTask(message, userId, channelId);
  }

  public Map<String, MessageAttributeValue> toAttributeValues() {
    HashMap<String, MessageAttributeValue> response = new HashMap<>();
    response.put("type", new MessageAttributeValue().withDataType("String").withStringValue(CHAT.name()));
    response.put("message", new MessageAttributeValue().withDataType("String").withStringValue(message));
    response.put("userId", new MessageAttributeValue().withDataType("String").withStringValue(userId));
    response.put("channelId", new MessageAttributeValue().withDataType("String").withStringValue(channelId));
    return response;
  }

  @Override
  public String toString() {
    return "ChatTask{" +
        "message='" + message + '\'' +
        ", userId='" + userId + '\'' +
        ", channelId='" + channelId + '\'' +
        '}';
  }
}
