package ru.skillbranch.sbdelivery.ui.screens.profile.profile

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.models.network.domains.ReqNewPassword
import ru.skillbranch.sbdelivery.models.network.domains.ReqUserProfile

public object ProfileFeature :
    IFeature<ProfileFeature.State, ProfileFeature.Msg, ProfileFeature.Eff> {
    override val target: String = "screen_profile_prf"

    public data class State(
        val id: String = "",
        val name: String = "",
        val lname: String = "",
        val email: String = "",
        val newPassword: String = "",
        val oldPassword: String = "",
        val isEdit: Boolean = false,
        val isShowDialog: Boolean = false,
        override val isLoading: Int = 0,
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class IsEdit(val mode: Boolean) : Msg()
        public data class IsShowDialog(val mode: Boolean) : Msg()

        public data class ChangeProfile(val profile: ReqUserProfile) : Msg()
        public data class ChangeProfileCompleteSuccess(val profile: ReqUserProfile) : Msg()
        public object ChangeProfileCompleteError : Msg()

        public data class ChangePassword(val password: ReqNewPassword) : Msg()
        public object ChangePasswordCompleteSuccess : Msg()
        public object ChangePasswordCompleteError : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class ChangeProfile(val profile: ReqUserProfile) : Eff()
        public data class ChangePassword(val password: ReqNewPassword) : Eff()
    }
}
