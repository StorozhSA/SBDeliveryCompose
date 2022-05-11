package ru.skillbranch.sbdelivery.ui.screens.orders.processing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
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
import ru.skillbranch.sbdelivery.models.network.domains.ReqOrder
import ru.skillbranch.sbdelivery.models.network.domains.ResOrder
import ru.skillbranch.sbdelivery.models.network.domains.ResOrdersStatusItem
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature.State
import java.util.*
import javax.inject.Inject

@HiltViewModel
public class OrderProcessingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val responseFlowOrder = MutableSharedFlow<Res<ResOrder>>()
    private val responseFlowOrdersStatuses = MutableSharedFlow<Res<List<ResOrdersStatusItem>>>()

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            launch {
                responseFlowOrder
                    .onEach { logd("Received order data $it") }
                    .catch { mutate(CreateNewOrderCompleteError) }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Received order success ${it.payload}")
                                mutate(CreateNewOrderCompleteSuccess(it.payload!!.id))
                                database.saveOrdersWithDishes(listOf(it.payload!!.toOrderWithDishes()))

                                // Запрашиваем статусы
                                network.getOrdersStatuses(
                                    date = Date(0),
                                    responseFlow = responseFlowOrdersStatuses,
                                    scope = viewModelScope
                                )
                            }
                            else -> {
                                logd("Received order error ${it.appCode}")
                                mutate(CreateNewOrderCompleteError)
                            }
                        }
                    }
            }


            launch {
                responseFlowOrdersStatuses
                    .onEach { logd("Received list orders statuses data from network $it") }
                    .catch { /*mutate(OrderListFeature.Msg.GetOrdersStatusesCompleteError)*/ }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Receive list orders statuses success from network ${it.payload}")
                                database.saveOrdersStatuses(it.payload!!.map(ResOrdersStatusItem::toEOrderStatus))
                                if (state.value.orderId.isNotBlank()) {
                                    points.navigate(
                                        Nav.To.Route(
                                            destination = OrderFeature.targetWithArgs(orderId = state.value.orderId),
                                            options = NavOptions.Builder()
                                                .setLaunchSingleTop(true)
                                                .setPopUpTo(
                                                    route = CartFeature.target,
                                                    inclusive = false
                                                ).build()
                                        )
                                    )
                                }
                            }
                            else -> {
                                logd("Receive list orders statuses error from network ${it.appCode}")
                            }
                        }
                    }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                OpenAddressMap -> this to setOf(Eff.OpenAddressMap)
                OpenAddressText -> this to setOf(Eff.OpenAddressText)
                CreateNewOrder -> copy(isLoading = isLoading.inc()) to setOf(Eff.CreateNewOrder)
                is CreateNewOrderCompleteSuccess -> copy(
                    isLoading = isLoading.decZero(),
                    orderId = msg.orderId
                ) to emptySet()
                CreateNewOrderCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {

            is Eff.OpenAddressText -> {
                points.navigate(
                    Nav.To.Route(
                        destination = AddressTextFeature.target,
                        options = NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                )
            }

            is Eff.OpenAddressMap -> {
                points.navigate(
                    Nav.To.Route(
                        destination = AddressMapFeature.target,
                        options = NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                )
            }

            is Eff.CreateNewOrder -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.orderNew(
                    request = ReqOrder(
                        address = appSharedPreferences.address,
                        entrance = appSharedPreferences.entrance.toIntOrNull() ?: 0,
                        floor = appSharedPreferences.floor.toIntOrNull() ?: 0,
                        apartment = appSharedPreferences.apartment,
                        intercom = appSharedPreferences.intercom,
                        comment = appSharedPreferences.comment
                    ),
                    responseFlow = responseFlowOrder,
                    scope = viewModelScope
                )
            }
        }
    }
}
