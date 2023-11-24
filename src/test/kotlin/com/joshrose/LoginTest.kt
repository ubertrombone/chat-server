package com.joshrose

import com.joshrose.Constants.INCORRECT_CREDS
import com.joshrose.requests.LoginRequest
import com.joshrose.requests.LogoutRequest
import com.joshrose.responses.SimpleResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginTest {
    @Test
    fun testPostLogin() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("josh_rose@baylor.edu", "test12345678"))
        }

        assertEquals(
            "You are now logged in!",
            response.body<SimpleResponse>().message
        )
        assertEquals(OK, response.status)
    }

    @Test
    fun testPostLoginWithOtherUsersEmail() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("test@baylor.edu", "test12345678"))
        }

        assertEquals(INCORRECT_CREDS, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testPostLoginWithNonExistingEmail() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("josh_rose_but_fake@baylor.edu", "test12345678"))
        }

        assertEquals(INCORRECT_CREDS, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testPostLoginWithCapitalizedEmail() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("Josh_Rose@Baylor.edu", "test12345678"))
        }

        assertEquals(
            "You are now logged in!",
            response.body<SimpleResponse>().message
        )
        assertEquals(OK, response.status)
    }

    @Test
    fun testPostLoginWithIncorrectPassword() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("josh_rose@baylor.edu", "test123456789"))
        }

        assertEquals(INCORRECT_CREDS, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }

    private val email = "testing@testing.com"
    private val actPWD = "test123456789"

    @Test
    fun testPostLogout() = testApplication {
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

        val response = client.post("/logout") {
            contentType(ContentType.Application.Json)
            setBody(LogoutRequest(3, false))
        }

        assertEquals(
            "You are now logged out!",
            response.body<SimpleResponse>().message
        )
        assertEquals(OK, response.status)
    }
}