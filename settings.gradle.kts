pluginManagement {
  repositories {
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    gradlePluginPortal()
  }
}

plugins {
  id("com.gradle.develocity") version ("4.3.2")
}

rootProject.name = "grpc-kotlin"

include("protos")
include("spring-boot-commons")
include("grpc-client")
include("grpc-server")
