package com.joshrose

import com.joshrose.Constants.EMAIL_EXISTS
import com.joshrose.Constants.INVALID_CHARS_PASSWORD
import com.joshrose.Constants.INVALID_CHARS_USERNAME
import com.joshrose.Constants.INVALID_EMAIL
import com.joshrose.Constants.PASSWORD_LONG
import com.joshrose.Constants.PASSWORD_REQUIRED_CHARS
import com.joshrose.Constants.PASSWORD_SHORT
import com.joshrose.Constants.USERNAME_EXISTS
import com.joshrose.Constants.USERNAME_TOO_LONG
import com.joshrose.requests.AccountRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterTest {
//    @Test
//    fun testPostRegister() = testApplication {
//        val client = createClient {
//            install(ContentNegotiation) {
//                json(Json {
//                    prettyPrint = true
//                    isLenient = true
//                })
//            }
//        }
//
//        val response = client.post("/register") {
//            contentType(ContentType.Application.Json)
//            setBody(AccountRequest("josh_rose+10000@baylor.edu", "test12345678", "ubertrombone400"))
//        }
//
//        assertEquals(
//            "Successfully created account!",
//            response.body<SimpleResponse>().message
//        )
//        assertEquals(OK, response.status)
//    }

    @Test
    fun testPostRegisterUsernameExists() = testApplication {
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
            setBody(AccountRequest("josh_rose+1000@baylor.edu", "test12345678!", "ubertrombone"))
        }

        assertEquals(USERNAME_EXISTS, response.bodyAsText())
        assertEquals(Conflict, response.status)
    }

    @Test
    fun testPostRegisterUsernameTooLong() = testApplication {
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
            setBody(AccountRequest("jmr1@gmail.com", "test12345678!", "ubertromboneWasMyOldScreenName"))
        }

        assertEquals(USERNAME_TOO_LONG, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterUsernameCharsInvalid() = testApplication {
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
            setBody(AccountRequest("jmr2@gmail.com", "test12345678!", "ubert is cool"))
        }

        assertEquals(INVALID_CHARS_USERNAME, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterInvalidEmail() = testApplication {
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
            setBody(AccountRequest("josh_rose@baylor..edu", "test12345678!", "ubertrombone4500"))
        }

        assertEquals(INVALID_EMAIL, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterEmailExists() = testApplication {
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
            setBody(AccountRequest("josh_rose@baylor.edu", "test12345678!", "ubertrombone450"))
        }

        assertEquals(EMAIL_EXISTS, response.bodyAsText())
        assertEquals(Conflict, response.status)
    }

    @Test
    fun testPostRegisterPasswordTooShort() = testApplication {
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
            setBody(AccountRequest("josh_rose+1000@baylor.edu", "test123!", "passwordTooShort"))
        }

        assertEquals(PASSWORD_SHORT, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterPasswordTooLong() = testApplication {
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
            setBody(AccountRequest("jmr3@gmail.com", "THIS_PASSWORD_IS_TOO_LONG!1", "PasswordTooLong"))
        }

        assertEquals(PASSWORD_LONG, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterMeetsNumberPasswordRequirements() = testApplication {
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
            setBody(AccountRequest("jmr5@gmail.com", "t*.!@\$%^&(){}:;<>,?/~_+", "NoNumbersPassword"))
        }

        assertEquals(PASSWORD_REQUIRED_CHARS, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterMeetsLetterPasswordRequirements() = testApplication {
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
            setBody(AccountRequest("jmr6@gmail.com", "1234567890!@", "NoLettersPassword"))
        }

        assertEquals(PASSWORD_REQUIRED_CHARS, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterNoInvalidCharsInPassword() = testApplication {
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
            setBody(AccountRequest("jmr7@gmail.com", "t\\est1ng1`bad#chars", "InvalidCharsPassword"))
        }

        assertEquals(INVALID_CHARS_PASSWORD, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }

    @Test
    fun testPostRegisterNoSpacesInPassword() = testApplication {
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
            setBody(AccountRequest("jmr8@gmail.com", "test 12345678!", "NoSpacesPassword"))
        }

        assertEquals(INVALID_CHARS_PASSWORD, response.bodyAsText())
        assertEquals(UnprocessableEntity, response.status)
    }
}