package com.pongbot.models;

public class EventInfo {
  private String type;
  private String event_ts;
  private String user;
  private String channel;
  private String channel_type;
  private String text;

  public EventInfo() {}

  public EventInfo(String type, String event_ts, String user, String channel, String channel_type, String text) {
    this.type = type;
    this.event_ts = event_ts;
    this.user = user;
    this.channel = channel;
    this.channel_type = channel_type;
    this.text = text;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEvent_ts() {
    return event_ts;
  }

  public void setEvent_ts(String event_ts) {
    this.event_ts = event_ts;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getChannel_type() {
    return channel_type;
  }

  public void setChannel_type(String channel_type) {
    this.channel_type = channel_type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "EventRequest{" +
        "type='" + type + '\'' +
        ", event_ts='" + event_ts + '\'' +
        ", user='" + user + '\'' +
        ", channel='" + channel + '\'' +
        ", channel_type='" + channel_type + '\'' +
        ", text='" + text + '\'' +
        '}';
  }
}
