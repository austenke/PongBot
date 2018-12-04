package com.pongbot.models;

import java.util.List;

public class InteractionRequest {
  private String type;
  private List<Action> actions;
  private String callback_id;
  private UserDetails user;
  private String response_url;
  private String action_ts;
  private String message_ts;
  private BotMessage original_message;

  public InteractionRequest() {}

  public InteractionRequest(String type,
                            List<Action> actions,
                            String callback_id,
                            UserDetails user,
                            String response_url,
                            String action_ts,
                            String message_ts,
                            BotMessage original_message) {
    this.type = type;
    this.actions = actions;
    this.callback_id = callback_id;
    this.user = user;
    this.response_url = response_url;
    this.action_ts = action_ts;
    this.message_ts = message_ts;
    this.original_message = original_message;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  public String getCallback_id() {
    return callback_id;
  }

  public void setCallback_id(String callback_id) {
    this.callback_id = callback_id;
  }

  public UserDetails getUser() {
    return user;
  }

  public void setUser(UserDetails user) {
    this.user = user;
  }

  public String getResponse_url() {
    return response_url;
  }

  public void setResponse_url(String response_url) {
    this.response_url = response_url;
  }

  public String getAction_ts() {
    return action_ts;
  }

  public void setAction_ts(String action_ts) {
    this.action_ts = action_ts;
  }

  public String getMessage_ts() {
    return message_ts;
  }

  public void setMessage_ts(String message_ts) {
    this.message_ts = message_ts;
  }

  public BotMessage getOriginal_message() {
    return original_message;
  }

  public void setOriginal_message(BotMessage original_message) {
    this.original_message = original_message;
  }

  @Override
  public String toString() {
    return "InteractionRequest{" +
        "type='" + type + '\'' +
        ", actions=" + actions +
        ", callback_id='" + callback_id + '\'' +
        ", user=" + user +
        ", response_url='" + response_url + '\'' +
        ", action_ts='" + action_ts + '\'' +
        ", message_ts='" + message_ts + '\'' +
        ", original_message=" + original_message +
        '}';
  }
}
