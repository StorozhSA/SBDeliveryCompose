package ru.skillbranch.sbdelivery.ui.screens.profile.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res.Success
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.network.domains.toReqUserProfile
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature.State
import javax.inject.Inject

@HiltViewModel
public class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints public val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    private val network: IRepoNetwork,
    @DIAppDefaultExceptionHandler private val handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(
    initialState,
    handler,
    points,
    savedStateHandle
) {
    private val passwordChangeFlow = MutableSharedFlow<RetrofitService.Res<Unit>>()

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch(handler) {

            // Subscription to profile events
            launch {
                points.eventProfile.i().distinctUntilChanged().collectLatest {

                    when (it) {
                        is Success -> {
                            logd("Profile change success $it")
                            mutate(IsEdit(false))

                            it.payload?.let { user ->

                                // Save to shared preferences
                                appSharedPreferences.apply {
                                    //userIsRegistered = true
                                    //userIsAuth = true
                                    userFirstName = user.firstName
                                    userLastName = user.lastName
                                    userEmail = user.email
                                }
                                mutate(ChangeProfileCompleteSuccess(user.toReqUserProfile()))
                            }

                        }
                        else -> {
                            logd("Profile change error ${it.appCode}")
                            mutate(ChangeProfileCompleteError)
                        }
                    }
                }
            }

            // Subscription to password change events
            launch {
                passwordChangeFlow.distinctUntilChanged().collectLatest {
                    when (it) {
                        is Success -> {
                            logd("Password change success $it")
                            mutate(ChangePasswordCompleteSuccess)
                        }
                        else -> {
                            logd("Password change error ${it.appCode}")
                            mutate(ChangePasswordCompleteError)
                        }
                    }
                }
            }

        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is ChangeProfile -> if (isLoading()) emptyPairStub() else
                    copy(isLoading = isLoading.inc()) to setOf(Eff.ChangeProfile(msg.profile))
                is ChangeProfileCompleteSuccess -> copy(
                    name = msg.profile.firstName,
                    lname = msg.profile.lastName,
                    email = msg.profile.email,
                    isLoading = isLoading.decZero()
                ) to emptySet()
                is IsEdit -> copy(isEdit = msg.mode) to emptySet()
                ChangeProfileCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                is ChangePassword -> if (isLoading()) emptyPairStub() else
                    copy(isLoading = isLoading.inc()) to setOf(Eff.ChangePassword(msg.password))
                ChangePasswordCompleteError -> copy(isLoading = isLoading.decZero()) to emptySet()
                ChangePasswordCompleteSuccess -> copy(
                    isLoading = isLoading.decZero(),
                    isShowDialog = false
                ) to emptySet()
                is IsShowDialog -> copy(isShowDialog = msg.mode) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.ChangeProfile -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.profile(
                    request = effect.profile,
                    responseFlow = points.eventProfile.m(),
                    scope = viewModelScope
                )
            }
            is Eff.ChangePassword -> {
                delay(PROGRESS_BAR_TIMEOUT * 5)
                network.changePassword(
                    request = effect.password,
                    responseFlow = passwordChangeFlow,
                    scope = viewModelScope
                )
            }
        }
    }
}
