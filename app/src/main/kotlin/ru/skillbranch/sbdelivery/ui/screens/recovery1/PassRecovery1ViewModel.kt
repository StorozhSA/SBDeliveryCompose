package ru.skillbranch.sbdelivery.ui.screens.recovery1

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
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryEmail
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.State
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature
import javax.inject.Inject

@HiltViewModel
public class PassRecovery1ViewModel @Inject constructor(
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

            // Subscription to recovery1 events
            launch {
                responseFlow
                    .onEach { logd("Recovery1 data $it") }
                    .catch { mutate(Msg.RecoveryCompleteError) }
                    .distinctUntilChanged()
                    .collectLatest {
                        when (it) {
                            is RetrofitService.Res.Success -> {
                                logd("Recovery1 success $it")
                                mutate(Msg.RecoveryCompleteSuccess)
                            }
                            else -> {
                                logd("Recovery1 error ${it.appCode}")
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
                is Msg.Recovery1 -> copy(
                    isLoading = isLoading.inc(),
                    email = msg.recoveryEmail
                ) to setOf(Eff.Recovery1)
                is Msg.RecoveryCompleteSuccess -> copy(isLoading = isLoading.decZero()) to setOf(Eff.RecoveryCompleteSuccess)
                is Msg.RecoveryCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.Recovery1 -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.recovery1(
                    request = ReqRecoveryEmail(state().email),
                    responseFlow = responseFlow,
                    scope = viewModelScope
                )
            }

            is Eff.RecoveryCompleteSuccess -> {
                // Go to next recovery step 2
                points.navigate(Nav.To.Route(PassRecovery2Feature.targetWithArgs(email = state.value.email)))
            }
        }
    }
}
