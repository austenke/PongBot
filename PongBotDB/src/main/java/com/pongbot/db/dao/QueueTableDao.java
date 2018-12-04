package com.pongbot.db.dao;

import java.util.Set;

import com.pongbot.db.dynamo.models.Player;

public interface QueueTableDao {
  Set<String> getOpponents(Player player, int count, String ignorePlayer);
  void enqueuePlayer(Player player, int count);
  void removePlayer(Player player);
  void decrementDesiredMatchCount(Player player);
}
