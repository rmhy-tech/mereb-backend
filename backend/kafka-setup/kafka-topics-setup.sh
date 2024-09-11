#!/bin/bash

# Wait for Kafka to start up
sleep 5

# Create a topic (example: user-topic) using KRaft mode
docker exec -it kafka-broker /opt/kafka/bin/kafka-topics.sh \
  --create \
  --topic user-topic \
  --partitions 1 \
  --replication-factor 1 \
  --if-not-exists \
  --bootstrap-server localhost:9092

echo "Kafka topics created successfully."
