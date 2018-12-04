package com.pongbot.models;

public class UserDetails {
  private String id;
  private String name;

  public UserDetails() {}

  public UserDetails(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "UserDetails{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
