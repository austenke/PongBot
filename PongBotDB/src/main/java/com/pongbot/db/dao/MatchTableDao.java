package com.pongbot.db.dao;

import java.util.Optional;

import com.pongbot.db.dynamo.models.Match;

public interface MatchTableDao {
  void putMatch(Match match);
  Optional<Match> getIncompleteMatches(String firstPlayerId, String secondPlayerId);
}
