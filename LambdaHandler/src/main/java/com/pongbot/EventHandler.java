package com.pongbot;

import static com.pongbot.Config.BOT_ID;
import static com.pongbot.Config.CHANNEL_ID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pongbot.db.dao.EventTableDao;
import com.pongbot.db.dynamo.models.Event;
import com.pongbot.models.EventInfo;
import com.pongbot.models.EventRequest;
import com.pongbot.queues.dao.EventQueueDao;
import com.pongbot.queues.models.Task;
import com.pongbot.queues.models.events.ActivityCheckTask;
import com.pongbot.queues.models.events.BuildLeaderboardTask;
import com.pongbot.queues.models.events.BuildTablesTask;
import com.pongbot.queues.models.events.ChatTask;
import com.pongbot.queues.models.events.UserJoinTask;
import com.pongbot.queues.models.events.UserLeaveTask;

public class EventHandler implements RequestHandler<EventRequest, String> {

  @Override
  public String handleRequest(EventRequest eventRequest, Context context) {
    LambdaLogger logger = context.getLogger();

    logger.log(String.format("Handling event %s", eventRequest));

    if (eventRequest.getEvent() == null) {
      throw new IllegalArgumentException("Received event with null event object");
    }

    EventInfo event = eventRequest.getEvent();

    dedupeEvent(event.getEvent_ts(), event.getUser(), event.getChannel());

    EventQueueDao eventQueueDao = QueueProvider.getEventQueueDao();
    Task task;

    if (event.getType().equals("message") && event.getChannel_type().equals("im")) {
      validateUser(event.getUser());
      validateText(event.getText());
      task = new ChatTask(event.getText(), event.getUser(), event.getChannel());
    } else if (event.getType().equals("activity_check")) {
      task = new ActivityCheckTask();
    } else if (event.getType().equals("build_leaderboard")) {
      task = new BuildLeaderboardTask();
    } else if (event.getType().equals("build_tables")) {
      task = new BuildTablesTask();
    } else if (event.getType().equals("member_joined_channel")) {
      validateUser(event.getUser());
      validateChannel(event.getChannel());
      task = new UserJoinTask(event.getUser());
    } else if (event.getType().equals("member_left_channel")) {
      validateUser(event.getUser());
      validateChannel(event.getChannel());
      task = new UserLeaveTask(event.getUser());
    } else {
      throw  new IllegalArgumentException(String.format("No handler for event type %s", event.getType()));
    }

    logger.log(String.format("Created task with attributes %s", task.toAttributeValues()));
    logger.log(String.format("Enqueuing %s", task));
    eventQueueDao.put(task);

    return "";
  }

  private void validateChannel(String channel) {
    if (!channel.equals(CHANNEL_ID)) {
      throw new IllegalArgumentException(String.format("Ignoring event for channel %s", channel));
    }
  }

  private void validateText(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Text is null");
    }

    if (text.split("!").length > 2) {
      throw new IllegalArgumentException("Received multiple commands in one message");
    }
  }

  private void validateUser(String user) {
    if (user == null || user.equals("null")) {
      throw new IllegalArgumentException("EventInfo user is null");
    }

    if (user.equals(BOT_ID)) {
      throw new IllegalArgumentException("EventInfo user is bot");
    }
  }

  private void dedupeEvent(String timestamp, String userId, String channelId) {
    if (timestamp.equals("")) return;

    EventTableDao eventTableDao = TableProvider.getEventTableDao();
    Event event = new Event(timestamp, userId, channelId, "event");

    if (eventTableDao.eventExists(event)) {
      throw new IllegalArgumentException("Event already exists");
    } else {
      eventTableDao.putEvent(event);
    }
  }
}
