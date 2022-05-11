package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqLogin(
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResLogin(
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
) : Serializable
