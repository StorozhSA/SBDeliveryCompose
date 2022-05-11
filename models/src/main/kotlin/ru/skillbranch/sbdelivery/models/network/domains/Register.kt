package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqRegister(
    @SerialName("email")
    val email: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("password")
    val password: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResRegister(
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
