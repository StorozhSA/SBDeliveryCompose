package ru.skillbranch.sbdelivery.ui.screens.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ru.skillbranch.sbdelivery.CAT_ROOT_UI
import ru.skillbranch.sbdelivery.data.domain.toUiDishItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.components.*
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Msg.*


@Composable
public fun MenuScreen(vm: MenuViewModel) {
    val state = vm.state.collectAsState().value

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose { vm.onStop() }
    }

    ProgressBar(state.isLoading())

   // if (state.categories.isNotEmpty()) {

        // Если корневые категории то плиточное меню, иначе табы
        if (state.currentCategory.id == CAT_ROOT_UI.id) {

            LazyGrid(
                items = state.categories,
                cols = 3
            ) { category ->
                CardCategory(
                    item = category,
                    onClick = { vm.mutate(ClickCategory(category)) }
                )
            }

        } else {

            // Перехват нажатия назад
            if (!state.isFar) {
                BackHandler {
                    // Двигаемся назад по локальному стеку
                    vm.mutate(PopCategory)
                }
            }

            Column {
                // Если категория имеет подкатегории или блюда
                if (!state.currentCategory.isDishRoot) {
                    TabsBar(
                        tabs = state.categories,
                        startTab = state.tabIndex,
                        onSelected = { _, index -> vm.mutate(ChangeTab(index)) }
                    )
                }

                val pagedItems: LazyPagingItems<VDish> = state.dishes.collectAsLazyPagingItems()

                LazyGridPaged(
                    items = pagedItems,
                    cols = 0
                ) { dish ->
                    CardProduct(
                        dish = dish.toUiDishItem(),
                        onClick = { vm.mutate(NavigateToDish(DishFeature.target(it.id))) },
                        onAddToCart = { vm.mutate(AddToBasket(it.id)) },
                        onToggleLike = { vm.mutate(ToggleLike(it.id, !it.isFavorite)) }
                    )
                }
            }
        }

    /* } else {
         COMPOSE_NOT_FOUND()
     }*/
}
