package ru.skillbranch.sbdelivery.ui.screens.splash

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Msg.Msg1
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Msg.Msg2
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.State
import javax.inject.Inject

@HiltViewModel
public class SplashViewModel @Inject constructor(
    initialState: State,
    savedStateHandle: SavedStateHandle,
    @DIAppPoints points: IAppFlowPoints,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(
    initialState,
    handler,
    points,
    savedStateHandle
) {
    override fun reduce(msg: Msg): Pair<State, Set<Eff>> = state.value.selfReduce(msg)

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.ActionX1 -> mutate(Msg1)
            is Eff.ActionX2 -> mutate(Msg2)
        }
    }
}
