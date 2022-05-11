package ru.skillbranch.sbdelivery.ui.screens.profile.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
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
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.State
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature
import javax.inject.Inject

@HiltViewModel
public class LoginViewModel @Inject constructor(
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

            // Subscription to login events
            launch {
                points.eventLogin.i().distinctUntilChanged().collectLatest {
                    when (it) {
                        is RetrofitService.Res.Success -> {
                            logd("Login success $it")
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

                                mutate(LoginCompleteSuccess)

                                logd("Prev = ${state().prev}, Next = ${state().next}")

                                // Go to next
                                points.navigate(
                                    Nav.To.Route(
                                        destination = state().next,
                                        options = NavOptions.Builder()
                                            .setPopUpTo(
                                                route = state().prev,
                                                inclusive = false
                                            )
                                            .setLaunchSingleTop(true)
                                            .build()
                                    )
                                )
                            }
                        }
                        else -> {
                            logd("Login error ${it.appCode}")
                            mutate(LoginCompleteError)
                        }
                    }
                }
            }
        }
    }


    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is Login -> if (isLoading()) emptyPairStub() else
                    copy(isLoading = isLoading.inc()) to setOf(Eff.Login(msg.loginData))
                is LoginCompleteSuccess -> copy(isLoading = isLoading.decZero()) to emptySet()
                is LoginCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                is GoToRegistration -> this to setOf(Eff.GoToRegistration)
                is GoToPassRecovery -> this to setOf(Eff.GoToPassRecovery)
                is SetRoute -> copy(prev = msg.prev, next = msg.next) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.Login -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.login(
                    request = effect.loginData,
                    responseFlow = points.eventLogin.m(),
                    scope = viewModelScope
                )
            }

            is Eff.GoToRegistration -> points.navigate(
                Nav.To.Route(
                    destination = RegistrationFeature.target,
                    options = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(
                            route = LoginFeature.target,
                            inclusive = true
                        )
                        .build()
                )
            )

            is Eff.GoToPassRecovery -> points.navigate(
                Nav.To.Route(
                    destination = PassRecovery1Feature.target,
                    options = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            )
        }
    }
}
