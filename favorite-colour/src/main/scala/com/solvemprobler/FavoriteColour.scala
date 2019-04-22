package scala.com.solvemprobler

import java.util.Properties

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig

object FavoriteColour extends App {
  import Serdes._

  override def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "favorite-colour-application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)
      // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
      p.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
      p
    }

    val DELIM = ","
    val SUPPORTED_COLOURS = Array("green", "blue", "red")

    val builder: StreamsBuilder = new StreamsBuilder
    val textLines = builder.stream[String, String]("favourite-colour-input")
    val usersAndColours = textLines
        .filter((_, value) => value.contains(DELIM))
        .selectKey((_, value) => value.split(DELIM)(0).trim.toLowerCase)
        .mapValues((_, value) => value.split(DELIM)(1).trim.toLowerCase)
        .filter((_, colour) => SUPPORTED_COLOURS.contains(colour))

    val intermediaryTopic = "user-keys-and-colours-scala"
    usersAndColours.to(intermediaryTopic)

    val usersAndColoursTable = builder.table[String, String](intermediaryTopic)

    val favouriteColours: KTable[String, Long] = usersAndColoursTable
        .groupBy((user, colour) => (colour, colour))
        .count()(Materialized.as("counts-store"))

    favouriteColours.toStream.to("favourite-colour-output-scala")


    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)

    streams.cleanUp() // dev only

    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close()
    }))
  }
}
