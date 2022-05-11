package ru.skillbranch.sbdelivery.data.domain

import ru.skillbranch.sbdelivery.models.database.domains.VCategory
import java.io.Serializable

public data class UiCategoryItem(
    val id: String,
    val name: String,
    val icon: String?,
    val order: Int,
    val parent: String,
    val isDishRoot: Boolean
) : Serializable

public fun VCategory.toUiCategoryItem(): UiCategoryItem = UiCategoryItem(
    id = id,
    name = name,
    icon = icon,
    order = order,
    parent = parent,
    isDishRoot = isDishRoot
)
