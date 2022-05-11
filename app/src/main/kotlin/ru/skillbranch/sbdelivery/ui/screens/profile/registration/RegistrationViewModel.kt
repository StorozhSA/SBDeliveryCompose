package ru.skillbranch.sbdelivery.ui.screens.profile.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature.Msg.Registration
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature.Msg.RegistrationComplete
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature.State
import javax.inject.Inject

@HiltViewModel
public class RegistrationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences private val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(
    initialState,
    handler,
    points,
    savedStateHandle
) {

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            // Subscription to registration events
            launch {
                points.eventRegister.i().distinctUntilChanged().collectLatest {
                    mutate(RegistrationComplete)
                    when (it) {
                        is RetrofitService.Res.Success -> {
                            logd("Registration success $it")

                            it.payload?.let { user ->

                                // Save to shared preferences
                                appSharedPreferences.apply {
                                    //userIsRegistered = true
                                    //userIsAuth = true
                                    accessToken = user.accessToken
                                    refreshToken = user.refreshToken
                                    userId = user.id
                                    userFirstName = user.firstName
                                    userLastName = user.lastName
                                    userEmail = user.email
                                }
                            }

                            // Go to previous
                            points.navigate(Nav.PopBackStack.Route(inclusive = true))

                            /*points.navigate(
                                Nav.To.Route(
                                    destination = ProfileFeature.target,
                                    options = NavOptions.Builder()
                                        .setLaunchSingleTop(true)
                                        .setPopUpTo(
                                            route = LoginFeature.target,
                                            inclusive = true
                                        )
                                        .build()
                                )
                            )*/
                        }
                        else -> logd("Registration error ${it.appCode}")
                    }
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is Registration -> if (isLoading()) emptyPairStub() else
                    copy(isLoading = isLoading.inc()) to setOf(Eff.Registration(msg.regData))
                is RegistrationComplete -> copy(isLoading = isLoading.decZero()) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.Registration -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.register(
                    request = effect.regData,
                    responseFlow = points.eventRegister.m()
                )
            }
        }
    }
}
