package com.pongbot.db.dao.implementations;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.pongbot.db.dao.QueueTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.db.dynamo.models.Queue;

public class QueueTableDaoImpl implements QueueTableDao {
  private DynamoDBMapper mapper;

  public QueueTableDaoImpl(DynamoDBMapper mapper) {
    this.mapper = mapper;
  }

  public Set<String> getOpponents(Player player, int count, String ignorePlayer) {
    return new HashSet<>(mapper.scan(Queue.class, new DynamoDBScanExpression())).stream()
        .sorted(Comparator.comparingLong(Queue::getTimestamp))
        .map(Queue::getPlayerId)
        .collect(Collectors.toSet());
  }

  public void decrementDesiredMatchCount(Player player) {
    Queue playerQueue = mapper.load(Queue.class, player.getId());
    playerQueue.decrementDesiredMatches();

    if (playerQueue.getDesiredMatches() <= 0) {
      mapper.delete(playerQueue);
    } else {
      mapper.save(playerQueue);
    }
  }

  public void removePlayer(Player player) {
    mapper.delete(new Queue(player.getId()), new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
  }

  public void enqueuePlayer(Player player, int count) {
    Queue queue = mapper.load(Queue.class, player.getId());

    if (queue == null) {
      queue = new Queue(player.getId(), System.currentTimeMillis(), count);
    } else {
      queue.incrementDesiredMatches(count);
    }

    mapper.save(queue);
  }
}
