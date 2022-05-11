package ru.skillbranch.sbdelivery.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.emptyFlow
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.data.domain.UiDrawerMenuItem
import ru.skillbranch.sbdelivery.ui.components.CartCount
import ru.skillbranch.sbdelivery.ui.screens.DishUiState
import ru.skillbranch.sbdelivery.ui.screens.address.map.AddressMapFeature
import ru.skillbranch.sbdelivery.ui.screens.address.text.AddressTextFeature
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.order.OrderFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.processing.OrderProcessingFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.login.LoginFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature
import ru.skillbranch.sbdelivery.ui.screens.splash.SplashFeature

@InstallIn(ViewModelComponent::class)
@Module
public object DIMStatesViewModel {

    @ViewModelScoped
    @Provides
    public fun provideSplashFeatureViewModelState(): SplashFeature.State =
        SplashFeature.State(x1 = "X1", x2 = "X2")


    @ViewModelScoped
    @Provides
    public fun provideRootFeatureViewModelState(menuItems: List<UiDrawerMenuItem>): RootFeature.State =
        RootFeature.State(
            menuItems = menuItems,
            notificationCount = 0,
            cartCount = CartCount(0)
        )

    @ViewModelScoped
    @Provides
    public fun provideFavoritesFeatureViewModelState(): FavoritesFeature.State =
        FavoritesFeature.State(payload = emptyFlow())

    @ViewModelScoped
    @Provides
    public fun provideMenuFeatureViewModelState(): MenuFeature.State = MenuFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideHomeFeatureViewModelState(): HomeFeature.State = HomeFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideCartFeatureViewModelState(): CartFeature.State = CartFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideDishFeatureViewModelState(): DishFeature.State = DishFeature.State(
        payload = DishUiState.Loading
    )

    @ViewModelScoped
    @Provides
    public fun provideSearchFeatureViewModelState(): SearchFeature.State = SearchFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideRegistrationFeatureViewModelState(): RegistrationFeature.State =
        RegistrationFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideProfileFeatureViewModelState(@DIAppSharedPreferences appSharedPreferences: IAppSharedPreferences): ProfileFeature.State {
        appSharedPreferences.apply {
            return ProfileFeature.State(
                id = userId,
                name = userFirstName,
                lname = userLastName,
                email = userEmail,
                isEdit = false,
                isShowDialog = false
            )
        }
    }

    @ViewModelScoped
    @Provides
    public fun provideLoginFeatureViewModelState(@DIAppSharedPreferences appSharedPreferences: IAppSharedPreferences): LoginFeature.State {
        appSharedPreferences.apply {
            return LoginFeature.State(
                id = userId,
                email = userEmail
            )
        }
    }

    @ViewModelScoped
    @Provides
    public fun providePassRecovery1FeatureViewModelState(@DIAppSharedPreferences appSharedPreferences: IAppSharedPreferences): PassRecovery1Feature.State {
        appSharedPreferences.apply {
            return PassRecovery1Feature.State(
                id = userId,
                email = userEmail
            )
        }
    }

    @ViewModelScoped
    @Provides
    public fun providePassRecovery2FeatureViewModelState(): PassRecovery2Feature.State =
        PassRecovery2Feature.State()

    @ViewModelScoped
    @Provides
    public fun providePassRecovery3FeatureViewModelState(): PassRecovery3Feature.State =
        PassRecovery3Feature.State()

    @ViewModelScoped
    @Provides
    public fun provideAddressTextFeatureViewModelState(): AddressTextFeature.State =
        AddressTextFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideAddressMapFeatureViewModelState(): AddressMapFeature.State =
        AddressMapFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideOrderProcessingViewModelState(): OrderProcessingFeature.State =
        OrderProcessingFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideOrderListViewModelState(): OrderListFeature.State = OrderListFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideOrderViewModelState(): OrderFeature.State = OrderFeature.State()

    @ViewModelScoped
    @Provides
    public fun provideAppBarFeatureViewModelState(menuItems: List<UiDrawerMenuItem>): AppBarFeature.State =
        AppBarFeature.State(
            menuItems = menuItems,
            position = HomeFeature.target,
            cartCount = 0,
        )
}
