plugins {
  id("java-library")
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.google.protobuf)
}

val projectJdkVersion = libs.versions.java.get().toInt()

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(projectJdkVersion)
  }
}

kotlin {
  jvmToolchain(projectJdkVersion)
}

repositories {
  maven { url = uri("https://maven.aliyun.com/repository/public/") }
  mavenCentral()
}

dependencies {
  implementation(libs.spring.boot)
  implementation(libs.spring.boot.starter.logging)
  implementation(kotlin("stdlib-jdk8"))
}
