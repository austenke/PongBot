package com.pongbot.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EmoteRandomizer {
  private static final List<String> sad = Arrays.asList(":sadpanda:", ":sad-panda:", ":sadpika:", ":saddonut:", ":sadcat2:", ":sadparrot:", ":sad-zoidberg:");
  private static final List<String> happy = Arrays.asList(":parrot:", ":partyparrot:", ":shuffleparrot:", ":party:", ":yay:", ":yay-fox:");
  private static final List<String> shrug = Arrays.asList(":shrug:", ":shrugs:", ":shrugs2:", ":shrugs-3:", ":moreshrugs:");
  private static final List<String> surprise = Arrays.asList(":shiba-surprise:", ":catsurprise:", ":woah:", ":wow-spin:");
  private static final List<String> crazy = Arrays.asList(":alert:", ":parrotintensifies:", ":go-crazy:", ":sharkdance:");
  private static final List<String> fire = Arrays.asList(":fire:", ":fireball2:", ":sharkdance:", ":shuffleparrot:");

  public static String getHappy() {
    return getRandom(happy);
  }

  public static String getSad() {
    return getRandom(sad);
  }

  public static String getShrug() {
    return getRandom(shrug);
  }

  public static String getSurprise() {
    return getRandom(surprise);
  }

  public static String getCrazy() {
    return getRandom(crazy);
  }

  public static String getFire() {
    return getRandom(fire);
  }

  private static String getRandom(List<String> list) {
    return list.get(new Random().nextInt(list.size()));
  }
}
