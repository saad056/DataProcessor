# Kafka Data Consumer Application

This Java application is designed to consume data from a Kafka topic and write it to a file in a specified format. Kafka is assumed to be running in a virtual machine (VM), while the Java application is running on the host machine.

## Overview
The program establishes a connection to the Kafka cluster, creates a consumer group, and consumes messages from the specified Kafka topic. It is capable of handling data in any format, with the data format being described in the Java class.

## Functionality
- **Consumption**: The application consumes messages from the Kafka topic in a multi-threaded manner.
- **Dynamic Format**: Data format is flexible and can be described within the properties or configuration file.
- **Threshold Handling**: When the number of consumed messages exceeds a threshold, the application writes the data to a file in the configured format.

## Architecture
- **Host Machine**: Where the Java application is running.
- **Virtual Machine (VM)**: Where Kafka is hosted.

## Requirements
- Java 8 or higher
- Apache Kafka
- Maven (for building)
- Docker (for running Kafka in a VM)

## Pre-Requisites
- Docker needs to be installed.
- Use the following docker-compose file to install Kafka:

```yaml
version: '3.7'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    hostname: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 2181:2181
    
  kafka:
    image: confluentinc/cp-kafka:latest
    hostname: kafka
    depends_on:
      - zookeeper
    ports:
      - 9092:9092
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://192.168.56.104:29092  # Replace <VM_IP> with the IP address of your VM accessible from the host machine
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  create-topic:
    image: confluentinc/cp-kafka:latest
    container_name: create-topic
    depends_on:
      - kafka
    environment:
      KAFKA_BROKER: kafka:9092
    command: >
      bash -c '
      sleep 10 &&
      kafka-topics --bootstrap-server kafka:9092 --create --topic TutorialTopic --partitions 5 --replication-factor 1
```
## Installation
1. git clone <repository_url>
2. cd DataConsumer
3. mvn clean install

## Usage
1. Modify the ConfigProperties class to specify Kafka server details, topic, group ID, etc.
2. Implement your custom file writing strategy by extending the FileWriterStrategy interface and adding it to the FileWriterFactory.
3. Run the application: mvn exec:java

## Configuration
- ConfigProperties: Configuration class for Kafka and file writing properties.
- ConsumerConfig: Kafka consumer configuration properties.
- FileWriterFactory: Factory class for obtaining file writer strategies.
- LogWriter: Logging utility class.

## Contributors
- Islam
