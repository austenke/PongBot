package com.pongbot.workers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.pongbot.TableProvider;
import com.pongbot.db.dynamo.DynamoUtils;

public class BuildTablesWorker {

  private final LambdaLogger logger;

  public BuildTablesWorker(LambdaLogger logger) {
    this.logger = logger;
  }

  public void buildTables() {
    logger.log("Building all tables ");
    new DynamoUtils(TableProvider.getAmazonDynamoDBClient(), TableProvider.getMapper()).buildTables();
  }
}
