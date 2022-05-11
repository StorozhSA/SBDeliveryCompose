package ru.skillbranch.sbdelivery.ui.screens.root

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.data.domain.UiDrawerMenuItem
import ru.skillbranch.sbdelivery.ui.components.CartCount
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.State

public object RootFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_root"

    public data class State(
        val menuItems: List<UiDrawerMenuItem> = emptyList(),
        val notificationCount: Int = 0,
        val cartCount: CartCount = CartCount(0),
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class CartChanged(val cartCount: CartCount) : Msg()
    }

    public sealed class Eff : IFeature.IEffect
}
