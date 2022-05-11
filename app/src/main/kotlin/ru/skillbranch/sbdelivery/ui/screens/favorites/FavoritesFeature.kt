package ru.skillbranch.sbdelivery.ui.screens.favorites

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.database.domains.VDish

public object FavoritesFeature :
    IFeature<FavoritesFeature.State, FavoritesFeature.Msg, FavoritesFeature.Eff> {
    override val target: String = "screen_favorites"

    public data class State(
        @Transient
        val payload: Flow<PagingData<VDish>> = emptyFlow(),
        val isBasketAddInProcess: Boolean = false,
        val isToggleFavoriteInProcess: Boolean = false,
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class SetPayload(val payload: Flow<PagingData<VDish>>) : Msg()
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
