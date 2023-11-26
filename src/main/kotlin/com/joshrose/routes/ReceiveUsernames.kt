package com.joshrose.routes

import com.joshrose.util.Username
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.receiverUsernames(): Pair<Username?, Username> {
    val username = Username(call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString())

    val request = try {
        call.receive<Username>()
    } catch (e: ContentTransformationException) {
        call.respond(HttpStatusCode.BadRequest)
        return null to username
    }

    return request to username
}