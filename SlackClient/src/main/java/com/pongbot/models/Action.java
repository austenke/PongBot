package com.pongbot.models;

public class Action {
  private String name;
  private String text;
  private String type;
  private String value;
  private String style;

  public Action() {}

  public Action(String name, String text, String type, String value, String style) {
    this.name = name;
    this.text = text;
    this.type = type;
    this.value = value;
    this.style = style;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  @Override
  public String toString() {
    return "Action{" +
        "name='" + name + '\'' +
        ", text='" + text + '\'' +
        ", type='" + type + '\'' +
        ", value='" + value + '\'' +
        ", style='" + style + '\'' +
        '}';
  }
}
