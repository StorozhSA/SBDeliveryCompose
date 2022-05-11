@file:Suppress("SpellCheckingInspection")

package ru.skillbranch.common.network.retrofit2

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import ru.skillbranch.common.network.retrofit2.RetrofitService.CompareDescriptor
import ru.skillbranch.common.network.retrofit2.RetrofitService.CompareDescriptor.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

public class RetrofitServiceTest {
    private var mockServer: ClientAndServer? = null
    private val defExHandler =
        CoroutineExceptionHandler { _, ex -> println("Exception handler message: $ex") }
    private val mapper = ObjectMapper()

    // Request
    private val req = ReqTest(
        email = "ss@ss.ru",
        password = "wwwww"
    )

    // Response
    private val resp = ResTest(
        accessToken = "11111",
        email = "dd@dd.ru",
        firstName = "firstname",
        id = "0",
        lastName = "lastname",
        refreshToken = "321"
    )

    @Before
    public fun startServer() {
        mockServer = startClientAndServer(1080)
    }

    @After
    public fun stopServer() {
        mockServer?.stop()
    }

    @Test
    public fun `unique CompareDescriptor by HttpCode in Set`() {
        val success = RetrofitService.ErrorItem("SUCCESS")
        val nothing = RetrofitService.ErrorItem("NOTHING")
        val failure = RetrofitService.ErrorItem("FAILURE")

        assertEquals(
            1,
            setOf(
                S(200, success),
                N(200, success),
                F(200, success),
            ).size
        )

        assertEquals(
            2,
            setOf(
                S(200, success),
                N(200, success),
                F(300, success),
            ).size
        )

        assertEquals(
            3,
            setOf(
                S(200, success),
                N(404, success),
                F(300, success),
            ).size
        )

        assertEquals(
            1,
            setOf(
                S(200, success),
                N(200, success),
                F(200, failure),
            ).size
        )

        assertEquals(
            2,
            setOf(
                S(200, success),
                N(200, nothing),
                F(300, failure),
            ).size
        )

        assertEquals(
            3,
            setOf(
                S(200, success),
                N(404, nothing),
                F(300, failure),
            ).size
        )
    }

    @Test
    public fun isMethod() {
        assertEquals(true, TestErrors.isMethod(TestErrors.E_SUCCESS().toString()))
    }

