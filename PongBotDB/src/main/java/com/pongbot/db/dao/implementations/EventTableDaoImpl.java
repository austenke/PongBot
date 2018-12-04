package com.pongbot.db.dao.implementations;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.pongbot.db.dao.EventTableDao;
import com.pongbot.db.dynamo.models.Event;

public class EventTableDaoImpl implements EventTableDao {
  private DynamoDBMapper mapper;

  public EventTableDaoImpl(DynamoDBMapper mapper) {
    this.mapper = mapper;
  }

  public void putEvent(Event event) {
    Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
    expectedAttributes.put("hashKey", new ExpectedAttributeValue(false));

    DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
    saveExpression.setExpected(expectedAttributes);

    mapper.save(event);
  }

  public boolean eventExists(Event event) {
    return mapper.load(Event.class, event.getCompoundKey()) != null;
  }
}
