package com.pongbot;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.pongbot.queues.dao.EventQueueDao;
import com.pongbot.queues.dao.MatchQueueDao;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.dao.implementations.EventQueueDaoImpl;
import com.pongbot.queues.dao.implementations.MatchQueueDaoImpl;
import com.pongbot.queues.dao.implementations.MatchmakingQueueDaoImpl;

public class QueueProvider {

  private static final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

  public static EventQueueDao getEventQueueDao() {
    return new EventQueueDaoImpl(sqs);
  }

  public static MatchmakingQueueDao getMatchmakingQueueDao() {
    return new MatchmakingQueueDaoImpl(sqs);
  }

  public static MatchQueueDao getMatchQueueDao() {
    return new MatchQueueDaoImpl(sqs);
  }
}
