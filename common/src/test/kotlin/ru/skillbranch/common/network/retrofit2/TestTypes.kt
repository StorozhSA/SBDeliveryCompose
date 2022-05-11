package ru.skillbranch.common.network.retrofit2

import kotlinx.serialization.SerialName

public data class ReqTest(
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = ""
)

public data class ResTest(
    @SerialName("accessToken")
    val accessToken: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("refreshToken")
    val refreshToken: String = ""
)

public data class ResAuth(
    @SerialName("accessToken")
    val accessToken: String = ""
)
