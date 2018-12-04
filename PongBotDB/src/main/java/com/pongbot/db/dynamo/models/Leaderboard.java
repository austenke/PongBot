package com.pongbot.db.dynamo.models;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

@DynamoDBTable(tableName = "LeaderboardTable")
public class Leaderboard extends DynamoTable {
  @DynamoDBHashKey
  private int rank;

  @DynamoDBIndexHashKey(globalSecondaryIndexName = "playerIdIndex")
  private String playerId;

  @DynamoDBVersionAttribute
  private Integer version;

  public Leaderboard() {}

  public Leaderboard(int rank, String playerId) {
    this.rank = rank;
    this.playerId = playerId;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(String playerId) {
    this.playerId = playerId;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "Leaderboard{" +
        "rank=" + rank +
        ", playerId='" + playerId + '\'' +
        '}';
  }

  public void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    buildTableIfMissing(client, mapper, Leaderboard.class, "LeaderboardTable");
  }
}
