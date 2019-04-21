# Create a kafka topic with 2 partitions
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic WordCountInput


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic WordCountOutput


./bin/kafka-console-consumer --bootstrap-server localhost:9092 \
--topic WordCountOutput \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer



./bin/kafka-console-producer --broker-list localhost:9092 --topic WordCountInput