# grpc-kotlin

A multi-module Kotlin project demonstrating a gRPC client/server architecture with Spring Boot 3 and Java 21.  
It showcases readiness guarding via AOP, connection monitoring with transport filters, periodic heartbeat tasks, and virtual-thread-friendly async configuration.

## Features

- gRPC server and client using net.devh grpc-spring-boot-starter
- Readiness guard annotation (`@RequireGrpcServerReady`) and aspect (`RequireGrpcServerReadyAspect`)
- Clear exception when server is not ready (`GrpcServerNotReadyException`)
- Connection registry services and monitoring transport filters (client/server)
- Periodic heartbeat using `TaskScheduler` and a unified cron task abstraction
- Virtual-thread-friendly `TaskExecutor` (Java 21) and dedicated `ThreadPoolTaskScheduler`
- Sample HTTP endpoints to drive the gRPC client
- Protobuf module shared by client and server
- Version catalog with Gradle (libs.versions.toml)

## Project Structure

- `protos`  
  Protobuf definitions and generated sources used by both client and server.

- `spring-boot-commons`  
  Common Spring Boot configuration, utilities, exceptions, and cron task abstraction.

- `grpc-server`  
  gRPC services, interceptors, transport filters, registry, tasks, and the server Spring Boot application.

- `grpc-client`  
  Client-side readiness guard (annotation + aspect), HTTP test controller, tasks, and the client Spring Boot application.

## Requirements

- JDK 21
- Gradle 8+ (wrapper included)
- Ports and gRPC target addresses are configurable in `application*.yml`
