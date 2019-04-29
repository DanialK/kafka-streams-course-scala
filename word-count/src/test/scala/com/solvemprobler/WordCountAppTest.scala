package com.solvemprobler

import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.test.{ConsumerRecordFactory, OutputVerifier}
import org.scalatest._


class WordCountAppTest extends FunSuite with Matchers with TestSpec {

  test("Dummytest") {
    val topology = WordCountApp.createTopology()

    val driver = new TopologyTestDriver(topology, getConfig(s"App_${System.currentTimeMillis}"))


    val recordFactory = new ConsumerRecordFactory("WordCountInput", new StringSerializer(), new StringSerializer())

    val words = "Hello Kafka Streams, All streams lead to Kafka"

    driver.pipeInput(recordFactory.create(words))

    val store: KeyValueStore[String, Long] = driver.getKeyValueStore("WordCountStore")
    store.get("hello") shouldBe 1
    store.get("kafka") shouldBe 2
    store.get("streams") shouldBe 2
    store.get("lead") shouldBe 1
    store.get("to") shouldBe 1

    driver.close()
  }

  test("Dummytest2") {
    val topology = WordCountApp.createTopology()

    val driver = new TopologyTestDriver(topology, getConfig(s"App_${System.currentTimeMillis}"))

    val recordFactory = new ConsumerRecordFactory("WordCountInput", new StringSerializer(), new StringSerializer())

    val words = "Hello Kafka Streams, All streams lead to Kafka"

    driver.pipeInput(recordFactory.create(words))

    val output1 = driver.readOutput("WordCountOutput", new StringDeserializer(), new LongDeserializer())

    OutputVerifier.compareKeyValue[String, java.lang.Long](output1, "hello", 1L)


    driver.close()
  }

}
