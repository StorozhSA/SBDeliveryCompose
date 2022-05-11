package ru.skillbranch.sbdelivery.ui.screens.splash

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature.State

public object SplashFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_splash"

    public data class State(
        val x1: String,
        val x2: String,
        val afterAnimationDelay: Int = 1000,
        val animationDuration: Int = 2000,
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Eff : IFeature.IEffect {
        public data class ActionX1(val d1: String) : Eff()
        public data class ActionX2(val d2: String) : Eff()
    }

    public sealed class Msg : IFeature.IMessage {
        public object Msg1 : Msg()
        public object Msg2 : Msg()
    }
}
