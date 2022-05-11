package ru.skillbranch.sbdelivery.data.domain

import androidx.annotation.DrawableRes
import java.io.Serializable

public data class UiDrawerMenuItem(
    @DrawableRes val icon: Int,
    val title: String,
    val route: Set<String>,
    val show: Boolean = true
) : Serializable
