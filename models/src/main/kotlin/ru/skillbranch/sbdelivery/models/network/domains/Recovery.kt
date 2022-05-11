package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqRecoveryEmail(
    @SerialName("email")
    val email: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ReqRecoveryCode(
    @SerialName("code")
    val code: String = "",
    /*@SerialName("email")
    val email: String = ""*/
) : Serializable


@kotlinx.serialization.Serializable
public data class ReqRecoveryPassword(
    @SerialName("code")
    val code: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ReqRecoveryToken(
    @SerialName("refreshToken")
    val refreshToken: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResRecoveryToken(
    @SerialName("accessToken")
    val accessToken: String = ""
) : Serializable
