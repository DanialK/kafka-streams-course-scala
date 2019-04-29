package com.solvemprobler

import java.util.Properties

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import org.apache.kafka.clients.consumer.ConsumerConfig


object WordCountApp extends App {

  import Serdes._

  override def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)
      p
    }

    val topology = createTopology()
    val streams: KafkaStreams = new KafkaStreams(topology, config)
    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      override def run(): Unit = {
        streams.close()
      }
    }))
  }

  def createTopology(): Topology = {
    val builder: StreamsBuilder = new StreamsBuilder

    val wordCountInput: KStream[String, String] = builder.stream[String, String]("WordCountInput")

    val wordCounts: KTable[String, Long] = wordCountInput
      .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
      .selectKey((_, word) => word)
      .groupByKey
      .count()(Materialized.as("WordCountStore"))

    wordCounts.toStream.to("WordCountOutput")

    builder.build()
  }
}
