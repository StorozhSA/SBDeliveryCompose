package ru.skillbranch.sbdelivery.ui.screens.address.map

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.common.elm.IFeature.*
import ru.skillbranch.sbdelivery.models.network.domains.ReqCoordinate
import ru.skillbranch.sbdelivery.models.network.domains.ResAddressItem
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature.State

public object AddressMapFeature : IFeature<State, Msg, Eff> {
    override val target: String = "screen_address_map"

    public data class State(
        val reqAddress: ReqCoordinate = ReqCoordinate(),
        val prvAddress: ReqCoordinate = ReqCoordinate(),
        val resAddress: List<ResAddressItem> = emptyList(),
        override val isLoading: Int = 0,
    ) : IState {
        public fun isChanged(): Boolean = reqAddress != prvAddress
    }

    public sealed class Msg : IMessage {
        public data class SetReqAddress(val payload: ReqCoordinate) : Msg()
        public data class SetReqAddressCompleteSuccess(val payload: List<ResAddressItem>) : Msg()
        public object SetReqAddressCompleteError : Msg()
        public data class SelectAddress(val payload: ResAddressItem) : Msg()
    }

    public sealed class Eff : IEffect {
        public object SetReqAddress : Eff()
        public object SetReqAddressCompleteSuccess : Eff()
        public data class SelectAddress(val payload: ResAddressItem) : Eff()
    }
}
