package ru.skillbranch.sbdelivery.ui.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import ru.skillbranch.sbdelivery.COMPOSE_NOT_FOUND
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem
import ru.skillbranch.sbdelivery.data.domain.UiDishItem
import ru.skillbranch.sbdelivery.ui.components.CardCategory
import ru.skillbranch.sbdelivery.ui.components.CardProduct
import ru.skillbranch.sbdelivery.ui.components.LazyGrid
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature
import java.io.Serializable
import java.util.*


@Composable
public fun SearchScreen(vm: SearchViewModel) {
    val state = vm.state.collectAsState().value
    val searched = LinkedList<Serializable>().apply {
        addAll(state.categories.toList())
        addAll(state.dishes.toList())
    }

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())

    if (searched.isNotEmpty()) {
        LazyGrid(items = searched, cols = 0) { item ->
            when (item) {
                is UiCategoryItem -> CardCategory(
                    item = item,
                    onClick = { vm.mutate(SearchFeature.Msg.NavigateToCategory(item)) }
                )
                is UiDishItem -> CardProduct(
                    dish = item,
                    onClick = { vm.mutate(SearchFeature.Msg.NavigateToDish(DishFeature.target(it.id))) },
                    onAddToCart = { vm.mutate(SearchFeature.Msg.AddToBasket(it.id)) },
                    onToggleLike = {
                        vm.mutate(
                            SearchFeature.Msg.ToggleLike(
                                it.id,
                                !it.isFavorite
                            )
                        )
                    }
                )
            }
        }
    } else {
        COMPOSE_NOT_FOUND()
    }
}
