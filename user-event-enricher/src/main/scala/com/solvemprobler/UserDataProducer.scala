package com.solvemprobler

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import scala.util.{Try, Success, Failure}
import org.apache.kafka.clients.producer.ProducerRecord


object UserDataProducer extends App {

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

    // 1 - we create a new user, then we send some data to Kafka
    println("\nExample 1 - new user\n")
    producer.send(userRecord("john", "First=John,Last=Doe,Email=john.doe@gmail.com")).get
    producer.send(purchaseRecord("john", "Apples and Bananas (1)")).get

    Thread.sleep(10000)

    // 2 - we receive user purchase, but it doesn't exist in Kafka
    println("\nExample 2 - non existing user\n")
    producer.send(purchaseRecord("bob", "Kafka Udemy Course (2)")).get

    Thread.sleep(10000)

    // 3 - we update user "john", and send a new transaction
    println("\nExample 3 - update to user\n")
    producer.send(userRecord("john", "First=Johnny,Last=Doe,Email=johnny.doe@gmail.com")).get
    producer.send(purchaseRecord("john", "Oranges (3)")).get

    Thread.sleep(10000)

    // 4 - we send a user purchase for stephane, but it exists in Kafka later
    println("\nExample 4 - non existing user then user\n")
    producer.send(purchaseRecord("stephane", "Computer (4)")).get
    producer.send(userRecord("stephane", "First=Stephane,Last=Maarek,GitHub=simplesteph")).get
    producer.send(purchaseRecord("stephane", "Books (4)")).get
    producer.send(userRecord("stephane", null)).get // delete for cleanup


    Thread.sleep(10000)

    // 5 - we create a user, but it gets deleted before any purchase comes through
    println("\nExample 5 - user then delete then data\n")
    producer.send(userRecord("alice", "First=Alice")).get
    producer.send(userRecord("alice", null)).get // that's the delete record

    producer.send(purchaseRecord("alice", "Apache Kafka Series (5)")).get

    Thread.sleep(10000)

    println("End of demo")

  }


  def userRecord(key: String, value: String) = {
    new ProducerRecord[String, String]("user-table", key, value)
  }


  def purchaseRecord(key: String, value: String) = {
    new ProducerRecord[String, String]("user-purchases", key, value)
  }
}
