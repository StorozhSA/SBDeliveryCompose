package ru.skillbranch.sbdelivery.data.domain

import java.io.Serializable

public data class UiCartItem(
    val id: String,
    val image: String,
    val title: String,
    val count: Int,
    val price: Int
) : Serializable
