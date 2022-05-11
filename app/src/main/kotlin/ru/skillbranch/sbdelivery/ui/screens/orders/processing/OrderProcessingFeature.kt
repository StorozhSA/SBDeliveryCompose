package ru.skillbranch.sbdelivery.ui.screens.orders.processing

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.State


public object OrderProcessingFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_order_processing"

    public data class State(
        val orderId: String = "",
        override val isLoading: Int = 0,
    ) : IState

    public sealed class Msg : IMessage {
        public object CreateNewOrder : Msg()
        public data class CreateNewOrderCompleteSuccess(val orderId: String) : Msg()
        public object CreateNewOrderCompleteError : Msg()
        public object OpenAddressText : Msg()
        public object OpenAddressMap : Msg()
    }

    public sealed class Eff : IEffect {
        public object OpenAddressText : Eff()
        public object OpenAddressMap : Eff()
        public object CreateNewOrder : Eff()
    }
}
