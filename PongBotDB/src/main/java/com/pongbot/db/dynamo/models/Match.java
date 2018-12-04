package com.pongbot.db.dynamo.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "MatchTable")
public class Match extends DynamoTable {
  @DynamoDBHashKey
  private String compoundKey;

  @DynamoDBRangeKey
  private long timestamp;

  @DynamoDBAttribute
  private Map<String, String> players;

  @DynamoDBAttribute
  private boolean complete;

  public Match() {}

  public Match(Long timestamp, Map<String, String> players, boolean complete) {
    this.compoundKey = buildMatchKey(players.keySet());
    this.timestamp = timestamp;
    this.complete = complete;
    this.players = players;
  }

  public String getCompoundKey() {
    return compoundKey;
  }

  public void setCompoundKey(String compoundKey) {
    this.compoundKey = compoundKey;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, String> getPlayers() {
    return players;
  }

  public void setPlayers(Map<String, String> players) {
    this.players = players;
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  @Override
  public String toString() {
    return "Match{" +
        "compoundKey='" + compoundKey + '\'' +
        ", timestamp=" + timestamp +
        ", players=" + players +
        ", complete=" + complete +
        '}';
  }

  public static String buildMatchKey(String firstPlayer, String secondPlayer) {
    String smallerId, largerId;

    if (firstPlayer.compareTo(secondPlayer) <= 0) {
      smallerId = firstPlayer;
      largerId = secondPlayer;
    } else {
      smallerId = secondPlayer;
      largerId = firstPlayer;
    }

    return String.format("%s-%s", smallerId, largerId);
  }

  public static String buildMatchKey(Set<String> players) {
    if (players.size() == 2) {
      List<String> playerList = new ArrayList<>(players);
      return buildMatchKey(playerList.get(0), playerList.get(1));
    } else {
      throw new IllegalArgumentException("A match must have exactly 2 players");
    }
  }

  public void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    buildTableIfMissing(client, mapper, Match.class, "MatchTable");
  }
}

