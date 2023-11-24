plugins {
    alias(deps.plugins.kotlin.jvm)
    alias(deps.plugins.ktor.plugin)
    alias(deps.plugins.kotlin.serialization)
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
    implementation(deps.logback.logbackClassic)
    implementation(deps.exposed.core)
    implementation(deps.exposed.dao)
    implementation(deps.exposed.jdbc)
    implementation(deps.exposed.javaTime)
    implementation(deps.xerial.sqliteJdbc)
    implementation(deps.codec.commons)
    implementation(deps.ktor.serverCore)
    implementation(deps.ktor.serverAuth)
    implementation(deps.ktor.serverAuthJwt)
    implementation(deps.ktor.serverHostCommon)
    implementation(deps.ktor.serverStatusPages)
    implementation(deps.ktor.serverCallLogging)
    implementation(deps.ktor.serverContentNegotiation)
    implementation(deps.ktor.serializationKotlinxJson)
    implementation(deps.ktor.serverWebsockets)
    implementation(deps.ktor.serverNetty)
    implementation(deps.ktor.serverRequestValidation)
    implementation(deps.ktor.clientAuth)
    implementation("io.ktor:ktor-server-auth-jvm:2.3.6")
    implementation("io.ktor:ktor-server-core-jvm:2.3.6")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.6")
    testImplementation(deps.kotlin.testJunit)
    testImplementation(deps.ktor.serverTestHost)
    testImplementation(deps.ktor.serverTests)
    testImplementation(deps.ktor.clientContentNegotiation)
}