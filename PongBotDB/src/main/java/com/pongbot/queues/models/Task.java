package com.pongbot.queues.models;

import java.util.Map;

import com.amazonaws.services.sqs.model.MessageAttributeValue;

abstract public class Task {

  private final String identifier;
  private String sqsMessageId;

  public Task(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getSqsMessageId() {
    return sqsMessageId;
  }

  public void setSqsMessageId(String sqsMessageId) {
    this.sqsMessageId = sqsMessageId;
  }

  abstract public Map<String, MessageAttributeValue> toAttributeValues();

  public String toString() {
    throw new IllegalArgumentException("This task does not override toString");
  }
}
