package com.pongbot.db.dynamo.models;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "EventTable")
public class Event extends DynamoTable {
  @DynamoDBHashKey
  private String compoundKey;

  public Event() {}

  public Event(String timestamp, String userId, String channelId, String eventType) {
    this.compoundKey = String.join("-", timestamp, userId, channelId, eventType);
  }

  public String getCompoundKey() {
    return compoundKey;
  }

  public void setCompoundKey(String compoundKey) {
    this.compoundKey = compoundKey;
  }

  @Override
  public String toString() {
    return "Event{" +
        "compoundKey='" + compoundKey + '\'' +
        '}';
  }

  public void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    deleteTable(client, "EventTable");

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new IllegalArgumentException("Encountered InterruptedException while trying to sleep after table deletion", e);
    }

    buildTableIfMissing(client, mapper, Event.class, "EventTable");
  }
}
