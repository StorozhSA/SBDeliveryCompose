package ru.skillbranch.sbdelivery.ui.screens.address.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.network.domains.ResAddressItem
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.State
import javax.inject.Inject

@HiltViewModel
public class AddressMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    private val network: IRepoNetwork,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val responseFlow = MutableSharedFlow<Res<List<ResAddressItem>>>()
    private val fromMapFlow = MutableSharedFlow<Unit>()

    @OptIn(FlowPreview::class)
    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            launch {
                responseFlow
                    .onEach { logd("Received addresses data $it") }
                    .catch { mutate(SetReqAddressCompleteError) }
                    .collectLatest {
                        when (it) {
                            is Res.Success -> {
                                logd("Received addresses success ${it.payload}")
                                mutate(SetReqAddressCompleteSuccess(it.payload!!))
                            }
                            else -> {
                                logd("Received addresses error ${it.appCode}")
                                mutate(SetReqAddressCompleteError)
                            }
                        }
                    }
            }

            // Subscription to keyboard events
            launch {
                fromMapFlow.debounce(500).collectLatest {
                    if (state().isChanged()) {
                        logd("Request addresses")
                        network.address(
                            request = state().reqAddress,
                            responseFlow = responseFlow,
                            scope = viewModelScope
                        )
                    }
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is SetReqAddress -> copy(
                    prvAddress = reqAddress,
                    reqAddress = msg.payload
                ) to setOf(Eff.SetReqAddress)

                is SetReqAddressCompleteSuccess -> copy(
                    isLoading = isLoading.decZero(),
                    prvAddress = reqAddress,
                    resAddress = msg.payload
                ) to setOf(Eff.SetReqAddressCompleteSuccess)
                is SetReqAddressCompleteError -> copy(
                    isLoading = isLoading.decZero(),
                    prvAddress = reqAddress,
                ) to emptySet()
                is SelectAddress -> this to setOf(Eff.SelectAddress(msg.payload))
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.SetReqAddress -> fromMapFlow.emit(Unit)
            is Eff.SelectAddress -> {}
            else -> {}
        }
    }
}
