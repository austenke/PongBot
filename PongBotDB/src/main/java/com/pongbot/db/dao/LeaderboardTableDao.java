package com.pongbot.db.dao;

import java.util.Map;
import java.util.Optional;

public interface LeaderboardTableDao {
  Optional<String> getPlayerWithRank(int rank);
  Optional<Integer> getPlayerRank(String playerId);
  Map<Integer, String> getAll();
  void put(int rank, String playerId);
  void putBatch(Map<Integer, String> rankings);
}
