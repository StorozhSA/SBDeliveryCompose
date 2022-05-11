package ru.skillbranch.sbdelivery.ui.screens.cart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.ext.asCartItem
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined
import ru.skillbranch.sbdelivery.models.network.domains.ReqCart
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature.State
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature
import javax.inject.Inject

@HiltViewModel
public class CartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            // Меняем состояние в зависимости от изменеий корзины в базе.
            // Подписаны на "глобальный" поток, локального состояния корзины
            launch {
                points.stateCartLocal.i().distinctUntilChanged().collectLatest {
                    if (it != state().dishes) {
                        mutate(Msg.SetDishes(it))
                    }
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is Msg.OpenOrderProcessing -> this to setOf(Eff.OpenOrderProcessing)
                is Msg.OpenLogin -> this to setOf(Eff.OpenLogin)
                is Msg.ApplyPromoCode -> copy(promoInited = true) to setOf(Eff.ApplyPromoCode)
                is Msg.CancelPromoCode -> copy(promoInited = false) to setOf(Eff.CancelPromoCode)
                is Msg.SetPromoCode -> copy(promocode = msg.promocode) to emptySet()
                is Msg.SetDishes -> copy(
                    dishes = msg.dishes,
                    total = msg.dishes.sumOf { item -> item.amount * item.price }) to emptySet()
                is Msg.Increment -> if (isIncrementInProcess) emptyPairStub() else
                    copy(
                        isLoading = isLoading.inc(),
                        isIncrementInProcess = true
                    ) to setOf(Eff.Increment(msg.dishId))
                is Msg.Decrement -> if (isDecrementInProcess) emptyPairStub() else
                    copy(
                        isLoading = isLoading.inc(),
                        isDecrementInProcess = true
                    ) to setOf(Eff.Decrement(msg.dishId))
                is Msg.IncrementComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isIncrementInProcess = false
                ) to emptySet()
                is Msg.DecrementComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isDecrementInProcess = false
                ) to emptySet()
                is Msg.Delete -> this to setOf(Eff.Delete(msg.dishId))
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            //
            is Eff.Increment -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.incrementCartItem(effect.dishId)
                }
            } finally {
                UnKillable { mutate(Msg.IncrementComplete(effect.dishId)) }
            }

            //
            is Eff.Decrement -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.decrementCartItem(effect.dishId)
                }
            } finally {
                UnKillable { mutate(Msg.DecrementComplete(effect.dishId)) }
            }

            //
            is Eff.Delete -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.removeCartItem(effect.dishId)
                }
            } finally {
                UnKillable { mutate(Msg.DecrementComplete(effect.dishId)) }
            }

            //
            is Eff.ApplyPromoCode -> network.getUpdateCart(
                request = ReqCart(
                    items = state().dishes.map(CartItemJoined::asCartItem),
                    promocode = state().promocode
                ),
                responseFlow = points.stateCartServer.m()
            )

            //
            is Eff.CancelPromoCode -> network.getUpdateCart(
                request = ReqCart(
                    items = state().dishes.map(CartItemJoined::asCartItem),
                    promocode = ""
                ),
                responseFlow = points.stateCartServer.m()
            )

            //
            is Eff.OpenOrderProcessing -> {
                network.getUpdateCart(
                    request = ReqCart(
                        items = state().dishes.map(CartItemJoined::asCartItem),
                        promocode = state().promocode
                    ),
                    responseFlow = points.stateCartServer.m()
                )
                delay(300)
                points.navigate(
                    Nav.To.Route(
                        destination = OrderProcessingFeature.target,
                        options = NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                )
            }

            //
            is Eff.OpenLogin -> {
                points.navigate(
                    Nav.To.Route(
                        destination = LoginFeature.target,
                        options = NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                )
            }
        }
    }
}
