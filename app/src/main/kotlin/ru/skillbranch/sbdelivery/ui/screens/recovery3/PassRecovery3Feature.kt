package ru.skillbranch.sbdelivery.ui.screens.recovery3

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryPassword
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature.State

public object PassRecovery3Feature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_pass_recovery3"
    public val targetWithArgsTemplate: String =
        "$target?${Args.email}={${Args.email}}&${Args.code}={${Args.code}}"

    public fun targetWithArgs(email: String, code: String): String =
        "$target?${Args.email}=${email}&${Args.code}=${code}"

    public object Args {
        public const val email: String = "email"
        public const val code: String = "code"
    }

    public data class State(
        val code: String = "",
        val email: String = "",
        val password: String = "",
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class SetEmail(val email: String) : Msg()
        public data class SetCode(val code: String) : Msg()
        public data class Recovery3(val recoveryPass: String) : Msg()
        public object RecoveryCompleteSuccess : Msg()
        public object RecoveryCompleteError : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Recovery3(val recoveryData: ReqRecoveryPassword) : Eff()
        public object RecoveryCompleteSuccess : Eff()
    }
}
