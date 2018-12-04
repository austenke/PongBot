package com.pongbot.db.dao.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.pongbot.db.dao.LeaderboardTableDao;
import com.pongbot.db.dynamo.models.Leaderboard;

public class LeaderboardTableDaoImpl implements LeaderboardTableDao {

  private final DynamoDBMapper mapper;

  public LeaderboardTableDaoImpl(DynamoDBMapper mapper) {
    this.mapper = mapper;
  }

  public Optional<String> getPlayerWithRank(int rank) {
    Leaderboard leaderboard = mapper.load(Leaderboard.class, rank);

    if (leaderboard == null) {
      return Optional.empty();
    }

    return Optional.of(leaderboard.getPlayerId());
  }

  public Optional<Integer> getPlayerRank(String playerId) {
    Map<String, AttributeValue> eav = new HashMap<>();
    eav.put(":v1",  new AttributeValue().withS(playerId));

    DynamoDBQueryExpression<Leaderboard> queryExpression = new DynamoDBQueryExpression<Leaderboard>()
        .withIndexName("playerIdIndex")
        .withConsistentRead(false)
        .withKeyConditionExpression("playerId = :v1")
        .withExpressionAttributeValues(eav);

    List<Leaderboard> results = new ArrayList<>(mapper.query(Leaderboard.class, queryExpression));

    if (results.isEmpty()) {
      return Optional.empty();
    }

    if (results.size() > 1) {
      throw new IllegalArgumentException(String.format("Found %s results when querying for %s", results.size(), playerId));
    }

    return Optional.of(results.get(0).getRank());
  }

  public Map<Integer, String> getAll() {
    return new HashMap<>(mapper.scan(Leaderboard.class, new DynamoDBScanExpression())
        .stream().collect(Collectors.toMap(Leaderboard::getRank, Leaderboard::getPlayerId)));
  }

  public void put(int rank, String playerId) {
    mapper.save(new Leaderboard(rank, playerId));
  }

  public void putBatch(Map<Integer, String> rankings) {
    rankings.forEach(this::put);
  }
}
