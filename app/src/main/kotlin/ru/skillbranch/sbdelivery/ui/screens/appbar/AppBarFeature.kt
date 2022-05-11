package ru.skillbranch.sbdelivery.ui.screens.appbar

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.common.SortBy
import ru.skillbranch.sbdelivery.data.domain.UiDrawerMenuItem
import ru.skillbranch.sbdelivery.ext.base
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.State
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature
import java.io.Serializable

public object AppBarFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_appbar"

    public sealed class BarState {
        public data class Search(
            val isSearch: Boolean = false,
            val input: String = "",
            val canBack: Boolean = isSearch,
            val canCart: Boolean = false,
            val suggestions: Map<String, Int> = emptyMap()
        ) : BarState(), Serializable

        public data class Default(
            val canBack: Boolean = false,
            val canSort: Boolean = false,
            val canCart: Boolean = true,
            val sortBy: SortBy = SortBy.Alphabetically,
            val sortOrder: Boolean = true
        ) : BarState(), Serializable
    }

    public data class State(
        val position: String = HomeFeature.target,
        val title: String = "",
        val menuItems: List<UiDrawerMenuItem> = emptyList(),
        val cartCount: Int = 0,
        override val isLoading: Int = 0,
        val barState: BarState = BarState.Default()
    ) : IFeature.IState {

        public fun appBarTitle(pos: String): String = menuItems
            .filter { it.route.contains(pos.base()) }
            .map { it.title }
            .firstOrNull() ?: ""
    }

    public sealed class Msg : IFeature.IMessage {
        public object SearchToggle : Msg()
        public data class SetPosition(val position: String, val title: String = "") : Msg()
        public data class SetSuggestions(val suggestions: Map<String, Int>) : Msg()
        public data class CartChanged(val cartCount: Int) : Msg()
        public data class OnInput(val input: String) : Msg()
        public object OnSubmit : Msg()
        public data class ChangeSortBy(val sortBy: SortBy) : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public object SearchToggle : Eff()
        public object OnSubmit : Eff()
        public data class ChangeSortBy(val sortBy: SortBy) : Eff()
    }
}
