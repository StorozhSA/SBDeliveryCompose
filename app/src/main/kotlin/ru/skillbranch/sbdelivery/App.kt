package ru.skillbranch.sbdelivery

import android.app.Application
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.CoilUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import ru.skillbranch.common.IScopeHolder
import ru.skillbranch.common.Notify.*
import ru.skillbranch.common.extension.getString
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res.Success
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.ext.asCartItem
import ru.skillbranch.sbdelivery.ext.asECartItem
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppScopeHolder
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.database.domains.CartItemJoined
import ru.skillbranch.sbdelivery.models.network.domains.CartItem
import ru.skillbranch.sbdelivery.models.network.domains.ReqCart
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
public class App : Application(), DefaultLifecycleObserver {

    @Inject
    @DIAppSharedPreferences
    public lateinit var appSharedPreferences: IAppSharedPreferences

    @Inject
    @DIAppScopeHolder
    public lateinit var scopeHolder: IScopeHolder

    @Inject
    @DIAppPoints
    public lateinit var points: IAppFlowPoints

    @Inject
    public lateinit var network: IRepoNetwork

    @Inject
    public lateinit var database: IRepoDataBase

    private val lifecycleOwner = ProcessLifecycleOwner.get()
    private var flagCartFromServerUpdated = false

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate() {
        super<Application>.onCreate()
        instance = this

        // region Coil init
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(this@App.applicationContext))
                    .build()
            }
            .componentRegistry {
                add(SvgDecoder(this@App))
            }
            .build()

        Coil.setImageLoader(imageLoader)
        // endregion

        // region SharedPreference listener
        appSharedPreferences.changesListener.apply {
            add { k, v ->
                logd("invoke OnSharedPreferenceChangeListener for key = $k value=$v")
                when (k) {
                    "userId" -> when ((v as? String)?.isNotEmpty()) {
                        true -> points.notify(LinkInt(R.string.is_registered))
                        false -> points.notify(LinkInt(R.string.is_unregistered))
                        else -> {}
                    }
                }
            }
        }
        // endregion

        // region Subscribe handle Notify. Show Toast.
        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            logd("Subscribe handle Notify for show toast.")
            points.eventNotify.i().collectLatest {
                logd("Notify $it")
                when (it) {
                    is LinkStr -> Toast.makeText(
                        applicationContext,
                        applicationContext.getString(it.resName),
                        Toast.LENGTH_LONG
                    ).show()
                    is LinkInt -> Toast.makeText(
                        applicationContext,
                        applicationContext.getString(it.resId),
                        Toast.LENGTH_LONG
                    ).show()
                    is Text -> Toast.makeText(
                        applicationContext,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()
                    is Error -> Toast.makeText(
                        applicationContext,
                        applicationContext.getString(it.appCode),
                        Toast.LENGTH_LONG
                    ).show()
                    is Action -> logd("Notify.ActionMessage ${it.actionLabel}")
                }
            }
        }
        //endregion
    }

    public override fun onStart(@NonNull owner: LifecycleOwner) {
        logd("APP ON_START")

        // Загружаем категории из сети и сохраняем в базе
        scopeHolder.appScope.launch(Dispatchers.IO) {
            network.loadAllCategories().distinctUntilChanged().collectLatest {
                database.saveCategories(it)
            }
        }

        // Загружаем блюда из сети и сохраняем в базе
        scopeHolder.appScope.launch(Dispatchers.IO) {
            network.loadAllDishes(database.getLastModDateDishes() - 86400000).distinctUntilChanged()
                .collectLatest {
                    database.saveDishes(it)
                }
        }

        // region Cart
        // Наблюдаем за локальной корзиной
        scopeHolder.appScope.launch(Dispatchers.Default) {
            points.stateCartLocal.i().distinctUntilChanged().collectLatest {
                logd("Cart changed. Amount ${it.sumOf { item -> item.amount }}")

                // Обновляем корзину на сервере если изменения в базе
                if (flagCartFromServerUpdated) {
                    network.getUpdateCart(
                        request = ReqCart(
                            items = it.map(CartItemJoined::asCartItem),
                            promocode = ""
                        ),
                        responseFlow = points.stateCartServer.m()
                    )
                }
            }
        }
        // Подключаемся к потоку из базы
        database.getCartFlow(points.stateCartLocal.m())
        //

        // Подписка на корзину с сервера
        scopeHolder.appScope.launch(Dispatchers.Default) {
            points.stateCartServer.i().collectLatest {
                when (it) {

                    is Success -> {

                        it.payload?.let { cart ->
                            if (database.getCart().map(CartItemJoined::asCartItem) != cart.items) {
                                database.saveCartFromServer(cart.items.map(CartItem::asECartItem))
                            }
                        }
                    }
                    else -> {

                    }
                }
                flagCartFromServerUpdated = true
            }
        }
        // Разовый запрос корзины с сервера
        network.getUpdateCart(
            request = null,
            responseFlow = points.stateCartServer.m()
        )
        // endregion

        //TODO
        /*scopeHolder.appScope.launch(Dispatchers.Default) {
            // Periodic timer for check last modified date of dishes and categories
            tickerFlow(
                period = 15.seconds,
                delay = 15.seconds
            ).collectLatest {
                points.eventNotify.m().emit(Text("MMMMMMMMMMMMMM"))
            }
        }*/
    }

    public override fun onStop(@NonNull owner: LifecycleOwner) {
        logd("APP ON_STOP")
        scopeHolder.cancel()
    }

    public companion object {
        public lateinit var instance: App private set
    }
}

public object StringsRes {
    public fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }
}

public fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd MMMM yyyy", Locale("ru", "RU"))
    return format.format(date)
}
