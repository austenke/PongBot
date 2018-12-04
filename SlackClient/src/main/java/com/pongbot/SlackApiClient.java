package com.pongbot;

import static com.pongbot.Config.BOT_ID;
import static com.pongbot.Config.TOKEN;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.pongbot.models.BotMessage;
import com.pongbot.models.ChannelRequest;
import com.pongbot.models.UserRequest;
import com.pongbot.models.requests.UpdateMessageRequest;

public class SlackApiClient {

  private HttpClient httpClient;

  private final static Gson mapper = new Gson();

  public SlackApiClient() {
    this.httpClient = new HttpClient();
  }

  public void updateMessage(String userId, UpdateMessageRequest message) {
    message.setChannel(getUserChannel(userId));

    PostMethod postMethod = new PostMethod("https://slack.com/api/chat.update");

    postMethod.setRequestBody(mapper.toJson(message));
    postMethod.setRequestHeader("Content-Type", "application/json");
    postMethod.setRequestHeader("Authorization", "Bearer " + TOKEN);

    String response = execute(postMethod);
    System.out.println("updateMessage http request returned " + response);
  }

  public String postToChannel(String channelId, BotMessage message) {
    PostMethod postMethod = new PostMethod("https://slack.com/api/chat.postMessage");

    message.setChannel(channelId);

    postMethod.setRequestBody(mapper.toJson(message));
    postMethod.setRequestHeader("Content-Type", "application/json");
    postMethod.setRequestHeader("Authorization", "Bearer " + TOKEN);

    String response = execute(postMethod);
    System.out.println("postToChannel http request returned " + response);

    return response;
  }

  public List<String> getPlayersInChannel(String channelId) {
    GetMethod getMethod = new GetMethod("https://slack.com/api/channels.info");

    NameValuePair tokenPair = new NameValuePair("token", TOKEN);
    NameValuePair channelPair = new NameValuePair("channel", channelId);
    NameValuePair[] nameValuePairs = new NameValuePair[] {tokenPair, channelPair};

    getMethod.setQueryString(nameValuePairs);

    String response = execute(getMethod);

    System.out.println("getPlayersInChannel http request returned " + response);

    ChannelRequest channelRequest = mapper.fromJson(response, ChannelRequest.class);
    System.out.println("Getting info for channel " + channelRequest.getChannel().getName());
    List<String> members = channelRequest.getChannel().getMembers();
    members.remove(BOT_ID);
    return members;
  }

  public void testSendMessage(BotMessage message) {
    PostMethod postMethod = new PostMethod("https://slack.com/api/im.open");

    NameValuePair tokenPair = new NameValuePair("token", TOKEN);
    NameValuePair channelPair = new NameValuePair("user", "UBHUPSUBE");
    NameValuePair[] nameValuePairs = new NameValuePair[] {tokenPair, channelPair};
    postMethod.setRequestBody(nameValuePairs);

    String response = execute(postMethod);
    System.out.println("im.open http request returned " + response);

    ChannelRequest channelRequest = mapper.fromJson(response, ChannelRequest.class);
    String channelId = channelRequest.getChannel().getId();
    System.out.println("Opening channel " + channelId);

    postToChannel(channelId, message);
  }

  public String sendMessage(String userId, BotMessage message) {
    return postToChannel(getUserChannel(userId), message);
  }

  public String getUsername(String userId) {
    PostMethod postMethod = new PostMethod("https://slack.com/api/users.info");

    NameValuePair tokenPair = new NameValuePair("token", TOKEN);
    NameValuePair channelPair = new NameValuePair("user", userId);
    NameValuePair[] nameValuePairs = new NameValuePair[] {tokenPair, channelPair};
    postMethod.setRequestBody(nameValuePairs);

    String response = execute(postMethod);
    System.out.println("users.info http request returned " + response);

    UserRequest userRequest = mapper.fromJson(response, UserRequest.class);
    String name;

    if (!userRequest.getUser().getProfile().getDisplay_name_normalized().equals("")) {
      name = userRequest.getUser().getProfile().getDisplay_name_normalized();
    } else {
      name = userRequest.getUser().getName();
    }

    System.out.println("Got name " + name + " from ID " + userId);

    return name;
  }

  public void deleteMessage(String userId, String messageTs) {
    PostMethod postMethod = new PostMethod("https://slack.com/api/chat.delete");

    NameValuePair tokenPair = new NameValuePair("token", TOKEN);
    NameValuePair tsPair = new NameValuePair("ts", messageTs);
    NameValuePair channelPair = new NameValuePair("channel", getUserChannel(userId));
    NameValuePair[] nameValuePairs = new NameValuePair[] {tokenPair, channelPair, tsPair};
    postMethod.setRequestBody(nameValuePairs);

    String response = execute(postMethod);
    System.out.println("deleteMessage returned " + response);
  }

  private String getUserChannel(String userId) {
    PostMethod postMethod = new PostMethod("https://slack.com/api/im.open");

    NameValuePair tokenPair = new NameValuePair("token", TOKEN);
    NameValuePair channelPair = new NameValuePair("user", userId);
    NameValuePair[] nameValuePairs = new NameValuePair[] {tokenPair, channelPair};
    postMethod.setRequestBody(nameValuePairs);

    String response = execute(postMethod);
    System.out.println("im.open http request returned " + response);

    ChannelRequest channelRequest = mapper.fromJson(response, ChannelRequest.class);
    String channelId = channelRequest.getChannel().getId();
    System.out.println("Opening channel " + channelId);

    return channelId;
  }

  private String execute(HttpMethod httpMethod) {
    String jsonString = "";

    try {
      int response = httpClient.executeMethod(httpMethod);

      if (response == 200) {
        StringWriter writer = new StringWriter();
        IOUtils.copy(httpMethod.getResponseBodyAsStream(), writer, StandardCharsets.UTF_8);
        jsonString = writer.toString();
      } else {
        throw new IllegalArgumentException("SLACK API RETURNED " + response);
      }
    } catch (IOException e) {
      System.out.println("Encountered: " + e.getMessage());
      e.printStackTrace();
    }

    httpMethod.releaseConnection();
    return jsonString;
  }
}
