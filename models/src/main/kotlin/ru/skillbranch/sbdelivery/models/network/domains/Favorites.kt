package ru.skillbranch.sbdelivery.models.network.domains

import kotlinx.serialization.SerialName
import java.io.Serializable


@kotlinx.serialization.Serializable
public data class ResFavoriteItem(
    @SerialName("dishId")
    val dishId: String = "",
    @SerialName("favorite")
    val favorite: Boolean = false,
    @SerialName("updatedAt")
    val updatedAt: Long = 0
) : Serializable

public class ReqFavorite : Serializable, ArrayList<ReqFavoriteItem>()


@kotlinx.serialization.Serializable
public data class ReqFavoriteItem(
    @SerialName("dishId")
    val dishId: String = "",
    @SerialName("favorite")
    val favorite: Boolean = false
) : Serializable
