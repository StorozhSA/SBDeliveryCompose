package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqUserProfile(
    @SerialName("email")
    val email: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("lastName")
    val lastName: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResUserProfile(
    @SerialName("email")
    val email: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("lastName")
    val lastName: String = ""
) : Serializable

public fun ResUserProfile.toReqUserProfile(): ReqUserProfile = ReqUserProfile(
    email = email,
    firstName = firstName,
    lastName = lastName
)


@kotlinx.serialization.Serializable
public data class ReqNewPassword(
    @SerialName("newPassword")
    val newPassword: String = "",
    @SerialName("oldPassword")
    val oldPassword: String = ""
) : Serializable
