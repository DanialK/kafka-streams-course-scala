# Create a kafka topic with 2 partitions
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bank-transactions


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bank-balance-exactly-once --config cleanup.policy=compact


./bin/kafka-console-consumer --bootstrap-server localhost:9092 \
--topic bank-balance-exactly-once \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

#john	{"count":1,"balance":61.0,"time":1555928461897}
#stephane	{"count":1,"balance":38.0,"time":1555928462002}
#alice	{"count":1,"balance":38.0,"time":1555928462105}
#john	{"count":2,"balance":148.0,"time":1555928462206}
#stephane	{"count":2,"balance":75.0,"time":1555928462309}
#alice	{"count":2,"balance":48.0,"time":1555928462410}
#...