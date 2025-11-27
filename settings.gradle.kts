pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
    }
}

rootProject.name = "grpc-kotlin"

include("protos")
include("spring-boot-commons")
include("grpc-client")
include("grpc-server")
