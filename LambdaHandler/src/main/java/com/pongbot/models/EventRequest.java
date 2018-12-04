package com.pongbot.models;

import java.util.List;

public class EventRequest {
  private String token;
  private String type;
  private String apiAppId;
  private EventInfo event;
  private List<String> authedUsers;
  private String eventId;
  private long eventTime;
  private String challenge;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getApiAppId() {
    return apiAppId;
  }

  public void setApiAppId(String apiAppId) {
    this.apiAppId = apiAppId;
  }

  public List<String> getAuthedUsers() {
    return authedUsers;
  }

  public void setAuthedUsers(List<String> authedUsers) {
    this.authedUsers = authedUsers;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public long getEventTime() {
    return eventTime;
  }

  public void setEventTime(long eventTime) {
    this.eventTime = eventTime;
  }

  public EventInfo getEvent() {
    return event;
  }

  public void setEvent(EventInfo event) {
    this.event = event;
  }

  public String getChallenge() {
    return challenge;
  }

  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  @Override
  public String toString() {
    return "SlackRequest{" +
        "token='" + token + '\'' +
        ", type='" + type + '\'' +
        ", apiAppId='" + apiAppId + '\'' +
        ", event=" + event +
        ", authedUsers=" + authedUsers +
        ", eventId='" + eventId + '\'' +
        ", eventTime=" + eventTime +
        ", challenge='" + challenge + '\'' +
        '}';
  }
}
