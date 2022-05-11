package ru.skillbranch.sbdelivery.ui.screens.address.text

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.sbdelivery.models.network.domains.ReqAddress
import ru.skillbranch.sbdelivery.models.network.domains.ResAddressItem
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature.State

public object AddressTextFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_address_text"

    public data class State(
        val reqAddress: ReqAddress = ReqAddress(""),
        val prvAddress: ReqAddress = ReqAddress(""),
        val resAddress: List<ResAddressItem> = emptyList(),
        override val isLoading: Int = 0,
    ) : IState {
        public fun isChanged(): Boolean = reqAddress != prvAddress
    }

    public sealed class Msg : IMessage {
        public data class SetReqAddress(val payload: ReqAddress) : Msg()
        public data class SetReqAddressCompleteSuccess(val payload: List<ResAddressItem>) : Msg()
        public object SetReqAddressCompleteError : Msg()
        public data class SelectAddress(val payload: ResAddressItem) : Msg()
        public object SaveAddress : Msg()
    }

    public sealed class Eff : IEffect {
        public object SetReqAddress : Eff()
        public object SetReqAddressCompleteSuccess : Eff()
        public data class SelectAddress(val payload: ResAddressItem) : Eff()
        public object SaveAddress : Eff()
    }
}
