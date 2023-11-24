package com.joshrose

import com.joshrose.dao.DatabaseFactory
import com.joshrose.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(
        driverClassName = environment.config.propertyOrNull("database.driverClassName")?.getString() ?: "",
        jdbcURL = environment.config.propertyOrNull("database.jdbcURL")?.getString() ?: ""
    )
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting()
}
