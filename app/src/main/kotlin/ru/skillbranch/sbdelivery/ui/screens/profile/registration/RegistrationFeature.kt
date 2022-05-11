package ru.skillbranch.sbdelivery.ui.screens.profile.registration

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.network.domains.ReqRegister

public object RegistrationFeature :
    IFeature<RegistrationFeature.State, RegistrationFeature.Msg, RegistrationFeature.Eff> {
    override val target: String = "screen_profile_reg"

    public data class State(
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class Registration(val regData: ReqRegister) : Msg()
        public object RegistrationComplete : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Registration(val regData: ReqRegister) : Eff()
    }
}
