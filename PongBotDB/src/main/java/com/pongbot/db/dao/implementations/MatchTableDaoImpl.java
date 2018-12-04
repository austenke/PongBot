package com.pongbot.db.dao.implementations;

import static com.pongbot.db.dynamo.models.Match.buildMatchKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.pongbot.db.dao.MatchTableDao;
import com.pongbot.db.dynamo.models.Match;

public class MatchTableDaoImpl implements MatchTableDao {
  private DynamoDBMapper mapper;

  public MatchTableDaoImpl(DynamoDBMapper mapper) {
    this.mapper = mapper;
  }

  public void putMatch(Match match) {
    mapper.save(match);
  }

  public Optional<Match> getIncompleteMatches(String firstPlayerId, String secondPlayerId) {
    System.out.println("Looking for incomplete matches between " + firstPlayerId + " and " + secondPlayerId);

    Map<String, AttributeValue> eav = new HashMap<>();
    eav.put(":compoundKey", new AttributeValue().withS(buildMatchKey(firstPlayerId, secondPlayerId)));
    eav.put(":false", new AttributeValue().withN("0"));

    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        .withLimit(1)
        .withFilterExpression("compoundKey = :compoundKey AND complete = :false").withExpressionAttributeValues(eav);

    List<Match> matches = mapper.scan(Match.class, scanExpression);
    System.out.println("Scan for incomplete matches returned " + new ArrayList<>(matches));

    if (matches.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(matches.get(0));
    }
  }
}
