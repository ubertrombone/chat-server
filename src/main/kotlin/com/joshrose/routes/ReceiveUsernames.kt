package com.joshrose.routes

import com.joshrose.plugins.dao
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

suspend fun PipelineContext<Unit, ApplicationCall>.receiveIds(): Pair<Int?, Int> =
    call.receiveOrNull<Username>()?.toId() to
        call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername().toId()

suspend fun Username.toId(): Int = dao.userID(this)!!