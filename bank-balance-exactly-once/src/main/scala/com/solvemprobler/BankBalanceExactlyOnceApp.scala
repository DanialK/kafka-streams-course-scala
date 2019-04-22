package com.solvemprobler

import java.util.Properties

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes.serdeFrom
import org.apache.kafka.streams.scala.Serdes
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import com.ovoenergy.kafka.serialization.core._
import com.ovoenergy.kafka.serialization.circe._
import org.apache.kafka.clients.CommonClientConfigs._

// Import the Circe generic support
import io.circe.generic.auto._
import io.circe.syntax._


object BankBalanceExactlyOnceApp extends App {
  import Serdes._

  case class Transaction(name: String, amount: Double, time: Long)
  case class Balance(count: Long, balance: Double, time: Long)

  override def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
      p.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
      // Exactly once processing!!
      p.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE)
      p
    }

    implicit val transactionJsonSerde: Serde[Transaction] = serdeFrom(
      circeJsonSerializer[Transaction],
      circeJsonDeserializer[Transaction]
    )

    implicit val balanceJsonSerde: Serde[Balance] = serdeFrom(
      circeJsonSerializer[Balance],
      circeJsonDeserializer[Balance]
    )

    val builder: StreamsBuilder = new StreamsBuilder

    val bankTransactions: KStream[String, Transaction] = builder.stream[String, Transaction]("bank-transactions")

    val initialBalance = Balance(0, 0, 0)

    val bankBalance: KTable[String, Balance] = bankTransactions
      .groupByKey
      .aggregate(
        initialBalance
      )(
        (_, transaction, balance) => getNewBalance(transaction, balance)
      )

    bankBalance.toStream.to("bank-balance-exactly-once")

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)

    streams.cleanUp() // dev only

    streams.start()

    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close()
    }))
  }

  def getNewBalance(transaction: Transaction, balance: Balance): Balance ={
    Balance(
      balance.count + 1,
      balance.balance + transaction.amount,
      Math.max(balance.time, transaction.time)
    )
  }
}
