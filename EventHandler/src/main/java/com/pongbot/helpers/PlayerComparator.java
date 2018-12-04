package com.pongbot.helpers;

import java.util.Comparator;

import com.pongbot.db.dynamo.models.Player;

public class PlayerComparator implements Comparator<Player> {
  @Override
  public int compare(Player firstPlayer, Player secondPlayer) {
    return Long.compare(firstPlayer.getElo(), secondPlayer.getElo());
  }
}
