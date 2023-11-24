package com.joshrose

import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.Constants.USER_ALREADY_BLOCKED
import com.joshrose.Constants.USER_NOT_BLOCKED
import com.joshrose.requests.BlockUserRequest
import com.joshrose.requests.UnblockUserRequest
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

@OptIn(DelicateCoroutinesApi::class)
class BlockRouteTest {
    private val email = "testing@testing.com"
    private val actPWD = "test123456789"
    private val secondFriend = "L33tG4m3z"

    @Test
    fun testGetBlockList() = testApplication {
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
        val response = client.get("/block")
        assertEquals("", response.bodyAsText())
        assertEquals(OK, response.status)
    }

    @Test
    fun testPostFriendsBlockAndUnblockEmptyList() = testApplication {
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
            val blockUser = async {
                val response = client.post("/block") {
                    contentType(ContentType.Application.Json)
                    setBody(BlockUserRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone is blocked!", response.bodyAsText())
                assertEquals(Accepted, response.status)
            }
            blockUser.await()

            val unblockResponse = client.post("/block/unblock") {
                contentType(ContentType.Application.Json)
                setBody(UnblockUserRequest(2, "ubertrombone"))
            }

            assertEquals("ubertrombone is unblocked!", unblockResponse.bodyAsText())
            assertEquals(Accepted, unblockResponse.status)
        }
    }

    @Test
    fun testPostFriendsBlockToList() = testApplication {
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
            val blockFirstUser = async {
                val response1 = client.post("/block") {
                    contentType(ContentType.Application.Json)
                    setBody(BlockUserRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone is blocked!", response1.bodyAsText())
                assertEquals(Accepted, response1.status)
            }
            blockFirstUser.await()

            val blockSecondUser = async {
                val response2 = client.post("/block") {
                    contentType(ContentType.Application.Json)
                    setBody(BlockUserRequest(2, "ubertrombone124"))
                }

                assertEquals("ubertrombone124 is blocked!", response2.bodyAsText())
                assertEquals(Accepted, response2.status)
            }
            blockSecondUser.await()

            val unblockFirstUser = async {
                val unblockResponse1 = client.post("/block/unblock") {
                    contentType(ContentType.Application.Json)
                    setBody(UnblockUserRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone is unblocked!", unblockResponse1.bodyAsText())
                assertEquals(Accepted, unblockResponse1.status)
            }
            unblockFirstUser.await()

            val unblockResponse2 = client.post("/block/unblock") {
                contentType(ContentType.Application.Json)
                setBody(UnblockUserRequest(2, "ubertrombone124"))
            }

            assertEquals("ubertrombone124 is unblocked!", unblockResponse2.bodyAsText())
            assertEquals(Accepted, unblockResponse2.status)
        }
    }

    @Test
    fun testPostFriendsBlockDuplicate() = testApplication {
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
            val blockUser = async {
                val response1 = client.post("/block") {
                    contentType(ContentType.Application.Json)
                    setBody(BlockUserRequest(2, "ubertrombone"))
                }

                assertEquals("ubertrombone is blocked!", response1.bodyAsText())
                assertEquals(Accepted, response1.status)
            }
            blockUser.await()

            val blockDuplicate = async {
                val response2 = client.post("/block") {
                    contentType(ContentType.Application.Json)
                    setBody(BlockUserRequest(2, "ubertrombone124"))
                }

                assertEquals(USER_ALREADY_BLOCKED, response2.bodyAsText())
                assertEquals(Conflict, response2.status)
            }
            blockDuplicate.await()

            val unblockResponse1 = client.post("/block/unblock") {
                contentType(ContentType.Application.Json)
                setBody(UnblockUserRequest(2, "ubertrombone"))
            }

            assertEquals("ubertrombone is unblocked!", unblockResponse1.bodyAsText())
            assertEquals(Accepted, unblockResponse1.status)
        }
    }

    @Test
    fun testPostFriendsBlockNonExistent() = testApplication {
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

        val response = client.post("/block") {
            contentType(ContentType.Application.Json)
            setBody(BlockUserRequest(2, secondFriend))
        }

        assertEquals(USERNAME_DOESNT_EXIST, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostFriendsUnblockNonExistent() = testApplication {
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

        val response = client.post("/block/unblock") {
            contentType(ContentType.Application.Json)
            setBody(UnblockUserRequest(2, "A_USERNAME_THAT..."))
        }

        assertEquals(USER_NOT_BLOCKED, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }
}