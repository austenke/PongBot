package com.pongbot.helpers;

public class EloHelper {
  private static final int K = 100;

  public static int calculateModifier(long winnerElo, long loserElo) {
    float winProbability = winProbability(winnerElo, loserElo);
    return Math.abs(Math.round(K * winProbability));
  }

  private static float winProbability(long winnerRating, long loserRating) {
    return (1 / (1.0f + (float) (Math.pow(10, 1.0f * (winnerRating - loserRating) / 400))));
  }
}