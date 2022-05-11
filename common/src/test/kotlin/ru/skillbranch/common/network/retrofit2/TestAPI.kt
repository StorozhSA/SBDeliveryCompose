package ru.skillbranch.common.network.retrofit2

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

public interface TestAPI : RetrofitService.APIDescriptor {

    @Headers("Content-Type: application/json")
    @PUT("favorite")
    public suspend fun favorite(
        @Body body: ReqTest,
        @Header("Authorization") token: String
    ): Response<ResTest>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    public fun refreshSynchronous(@Body body: String): Call<ResAuth>
}
