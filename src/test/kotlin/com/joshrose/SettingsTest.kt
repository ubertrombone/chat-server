package com.joshrose

import com.joshrose.Constants.INCORRECT_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.PASSWORDS_DONT_MATCH
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_MUST_BE_NEW
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.plugins.dao
import com.joshrose.requests.AccountRequest
import com.joshrose.requests.RemoveUserRequest
import com.joshrose.requests.UpdatePasswordRequest
import com.joshrose.requests.UpdateUsernameRequest
import com.joshrose.responses.SimpleResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

class SettingsTest {
    private val email = "josh_rose@baylor.edu"
    private val actPWD = "test12345678"

    @Test
    fun testGetSettings() = testApplication {
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
        val response = client.get("/settings")
        assertEquals("Hello, $email!", response.bodyAsText())
        assertEquals(OK, response.status)
    }

    @Test
    fun testGetSettingsCapitalizedEmail() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = email.uppercase(),
                            password = actPWD
                        )
                    }
                    realm = "Chat Server"
                }
            }
        }
        val response = client.get("/settings")
        assertEquals("Hello, $email!", response.bodyAsText())
        assertEquals(OK, response.status)
    }

//    private val updatePWDEmail = "testing@testing.com"
//    private val oldPWD = "test12345678"
//    private val newPWD = "test123456789"
//    @Test
//    fun testPostSettingsUpdatepwd() = testApplication {
//        val client = createClient {
//            install(Auth) {
//                basic {
//                    credentials {
//                        BasicAuthCredentials(
//                            username = updatePWDEmail,
//                            password = oldPWD
//                        )
//                    }
//                    realm = "Chat Server"
//                }
//            }
//            install(ContentNegotiation) {
//                json(Json {
//                    prettyPrint = true
//                    isLenient = true
//                })
//            }
//        }
//
//        val response = client.post("/settings/updatepwd") {
//            contentType(ContentType.Application.Json)
//            setBody(UpdatePasswordRequest(oldPWD, newPWD, newPWD))
//        }
//
//        assertEquals(
//            "Password reset successfully!",
//            response.body<SimpleResponse>().message
//        )
//        assertEquals(OK, response.status)
//    }

    private val updatePWDEmail = "testing@testing.com"
    private val oldPWD = "test123456789"

    @Test
    fun testPostSettingsUpdatepwdIncorrectPassword() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, "INCORRECT_PWD", "newPWD123456!", "newPWD123456!"))
        }

        assertEquals(INCORRECT_PASSWORD, response.bodyAsText())
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun testPostSettingsUpdatepwdNewCantMatchOld() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, oldPWD, oldPWD))
        }

        assertEquals(PASSWORD_MUST_BE_NEW, response.bodyAsText())
        assertEquals(Conflict, response.status)
    }


    private val testPWD = "NEW_PASSWORD_TEST"

    @Test
    fun testPostSettingsUpdatepwdPasswordsDontMatch() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, testPWD, "SOMETHING_ELSE_FAKE!"))
        }

        assertEquals(PASSWORDS_DONT_MATCH, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    private val shortPWD = "pwd"

    @Test
    fun testPostSettingsUpdatepwdPasswordTooShort() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, shortPWD, shortPWD))
        }

        assertEquals(PASSWORD_SHORT, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    private val longPWD = "TOO_LONG_PASSWORD_AND_FAKE!"

    @Test
    fun testPostSettingsUpdatepwdPasswordTooLong() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, longPWD, longPWD))
        }

        assertEquals(PASSWORD_LONG, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostSettingsUpdatepwdPasswordMeetsLetterReq() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, "1234567890!@", "1234567890!@"))
        }

        assertEquals(PASSWORD_REQUIRED_CHARS, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostSettingsUpdatepwdPasswordMeetsNumberReq() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, "testingtest!@", "testingtest!@"))
        }

        assertEquals(PASSWORD_REQUIRED_CHARS, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostSettingsUpdatepwdPasswordNoInvalidChars() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, "t3sting\test#`", "t3sting\test#`"))
        }

        assertEquals(INVALID_CHARS_PASSWORD, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostSettingsUpdatepwdPasswordNoSpaces() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updatepwd") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequest(2, oldPWD, "t3sting test@!", "t3sting test@!"))
        }

        assertEquals(INVALID_CHARS_PASSWORD, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

//    @Test
//    fun testPostSettingsUpdateuser() = testApplication {
//        val client = createClient {
//            install(Auth) {
//                basic {
//                    credentials {
//                        BasicAuthCredentials(
//                            username = updatePWDEmail,
//                            password = oldPWD
//                        )
//                    }
//                    realm = "Chat Server"
//                }
//            }
//            install(ContentNegotiation) {
//                json(Json {
//                    prettyPrint = true
//                    isLenient = true
//                })
//            }
//        }
//
//        val response = client.post("/settings/updateuser") {
//            contentType(ContentType.Application.Json)
//            setBody(UpdateUsernameRequest("ubertrombone555"))
//        }
//
//        assertEquals(
//            "Username changed: ubertrombone555",
//            response.body<SimpleResponse>().message
//        )
//        assertEquals(OK, response.status)
//    }

    @Test
    fun testPostSettingsUpdateuserUsernameExists() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updateuser") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUsernameRequest(2, "ubertrombone"))
        }

        assertEquals(USERNAME_EXISTS, response.bodyAsText())
        assertEquals(Conflict, response.status)
    }

    @Test
    fun testPostRegisterUsernameTooLong() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updateuser") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUsernameRequest(2, "ubertromboneWasMyOldScreenName"))
        }

        assertEquals(USERNAME_TOO_LONG, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterUsernameCharsInvalid() = testApplication {
        val client = createClient {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = updatePWDEmail,
                            password = oldPWD
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

        val response = client.post("/settings/updateuser") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUsernameRequest(2, "ubertrombone!"))
        }

        assertEquals(INVALID_CHARS_USERNAME, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @DelicateCoroutinesApi
    @Test
    fun testPostDeleteAccount() = testApplication {
        val email = "Test@test.com"
        val password = "Thispassword1"
        val username = "ACoolUsername"

        GlobalScope.launch {
            val register = async {
                val client = createClient {
                    install(ContentNegotiation) {
                        json(Json {
                            prettyPrint = true
                            isLenient = true
                        })
                    }
                }

                val response = client.post("/register") {
                    contentType(ContentType.Application.Json)
                    setBody(AccountRequest(email, password, username))
                }

                assertEquals(
                    "Successfully created account!",
                    response.body<SimpleResponse>().message
                )
                assertEquals(OK, response.status)
            }
            register.await()

            val client = createClient {
                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(
                                username = email,
                                password = password
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

            val id = dao.loginUser(email)
            val response = client.post("/settings/delete") {
                contentType(ContentType.Application.Json)
                setBody(RemoveUserRequest(id, true))
            }

            assertEquals("Account Deleted!", response.body<SimpleResponse>().message)
            assertEquals(OK, response.status)
        }
    }
}