package com.joshrose.plugins

import com.joshrose.dao.*
import com.joshrose.routes.*
import com.joshrose.security.getHashWithSalt
import com.joshrose.util.toUsername
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

val dao: DAOUser = DAOUserImpl().apply {
    runBlocking {
        if (allUsers().isEmpty()) {
            addNewUser(
                username = "ubertrombone".toUsername(),
                password = getHashWithSalt("p@ssw0rd"),
                isOnline = true,
                lastOnline = Clock.System.now(),
                friendList = emptySet(),
                blockedList = emptySet(),
                status = null,
                cache = true
            )
        }
    }
}

val friendRequestDao: DAOFriendRequest = DAOFriendRequestImpl()
val groupChatDao: DAOGroupChat = DAOGroupChatImpl()
val archiveDao: DAOArchive = DAOArchiveImpl()

fun Application.configureRouting() {
    val domain = environment.config.property("jwt.issuer").getString()
    val secret = environment.config.property("jwt.secret").getString()

    routing {
        loginRoute(domain, secret)
        registerRoute(domain, secret)
        settingsRoute()
        friendsRoute()
        blockRoute()
        friendRequestRoute()
        statusRoute()
        groupChatRoute()
    }
}
