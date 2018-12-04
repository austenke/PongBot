package com.pongbot.db.dynamo.models;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

public abstract class DynamoTable {
  private ProvisionedThroughput defaultProvisionedThroughput = new ProvisionedThroughput(1L, 1L);

  public abstract void buildTable(AmazonDynamoDBClient client, DynamoDBMapper mapper);

  private static Optional<DescribeTableResult> getTable(AmazonDynamoDBClient client, String tableName) {
    try {
      return Optional.of(client.describeTable(tableName));
    } catch (ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  void buildTableIfMissing(AmazonDynamoDBClient client, DynamoDBMapper mapper, Class<?> clazz, String tableName) {
    if (!getTable(client, tableName).isPresent()) {
      System.out.println(String.format("Creating %s", tableName));

      CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(clazz);
      createTableRequest.setProvisionedThroughput(defaultProvisionedThroughput);
      createTableRequest.getGlobalSecondaryIndexes().forEach(index -> index.setProvisionedThroughput(defaultProvisionedThroughput));

      try {
        client.createTable(createTableRequest);
      } catch (ResourceInUseException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

  void deleteTable(AmazonDynamoDBClient client, String tableName) {
    if (getTable(client, tableName).isPresent()) {
      System.out.println(String.format("Deleting table %s", tableName));
      client.deleteTable(tableName);
    }
  }
}
