package ru.skillbranch.sbdelivery.hilt

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import ru.skillbranch.common.AppException
import ru.skillbranch.common.IScopeHolder
import ru.skillbranch.common.Notify.Error
import ru.skillbranch.common.extension.loge
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.common.network.retrofit2.RetrofitService.Authenticator
import ru.skillbranch.common.network.retrofit2.RetrofitServiceImpl
import ru.skillbranch.sbdelivery.*
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.skillbranch.sbdelivery.data.domain.UiDrawerMenuItem
import ru.skillbranch.sbdelivery.models.database.DeliveryDatabaseService
import ru.skillbranch.sbdelivery.models.network.DeliveryAPI
import ru.skillbranch.sbdelivery.models.network.DeliveryAuthenticator
import ru.skillbranch.sbdelivery.models.network.DeliveryConnector
import ru.skillbranch.sbdelivery.models.network.ForDataInterceptor
import ru.skillbranch.sbdelivery.ui.screens.cart.CartFeature
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature
import ru.skillbranch.sbdelivery.ui.screens.orders.list.OrderListFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.profile.ProfileFeature
import ru.skillbranch.sbdelivery.ui.screens.profile.registration.RegistrationFeature
import ru.skillbranch.sbdelivery.ui.screens.recovery1.PassRecovery1Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery2.PassRecovery2Feature
import ru.skillbranch.sbdelivery.ui.screens.recovery3.PassRecovery3Feature
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
public object DIMApp {

    private val json = Json {
        coerceInputValues = true
        encodeDefaults = true
    }

    @DIAppSharedPreferences
    @Provides
    @Singleton
    public fun provideSharedPreference(@ApplicationContext appContext: Context): IAppSharedPreferences =
        AppSharedPreferences(appContext)

    @DIAppScopeHolder
    @Provides
    @Singleton
    public fun provideAppScopeHolder(): IScopeHolder = AppScopeHolder()

    @DIAppScope
    @Provides
    @Singleton
    public fun provideAppScope(@DIAppScopeHolder scopeHolder: IScopeHolder): CoroutineScope =
        scopeHolder.appScope

    @DIAppPoints
    @Provides
    @Singleton
    public fun provideAppFlowPoints(): IAppFlowPoints = AppFlowPoints()

    @DIAppErrors
    @Provides
    @Singleton
    public fun provideAppErrors(): IAppErrors = AppErrors()

    @DIAppDefaultExceptionHandler
    @Provides
    @Singleton
    public fun provideAppDefaultExceptionHandler(
        @DIAppPoints appFlowPoints: IAppFlowPoints,
        @DIAppErrors appErrors: IAppErrors
    ): CoroutineExceptionHandler = CoroutineExceptionHandler { _, ex ->
        loge(ex.localizedMessage ?: "Not found error message text")

        when (ex) {
            is AppException -> appFlowPoints.notify(
                Error(ex.message ?: "", ex.appCode) { _, _ -> }
            )
            else -> appFlowPoints.notify(
                Error(ex.localizedMessage ?: "", appErrors.E_UNKNOWN().toString()) { _, _ -> }
            )
        }

        ex.printStackTrace()
    }

    @DIDefaultRetrofitCache
    @Provides
    @Singleton
    public fun provideDefaultRetrofitCache(@ApplicationContext appContext: Context): Cache {
        return Cache(
            directory = File(appContext.cacheDir, "http-cache"),
            maxSize = 10485760L // 10 MB
        )
    }

    @DIDeliveryAuthenticator
    @Provides
    @Singleton
    public fun provideDeliveryAuthenticator(
        @DIAppSharedPreferences appSharedPreferences: IAppSharedPreferences
    ): Authenticator<DeliveryConnector, DeliveryAPI, IAppErrors> =
        DeliveryAuthenticator(appSharedPreferences)

