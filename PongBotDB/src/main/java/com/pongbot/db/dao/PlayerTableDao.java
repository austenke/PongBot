package com.pongbot.db.dao;

import java.util.List;

import com.pongbot.db.dynamo.models.Player;

public interface PlayerTableDao {
  void put(Player player);
  Player get(String id);
  List<Player> getAllPlayers();
}
