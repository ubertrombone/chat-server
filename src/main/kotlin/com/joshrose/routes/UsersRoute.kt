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
import kotlinx.coroutines.awaitAll

fun Route.usersRoute() {
    route("/users") {
        authenticate {
            post {
                val queryRequest = call.receiveOrNull<String>() ?: return@post

                val user = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString().toUsername()
                val friendsList = async { dao.getFriends(user).map { it.username }.toSet() }
                val queryResult = async { dao.queryUsers(queryRequest) }
                // TODO: Should return only usernames not in user's friends list
                val result = awaitAll(friendsList, queryResult).flatten().distinctBy { it }.toSet()

                call.respond(OK, result)
            }
        }
    }
}