    @ExperimentalSerializationApi
    @DIDefaultJSONFactoryKotlin
    @Provides
    @Singleton
    public fun provideJSONConverterFactoryByKotlin(): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())


    @DIDeliveryServiceNetwork
    @Provides
    @Singleton
    public fun provideDeliveryServiceNetwork(
        @DIAppErrors errors: IAppErrors,
        @DIDefaultRetrofitCache cache: Cache,
        @DIDeliveryAuthenticator authenticator: Authenticator<DeliveryConnector, DeliveryAPI, IAppErrors>,
        @DIDefaultJSONFactoryKotlin jsonFactory: Converter.Factory
    ): RetrofitService<DeliveryConnector, DeliveryAPI, IAppErrors> {
        return RetrofitServiceImpl(
            connector = DeliveryConnector,
            errors = errors,
            cache = cache,
            authenticator = authenticator,
            interceptorsApp = setOf(ForDataInterceptor()),
            factory = jsonFactory
        )
    }

    @Provides
    @Singleton
    public fun provideNetwork(
        @DIDeliveryServiceNetwork service: RetrofitService<DeliveryConnector, DeliveryAPI, IAppErrors>,
        @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler,
        @DIAppPoints points: IAppFlowPoints,
        @DIAppSharedPreferences prefs: IAppSharedPreferences,
        @DIAppScope scope: CoroutineScope,
        @DIAppErrors errors: IAppErrors
    ): IRepoNetwork = RepoNetwork(
        service = service,
        handler = handler,
        points = points,
        prefs = prefs,
        scope = scope,
        errors = errors
    )

    @DIDeliveryServiceDataBase
    @Provides
    @Singleton
    public fun provideDeliveryServiceDatabase(@ApplicationContext appContext: Context): DeliveryDatabaseService =
        DeliveryDatabaseService.getInstance(appContext)

    @Provides
    @Singleton
    public fun provideDatabase(
        @DIDeliveryServiceDataBase service: DeliveryDatabaseService,
        @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler,
        //@DIAppPoints points: IAppFlowPoints,
        //@DIAppSharedPreferences prefs: IAppSharedPreferences,
        @DIAppScope scope: CoroutineScope,
        @DIAppErrors errors: IAppErrors
    ): IRepoDataBase = RepoDataBase(
        service = service,
        handler = handler,
        //points = points,
        //prefs = prefs,
        scope = scope,
        errors = errors
    )

    /*@DIDefaultNetworkMonitor
    @Provides
    @Singleton
    public fun provideDefaultNetworkMonitor(
        @ApplicationContext appContext: Context,
        @DIAppPoints appFlowPoints: IAppFlowPoints
    ): INetworkMonitor = NetworkMonitor(appContext, appFlowPoints.stateNetworkStatus.m())*/

    @Singleton
    @Provides
    public fun provideUiDrawerMenuItemList(@ApplicationContext appContext: Context): List<UiDrawerMenuItem> =
        listOf(
            UiDrawerMenuItem(
                R.drawable.ic_home,
                appContext.getString(R.string.title_home),
                setOf(HomeFeature.target)
            ),
            UiDrawerMenuItem(
                R.drawable.ic_menu,
                appContext.getString(R.string.title_menu),
                setOf(MenuFeature.target)
            ),
            UiDrawerMenuItem(
                R.drawable.ic_favorite,
                appContext.getString(R.string.title_favorites),
                setOf(FavoritesFeature.target)
            ),
            UiDrawerMenuItem(
                R.drawable.ic_baseline_shopping_cart_24,
                appContext.getString(R.string.title_cart),
                setOf(CartFeature.target)
            ),
            UiDrawerMenuItem(
                R.drawable.ic_user,
                appContext.getString(R.string.title_profile),
                setOf(
                    ProfileFeature.target,
                    //LoginFeature.target,
                    RegistrationFeature.target,
                    PassRecovery1Feature.target,
                    PassRecovery2Feature.target,
                    PassRecovery3Feature.target
                )
            ),
            UiDrawerMenuItem(
                R.drawable.ic_orders,
                appContext.getString(R.string.title_orders),
                setOf(OrderListFeature.target /*LoginFeature.target*/)
            ),
            /* UiDrawerMenuItem(
                 R.drawable.ic_notification,
                 appContext.getString(R.string.title_notifications),
                 setOf(OrderFeature.target)
             ),*/
            /* UiDrawerMenuItem(
                 R.drawable.ic_baseline_engineering_24,
                 "Order",
                 setOf(OrderFeature.target),
                 show = BuildConfig.DEBUG
             ),
             UiDrawerMenuItem(
                 R.drawable.ic_baseline_engineering_24,
                 "AddressMap",
                 setOf(AddressMapFeature.target),
                 show = BuildConfig.DEBUG
             ),
             UiDrawerMenuItem(
                 R.drawable.ic_baseline_engineering_24,
                 "OrderProcessing",
                 setOf(OrderProcessingFeature.target),
                 show = BuildConfig.DEBUG
             ),*/
        )
}
