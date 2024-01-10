package com.joshrose.routes

import com.joshrose.plugins.dao
import com.joshrose.util.receiveOrNull
import com.joshrose.util.toUsername
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async

fun Route.usersRoute() {
    route("/users") {
        authenticate {
            post {
                val queryRequest = call.receiveOrNull<String>() ?: return@post

                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val friends = async { dao.user(user)!!.friendList }
                val queryResult = async { dao.queryUsers(queryRequest) }
                val f = friends.await()
                val q = queryResult.await()
                val result = q.mapNotNull { it.takeUnless { f.contains(dao.userID(it)) || it == user } }.toSet()

                call.respond(OK, result)
            }
        }
    }
}