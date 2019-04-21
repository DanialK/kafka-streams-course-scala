package com.solvemprobler

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object StreamsStarterApp extends App {
  override def main(args: Array[String]): Unit = {

    val config = new Properties
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-starter-app")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

    val builder = new StreamsBuilder

    val topology = builder.build()
    println("Topology")
    topology.describe()

    val kStream = builder.stream("input-topic-name")

    kStream.to("word-count-output")

    val streams = new KafkaStreams(topology, config)

    streams.cleanUp() // only do this in dev - not in prod

    streams.start()

    // print the topology
    println("streams Topology")
    System.out.println(streams.toString())

    // shutdown hook to correctly close the streams application
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close()
    }))
  }
}
