package com.pongbot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pongbot.db.dao.EventTableDao;
import com.pongbot.db.dynamo.models.Event;
import com.pongbot.models.Action;
import com.pongbot.models.InteractionRequest;
import com.pongbot.queues.dao.MatchQueueDao;
import com.pongbot.queues.models.events.UserJoinTask;
import com.pongbot.queues.models.matches.MatchResultTask;
import com.pongbot.queues.models.matches.MatchResultTask.MatchResultType;

public class InteractionHandler implements RequestHandler<InteractionRequest, String> {

  @Override
  public String handleRequest(InteractionRequest interactionRequest, Context context) {
    LambdaLogger logger = context.getLogger();

    logger.log(String.format("Handling interaction %s", interactionRequest));

    dedupeEvent(interactionRequest.getAction_ts(), interactionRequest.getUser().getId(), interactionRequest.getType());

    if (!interactionRequest.getType().equals("interactive_message")) {
      throw new IllegalArgumentException("Interaction is not of type interactive_message, ignoring");
    }

    switch (interactionRequest.getCallback_id()) {
      case "match_result":
        if (interactionRequest.getActions().size() > 1) {
          throw new IllegalArgumentException("Too many actions returned for match result");
        }

        Action action = interactionRequest.getActions().get(0);
        String actionType = action.getValue();
        MatchResultType resultType;

        String caller = interactionRequest.getUser().getId();
        String opponent = action.getName();

        switch (actionType) {
          case "win":
            resultType = MatchResultType.WIN_CLAIM;
            break;
          case "deny":
            resultType = MatchResultType.WIN_DENY;
            break;
          case "loss":
            resultType = MatchResultType.LOSS;
            break;
          case "confirm":
            resultType = MatchResultType.WIN_CONFIRMATION;
            break;
          default:
            throw new IllegalArgumentException(String.format("No response found for action %s", action.getValue()));
        }

        MatchResultTask task = new MatchResultTask(caller, opponent, resultType);

        logger.log(String.format("Enqueuing MatchResultTask %s", task));

        MatchQueueDao matchQueueDao = QueueProvider.getMatchQueueDao();
        matchQueueDao.put(task);
        return "";

      case "rejoin":
        UserJoinTask userJoinTask = new UserJoinTask(interactionRequest.getUser().getId());
        QueueProvider.getEventQueueDao().put(userJoinTask);
        return "";

      default:
        throw new IllegalArgumentException(String.format("No response found for interaction callback %s", interactionRequest.getCallback_id()));
    }
  }

  private void dedupeEvent(String timestamp, String userId, String type) {
    if (timestamp.equals("")) return;

    EventTableDao eventTableDao = TableProvider.getEventTableDao();
    Event event = new Event(timestamp, userId, type, "event");

    if (eventTableDao.eventExists(event)) {
      throw new IllegalArgumentException("Event already exists");
    } else {
      eventTableDao.putEvent(event);
    }
  }
}
