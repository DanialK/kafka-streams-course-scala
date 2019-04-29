name := "kafka-streams"
organization in ThisBuild := "com.solvemprobler"
scalaVersion in ThisBuild := "2.12.8"

lazy val dependencies = new {
  val scalaTic = "org.scalactic" %% "scalactic" % "3.0.5"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test

  val kafka = "org.apache.kafka" %% "kafka" % "2.2.0"
  val kafkaStreams = "org.apache.kafka" % "kafka-streams" % "2.2.0"
  val kafkaStreamsScala = "org.apache.kafka" %% "kafka-streams-scala" % "2.2.0"
  val kafkaSteamsTestUtils =  "org.apache.kafka" % "kafka-streams-test-utils" % "2.2.0" % Test

  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.26"
  val slf4jLog4j = "org.slf4j" % "slf4j-log4j12" % "1.7.26"


  val kafkaSerializationVersion  = "0.5.1"
  val kafkaSerializationCore = "com.ovoenergy" %% "kafka-serialization-core" % kafkaSerializationVersion
  val kafkaSerializationCirce = "com.ovoenergy" %% "kafka-serialization-circe" % kafkaSerializationVersion

  val circeVersion = "0.11.1"
  val circeCore =  "io.circe" %% "circe-core" % circeVersion
  val circeGeneric =  "io.circe" %% "circe-generic" % circeVersion
  val circeParser =  "io.circe" %% "circe-parser" % circeVersion
}

lazy val commonDependencies = Seq(
  dependencies.scalaTic,
  dependencies.scalaTest,
  dependencies.kafka,
  dependencies.kafkaStreams,
  dependencies.kafkaStreamsScala,
  dependencies.kafkaSteamsTestUtils,
  dependencies.slf4jApi,
  dependencies.slf4jLog4j
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

lazy val settings = Seq(
  scalacOptions ++=  Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("ovotech", "maven")
  )
)

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    streamsStarterProject,
    wordCount,
    favoriteColour,
    bankBalanceExactlyOnce,
    userEventEnricher
  )

lazy val common = project
  .settings(
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val streamsStarterProject = project
  .in(file("./streams-starter-project"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val wordCount = project
  .in(file("./word-count"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val favoriteColour = project
  .in(file("./favorite-colour"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )


lazy val bankBalanceExactlyOnce = project
  .in(file("./bank-balance-exactly-once"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.circeCore,
      dependencies.circeGeneric,
      dependencies.circeParser,
      dependencies.kafkaSerializationCore,
      dependencies.kafkaSerializationCirce
    )
  )
  .dependsOn(
    common
  )


lazy val userEventEnricher = project
  .in(file("./user-event-enricher"))
  .settings(
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )