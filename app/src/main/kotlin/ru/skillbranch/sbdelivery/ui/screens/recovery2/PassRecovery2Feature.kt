package ru.skillbranch.sbdelivery.ui.screens.recovery2

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.network.domains.ReqRecoveryCode
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature.State

public object PassRecovery2Feature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_pass_recovery2"
    public val targetWithArgsTemplate: String = "$target?${Args.email}={${Args.email}}"
    public fun targetWithArgs(email: String): String = "$target?${Args.email}=${email}"

    public object Args {
        public const val email: String = "email"
    }

    public data class State(
        val code: String = "",
        val email: String = "",
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class SetEmail(val email: String) : Msg()
        public data class Recovery2(val recoveryCode: String) : Msg()
        public object RecoveryCompleteSuccess : Msg()
        public object RecoveryCompleteError : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Recovery2(val recoveryData: ReqRecoveryCode) : Eff()
        public object RecoveryCompleteSuccess : Eff()

    }
}
