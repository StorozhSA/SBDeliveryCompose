package ru.skillbranch.common.network.retrofit2

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import ru.skillbranch.common.AppException
import ru.skillbranch.common.network.retrofit2.RetrofitService.*
import ru.skillbranch.common.network.retrofit2.RetrofitService.CompareDescriptor.*
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res.*
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res.Nothing
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

public class RetrofitServiceImpl<RC : Connector<API>, API : APIDescriptor, DE : DefaultErrors>(
    connector: RC,
    public val errors: DE,
    cache: Cache? = null,
    authenticator: Authenticator<RC, API, DE>? = null,
    interceptorsApp: Set<Interceptor> = setOf(),
    interceptorsNet: Set<Interceptor> = setOf(),
    factory: Converter.Factory? = null
) : RetrofitService<RC, API, DE> {

    private val json = Json { coerceInputValues = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val defaultJSONConverterFactory: Converter.Factory by lazy {
        json.asConverterFactory("application/json".toMediaType())
    }

    // Build retrofit client object
    override val api: API by lazy {
        val cb = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(connector.timeoutConnect, TimeUnit.MILLISECONDS)
            .readTimeout(connector.timeoutRead, TimeUnit.MILLISECONDS)
            .writeTimeout(connector.timeoutWrite, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(connector.retryOnConnectionFailure)
            .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })

        interceptorsApp.forEach { cb.addInterceptor(it) }
        interceptorsNet.forEach { cb.addNetworkInterceptor(it) }
        authenticator?.let {
            authenticator.ownerService = this
            cb.authenticator(authenticator)
        }

        Retrofit.Builder()
            .client(cb.build())
            .baseUrl(connector.baseUrl)
            .addConverterFactory(factory ?: defaultJSONConverterFactory)
            .build()
            .create(connector.api.java)
    }

    override fun <OUT> send(
        out: MutableSharedFlow<Res<OUT>>,
        handler: CoroutineExceptionHandler,
        match: Set<CompareDescriptor>,
        scope: CoroutineScope,
        onStart: () -> Unit,
        onEnd: () -> Unit,
        api: suspend () -> Response<OUT>
    ): Job {
        val zero = match.firstOrNull { it.httpCode == 0 } ?: F(0, errors.E_UNKNOWN())

        // Run onStart()
        scope.launch(SupervisorJob()) { onStart.invoke() }

        // Run network request
        return scope.launch(Dispatchers.IO + handler + SupervisorJob()) {
            try {
                val ill = InfoLowLevel(api())
                match.firstOrNull { it.httpCode == ill.httpCode }
                    ?.let {
                        when (it) {
                            is S -> out.emit(Success(it, ill))
                            is N -> out.emit(Nothing(it, ill))
                            is F -> {
                                out.emit(Failure(it, ill))
                                throw AppException(appCode = it.appCode.toString())
                            }
                        }
                    }
                    ?: run { out.emit(Failure(zero)) }
            } catch (ex: SocketTimeoutException) {
                appExceptionEmit(errors.E_TIMEOUT_SOCKET(), ex, out)
            } catch (ex: IOException) {
                appExceptionEmit(errors.E_NOT_CONNECTED(), ex, out)
            } catch (ex: CancellationException) {
                appExceptionEmit(errors.E_CANCELLED(), ex, out)
            } catch (ex: Exception) {
                appExceptionEmit(zero.appCode, ex, out)
            } finally {

                // Run onEnd()
                scope.launch(SupervisorJob()) { onEnd.invoke() }
            }
        }
    }

    private suspend fun <OUT> appExceptionEmit(
        error: ErrorItem,
        ex: Throwable,
        out: MutableSharedFlow<Res<OUT>>
    ) {
        Log.d("RetrofitServiceImpl", ex.localizedMessage?.toString() ?: "")
        out.emit(Failure(error))
        throw AppException(appCode = error.toString(), cause = ex)
    }
}

