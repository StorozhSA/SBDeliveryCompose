package ru.skillbranch.sbdelivery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem

public const val DISH_ID: String = "dishId"
public const val DISH_AMOUNT: String = "dishAmount"
public const val CATEGORY_ID: String = "categoryId"
public const val CATEGORY_NAME: String = "categoryName"
public const val CATEGORY_IS_DISHES: String = "categoryIsDishes"
public const val SORT_BY_ALPHABET: String = "alphabet"
public const val SORT_BY_POPULAR: String = "popular"
public const val SORT_BY_RATING: String = "rating"

public const val PROGRESS_BAR_TIMEOUT: Long = 700L

public val CAT_ROOT_UI: UiCategoryItem = UiCategoryItem("root", "", "", 0, "root", false)

@Composable
public fun COMPOSE_NOT_FOUND() {
    Image(
        painter = painterResource(id = R.drawable.ic_ufo_not_found),
        contentDescription = "Not found",
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )
}
