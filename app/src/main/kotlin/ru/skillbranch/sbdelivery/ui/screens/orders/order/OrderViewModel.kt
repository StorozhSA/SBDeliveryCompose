package ru.skillbranch.sbdelivery.ui.screens.orders.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.ext.toEOrderStatus
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.database.domains.Order
import ru.skillbranch.sbdelivery.models.network.domains.ResOrderCancel
import ru.skillbranch.sbdelivery.models.network.domains.ResOrdersStatusItem
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature.State
import java.util.*
import javax.inject.Inject

@HiltViewModel
public class OrderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val responseFlowOrder = MutableSharedFlow<Order>()
    private val responseFlowOrderCancel = MutableSharedFlow<Res<ResOrderCancel>>()
    private val responseFlowOrdersStatuses = MutableSharedFlow<Res<List<ResOrdersStatusItem>>>()

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            launch {
                responseFlowOrder
                    .onEach { logd("Received order data $it") }
                    .catch { /*mutate(CreateNewOrderCompleteError)*/ }
                    .collectLatest {
                        mutate(SetOrder(it))
                    }
            }

            launch {
                responseFlowOrderCancel
                    .onEach { logd("Received cancel order data $it") }
                    .catch { mutate(CancelOrderCompleteError) }
                    .collectLatest {
                        network.getOrdersStatuses(
                            date = Date(0),
                            responseFlow = responseFlowOrdersStatuses,
                            scope = viewModelScope
                        )
                    }
            }

            launch {
                responseFlowOrdersStatuses
                    .onEach { logd("Received list orders statuses data from network $it") }
                    .catch { mutate(CancelOrderCompleteError) }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Receive list orders statuses success from network ${it.payload}")
                                mutate(CancelOrderCompleteSuccess)
                                database.saveOrdersStatuses(it.payload!!.map(ResOrdersStatusItem::toEOrderStatus))
                            }
                            else -> {
                                logd("Receive list orders statuses error from network ${it.appCode}")
                                mutate(CancelOrderCompleteError)
                            }
                        }
                    }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is CancelOrderCompleteSuccess -> copy(isLoading = isLoading.decZero()) to emptySet()
                is CancelOrderCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                is SetOrder -> copy(order = msg.order) to setOf(Eff.SetOrder(msg.order))
                is GetOrder -> this to setOf(Eff.GetOrder(msg.orderId))
                CancelOrder -> copy(isLoading = isLoading.inc()) to setOf(Eff.CancelOrder)
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {

            is Eff.GetOrder -> responseFlowOrder.emitAll(database.getOrderWithDishes(effect.orderId))

            is Eff.SetOrder -> {

            }

            is Eff.CancelOrder -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.orderCancel(
                    orderId = state().order.order.id,
                    responseFlow = responseFlowOrderCancel,
                    scope = viewModelScope
                )
            }

        }
    }
}
