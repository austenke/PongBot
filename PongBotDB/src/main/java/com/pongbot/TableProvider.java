package com.pongbot;

import static com.pongbot.Config.AWS_CREDENTIALS;
import static com.pongbot.Config.REGION;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.pongbot.db.dao.EventTableDao;
import com.pongbot.db.dao.LeaderboardTableDao;
import com.pongbot.db.dao.MatchTableDao;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dao.QueueTableDao;
import com.pongbot.db.dao.implementations.EventTableDaoImpl;
import com.pongbot.db.dao.implementations.LeaderboardTableDaoImpl;
import com.pongbot.db.dao.implementations.MatchTableDaoImpl;
import com.pongbot.db.dao.implementations.PlayerTableDaoImpl;
import com.pongbot.db.dao.implementations.QueueTableDaoImpl;

public class TableProvider {
  private static final DynamoDBMapper mapper = getMapper();

  public static MatchTableDao getMatchTableDao() {
    return new MatchTableDaoImpl(mapper);
  }

  public static PlayerTableDao getPlayerTableDao() {
    return new PlayerTableDaoImpl(mapper);
  }

  public static QueueTableDao getQueueTableDao() {
    return new QueueTableDaoImpl(mapper);
  }

  public static EventTableDao getEventTableDao() {
    return new EventTableDaoImpl(mapper);
  }

  public static LeaderboardTableDao getLeaderboardTableDao() {
    return new LeaderboardTableDaoImpl(mapper);
  }

  public static DynamoDBMapper getMapper() {
    return new DynamoDBMapper(getAmazonDynamoDBClient());
  }

  public static AmazonDynamoDBClient getAmazonDynamoDBClient() {
    AmazonDynamoDBClient client = new AmazonDynamoDBClient(AWS_CREDENTIALS);
    client.setRegion(REGION);
    return client;
  }
}
