package com.pongbot.models;

import java.util.List;

public class ChannelRequest {
  private ChannelDetailsRequest channel;

  public ChannelRequest() { }

  public ChannelRequest(ChannelDetailsRequest channel) {
    this.channel = channel;
  }

  public ChannelDetailsRequest getChannel() {
    return channel;
  }

  public void setChannel(ChannelDetailsRequest channel) {
    this.channel = channel;
  }

  @Override
  public String toString() {
    return "ChannelRequest{" +
        "channel=" + channel +
        '}';
  }

  public static class ChannelDetailsRequest {
    private String id;
    private List<String> members;
    private String name;

    public ChannelDetailsRequest() {}

    public ChannelDetailsRequest(String id, List<String> members, String name) {
      this.id = id;
      this.members = members;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public List<String> getMembers() {
      return members;
    }

    public void setMembers(List<String> members) {
      this.members = members;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "ChannelDetailsRequest{" +
          "id='" + id + '\'' +
          ", members=" + members +
          ", name='" + name + '\'' +
          '}';
    }
  }
}
