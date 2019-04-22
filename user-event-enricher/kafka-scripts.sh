# Create a kafka topic with 2 partitions
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-table


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-purchases --config cleanup.policy=compact


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-purchases-enriched-left-join --config cleanup.policy=compact


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-purchases-enriched-inner-join --config cleanup.policy=compact


./bin/kafka-console-consumer --bootstrap-server localhost:9092 \
--topic user-purchases-enriched-inner-join \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

#john	Purchase=Apples and Bananas (1),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#john	Purchase=Oranges (3),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#john	Purchase=Apples and Bananas (1),UserInfo=[First=John,Last=Doe,Email=john.doe@gmail.com]
#john	Purchase=Oranges (3),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#stephane	Purchase=Books (4),UserInfo=[First=Stephane,Last=Maarek,GitHub=simplesteph]


./bin/kafka-console-consumer --bootstrap-server localhost:9092 \
--topic user-purchases-enriched-left-join \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer


#john	Purchase=Apples and Bananas (1),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#bob	Purchase=Kafka Udemy Course (2),UserInfo=null
#john	Purchase=Oranges (3),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#stephane	Purchase=Computer (4),UserInfo=null
#stephane	Purchase=Books (4),UserInfo=null
#alice	Purchase=Apache Kafka Series (5),UserInfo=null
#john	Purchase=Apples and Bananas (1),UserInfo=[First=John,Last=Doe,Email=john.doe@gmail.com]
#bob	Purchase=Kafka Udemy Course (2),UserInfo=null
#john	Purchase=Oranges (3),UserInfo=[First=Johnny,Last=Doe,Email=johnny.doe@gmail.com]
#stephane	Purchase=Computer (4),UserInfo=null
#stephane	Purchase=Books (4),UserInfo=[First=Stephane,Last=Maarek,GitHub=simplesteph]
#alice	Purchase=Apache Kafka Series (5),UserInfo=null