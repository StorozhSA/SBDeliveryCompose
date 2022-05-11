package ru.skillbranch.sbdelivery.ui.screens.orders.order

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.sbdelivery.models.database.domains.EOrder
import ru.skillbranch.sbdelivery.models.database.domains.EOrderStatus
import ru.skillbranch.sbdelivery.models.database.domains.Order
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.State


public object OrderFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_order"
    public val targetWithArgsTemplate: String = "${target}?${Args.orderId}={${Args.orderId}}"
    public fun targetWithArgs(orderId: String): String = "${target}?${Args.orderId}=${orderId}"

    public object Args {
        public const val orderId: String = "orderId"
    }

    public data class State(
        val order: Order = emptyOrder,
        override val isLoading: Int = 0,
    ) : IState

    public sealed class Msg : IMessage {
        public data class GetOrder(val orderId: String) : Msg()
        public object CancelOrder : Msg()
        public object CancelOrderCompleteSuccess : Msg()
        public object CancelOrderCompleteError : Msg()
        public data class SetOrder(val order: Order) : Msg()
    }

    public sealed class Eff : IEffect {
        public data class GetOrder(val orderId: String) : Eff()
        public object CancelOrder : Eff()
        public data class SetOrder(val order: Order) : Eff()
    }

    private val emptyOrder: Order = Order(
        order = EOrder(
            id = "",
            total = 0,
            active = true,
            address = "",
            completed = true,
            statusId = "",
            createdAt = 0,
            updatedAt = 0
        ),
        status = EOrderStatus(
            id = "",
            active = false,
            cancelable = false,
            createdAt = 0,
            name = "",
            updatedAt = 0
        ),
        dishes = emptyList()
    )
}
