package com.pongbot.models;

public class PostMessageResponse {
  private String channel;
  private String ts;

  public PostMessageResponse() { }

  public PostMessageResponse(String channel, String ts) {
    this.channel = channel;
    this.ts = ts;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getTs() {
    return ts;
  }

  public void setTs(String ts) {
    this.ts = ts;
  }

  @Override
  public String toString() {
    return "PostMessageResponse{" +
        "channel='" + channel + '\'' +
        ", ts='" + ts + '\'' +
        '}';
  }
}
