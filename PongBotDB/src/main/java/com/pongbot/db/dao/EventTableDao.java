package com.pongbot.db.dao;

import com.pongbot.db.dynamo.models.Event;

public interface EventTableDao {
  void putEvent(Event event);
  boolean eventExists(Event event);
}
