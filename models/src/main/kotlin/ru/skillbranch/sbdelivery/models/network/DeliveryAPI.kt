package ru.skillbranch.sbdelivery.models.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.sbdelivery.models.network.domains.*
import java.util.*

public interface DeliveryAPI : RetrofitService.APIDescriptor {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    public suspend fun login(@Body body: ReqLogin): Response<ResLogin>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    public suspend fun register(@Body body: ReqRegister): Response<ResRegister>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/email")
    public suspend fun recovery(@Body body: ReqRecoveryEmail): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/code")
    public suspend fun recovery(@Body body: ReqRecoveryCode): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/password")
    public suspend fun recovery(@Body body: ReqRecoveryPassword): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    public suspend fun refresh(@Body body: ReqRecoveryToken): Response<ResRecoveryToken>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    public fun refreshSynchronous(@Body body: ReqRecoveryToken): Call<ResRecoveryToken>

    @Headers("Content-Type: application/json")
    @GET("profile")
    public suspend fun profile(@Header("Authorization") token: String): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @GET("profile")
    public suspend fun profile(): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @PUT("profile")
    public suspend fun profile(
        @Body body: ReqUserProfile,
        @Header("Authorization") token: String
    ): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @PUT("profile/password")
    public suspend fun changePassword(
        @Body body: ReqNewPassword,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("favorite")
    public suspend fun favorite(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String,
        @Header("If-Modified-Since") date: Date
    ): Response<List<ResFavoriteItem>>

    @Headers("Content-Type: application/json")
    @PUT("favorite")
    public suspend fun favorite(
        @Body body: ReqFavorite,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("main/recommend")
    public suspend fun getRecommended(): Response<Set<String>>

    @Headers("Content-Type: application/json")
    @GET("categories")
    public suspend fun getCategories(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("If-Modified-Since") date: Date
    ): Response<List<ResCategoryItem>>

    @Headers("Content-Type: application/json")
    @GET("dishes")
    public suspend fun getDishes(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("If-Modified-Since") date: Date
    ): Response<List<ResDishItem>>

    @Headers("Content-Type: application/json")
    @GET("reviews/{dishId}")
    public suspend fun getReviews(
        @Path("dishId") dishId: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("FOR_DATA") fordata: String = dishId
    ): Response<List<ResReviewsItem>>

    @Headers("Content-Type: application/json")
    @POST("reviews/{dishId}")
    public suspend fun addReview(
        @Path("dishId") dishId: String,
        @Body body: ReqReview,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("cart")
    public suspend fun cart(@Header("Authorization") token: String): Response<ResCart>

    @Headers("Content-Type: application/json")
    @PUT("cart")
    public suspend fun cart(
        @Header("Authorization") token: String,
        @Body cart: ReqCart
    ): Response<ResCart>

    @Headers("Content-Type: application/json")
    @POST("address/input")
    public suspend fun address(@Body body: ReqAddress): Response<List<ResAddressItem>>

    @Headers("Content-Type: application/json")
    @POST("address/coordinates")
    public suspend fun address(@Body body: ReqCoordinate): Response<List<ResAddressItem>>

    @Headers("Content-Type: application/json")
    @POST("orders/new")
    public suspend fun orderNew(
        @Header("Authorization") token: String,
        @Body body: ReqOrder
    ): Response<ResOrder>

    @Headers("Content-Type: application/json")
    @GET("orders")
    public suspend fun getOrders(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String,
        @Header("If-Modified-Since") date: Date
    ): Response<List<ResOrder>>

    @Headers("Content-Type: application/json")
    @GET("orders/statuses")
    public suspend fun getOrdersStatuses(@Header("If-Modified-Since") date: Date): Response<List<ResOrdersStatusItem>>

    @Headers("Content-Type: application/json")
    @PUT("orders/cancel")
    public suspend fun orderCancel(
        @Header("Authorization") token: String,
        @Body order: ReqOrderCancel
    ): Response<ResOrderCancel>
}
