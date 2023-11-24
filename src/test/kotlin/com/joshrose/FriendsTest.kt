@file:OptIn(DelicateCoroutinesApi::class)

package com.joshrose

import com.joshrose.Constants.FRIEND_DOESNT_EXIST
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.requests.AddFriendRequest
import com.joshrose.requests.RemoveFriendRequest
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class FriendsTest {
    private val email = "testing@testing.com"
    private val actPWD = "test123456789"

    @Test
    fun testPostFriendsAddAndRemoveEmptyList() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        GlobalScope.launch {
            val addFriend = async {
                val response = client.post("/friends/add") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFriendRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone added!", response.bodyAsText())
                assertEquals(Accepted, response.status)
            }
            addFriend.await()

            val removeResponse = client.post("/friends/remove") {
                contentType(ContentType.Application.Json)
                setBody(RemoveFriendRequest(2, "ubertrombone"))
            }

            assertEquals("ubertrombone removed!", removeResponse.bodyAsText())
            assertEquals(Accepted, removeResponse.status)
        }
    }

    @Test
    fun testPostFriendsAddToList() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        GlobalScope.launch {
            val addFirstFriend = async {
                val response1 = client.post("/friends/add") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFriendRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone added!", response1.bodyAsText())
                assertEquals(Accepted, response1.status)
            }
            addFirstFriend.await()

            val addSecondFriend = async {
                val response2 = client.post("/friends/add") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFriendRequest(2, "ubertrombone124"))
                }

                assertEquals("ubertrombone124 added!", response2.bodyAsText())
                assertEquals(Accepted, response2.status)
            }
            addSecondFriend.await()

            val removeFirstFriend = async {
                val removeResponse1 = client.post("/friends/remove") {
                    contentType(ContentType.Application.Json)
                    setBody(RemoveFriendRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone removed!", removeResponse1.bodyAsText())
                assertEquals(Accepted, removeResponse1.status)
            }
            removeFirstFriend.await()

            val removeResponse2 = client.post("/friends/remove") {
                contentType(ContentType.Application.Json)
                setBody(RemoveFriendRequest(2, "ubertrombone124"))
            }

            assertEquals("ubertrombone124 removed!", removeResponse2.bodyAsText())
            assertEquals(Accepted, removeResponse2.status)
        }
    }

    @Test
    fun testPostFriendsAddDuplicate() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        GlobalScope.launch {
            val addFriend = async {
                val response1 = client.post("/friends/add") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFriendRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone added!", response1.bodyAsText())
                assertEquals(Accepted, response1.status)
            }
            addFriend.await()

            val addDuplicate = async {
                val responseDuplicate = client.post("/friends/add") {
                    contentType(ContentType.Application.Json)
                    setBody(AddFriendRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone already added!", responseDuplicate.bodyAsText())
                assertEquals(Conflict, responseDuplicate.status)
            }
            addDuplicate.await()

            val removeResponse1 = client.post("/friends/remove") {
                contentType(ContentType.Application.Json)
                setBody(RemoveFriendRequest(2, "ubertrombone"))
            }

            assertEquals("ubertrombone removed!", removeResponse1.bodyAsText())
            assertEquals(Accepted, removeResponse1.status)
        }
    }

    private val secondFriend = "L33tG4m3z"

    @Test
    fun testPostFriendsAddNonExistent() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/friends/add") {
            contentType(ContentType.Application.Json)
            setBody(AddFriendRequest(2, secondFriend))
        }

        assertEquals(USERNAME_DOESNT_EXIST, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostFriendsRemoveNonExistent() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/friends/remove") {
            contentType(ContentType.Application.Json)
            setBody(RemoveFriendRequest(2, "A_USERNAME_THAT..."))
        }

        assertEquals(FRIEND_DOESNT_EXIST, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testGetFriendList() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email,
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
        }
        val response = client.get("/friends")
        assertEquals("", response.bodyAsText())
        assertEquals(OK, response.status)
    }
}