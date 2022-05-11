package ru.skillbranch.sbdelivery.ui.screens.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavOptions
import ru.skillbranch.common.Nav
import ru.skillbranch.sbdelivery.common.SortBy
import ru.skillbranch.sbdelivery.ui.components.toolbars.DefaultToolBar
import ru.skillbranch.sbdelivery.ui.components.toolbars.SearchToolbar
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.BarState.Search
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature

private val routeToCart = Nav.To.Route(
    destination = CartFeature.target,
    options = NavOptions.Builder().setLaunchSingleTop(true).build()
)


@Composable
public fun AppBarScreen(vm: AppBarViewModel, onToggleDrawer: () -> Unit) {
    val state = vm.state.collectAsState().value

    when (state.barState) {
        is AppBarFeature.BarState.Default -> DefaultToolBar(
            title = state.title,
            cartCount = state.cartCount,
            canCart = state.barState.canCart,
            canBack = state.barState.canBack,
            canSort = state.barState.canSort,
            sortBy = state.barState.sortBy,
            sortOrder = state.barState.sortOrder,
            onClickMenuByAlphabetically = { vm.mutate(ChangeSortBy(SortBy.Alphabetically)) },
            onClickMenuByPopularity = { vm.mutate(ChangeSortBy(SortBy.Popularity)) },
            onClickMenuByRating = { vm.mutate(ChangeSortBy(SortBy.Rating)) },
            onClickSort = {},
            onClickCart = { vm.points.eventNav.m().tryEmit(routeToCart) },
            onDrawer = { onToggleDrawer.invoke() }
        )
        is Search -> SearchToolbar(
            title = state.title,
            cartCount = state.cartCount,
            input = state.barState.input,
            isSearch = state.barState.isSearch,
            canCart = state.barState.canCart,
            canBack = state.barState.canBack,
            suggestions = state.barState.suggestions,
            onInput = { vm.mutate(OnInput(it)) },
            onSubmit = { vm.mutate(OnSubmit) },
            onSuggestionClick = {
                vm.mutate(OnInput(it))
                vm.mutate(OnSubmit)
            },
            onSearchToggle = { vm.mutate(SearchToggle) },
            onBackClick = { vm.mutate(SearchToggle) },
            onClickCart = { vm.points.eventNav.m().tryEmit(routeToCart) },
            onDrawer = { onToggleDrawer.invoke() }
        )
    }
}


