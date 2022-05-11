package ru.skillbranch.sbdelivery.ui.screens.menu

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.CAT_ROOT_UI
import ru.skillbranch.sbdelivery.common.SortBy
import ru.skillbranch.sbdelivery.data.domain.UiCategoryItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.State

public object MenuFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_menu"

    public data class State(
        @Transient
        val dishes: Flow<PagingData<VDish>> = emptyFlow(),
        val categories: List<UiCategoryItem> = emptyList(),
        val currentCategory: UiCategoryItem = CAT_ROOT_UI,
        val stackCategories: MutableList<UiCategoryItem> = mutableListOf(),
        override val isLoading: Int = 0,
        val isBasketAddInProcess: Boolean = false,
        val isToggleFavoriteInProcess: Boolean = false,
        val isFar: Boolean = false,
        val sortBy: SortBy = SortBy.Alphabetically,
        val sortOrder: Boolean = false,
        val tabIndex: Int = 0
    ) : IFeature.IState

    public sealed class Eff : IFeature.IEffect {
        public data class AddToBasket(val id: String) : Eff()
        public data class GetDishes(val category: UiCategoryItem) : Eff()
        public data class NavigateToDish(val routeToDish: String) : Eff()
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Eff()
        public object ClickCategory : Eff()
        public data class OpenCategory(val categoryId: String) : Eff()
    }

    public sealed class Msg : IFeature.IMessage {
        public data class AddToBasket(val id: String) : Msg()
        public data class ClickCategory(val category: UiCategoryItem) : Msg()
        public data class GetDishes(val category: UiCategoryItem) : Msg()
        public data class NavigateToDish(val routeToDish: String) : Msg()
        public data class SetCategories(val categories: List<UiCategoryItem>) : Msg()
        public data class SetDishes(val dishes: Flow<PagingData<VDish>>) : Msg()
        public data class ToggleLike(val id: String, val isFavorite: Boolean) : Msg()
        public object AddToBasketComplete : Msg()
        public object ToggleFavoriteComplete : Msg()
        public object PopCategory : Msg()
        public data class OpenCategory(val categoryId: String) : Msg()
        public data class ChangeSortBy(val sortBy: SortBy) : Msg()
        public data class ChangeTab(val tab: Int) : Msg()
    }
}
