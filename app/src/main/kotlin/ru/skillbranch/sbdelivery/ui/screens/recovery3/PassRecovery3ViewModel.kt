package ru.skillbranch.sbdelivery.ui.screens.recovery3

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
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryPassword
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.State
import javax.inject.Inject

@HiltViewModel
public class PassRecovery3ViewModel @Inject constructor(
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

            // Subscription to recovery3 events
            launch {
                responseFlow
                    .onEach { logd("Recovery3 data $it") }
                    .catch { mutate(Msg.RecoveryCompleteError) }
                    .distinctUntilChanged()
                    .collectLatest {
                        when (it) {
                            is RetrofitService.Res.Success -> {
                                logd("Recovery3 success $it")
                                mutate(Msg.RecoveryCompleteSuccess)
                            }
                            else -> {
                                logd("Recovery3 error ${it.appCode}")
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
                is Msg.SetCode -> copy(code = msg.code) to emptySet()
                is Msg.Recovery3 -> copy(isLoading = isLoading.inc()) to setOf(
                    Eff.Recovery3(
                        recoveryData = ReqRecoveryPassword(
                            code = code,
                            email = email,
                            password = msg.recoveryPass
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
            is Eff.Recovery3 -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.recovery3(
                    request = effect.recoveryData,
                    responseFlow = responseFlow,
                    scope = viewModelScope
                )
            }

            is Eff.RecoveryCompleteSuccess -> {
                // Go to back to Login screen
                points.navigate(Nav.PopBackStack.Route(LoginFeature.target))
            }
        }
    }
}
