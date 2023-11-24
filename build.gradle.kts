val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

group = "com.joshrose"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("io.ktor:ktor-server-core-jvm:2.2.3")
    implementation("io.ktor:ktor-server-auth-jvm:2.2.3")
    implementation("io.ktor:ktor-server-host-common-jvm:2.2.3")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.2.3")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.3")
    implementation("io.ktor:ktor-server-websockets-jvm:2.2.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.3")
    implementation("io.ktor:ktor-server-request-validation:2.2.3")
    implementation("io.ktor:ktor-client-auth-jvm:2.2.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.2.3")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.3")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.2.3")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.2.3")
}