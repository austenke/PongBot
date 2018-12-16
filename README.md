# Ping Pong Slack Bot
This bot creates new ping pong matches between players in a channel and tracks their ELO scores. It is written in Java and runs on AWS Lambda. The bot uses AWS SQS queues and multiple Lambda workers to distribute operations to their corresponding worker. 
