scalaVersion := "2.12.7" // Also supports 2.11.x

val http4sVersion = "0.19.0"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,
  "de.heikoseeberger" %% "akka-http-circe" % "1.22.0",
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.10.0",
  "io.circe" %% "circe-parser"  % "0.10.0",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.10.0"
)

scalacOptions ++= Seq("-Ypartial-unification")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)