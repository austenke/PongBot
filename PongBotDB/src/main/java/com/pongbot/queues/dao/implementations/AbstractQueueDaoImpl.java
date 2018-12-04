package com.pongbot.queues.dao.implementations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.pongbot.Config;
import com.pongbot.queues.models.Task;

public abstract class AbstractQueueDaoImpl {

  private final AmazonSQS sqs;
  private final String queueUrl;
  private final int queueThreshold;
  private final String lambdaFunction;

  AbstractQueueDaoImpl(AmazonSQS sqs, String queueUrl, int queueThreshold, String lambdaFunction) {
    this.sqs = sqs;
    this.queueUrl = queueUrl;
    this.queueThreshold = queueThreshold;
    this.lambdaFunction = lambdaFunction;
  }

  public void put(Task task) {
    String id = String.join("-", task.getIdentifier(), String.valueOf(System.currentTimeMillis()));

    SendMessageRequest sendMessageRequest = new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageGroupId(id)
        .withMessageBody(id)
        .withMessageAttributes(task.toAttributeValues())
        .withMessageDeduplicationId(id);

    System.out.println("Sending message " + sendMessageRequest);

    sqs.sendMessage(sendMessageRequest);

    GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest();
    getQueueAttributesRequest.setQueueUrl(queueUrl);
    getQueueAttributesRequest.setAttributeNames(Collections.singletonList("ApproximateNumberOfMessages"));

    GetQueueAttributesResult getQueueAttributesResult = sqs.getQueueAttributes(getQueueAttributesRequest);
    String messageCountStr = getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessages");

    if (messageCountStr == null) throw new IllegalArgumentException(String.format("SQS queue %s returned null ApproximateNumberOfMessages value", queueUrl));

    int messageCount = Integer.valueOf(messageCountStr);

    if (messageCount >= queueThreshold) {
      AWSLambda awsLambda = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(Config.AWS_CREDENTIALS)).withRegion("us-east-2").build();
      InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaFunction).withInvocationType(InvocationType.Event);
      awsLambda.invoke(invokeRequest);
    }
  }

  public void delete(Task task) {
    if (task.getSqsMessageId() == null) {
      throw new IllegalArgumentException(String.format("Message id for task %s is null", task));
    }

    DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
    deleteMessageRequest.setQueueUrl(queueUrl);
    deleteMessageRequest.setReceiptHandle(task.getSqsMessageId());

    sqs.deleteMessage(deleteMessageRequest);
  }

  public List<Task> get(int count) {
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
    receiveMessageRequest.setQueueUrl(queueUrl);
    receiveMessageRequest.setMaxNumberOfMessages(count);
    receiveMessageRequest.setMessageAttributeNames(Collections.singletonList("All"));

    ReceiveMessageResult result = sqs.receiveMessage(receiveMessageRequest);
    List<Message> messages = result.getMessages();

    return messages.stream().map(this::convertMessageToTask).collect(Collectors.toList());
  }

  abstract Task convertMessageToTask(Message message);
}
