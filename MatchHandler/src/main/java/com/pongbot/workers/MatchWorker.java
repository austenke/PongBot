package com.pongbot.workers;

import static com.pongbot.Config.CHANNEL_ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.json.Jackson;
import com.pongbot.QueueProvider;
import com.pongbot.SlackApiClient;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.MatchTableDao;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Match;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.helpers.EloHelper;
import com.pongbot.helpers.EmoteRandomizer;
import com.pongbot.helpers.MatchAnnouncementGenerator;
import com.pongbot.models.Action;
import com.pongbot.models.Attachment;
import com.pongbot.models.BotMessage;
import com.pongbot.models.PostMessageResponse;
import com.pongbot.queues.dao.MatchmakingQueueDao;
import com.pongbot.queues.models.matches.MatchResultTask;
import com.pongbot.models.requests.UpdateMessageRequest;
import com.pongbot.queues.models.matchmaking.EnqueueTask;

public class MatchWorker {

  private LambdaLogger logger;

  private MatchTableDao matchTableDao;
  private PlayerTableDao playerTableDao;
  private MatchmakingQueueDao matchmakingQueueDao;

  private SlackApiClient slackApiClient;

  public MatchWorker(LambdaLogger logger) {
    this.matchTableDao = TableProvider.getMatchTableDao();
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.matchmakingQueueDao = QueueProvider.getMatchmakingQueueDao();
    this.slackApiClient = new SlackApiClient();
    this.logger = logger;
  }

  public void handleMatchResult(MatchResultTask matchResultTask) {
    Player player = playerTableDao.get(matchResultTask.getSubjectId());

    if (!player.isActive()) {
      slackApiClient.sendMessage(matchResultTask.getSubjectId(), new BotMessage("You are not currently a member of the #ping-pong-league channel, please join it to be assigned matches again"));
      logger.log(String.format("Cannot process match for player %s because they are not active", player.getName()));
      return;
    }

    if (!player.getActiveMatches().contains(matchResultTask.getOpponentId())) {
      slackApiClient.sendMessage(matchResultTask.getSubjectId(), new BotMessage(generateMatchNotFoundMessage(matchResultTask.getOpponentId())));
      logger.log(String.format("Player %s does not have %s in %s", player.getName(), matchResultTask.getOpponentId(), player.getActiveMatches()));
      return;
    }

    logger.log(String.format("Processing match result between %s and %s", matchResultTask.getSubjectId(), matchResultTask.getOpponentId()));

    Optional<Match> optionalMatch = matchTableDao.getIncompleteMatches(matchResultTask.getSubjectId(), matchResultTask.getOpponentId());

    if (!optionalMatch.isPresent()) {
      slackApiClient.sendMessage(matchResultTask.getSubjectId(), new BotMessage(generateMatchNotFoundMessage(matchResultTask.getOpponentId())));
      logger.log(String.format("Match between %s and %s not found", matchResultTask.getSubjectId(), matchResultTask.getOpponentId()));
      return;
    }

    switch (matchResultTask.getResultType()) {
      case WIN_CLAIM:
        processWinClaim(matchResultTask.getSubjectId(), matchResultTask.getOpponentId(), true, optionalMatch.get());
        break;
      case WIN_DENY:
        processWinClaim(matchResultTask.getSubjectId(), matchResultTask.getOpponentId(), false, optionalMatch.get());
        break;
      case WIN_CONFIRMATION:
      case LOSS:
        processLoss(matchResultTask.getOpponentId(), matchResultTask.getSubjectId(), optionalMatch.get());

        EnqueueTask enqueueTask = new EnqueueTask(matchResultTask.getSubjectId(), matchResultTask.getOpponentId());
        matchmakingQueueDao.put(enqueueTask);

        enqueueTask = new EnqueueTask(matchResultTask.getOpponentId(), matchResultTask.getSubjectId());
        matchmakingQueueDao.put(enqueueTask);

        break;
      default:
        throw new IllegalArgumentException(String.format("Unexpected match result type %s", matchResultTask.getResultType()));
    }
  }

  private void processWinClaim(String winnerId, String loserId, boolean winClaim, Match match) {

    if (winClaim) {
      Map<String, String> playerMessageMap = match.getPlayers();
      String loserMessageId = playerMessageMap.get(loserId);
      slackApiClient.deleteMessage(loserId, loserMessageId);
    }

    Action winButton = new Action(winnerId, "Confirm", "button", "confirm", "primary");
    Action loseButton = new Action(winnerId, "Deny", "button", "deny", "danger");
    Attachment attachment = new Attachment("", "", "match_result", "#3AA3E3", "default", Arrays.asList(winButton, loseButton));

    String claimOrDeny = winClaim? "" : "denied your win report and";
    String message = String.format("<@%s> has %s reported a win against you. Please confirm or deny.", winnerId, claimOrDeny);
    BotMessage botMessage = new BotMessage(message, Collections.singletonList(attachment));

    String winnerMessageId = match.getPlayers().get(winnerId);
    UpdateMessageRequest updateMessageRequest = new UpdateMessageRequest(String.format("Confirming your match win with <@%s>. I will notify you when they respond!", loserId),
        winnerMessageId, Collections.emptyList());
    slackApiClient.updateMessage(winnerId, updateMessageRequest);

    PostMessageResponse response = Jackson.fromJsonString(slackApiClient.sendMessage(loserId, botMessage), PostMessageResponse.class);
    match.getPlayers().put(loserId, response.getTs());
    matchTableDao.putMatch(match);
  }

  private void processLoss(String winnerId, String loserId, Match match) {
    match.setComplete(true);
    matchTableDao.putMatch(match);

    Player winner = playerTableDao.get(winnerId);
    long winnerPrevElo = winner.getElo();

    Player loser = playerTableDao.get(loserId);
    long loserPrevElo = loser.getElo();
    int loserPrevWinStreak = loser.getWinStreak();

    int modifier = EloHelper.calculateModifier(winner.getElo(), loser.getElo());

    winner.applyMatch(loser, modifier, true);
    playerTableDao.put(winner);

    loser.applyMatch(winner, modifier, false);
    playerTableDao.put(loser);

    Map<String, String> playerMessageMap = match.getPlayers();

    String winnerMessageId = playerMessageMap.get(winnerId);
    String loserMessageId = playerMessageMap.get(loserId);

    slackApiClient.deleteMessage(winnerId, winnerMessageId);
    slackApiClient.sendMessage(winnerId, new BotMessage(generateWinMessage(loserId, winner.getElo())));
    slackApiClient.updateMessage(loserId, new UpdateMessageRequest(generateLossMessage(winnerId, loser.getElo()), loserMessageId, Collections.emptyList()));
    slackApiClient.postToChannel(CHANNEL_ID, MatchAnnouncementGenerator.announceMatchResults(winner, winnerPrevElo, loser, loserPrevElo, loserPrevWinStreak));
  }

  private static String generateMatchNotFoundMessage(String opponentId) {
    return String.format("%s You do not have any active matches against <@%s>.", EmoteRandomizer.getShrug(), opponentId);
  }

  private static String generateWinMessage(String opponentId, long elo) {
    return String.format("%s Your win against <@%s> has been recorded, your elo score is now %s.", EmoteRandomizer.getHappy(), opponentId, elo);
  }

  private static String generateLossMessage(String opponentId, long elo) {
    return String.format("%s Your loss against <@%s> has been recorded, your elo score is now %s.", EmoteRandomizer.getSad(), opponentId, elo);
  }
}
