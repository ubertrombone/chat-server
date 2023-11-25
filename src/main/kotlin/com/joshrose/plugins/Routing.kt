package com.joshrose.plugins

import com.joshrose.dao.*
import com.joshrose.routes.*
import com.joshrose.security.getHashWithSalt
import com.joshrose.status_page.statusPages
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

val dao: DAOUser = DAOUserImpl().apply {
    runBlocking {
        if (allUsers().isEmpty()) {
            addNewUser(
                email = "test@baylor.edu",
                password = getHashWithSalt("p@ssw0rd"),
                username = "ubertrombone",
                isOnline = true,
                lastOnline = LocalDateTime.now(),
                friendList = null,
                blockedList = null,
                status = null
            )
        }
    }
}

val friendRequestDao: DAOFriendRequest = DAOFriendRequestImpl()
val groupChatDao: DAOGroupChat = DAOGroupChatImpl()

fun Application.configureRouting() {
    val audience = environment.config.property("jwt.audience").getString()
    val domain = environment.config.property("jwt.issuer").getString()
    val secret = environment.config.property("jwt.secret").getString()

    install(StatusPages) {
        statusPages()
    }
    routing {
        loginRoute(audience, domain, secret)
        registerRoute()
        settingsRoute()
        friendsRoute()
        blockRoute()
        friendRequestRoute()
        statusRoute()
        groupChatRoute()
    }
}
