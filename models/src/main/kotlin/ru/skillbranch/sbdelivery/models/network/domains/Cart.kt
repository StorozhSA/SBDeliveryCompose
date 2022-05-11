package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ResCart(
    @SerialName("items")
    val items: List<CartItem> = emptyList(),
    @SerialName("promocode")
    val promocode: String = "",
    @SerialName("promotext")
    val promotext: String = "",
    @SerialName("total")
    val total: Int = 0
) : Serializable


@kotlinx.serialization.Serializable
public data class CartItem(
    @SerialName("amount")
    val amount: Int = 0,
    @SerialName("id")
    val id: String = "",
    @SerialName("price")
    val price: Int = 0
) : Serializable

@kotlinx.serialization.Serializable
public data class ReqCart(
    @SerialName("items")
    val items: List<CartItem> = emptyList(),
    @SerialName("promocode")
    val promocode: String = ""
) : Serializable
