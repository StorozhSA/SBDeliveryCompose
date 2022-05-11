package ru.skillbranch.common.network.retrofit2

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.Headers
import okhttp3.internal.EMPTY_HEADERS
import retrofit2.Response
import kotlin.reflect.KClass

/**
 * Вспомогательный интерфейс для реализации сервисов на основе Retrofit 2
 */
@Suppress("FunctionName")
public interface RetrofitService<RC : RetrofitService.Connector<API>, API : RetrofitService.APIDescriptor, DE : RetrofitService.DefaultErrors> {
    public val api: API

    public fun <OUT> send(
        out: MutableSharedFlow<Res<OUT>>,
        handler: CoroutineExceptionHandler,
        match: Set<CompareDescriptor>,
        scope: CoroutineScope,
        onStart: () -> Unit = {},
        onEnd: () -> Unit = {},
        api: suspend () -> Response<OUT>
    ): Job

    /**
     * Ошибки по-умолчанию реализованные в сервисе. Требуется дальнейшая реализация на уровне приложения.
     * Например в strings.xml E_NOT_CONNECTED = "Нет соединения"
     */
    public interface DefaultErrors {
        public fun E_TIMEOUT_SOCKET(): ErrorItem {
            return ErrorItem("E_TIMEOUT_SOCKET")
        }

        public fun E_NOT_CONNECTED(): ErrorItem {
            return ErrorItem("E_NOT_CONNECTED")
        }

        public fun E_UNKNOWN(): ErrorItem {
            return ErrorItem("E_UNKNOWN")
        }

        public fun E_NOT_MODIFIED(): ErrorItem {
            return ErrorItem("E_NOT_MODIFIED")
        }

        public fun E_SUCCESS(): ErrorItem {
            return ErrorItem("E_SUCCESS")
        }

        public fun E_SUCCESS_REGISTRATION(): ErrorItem {
            return ErrorItem("E_SUCCESS_REGISTRATION")
        }

        public fun E_SUCCESS_LOGIN(): ErrorItem {
            return ErrorItem("E_SUCCESS_LOGIN")
        }

        public fun E_CANCELLED(): ErrorItem {
            return ErrorItem("E_CANCELLED")
        }

        public fun isMethod(s: String): Boolean {
            return try {
                this::class.java.getMethod(s) // or t::class.java.getMethod(s)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    public class ErrorItem(private val appCode: String) {
        override fun toString(): String = appCode
    }

    /**
     * Интерфейс описывающий API для Retrofit 2
     */
    public interface APIDescriptor

    /**
     * Интерфейс описывающий коннектор. Какие будут таймауты, базовые URI и т.д.
     */
    public interface Connector<API : APIDescriptor> {

        public val api: KClass<API>
        public val baseUrl: String
            get() = ""
        public val cacheSize: Long
            get() = 10 * 1024 * 1024
        public val timeoutConnect: Long
            get() = 30000
        public val timeoutRead: Long
            get() = 30000
        public val timeoutWrite: Long
            get() = 30000
        public val retryOnConnectionFailure: Boolean
            get() = false
    }

    /**
     * Интерфейс который должны реализовывать аутентификаторы для Service
     */
    public interface Authenticator<RC : Connector<API>, API : APIDescriptor, DE : DefaultErrors> :
        okhttp3.Authenticator {
        public var ownerService: RetrofitService<RC, API, DE>?
    }

    /**
     * Класс описывает соответсвие кода HTTP-ответа с ссылкой на сообщение,
     * и как воспринимать данный ответ успешно, не успешно, никак (пустой ответ).
     */
    public sealed class CompareDescriptor {
        public abstract val httpCode: Int
        public abstract val appCode: ErrorItem

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CompareDescriptor) return false
            if (httpCode != other.httpCode) return false
            return true
        }

        override fun hashCode(): Int = 31 * httpCode

        // Ответ с кодом httpErrorCode будет считаться SUCCESS
        public class S(override val httpCode: Int, override val appCode: ErrorItem) :
            CompareDescriptor()

        // Ответ с кодом httpErrorCode будет считаться NOTHING (EMPTY ANSWER OR NOT MODIFIED DATA)
        public class N(override val httpCode: Int, override val appCode: ErrorItem) :
            CompareDescriptor()

        // Ответ с кодом httpErrorCode будет считаться FAILURE
        public class F(override val httpCode: Int, override val appCode: ErrorItem) :
            CompareDescriptor()
    }

    public class InfoLowLevel<T>(response: Response<T>? = null) {
        public val httpCode: Int = response?.code() ?: 0
        public val httpMessage: String = response?.message() ?: ""
        public val httpHeaders: Headers = response?.headers() ?: EMPTY_HEADERS
        public val payload: T? = response?.body()
    }

    /**
     * Класс отдаваемый сервисом как результат.
     */
    public sealed class Res<P> {
        override fun toString(): String =
            "${javaClass.simpleName}-> $appCode:[${infoLowLevel.httpMessage}]"

        public abstract val appCode: ErrorItem
        public abstract val infoLowLevel: InfoLowLevel<P>

        public class Success<P>(
            override val appCode: ErrorItem,
            override val infoLowLevel: InfoLowLevel<P>
        ) : Res<P>() {
            public val payload: P? = infoLowLevel.payload

            public constructor(desc: CompareDescriptor, infoLowLevel: InfoLowLevel<P>) : this(
                appCode = desc.appCode,
                infoLowLevel = infoLowLevel
            )
        }

        public class Nothing<P>(
            override val appCode: ErrorItem,
            override val infoLowLevel: InfoLowLevel<P>
        ) : Res<P>() {

            public constructor(desc: CompareDescriptor, infoLowLevel: InfoLowLevel<P>) : this(
                appCode = desc.appCode,
                infoLowLevel = infoLowLevel
            )
        }

        public class Failure<P>(
            override val appCode: ErrorItem,
            override val infoLowLevel: InfoLowLevel<P> = InfoLowLevel(null)
        ) : Res<P>() {

            public constructor(
                desc: CompareDescriptor,
                infoLowLevel: InfoLowLevel<P> = InfoLowLevel(null)
            ) : this(
                appCode = desc.appCode,
                infoLowLevel = infoLowLevel
            )
        }
    }
}
