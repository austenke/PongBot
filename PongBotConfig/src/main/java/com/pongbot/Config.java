package com.pongbot;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;

public class Config {
  public static final String ADMIN_ID = "";
  public static final String BOT_ID = "";
  public static final String TOKEN = "";
  public static final String CHANNEL_ID = "";
  public static final AWSCredentials AWS_CREDENTIALS = new BasicAWSCredentials();
  public static final Region REGION = RegionUtils.getRegion("");
}
