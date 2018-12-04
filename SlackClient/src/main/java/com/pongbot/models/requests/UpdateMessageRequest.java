package com.pongbot.models.requests;

import java.util.List;

import com.pongbot.models.Attachment;

public class UpdateMessageRequest {
  private String channel;
  private String text;
  private String ts;
  private List<Attachment> attachments;

  public UpdateMessageRequest() {
  }

  public UpdateMessageRequest(String text, String ts, List<Attachment> attachments) {
    this.text = text;
    this.ts = ts;
    this.attachments = attachments;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTs() {
    return ts;
  }

  public void setTs(String ts) {
    this.ts = ts;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }
}
