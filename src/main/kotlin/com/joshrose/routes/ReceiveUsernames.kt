package com.joshrose.routes

import com.joshrose.util.Username
import com.joshrose.util.receiveOrNull
import com.joshrose.util.toUsername
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.receiveUsernames(): Pair<Username?, Username> =
    call.receiveOrNull<Username>() to
            call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()