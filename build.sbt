name := "hyperscan-java"

version := "0.4.12"

scalaVersion := "2.11.7"

// Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test

// inherit from hyperscan-java
libraryDependencies += "org.junit.platform" % "junit-platform-surefire-provider"  % "1.0.0-M6"
libraryDependencies += "net.java.dev.jna"   % "jna"                               % "4.4.0"
libraryDependencies += "org.junit.jupiter"  % "junit-jupiter-api"                 % "5.0.0-M6" % "test"
libraryDependencies += "org.junit.jupiter"  % "junit-jupiter-engine"              % "5.0.0-M6" % "test"
