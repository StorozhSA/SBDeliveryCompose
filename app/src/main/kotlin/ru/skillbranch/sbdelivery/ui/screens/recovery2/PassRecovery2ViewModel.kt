package ru.skillbranch.sbdelivery.ui.screens.recovery2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryCode
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.State
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature
import javax.inject.Inject

@HiltViewModel
public class PassRecovery2ViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    private val network: IRepoNetwork,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(
    initialState,
    handler,
    points,
    savedStateHandle
) {
    private val responseFlow: MutableSharedFlow<RetrofitService.Res<Unit>> =
        MutableSharedFlow(replay = 0)

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            // Subscription to recovery2 events
            launch {
                responseFlow
                    .onEach { logd("Recovery2 data $it") }
                    .catch { mutate(Msg.RecoveryCompleteError) }
                    .distinctUntilChanged()
                    .collectLatest {
                        when (it) {
                            is RetrofitService.Res.Success -> {
                                logd("Recovery2 success $it")
                                mutate(Msg.RecoveryCompleteSuccess)
                            }
                            else -> {
                                logd("Recovery2 error ${it.appCode}")
                                mutate(Msg.RecoveryCompleteError)
                            }
                        }
                    }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is Msg.SetEmail -> copy(email = msg.email) to emptySet()
                is Msg.Recovery2 -> copy(isLoading = isLoading.inc()) to setOf(
                    Eff.Recovery2(
                        ReqRecoveryCode(
                            code = msg.recoveryCode,
                           // email = email
                        )
                    )
                )
                is Msg.RecoveryCompleteSuccess -> copy(isLoading = isLoading.decZero()) to setOf(Eff.RecoveryCompleteSuccess)
                is Msg.RecoveryCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.Recovery2 -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.recovery2(
                    request = effect.recoveryData,
                    responseFlow = responseFlow,
                    scope = viewModelScope
                )
            }

            is Eff.RecoveryCompleteSuccess -> {
                // Go to next recovery step 3
                points.navigate(
                    Nav.To.Route(
                        PassRecovery3Feature.targetWithArgs(
                            email = state.value.email,
                            code = state.value.code
                        )
                    )
                )
            }
        }
    }
}
