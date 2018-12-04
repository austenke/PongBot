package com.pongbot.workers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.TableProvider;
import com.pongbot.db.dao.LeaderboardTableDao;
import com.pongbot.db.dao.PlayerTableDao;
import com.pongbot.db.dynamo.models.Player;
import com.pongbot.queues.models.matchmaking.UpdateRankTask;

public class RankWorker {

  private final LambdaLogger logger;

  private final PlayerTableDao playerTableDao;
  private final LeaderboardTableDao leaderboardTableDao;

  public RankWorker(LambdaLogger logger) {
    this.logger = logger;
    this.playerTableDao = TableProvider.getPlayerTableDao();
    this.leaderboardTableDao = TableProvider.getLeaderboardTableDao();
  }

  public void adjustRank(UpdateRankTask task) {
    Player player = playerTableDao.get(task.getPlayerId());
    boolean moveUp = task.getMoveUp();

    Map<Integer, String> changes = new HashMap<>();

    Optional<Integer> optionalRank = leaderboardTableDao.getPlayerRank(player.getId());

    if (!optionalRank.isPresent()) {
      scanAndPlace(player);
      return;
    }

    int currentRank = optionalRank.get();

    int desiredRank = lookAtNewRank(currentRank, moveUp);
    Optional<String> playerToSwap = canMove(desiredRank, currentRank, player.getElo());

    while (playerToSwap.isPresent()) {
      changes.put(currentRank, playerToSwap.get());

      currentRank = desiredRank;
      desiredRank = lookAtNewRank(currentRank, moveUp);
    }

    if (changes.isEmpty()) {
      return;
    }

    changes.put(currentRank, player.getId());
    leaderboardTableDao.putBatch(changes);
  }

  private void scanAndPlace(Player player) {
    Map<Integer, String> leaderboard = leaderboardTableDao.getAll();
    Map<Integer, String> newLeaderboard = new HashMap<>();

    for(int i = 0; i < leaderboard.size(); i++) {
      Player playerAtRank = playerTableDao.get(leaderboard.get(i));

      if (player.getElo() > playerAtRank.getElo()) {
        newLeaderboard.put(i, player.getId());

        for (int x = i; x < leaderboard.size(); x++) {
          newLeaderboard.put(x + 1, leaderboard.get(x));
        }
      }
    }

    if (newLeaderboard.isEmpty()) {
      leaderboardTableDao.put(leaderboard.size(), player.getId());
    } else {
      leaderboardTableDao.putBatch(newLeaderboard);
    }
  }

  private Optional<String> canMove(int desiredRank, int currentRank, long playerElo) {
    Optional<String> playerId = leaderboardTableDao.getPlayerWithRank(desiredRank);

    if (!playerId.isPresent()) {
      return Optional.empty();
    }

    Player playerAtRank = playerTableDao.get(playerId.get());

    if (playerAtRank.isActive()) {
      if (desiredRank > currentRank && playerAtRank.getElo() < playerElo) {
        return Optional.empty();
      }

      if (desiredRank < currentRank && playerAtRank.getElo() > playerElo) {
        return Optional.empty();
      }
    }

    return Optional.of(playerAtRank.getId());
  }

  private int lookAtNewRank(int rank, boolean moveUp) {
    if (moveUp) {
      return rank - 1;
    } else {
      return rank + 1;
    }
  }
}
