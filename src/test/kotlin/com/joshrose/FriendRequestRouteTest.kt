package com.joshrose

import com.joshrose.Constants.FRIEND_REQUEST_EXISTS
import com.joshrose.Constants.USERNAME_DOESNT_EXIST
import com.joshrose.requests.CancelFriendRequestRequest
import com.joshrose.requests.SendRequestRequest
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Accepted
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
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(DelicateCoroutinesApi::class)
class FriendRequestRouteTest {
    private val email = "testing@testing.com"
    private val actPWD = "test123456789"

    @Test
    fun testGetSentRequests() = testApplication {
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
            val sendRequest = async {
                val sendRequestResponse = client.post("/friend_request") {
                    contentType(ContentType.Application.Json)
                    setBody(SendRequestRequest(2, 1))
                }

                assertEquals("Request sent!", sendRequestResponse.bodyAsText())
                assertEquals(Accepted, sendRequestResponse.status)
            }
            sendRequest.await()

            val getSentRequests = async {
                val response = client.get("/friend_request/sent_friend_requests")
                assertEquals("[{'id': 3, 'requesterId': 2, 'toId': 1}]", response.body())
                assertEquals(OK, response.status)
            }
            getSentRequests.await()

            val cancelRequestResponse = client.post("/friend_request/cancel_request") {
                contentType(ContentType.Application.Json)
                setBody(CancelFriendRequestRequest(3))
            }

            assertEquals("Request cancelled!", cancelRequestResponse.bodyAsText())
            assertEquals(OK, cancelRequestResponse.status)
        }
    }

    @Test
    @Ignore
    fun testGetReceivedRequests() = testApplication {
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

        val response = client.get("/friend_request/received_friend_requests")
        assertEquals("[{'id': 2, 'requesterId': 3, 'toId': 2}]", response.body())
        assertEquals(OK, response.status)
    }

    @Test
    fun testPostSendAndCancelRequest() = testApplication {
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
            val sendRequest = async {
                val sendRequestResponse = client.post("/friend_request") {
                    contentType(ContentType.Application.Json)
                    setBody(SendRequestRequest(2, 1))
                }

                assertEquals("Request sent!", sendRequestResponse.bodyAsText())
                assertEquals(Accepted, sendRequestResponse.status)
            }
            sendRequest.await()

            val cancelRequestResponse = client.post("/friend_request/cancel_request") {
                contentType(ContentType.Application.Json)
                setBody(CancelFriendRequestRequest(3))
            }

            assertEquals("Request cancelled!", cancelRequestResponse.bodyAsText())
            assertEquals(OK, cancelRequestResponse.status)
        }
    }

    @Test
    fun testPostSendRequestAlreadyExists() = testApplication {
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
            val sendRequest = async {
                val sendRequestResponse = client.post("/friend_request") {
                    contentType(ContentType.Application.Json)
                    setBody(SendRequestRequest(2, 1))
                }

                assertEquals("Request sent!", sendRequestResponse.bodyAsText())
                assertEquals(Accepted, sendRequestResponse.status)
            }
            sendRequest.await()

            val sendDuplicateRequest = async {
                val sendRequestResponse2 = client.post("/friend_request") {
                    contentType(ContentType.Application.Json)
                    setBody(SendRequestRequest(2, 1))
                }

                assertEquals(FRIEND_REQUEST_EXISTS, sendRequestResponse2.bodyAsText())
                assertEquals(Conflict, sendRequestResponse2.status)
            }
            sendDuplicateRequest.await()

            val cancelRequestResponse = client.post("/friend_request/cancel_request") {
                contentType(ContentType.Application.Json)
                setBody(CancelFriendRequestRequest(3))
            }

            assertEquals("Request cancelled!", cancelRequestResponse.bodyAsText())
            assertEquals(OK, cancelRequestResponse.status)
        }
    }

    @Test
    fun testPostSendRequestUserDoesntExist() = testApplication {
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

        val sendRequestResponse = client.post("/friend_request") {
            contentType(ContentType.Application.Json)
            setBody(SendRequestRequest(2, 0))
        }

        assertEquals(USERNAME_DOESNT_EXIST, sendRequestResponse.bodyAsText())
        assertEquals(UnprocessableEntity, sendRequestResponse.status)
    }
}