package com.pongbot.models;

public class ProfileDetails {
  private String display_name_normalized;

  public ProfileDetails() {}

  public ProfileDetails(String display_name_normalized) {
    this.display_name_normalized = display_name_normalized;
  }

  public String getDisplay_name_normalized() {
    return display_name_normalized;
  }

  public void setDisplay_name_normalized(String display_name_normalized) {
    this.display_name_normalized = display_name_normalized;
  }

  @Override
  public String toString() {
    return "ProfileDetails{" +
        "display_name_normalized='" + display_name_normalized + '\'' +
        '}';
  }
}
