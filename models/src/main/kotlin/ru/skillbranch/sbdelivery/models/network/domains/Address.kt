package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqAddress(
    @SerialName("address")
    val address: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResAddressItem(
    @SerialName("city")
    val city: String = "",
    @SerialName("house")
    val house: String = "",
    @SerialName("street")
    val street: String = "",
    @SerialName("value")
    val value: String = ""
) : Serializable

@kotlinx.serialization.Serializable
public data class ReqCoordinate(
    @SerialName("lat")
    val lat: Double = 0.0,
    @SerialName("lon")
    val lon: Double = 0.0
) : Serializable
