package com.pongbot.db.dao.implementations;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;

public class PlayerTableDaoImpl implements PlayerTableDao {
  private DynamoDBMapper mapper;


  public PlayerTableDaoImpl(DynamoDBMapper mapper) {
    this.mapper = mapper;
  }

  public void put(Player player) {
    mapper.save(player);
  }

  public Player get(String id) {
    return mapper.load(Player.class, id);
  }

  public List<Player> getAllPlayers() {
    return new ArrayList<>(mapper.scan(Player.class, new DynamoDBScanExpression()));
  }
}