package com.joshrose.routes

import com.joshrose.util.Username
import com.joshrose.util.toUsername
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.receiveUsernames(): Pair<Username?, Username> =
    with(call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()) {
        try { call.receive<Username>() } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest)
            null
        } to this
    }