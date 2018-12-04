package com.pongbot.db.dynamo;

import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.pongbot.db.dynamo.models.DynamoTable;
import com.pongbot.db.dynamo.models.Event;
import com.pongbot.db.dynamo.models.Leaderboard;
import com.pongbot.db.dynamo.models.Match;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.db.dynamo.models.Queue;

public class DynamoUtils {

  private final List<DynamoTable> tables = Arrays.asList(new Event(),
                                                         new Leaderboard(),
                                                         new Match(),
                                                         new Player(),
                                                         new Queue());

  private final AmazonDynamoDBClient client;
  private final DynamoDBMapper mapper;

  public DynamoUtils(AmazonDynamoDBClient client, DynamoDBMapper mapper) {
    this.client = client;
    this.mapper = mapper;
  }

  // This function runs on a weekly cron
  public void buildTables() {
    tables.forEach(table -> table.buildTable(client, mapper));
  }
}
