package com.pongbot.models;

import java.util.List;

public class BotMessage {
  private String text;
  private List<Attachment> attachments;
  private String token;
  private String channel;
  private boolean replace_original;

  public BotMessage() { }

  public BotMessage(String text, List<Attachment> attachments) {
    this.text = text;
    this.attachments = attachments;
  }

  public BotMessage(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public boolean isReplace_original() {
    return replace_original;
  }

  public void setReplace_original(boolean replace_original) {
    this.replace_original = replace_original;
  }

  @Override
  public String toString() {
    return "BotMessage{" +
        "text='" + text + '\'' +
        ", attachments=" + attachments +
        ", token='" + token + '\'' +
        ", channel='" + channel + '\'' +
        ", replace_original=" + replace_original +
        '}';
  }
}
