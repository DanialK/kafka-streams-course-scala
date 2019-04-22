package com.solvemprobler

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.{Serdes, _}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}


object UserEventEnricherApp extends App {
  import Serdes._

  case class Transaction(name: String, amount: Double, time: Long)
  case class Balance(count: Long, balance: Double, time: Long)

  override def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-event-enricher-app")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
      p
    }

    val builder: StreamsBuilder = new StreamsBuilder

    val usersGlobalTable: GlobalKTable[String, String] = builder.globalTable[String, String]("user-table")

    val userPurchases: KStream[String, String] = builder.stream[String, String]("user-purchases")


    val userPurchasesEnrichedJoin: KStream[String, String] = userPurchases
      .join(usersGlobalTable)(
          (key, _) => key,
          (userPurchase, userInfo) => "Purchase=" + userPurchase + ",UserInfo=[" + userInfo + "]"
      )

    userPurchasesEnrichedJoin.to("user-purchases-enriched-inner-join")

    val userPurchasesEnrichedLeftJoin: KStream[String, String] = userPurchases
      .leftJoin(usersGlobalTable)(
        (key, _) => key,
        (userPurchase, userInfo) => {
          if (userInfo != null) {
            "Purchase=" + userPurchase + ",UserInfo=[" + userInfo + "]"
          } else {
            "Purchase=" + userPurchase + ",UserInfo=null"
          }
        }
      )

    userPurchasesEnrichedLeftJoin.to("user-purchases-enriched-left-join")



    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)

    streams.cleanUp() // dev only

    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close()
    }))
  }
}
