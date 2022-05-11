package ru.skillbranch.sbdelivery.ui.screens.orders.list

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.sbdelivery.models.database.domains.Order
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.State


public object OrderListFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_order_list"

    public data class State(
        val orders: List<Order> = emptyList(),
        override val isLoading: Int = 0,
    ) : IState

    public sealed class Msg : IMessage {
        public data class SetOrders(val orders: List<Order>) : Msg()
        public object GetOrdersStatuses : Msg()
        public object GetOrders : Msg()
        public object GetOrdersCompleteSuccess : Msg()
        public object GetOrdersCompleteError : Msg()
        public object GetOrdersStatusesCompleteSuccess : Msg()
        public object GetOrdersStatusesCompleteError : Msg()
        public data class GotoOrder(val order: Order) : Msg()
    }

    public sealed class Eff : IEffect {
        public object GetOrders : Eff()
        public object GetOrdersStatuses : Eff()
        public data class GotoOrder(val order: Order) : Eff()
    }
}
