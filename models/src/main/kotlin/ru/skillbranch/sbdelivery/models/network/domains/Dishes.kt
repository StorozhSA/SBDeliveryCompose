package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import ru.skillbranch.common.extension.randomString
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ResCategoryItem(
    @SerialName("categoryId")
    val id: String = "0123456789abcdef".randomString(16),
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("icon")
    val icon: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("order")
    val order: Int = 0,
    @SerialName("parent")
    val parent: String = "root",
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
    @SerialName("createdAt")
    val createdAt: Long = 0
) : Serializable


@kotlinx.serialization.Serializable
public data class ResDishItem(
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("category")
    val category: String = "root",
    @SerialName("commentsCount")
    val commentsCount: Int = 0,
    @SerialName("createdAt")
    val createdAt: Long = 0,
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: String = "0123456789abcdef".randomString(16),
    @SerialName("image")
    val image: String = "",
    @SerialName("likes")
    val likes: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("oldPrice")
    val oldPrice: Int = 0,
    @SerialName("price")
    val price: Int = 0,
    @SerialName("rating")
    val rating: Double = 0.0,
    @SerialName("updatedAt")
    val updatedAt: Long = 0
) : Serializable
