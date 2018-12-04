package com.pongbot;

public class QueueConfig {
  public static final String MATCH_QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/423794175605/MatchQueue.fifo";
  public static final String MATCH_QUEUE_FUNCTION = "MatchHandler";
  public static final int MATCH_QUEUE_TRIGGER_THRESHOLD = 1;

  public static final String MATCHMAKING_QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/423794175605/MatchmakingQueue.fifo";
  public static final String MATCHMAKING_QUEUE_FUNCTION = "MatchmakingHandler";
  public static final int MATCHMAKING_QUEUE_TRIGGER_THRESHOLD = 1;

  public static final String EVENT_QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/423794175605/EventQueue.fifo";
  public static final String EVENT_QUEUE_FUNCTION = "EventHandler";
  public static final int EVENT_QUEUE_TRIGGER_THRESHOLD = 1;
}
