package com.pongbot.db.dynamo.models;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

@DynamoDBTable(tableName = "QueueTable")
public class Queue extends DynamoTable {

  @DynamoDBHashKey
  private String playerId;

  @DynamoDBAttribute
  private Long timestamp;

  @DynamoDBAttribute
  private int desiredMatches;

  @DynamoDBVersionAttribute
  private Integer version;

  public Queue() {}

  public Queue(String playerId) {
    this.playerId = playerId;
  }

  public Queue(String playerId, Long timestamp, int desiredMatches) {
    this.playerId = playerId;
    this.timestamp = timestamp;
    this.desiredMatches = desiredMatches;
  }

  public String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(String playerId) {
    this.playerId = playerId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public int getDesiredMatches() {
    return desiredMatches;
  }

  public void setDesiredMatches(int desiredMatches) {
    this.desiredMatches = desiredMatches;
  }

  public void decrementDesiredMatches() {
    this.desiredMatches--;
  }

  public void incrementDesiredMatches(int count) {
    this.desiredMatches = this.desiredMatches + count;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "Queue{" +
        "playerId='" + playerId + '\'' +
        ", timestamp=" + timestamp +
        ", desiredMatches=" + desiredMatches +
        ", version=" + version +
        '}';
  }

  public void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    buildTableIfMissing(client, mapper, Queue.class, "QueueTable");
  }
}
