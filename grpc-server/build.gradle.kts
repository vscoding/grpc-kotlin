plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
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
    implementation(project(":protos"))
    implementation(project(":spring-boot-commons"))
    implementation(libs.grpc.server.spring.boot.starter)
    implementation("org.springframework.boot:spring-boot-starter-web")
}
