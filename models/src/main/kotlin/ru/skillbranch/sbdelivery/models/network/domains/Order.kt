package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ReqOrder(
    @SerialName("address")
    val address: String = "",
    @SerialName("apartment")
    val apartment: String = "",
    @SerialName("comment")
    val comment: String = "",
    @SerialName("entrance")
    val entrance: Int = 0,
    @SerialName("floor")
    val floor: Int = 0,
    @SerialName("intercom")
    val intercom: String = ""
) : Serializable


@kotlinx.serialization.Serializable
public data class ResOrder(
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("address")
    val address: String = "",
    @SerialName("completed")
    val completed: Boolean = false,
    @SerialName("createdAt")
    val createdAt: Long = 0,
    @SerialName("id")
    val id: String = "",
    @SerialName("items")
    val items: List<OrderItem> = emptyList(),
    @SerialName("statusId")
    val statusId: String = "0",
    @SerialName("total")
    val total: Int = 0,
    @SerialName("updatedAt")
    val updatedAt: Long = 0
) : Serializable


@kotlinx.serialization.Serializable
public data class OrderItem(
    @SerialName("amount")
    val amount: Int = 0,
    @SerialName("dishId")
    val dishId: String = "",
    @SerialName("image")
    val image: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("price")
    val price: Int = 0
) : Serializable


@kotlinx.serialization.Serializable
public data class ResOrdersStatusItem(
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("cancelable")
    val cancelable: Boolean = false,
    @SerialName("createdAt")
    val createdAt: Long = 0,
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("updatedAt")
    val updatedAt: Long = 0
)


@kotlinx.serialization.Serializable
public data class ReqOrderCancel(
    @SerialName("orderId")
    val orderId: String
)


@kotlinx.serialization.Serializable
public data class ResOrderCancel(
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("address")
    val address: String = "",
    @SerialName("completed")
    val completed: Boolean = false,
    @SerialName("createdAt")
    val createdAt: Long = 0,
    @SerialName("id")
    val id: String = "",
    @SerialName("items")
    val items: List<OrderItem> = emptyList(),
    @SerialName("statusId")
    val statusId: String = "",
    @SerialName("total")
    val total: Int = 0,
    @SerialName("updatedAt")
    val updatedAt: Long = 0
)
