package ru.skillbranch.sbdelivery.data.domain

import ru.skillbranch.sbdelivery.models.database.domains.VDish
import java.io.Serializable

public data class UiDishItem(
    val id: String,
    val image: String?,
    val price: String,
    val title: String,
    val isSale: Boolean = false,
    val isFavorite: Boolean = false,
) : Serializable

public fun VDish.toUiDishItem(): UiDishItem = UiDishItem(
    id = this.id,
    image = this.image,
    price = this.price.toString(),
    title = this.name,
    isSale = this.oldPrice!! > 0,
    isFavorite = this.favorite
)
