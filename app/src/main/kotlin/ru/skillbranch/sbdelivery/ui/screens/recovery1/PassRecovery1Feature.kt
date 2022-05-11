package ru.skillbranch.sbdelivery.ui.screens.recovery1

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.Eff
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.Msg
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature.State

public object PassRecovery1Feature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_pass_recovery1"

    public data class State(
        val id: String = "",
        val email: String = "",
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class Recovery1(val recoveryEmail: String) : Msg()
        public object RecoveryCompleteSuccess : Msg()
        public object RecoveryCompleteError : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public object Recovery1 : Eff()
        public object RecoveryCompleteSuccess : Eff()

    }
}
