package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ResReviewsItem(
    @SerialName("active")
    val active: Boolean = false,
    @SerialName("author")
    val author: String = "",
    @SerialName("createdAt")
    val createdAt: Long = 0,
    @SerialName("date")
    val date: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("rating")
    val rating: Int = 0,
    @SerialName("text")
    val text: String = "",
    @SerialName("updatedAt")
    val updatedAt: Long = 0
) : Serializable


@kotlinx.serialization.Serializable
public data class ReqReview(
    @SerialName("rating")
    val rating: Int = 0,
    @SerialName("text")
    val text: String = ""
) : Serializable
