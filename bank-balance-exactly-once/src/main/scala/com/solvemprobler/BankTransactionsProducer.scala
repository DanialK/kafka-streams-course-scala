package com.solvemprobler

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import scala.util.{Try, Success, Failure}

object BankTransactionsProducer extends App {

  override def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties
      // kafka bootstrap server
      p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
      p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
      p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
      // producer acks
      p.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // strongest producing guarantee
      p.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
      p.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
      // leverage idempotent producer from Kafka 0.11 !
      p.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // ensure we don't push duplicates
      p
    }

    val producer = new KafkaProducer[String, String](config)

    var i = 0

    while (true) {
      println("Producing batch: " + i)
      Try({
        producer.send(newRandomTransaction("john"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("stephane"))
        Thread.sleep(100)
        producer.send(newRandomTransaction("alice"))
        Thread.sleep(100)
        i += 1
      }) match {
        case Failure(e) =>
          println("Error!")
        case _ =>
      }
    }

    producer.close
  }

  def newRandomTransaction(name: String): ProducerRecord[String, String] = {
    val r = scala.util.Random
    val transaction =
      s"""
        |{
        |   "name": "$name",
        |   "amount": ${r.nextInt(100)},
        |   "time": ${System.currentTimeMillis()}
        |}
      """.stripMargin

    return new ProducerRecord("bank-transactions", name, transaction)
  }
}
