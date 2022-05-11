package ru.skillbranch.sbdelivery.ui.screens.search

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem
import ru.skillbranch.sbdelivery.data.domain.UiDishItem
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.State

public object SearchFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_search"

    public data class State(
        val input: String = "",
        val dishes: Set<UiDishItem> = emptySet(),
        val categories: Set<UiCategoryItem> = emptySet(),
        val isBasketAddInProcess: Boolean = false,
        val isToggleFavoriteInProcess: Boolean = false,
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class Search(val input: String) : Msg()
        public data class SetCategories(val categories: Set<UiCategoryItem>) : Msg()
        public data class SetDishes(val dishes: Set<UiDishItem>) : Msg()

        public data class NavigateToCategory(val category: UiCategoryItem) : Msg()
        public data class NavigateToDish(val routeToDish: String) : Msg()
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Msg()
        public data class AddToBasket(val id: String) : Msg()
        public object AddToBasketComplete : Msg()
        public object ToggleFavoriteComplete : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public object SearchCategories : Eff()
        public object SearchDishes : Eff()
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Eff()
        public data class NavigateToCategory(val category: UiCategoryItem) : Eff()
        public data class NavigateToDish(val routeToDish: String) : Eff()
        public data class AddToBasket(val id: String) : Eff()
    }
}
