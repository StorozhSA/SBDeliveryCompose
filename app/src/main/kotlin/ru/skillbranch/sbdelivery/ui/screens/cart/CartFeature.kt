package ru.skillbranch.sbdelivery.ui.screens.cart

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined

public object CartFeature :
    IFeature<CartFeature.State, CartFeature.Msg, CartFeature.Eff> {
    override val target: String = "screen_cart"

    public data class State(
        val dishes: List<CartItemJoined> = emptyList(),
        val promocode: String = "",
        val promotext: String = "",
        val promoInited: Boolean = false,
        val total: Int = 0,
        override val isLoading: Int = 0,
        val isIncrementInProcess: Boolean = false,
        val isDecrementInProcess: Boolean = false
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public object OpenOrderProcessing : Msg()
        public object OpenLogin : Msg()
        public data class SetPromoCode(val promocode: String) : Msg()
        public data class SetDishes(val dishes: List<CartItemJoined>) : Msg()
        public object ApplyPromoCode : Msg()
        public object CancelPromoCode : Msg()
        public data class Increment(val dishId: String) : Msg()
        public data class IncrementComplete(val dishId: String) : Msg()
        public data class Decrement(val dishId: String) : Msg()
        public data class DecrementComplete(val dishId: String) : Msg()
        public data class Delete(val dishId: String) : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Increment(val dishId: String) : Eff()
        public data class Decrement(val dishId: String) : Eff()
        public data class Delete(val dishId: String) : Eff()
        public object ApplyPromoCode : Eff()
        public object CancelPromoCode : Eff()
        public object OpenOrderProcessing : Eff()
        public object OpenLogin : Eff()
    }
}
