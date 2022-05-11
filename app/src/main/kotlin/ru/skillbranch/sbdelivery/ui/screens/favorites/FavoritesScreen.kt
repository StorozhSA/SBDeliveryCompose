package ru.skillbranch.sbdelivery.ui.screens.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ru.skillbranch.sbdelivery.COMPOSE_NOT_FOUND
import ru.skillbranch.sbdelivery.data.domain.toUiDishItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.components.CardProduct
import ru.skillbranch.sbdelivery.ui.components.LazyGridPaged
import ru.skillbranch.sbdelivery.ui.components.ProgressBar
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature


@Composable
public fun FavoritesScreen(vm: FavoritesViewModel) {
    val state = vm.state.collectAsState().value
    val lazyMovieItems: LazyPagingItems<VDish> = state.payload.collectAsLazyPagingItems()

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())

    if (lazyMovieItems.itemCount > 0) {
        LazyGridPaged(
            items = lazyMovieItems,
            cols = 0 // Adaptive size
        ) { it ->
            CardProduct(
                dish = it.toUiDishItem(),
                onClick = { vm.mutate(FavoritesFeature.Msg.NavigateToDish(DishFeature.target(it.id))) },
                onAddToCart = { vm.mutate(FavoritesFeature.Msg.AddToBasket(it.id)) },
                onToggleLike = {
                    vm.mutate(
                        FavoritesFeature.Msg.ToggleLike(
                            id = it.id,
                            isFavorite = !it.isFavorite
                        )
                    )
                }
            )
        }

    } else {
        COMPOSE_NOT_FOUND()
    }
}
