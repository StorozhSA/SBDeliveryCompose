package ru.skillbranch.sbdelivery.models.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

public const val FOR_DATA: String = "FOR_DATA"

public class ForDataInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val originalResponse = chain.proceed(chain.request())

        if (originalRequest.header(FOR_DATA) != null) {
            val clippedRequest = originalRequest.newBuilder()
                .removeHeader(FOR_DATA)
                .build()

            return chain.proceed(clippedRequest).newBuilder()
                .header(FOR_DATA, originalRequest.header(FOR_DATA)!!)
                .build()
        }

        return originalResponse
    }
}
