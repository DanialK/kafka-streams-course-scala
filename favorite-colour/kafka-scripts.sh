# Create a kafka topic with 2 partitions
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic favourite-colour-input


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-keys-and-colours-scala


./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic favourite-colour-output-scala


./bin/kafka-console-producer --broker-list localhost:9092 --topic favourite-colour-input

#>danial,red
#>mojgan,blue
#>mojgan,blue
#>dorsa,green
#>heinz,red
#>danial,green
#>heinz,green



./bin/kafka-console-consumer --bootstrap-server localhost:9092 \
--topic favourite-colour-output-scala \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


#red	1
#blue	1
#blue	0
#blue	1
#green	1
#red	2
#red	1
#green	2
#red	0
#green	3