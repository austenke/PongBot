package com.pongbot.models;

public class UserRequest {
  private UserDetailsRequest user;

  public UserRequest() {}

  public UserRequest(UserDetailsRequest user) {
    this.user = user;
  }

  public UserDetailsRequest getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "UserRequest{" +
        "user=" + user +
        '}';
  }

  public void setUser(UserDetailsRequest user) {
    this.user = user;
  }

  public static class UserDetailsRequest {
    private ProfileDetails profile;
    private String name;

    public UserDetailsRequest() {}

    public UserDetailsRequest(ProfileDetails profile, String name) {
      this.profile = profile;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public ProfileDetails getProfile() {
      return profile;
    }

    public void setProfile(ProfileDetails profile) {
      this.profile = profile;
    }

    @Override
    public String toString() {
      return "UserDetailsRequest{" +
          "profile=" + profile +
          ", name='" + name + '\'' +
          '}';
    }
  }
}
