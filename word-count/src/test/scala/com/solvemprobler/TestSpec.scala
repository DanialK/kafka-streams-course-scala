package com.solvemprobler

import java.util.Properties

import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.StreamsConfig

trait TestSpec {
  def getConfig(name: String): Properties = {
    val config = new Properties()
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, name)
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
    config
  }
}