package com.pongbot.helpers;

import com.pongbot.db.dynamo.models.Player;
import com.pongbot.models.BotMessage;

public class MatchAnnouncementGenerator {
  public static BotMessage announceMatchResults(Player winner, long winnerPrevElo, Player loser, long loserPrevElo, int loserPrevWinStreak) {
    StringBuilder text = new StringBuilder();
    String emote = ":pingpong:";
    String winMessage = " won against ";
    String appendMessage = "";

    if (loser.getRanking() == 1) {
      emote = EmoteRandomizer.getCrazy();
      winMessage = " beat the reigning champion ";
    } else if (loserPrevElo - winnerPrevElo > 100) {
      emote = EmoteRandomizer.getSurprise();
      winMessage = " won a major upset against ";
    } else if (winner.getRanking() < 4 && loser.getRanking() < 4 && winner.getRanking() > 0 && loser.getRanking() > 0) {
      emote = EmoteRandomizer.getCrazy();
    }

    if (winner.getWinStreak() > 2) {
      emote = EmoteRandomizer.getFire();
      appendMessage = String.format("%s is on a %s win streak! ", winner.getName(), winner.getWinStreak());
    } else if (loserPrevWinStreak > 2) {
      appendMessage = String.format("The %s win streak of %s has been broken! ", loserPrevWinStreak, loser.getName());
    }

    text.append(emote);
    text.append(" ");
    text.append(String.format("*%s* (%s -> %s)", winner.getName(), winnerPrevElo, winner.getElo()));
    text.append(winMessage);
    text.append(String.format("*%s* (%s -> %s)", loser.getName(), loserPrevElo, loser.getElo()));
    text.append("! ");
    text.append(appendMessage);
    text.append(emote);

    return new BotMessage(text.toString());
  }
}
