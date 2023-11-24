@file:OptIn(DelicateCoroutinesApi::class)

package com.joshrose

import com.joshrose.requests.StatusRequest
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusTest {

    private val email = "testing@testing.com"
    private val actPWD = "test123456789"

    @Test
    fun testGetStatus() = testApplication {
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
        val response = client.get("/status")
        assertEquals("", response.bodyAsText())
        assertEquals(OK, response.status)
    }

    @Test
    fun testPostStatus() = testApplication {
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
            val addStatus = async {
                val response = client.post("/status") {
                    contentType(ContentType.Application.Json)
                    setBody(StatusRequest(2, "New Status"))
                }

                assertEquals("Status updated!", response.bodyAsText())
                assertEquals(Accepted, response.status)
            }
            addStatus.await()

            val nullResponse = client.post("/status") {
                contentType(ContentType.Application.Json)
                setBody(StatusRequest(2, null))
            }

            assertEquals("Status updated!", nullResponse.bodyAsText())
            assertEquals(Accepted, nullResponse.status)
        }
    }
}