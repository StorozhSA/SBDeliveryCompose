package ru.skillbranch.sbdelivery.ui.screens.profile.login

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.network.domains.ReqLogin
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature.State

public object LoginFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_profile_login"

    public val targetWithArgsTemplate: String =
        "${target}?${Args.prev}={${Args.prev}}&${Args.next}={${Args.next}}"

    public fun targetWithArgs(prev: String, next: String): String =
        "${target}?${Args.prev}=${prev}&${Args.next}=${next}"

    public object Args {
        public const val prev: String = "prev"
        public const val next: String = "next"
    }

    public data class State(
        val id: String = "",
        val email: String = "",
        val prev: String = "",
        val next: String = "",
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class SetRoute(val prev: String, val next: String) : Msg()
        public data class Login(val loginData: ReqLogin) : Msg()
        public object LoginCompleteSuccess : Msg()
        public object LoginCompleteError : Msg()
        public object GoToRegistration : Msg()
        public object GoToPassRecovery : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Login(val loginData: ReqLogin) : Eff()
        public object GoToRegistration : Eff()
        public object GoToPassRecovery : Eff()
    }
}
