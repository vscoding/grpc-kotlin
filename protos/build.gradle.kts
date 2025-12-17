import com.google.protobuf.gradle.id

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

dependencies {
    // Align all gRPC components via BOM
    api(platform(libs.grpc.bom))
    api("io.grpc:grpc-protobuf") {
        exclude("com.google.protobuf", "protobuf-java") // Exclude protobuf-java to avoid version conflicts
    }
    api(libs.protobuf.java)     // Protobuf runtime must match protoc 4.x used for code generation

    api("io.grpc:grpc-services")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-netty-shaded")

    // api(libs.grpc.protobuf)
    // api(libs.grpc.services)
    // api(libs.grpc.stub)
    // api(libs.grpc.netty.shaded)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    api(libs.jackson.core)
    api(libs.jackson.databind)
    api(libs.jackson.annotations)
    api(libs.jackson.module.kotlin)

    api(libs.jetbrains.annotations)

    implementation(libs.spring.boot)

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protoc.get()}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }
    generateProtoTasks {
        all().configureEach {
            plugins {
                id("grpc")
            }
        }
    }
}
