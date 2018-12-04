package com.pongbot.db.dynamo.models;

import java.util.Collections;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;

@DynamoDBTable(tableName = "PlayerTable")
public class Player extends DynamoTable {
  @DynamoDBHashKey
  private String id;

  @DynamoDBAttribute
  private long elo;

  @DynamoDBAttribute
  private int matchesPlayed;

  @DynamoDBAttribute
  private int wins;

  @DynamoDBAttribute
  private int losses;

  @DynamoDBAttribute
  private int ranking;

  @DynamoDBAttribute
  private String name;

  @DynamoDBAttribute
  private List<String> activeMatches;

  @DynamoDBAttribute
  private Boolean active;

  @DynamoDBAttribute
  private int daysInactive;

  @DynamoDBAttribute
  private int winStreak;

  @DynamoDBVersionAttribute
  private Integer version;

  public Player() {}

  public Player(String id, String name) {
    this.id = id;
    this.name = name;
    this.elo = 1000;
    this.matchesPlayed = 0;
    this.wins = 0;
    this.losses = 0;
    this.ranking = -1;
    this.activeMatches = Collections.emptyList();
    this.active = true;
    this.daysInactive = 0;
    this.winStreak = 0;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getElo() {
    return elo;
  }

  public void setElo(long elo) {
    this.elo = elo;
  }

  public int getMatchesPlayed() {
    return matchesPlayed;
  }

  public void setMatchesPlayed(int matchesPlayed) {
    this.matchesPlayed = matchesPlayed;
  }

  public int getWins() {
    return wins;
  }

  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getLosses() {
    return losses;
  }

  public void setLosses(int losses) {
    this.losses = losses;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getActiveMatches() {
    return activeMatches;
  }

  public void setActiveMatches(List<String> activeMatches) {
    this.activeMatches = activeMatches;
  }

  public boolean isActive() {
    return active == null || active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int getDaysInactive() {
    return daysInactive;
  }

  public void setDaysInactive(int daysInactive) {
    this.daysInactive = daysInactive;
  }

  public int getWinStreak() {
    return winStreak;
  }

  public void setWinStreak(int winStreak) {
    this.winStreak = winStreak;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "Player{" +
        "id='" + id + '\'' +
        ", elo=" + elo +
        ", matchesPlayed=" + matchesPlayed +
        ", wins=" + wins +
        ", losses=" + losses +
        ", ranking=" + ranking +
        ", name='" + name + '\'' +
        ", activeMatches=" + activeMatches +
        ", active=" + active +
        ", daysInactive=" + daysInactive +
        ", winStreak=" + winStreak +
        ", version=" + version +
        '}';
  }

  public void cancelMatch(Player opponent) {
    this.activeMatches.remove(opponent.getId());
  }

  public void addMatch(Player opponent) {
    this.activeMatches.add(opponent.getId());
  }

  public void applyMatch(Player opponent, int modifier, boolean win) {
    this.daysInactive = 0;
    this.activeMatches.remove(opponent.getId());
    this.matchesPlayed++;

    if (win) {
      this.wins++;
      this.winStreak++;
      this.elo += modifier;
    } else {
      this.losses++;
      this.winStreak = 0;
      this.elo -= modifier;
    }
  }

  public void setInactive() {
    this.active = false;
    this.ranking = -1;
  }

  public void incrementDaysInactive() {
    this.daysInactive++;
  }

  public void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    buildTableIfMissing(client, mapper, Player.class, "PlayerTable");
  }
}