    @Test
    public fun `service work - simple request`(): Unit = runBlocking {
        // Mock
        mockServer
            ?.`when`(
                request().withMethod("PUT")
                    .withPath("/favorite")
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))
            )?.respond(
                response()
                    .withStatusCode(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(resp))
                    .withDelay(TimeUnit.SECONDS, 1)
            )

        // Result flow
        val out = MutableSharedFlow<RetrofitService.Res<ResTest>>()

        // Tested service
        val service = RetrofitServiceImpl(
            connector = TestConnector,
            authenticator = TestAuthenticator,
            errors = TestErrors
        )

        // Subscribe result
        val job = launch {
            out.collect {
                when (it) {
                    is RetrofitService.Res.Success<ResTest> -> assertEquals(resp, it.payload)
                    else -> throw AssertionError()
                }
            }
        }
        // Send request
        launch {
            service.send(
                out = out,
                handler = defExHandler,
                match = setOf<CompareDescriptor>(S(201, TestErrors.E_SUCCESS())),
                scope = CoroutineScope(EmptyCoroutineContext),
                onStart = { },
                onEnd = { }
            ) {
                service.api.favorite(req, "1234567890")
            }
        }

        delay(2000)
        job.cancel()
    }

    @Test
    public fun `service work - timeout error`(): Unit = runBlocking {
        val timeout = 2000L

        // Mock
        mockServer
            ?.`when`(
                request().withMethod("PUT")
                    .withPath("/favorite")
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))
            )?.respond(
                response()
                    .withStatusCode(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(resp))
                    .withDelay(TimeUnit.MILLISECONDS, timeout + 1000)
            )

        // Result flow
        val out = MutableSharedFlow<RetrofitService.Res<ResTest>>()

        // Tested service
        val service = RetrofitServiceImpl(
            connector = object : RetrofitService.Connector<TestAPI> {
                override val baseUrl: String = "http://127.0.0.1:1080/"
                override val api: KClass<TestAPI> = TestAPI::class
                override val timeoutConnect: Long = timeout
                override val timeoutRead: Long = timeout
                override val timeoutWrite: Long = timeout
            },
            errors = TestErrors
        )

        // Subscribe result
        val job = launch {
            out.collect {
                when (it) {
                    is RetrofitService.Res.Failure<ResTest> -> assertEquals(
                        service.errors.E_TIMEOUT_SOCKET().toString(), it.appCode.toString()
                    )
                    else -> throw AssertionError()
                }
            }
        }

        // Send request
        launch {
            service.send(
                out = out,
                handler = defExHandler,
                match = setOf<CompareDescriptor>(S(201, TestErrors.E_SUCCESS())),
                scope = CoroutineScope(EmptyCoroutineContext),
                onStart = { },
                onEnd = { }
            ) {
                service.api.favorite(req, "1234567890")
            }
        }

        delay(timeout + 3000)
        job.cancel()
    }

    @Test
    public fun `service work - pre-post exception`(): Unit = runBlocking {
        mockServer
            ?.`when`(
                request().withMethod("PUT")
                    .withPath("/favorite")
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))
            )?.respond(
                response()
                    .withStatusCode(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(resp))
                    .withDelay(TimeUnit.SECONDS, 1)
            )

        val out = MutableSharedFlow<RetrofitService.Res<ResTest>>()

        val service = RetrofitServiceImpl(
            connector = TestConnector,
            authenticator = TestAuthenticator,
            errors = TestErrors
        )

        val job = launch {
            out.collect {
                when (it) {
                    is RetrofitService.Res.Success<ResTest> -> assertEquals(resp, it.payload)
                    else -> throw AssertionError()
                }
            }
        }

        launch {
            service.send(
                out = out,
                handler = CoroutineExceptionHandler { _, ex -> println("Exception handler message: $ex") },
                match = setOf<CompareDescriptor>(S(201, TestErrors.E_SUCCESS())),
                scope = CoroutineScope(EmptyCoroutineContext),
                onStart = { throw Exception("Exception A") },
                onEnd = { throw Exception("Exception B") }
            ) {
                service.api.favorite(req, "1234567890")
            }
        }

        delay(5000)
        job.cancel()
    }

    @Test
    public fun `service work - authenticator`(): Unit = runBlocking {

        // Mock
        mockServer
            ?.`when`(
                request().withMethod("PUT")
                    .withPath("/favorite")
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Authorization", "OLD_TOKEN")
                    .withBody(mapper.writeValueAsString(req))
            )?.respond(
                response()
                    .withStatusCode(401)
                    .withHeader("Content-Type", "application/json")
                    .withDelay(TimeUnit.SECONDS, 1)
            )

        mockServer
            ?.`when`(
                request().withMethod("POST")
                    .withPath("/auth/refresh")
                    .withHeader("Content-Type", "application/json")
            )?.respond(
                response()
                    .withStatusCode(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"accessToken\": \"NEW_TOKEN\"}")
                    .withDelay(TimeUnit.SECONDS, 1)
            )

        mockServer
            ?.`when`(
                request().withMethod("PUT")
                    .withPath("/favorite")
                    .withHeader("Content-Type", "application/json")
                    //.withHeader("Authorization", "NEW_TOKEN")
                    .withBody(mapper.writeValueAsString(req))
            )?.respond(
                response()
                    .withStatusCode(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(resp))
                    .withDelay(TimeUnit.SECONDS, 1)
            )
        // Result flow
        val out = MutableSharedFlow<RetrofitService.Res<ResTest>>()

        // Tested service
        val service = RetrofitServiceImpl(
            connector = TestConnector,
            authenticator = TestAuthenticator,
            errors = TestErrors
        )

        // Subscribe result
        val job = launch {
            out.collect {
                when (it) {
                    is RetrofitService.Res.Success<ResTest> -> assertEquals(resp, it.payload)
                    else -> throw AssertionError()
                }
            }
        }
        // Send request
        launch {
            service.send(
                out = out,
                handler = defExHandler,
                match = setOf<CompareDescriptor>(S(201, TestErrors.E_SUCCESS())),
                scope = CoroutineScope(EmptyCoroutineContext),
                onStart = { },
                onEnd = { }
            ) {
                service.api.favorite(req, "OLD_TOKEN")
            }
        }

        delay(6000)
        job.cancel()
    }
}
