package ru.skillbranch.sbdelivery.ui.screens.orders.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.skillbranch.common.Nav
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
import ru.skillbranch.sbdelivery.ext.toOrderWithDishes
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.network.domains.ResOrder
import ru.skillbranch.sbdelivery.models.network.domains.ResOrdersStatusItem
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature.State
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature
import java.util.*
import javax.inject.Inject

@HiltViewModel
public class OrderListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val responseFlowOrders = MutableSharedFlow<Res<List<ResOrder>>>()
    private val responseFlowOrdersStatuses = MutableSharedFlow<Res<List<ResOrdersStatusItem>>>()
    private val orders = database.getOrdersWithDishes()

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            launch {
                responseFlowOrders
                    .onEach { logd("Received list orders data from network $it") }
                    .catch { mutate(GetOrdersCompleteError) }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Receive list orders success from network ${it.payload}")
                                mutate(GetOrdersCompleteSuccess)

                                database.saveOrdersWithDishes(it.payload!!.map(ResOrder::toOrderWithDishes))

                            }
                            else -> {
                                logd("Receive list orders error from network ${it.appCode}")
                                mutate(GetOrdersCompleteError)
                            }
                        }
                    }
            }

            launch {
                orders
                    .onEach { logd("Received list orders data from database $it") }
                    .catch { }
                    .collectLatest {
                        mutate(SetOrders(it))
                    }
            }

            launch {
                responseFlowOrdersStatuses
                    .onEach { logd("Received list orders statuses data from network $it") }
                    .catch { mutate(GetOrdersStatusesCompleteError) }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Receive list orders statuses success from network ${it.payload}")
                                mutate(GetOrdersStatusesCompleteSuccess)
                                database.saveOrdersStatuses(it.payload!!.map(ResOrdersStatusItem::toEOrderStatus))
                            }
                            else -> {
                                logd("Receive list orders statuses error from network ${it.appCode}")
                                mutate(GetOrdersStatusesCompleteError)
                            }
                        }
                    }
            }

            // Launch orders statuses request
            mutate(GetOrdersStatuses)
            delay(PROGRESS_BAR_TIMEOUT * 5)
            // Launch orders request
            mutate(GetOrders)
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is SetOrders -> copy(orders = msg.orders) to emptySet()
                GetOrdersStatuses -> copy(isLoading = isLoading.inc()) to setOf(Eff.GetOrdersStatuses)
                GetOrders -> copy(isLoading = isLoading.inc()) to setOf(Eff.GetOrders)
                GetOrdersCompleteSuccess -> copy(isLoading = isLoading.decZero()) to emptySet()
                GetOrdersCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                GetOrdersStatusesCompleteSuccess -> copy(isLoading = isLoading.decZero()) to emptySet()
                GetOrdersStatusesCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                is GotoOrder -> this to setOf(Eff.GotoOrder(msg.order))
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {

            is Eff.GetOrders -> {
                //delay(PROGRESS_BAR_TIMEOUT * 5)
                network.getOrders(
                    date = Date(0),
                    responseFlow = responseFlowOrders,
                    scope = viewModelScope
                )
            }

            is Eff.GetOrdersStatuses -> {
                network.getOrdersStatuses(
                    date = Date(0),
                    responseFlow = responseFlowOrdersStatuses,
                    scope = viewModelScope
                )
            }

            is Eff.GotoOrder -> {
                points.navigate(Nav.To.Route(OrderFeature.targetWithArgs(orderId = effect.order.order.id)))
            }

        }
    }
}
