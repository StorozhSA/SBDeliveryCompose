package ru.skillbranch.sbdelivery.data.domain

import ru.skillbranch.sbdelivery.models.database.domains.EReviews
import java.io.Serializable

public class UiDish(
    public val id: String,
    public val title: String,
    public val description: String,
    public val image: String,
    public val oldPrice: Int?,
    public val price: Int,
    public val rating: Float,
    public val isFavorite: Boolean = false,
) : Serializable

public data class UiReviews(
    val id: String,
    val dishId: String,
    val active: Boolean,
    val author: String,
    val createdAt: Long,
    val date: String,
    val rating: Int,
    val text: String,
    val updatedAt: Long
) : Serializable {
    public companion object {
        private const val serialVersionUID = 10670000000001L
    }
}

public fun EReviews.toUiReviews(): UiReviews {
    return UiReviews(
        id = id,
        dishId = dishId,
        active = active,
        author = author,
        createdAt = createdAt,
        date = date,
        rating = rating,
        text = text,
        updatedAt = updatedAt
    )
}

