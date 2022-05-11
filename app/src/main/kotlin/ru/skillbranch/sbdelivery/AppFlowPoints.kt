package ru.skillbranch.sbdelivery

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.skillbranch.common.IFlowPoints
import ru.skillbranch.common.IFlowPoints.*
import ru.skillbranch.common.Nav
import ru.skillbranch.common.Notify
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res
import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined
import ru.skillbranch.sbdelivery.models.network.domains.ResCart
import ru.skillbranch.sbdelivery.models.network.domains.ResLogin
import ru.skillbranch.sbdelivery.models.network.domains.ResRegister
import ru.skillbranch.sbdelivery.models.network.domains.ResUserProfile
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature

public interface IAppFlowPoints : IFlowPoints {
    //public val eventFavoriteAll: IPointShared<List<VDish>>
    public val eventAppBarMsg: IPointShared<AppBarFeature.Msg>
    public val eventMenuMsg: IPointShared<MenuFeature.Msg>
    public val eventLogin: IPointShared<Res<ResLogin>>
    public val eventRegister: IPointShared<Res<ResRegister>>
    public val eventProfile: IPointShared<Res<ResUserProfile>>
    public val eventSearchMsg: IPointShared<SearchFeature.Msg>
    public val stateCartLocal: IPointShared<List<CartItemJoined>>
    public val stateCartServer: IPointShared<Res<ResCart>>
    public val stateNetworkStatus: IPointState<Boolean>
}

public class AppFlowPoints : IAppFlowPoints {

    public override val eventNotify: IPointShared<Notify> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    public override val eventProgressBar: IPointShared<Boolean> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    public override val eventNav: IPointShared<Nav> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    public override val stateNav: IPointState<Nav> by lazy {
        PointState(MutableStateFlow(Nav.Position(destination = HomeFeature.target)))
    }

    // Flow for support global events network connect status
    public override val stateNetworkStatus: IPointState<Boolean> by lazy {
        PointState(MutableStateFlow(false))
    }

    // Flow for support global events user registration
    public override val eventRegister: IPointShared<Res<ResRegister>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    // Flow for support global events user profile
    public override val eventProfile: IPointShared<Res<ResUserProfile>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    // Flow for support global events user login/logout
    public override val eventLogin: IPointShared<Res<ResLogin>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

/*    public override val eventFavoriteAll: IPointShared<List<VDish>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        )
    }*/

    public override val stateCartLocal: IPointShared<List<CartItemJoined>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        )
    }

    public override val stateCartServer: IPointShared<Res<ResCart>> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        )
    }

    public override val eventAppBarMsg: IPointShared<AppBarFeature.Msg> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    public override val eventMenuMsg: IPointShared<MenuFeature.Msg> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }

    public override val eventSearchMsg: IPointShared<SearchFeature.Msg> by lazy {
        PointShared(
            MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
        )
    }
}
