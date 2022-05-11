package ru.skillbranch.sbdelivery.ui.screens.home

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.data.domain.UiDishItem
import ru.skillbranch.sbdelivery.ui.screens.DishesUiState

public object HomeFeature :
    IFeature<HomeFeature.State, HomeFeature.Msg, HomeFeature.Eff> {
    override val target: String = "screen_home"

    public data class State(
        val recommended: DishesUiState = DishesUiState.Loading,
        val best: DishesUiState = DishesUiState.Loading,
        val popular: DishesUiState = DishesUiState.Loading,
        override val isLoading: Int = 0,
        val isBasketAddInProcess: Boolean = false,
        val isToggleFavoriteInProcess: Boolean = false,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class ShowRecommended(val dishes: Set<UiDishItem>) : Msg()
        public data class ShowBest(val dishes: Set<UiDishItem>) : Msg()
        public data class ShowPopular(val dishes: Set<UiDishItem>) : Msg()
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Msg()
        public data class NavigateToDish(val routeToDish: String) : Msg()
        public data class AddToBasket(val id: String) : Msg()
        public object AddToBasketComplete : Msg()
        public object ToggleFavoriteComplete : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Eff()
        public data class NavigateToDish(val routeToDish: String) : Eff()
        public data class AddToBasket(val id: String) : Eff()
    }
}
