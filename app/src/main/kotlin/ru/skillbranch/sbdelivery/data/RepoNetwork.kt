package ru.skillbranch.sbdelivery.data

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.common.network.retrofit2.RetrofitService.CompareDescriptor.*
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res
import ru.skillbranch.sbdelivery.IAppErrors
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.hilt.*
import ru.skillbranch.sbdelivery.models.network.DeliveryAPI
import ru.skillbranch.sbdelivery.models.network.DeliveryConnector
import ru.skillbranch.sbdelivery.models.network.domains.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

public interface IRepoNetwork {
    public val scope: CoroutineScope
    public fun login(
        request: ReqLogin,
        responseFlow: MutableSharedFlow<Res<ResLogin>>,
        scope: CoroutineScope? = null
    ): Job

    public fun loadAllDishes(lastModified: Long = 0): Flow<List<ResDishItem>>
    public fun loadAllCategories(lastModified: Long = 0): Flow<List<ResCategoryItem>>
    public fun getRecommended(
        responseFlow: MutableSharedFlow<Res<Set<String>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun getReviews(
        dishId: String,
        offset: Int,
        limit: Int,
        responseFlow: MutableSharedFlow<Res<List<ResReviewsItem>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun addReview(
        request: ReqReview,
        dishId: String,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope? = null
    ): Job

    public fun register(
        request: ReqRegister,
        responseFlow: MutableSharedFlow<Res<ResRegister>>,
        scope: CoroutineScope? = null
    ): Job

    public fun profile(
        request: ReqUserProfile,
        responseFlow: MutableSharedFlow<Res<ResUserProfile>>,
        scope: CoroutineScope? = null
    ): Job

    public fun changePassword(
        request: ReqNewPassword,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope? = null
    ): Job

    public fun recovery1(
        request: ReqRecoveryEmail,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope? = null
    ): Job

    public fun recovery2(
        request: ReqRecoveryCode,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope? = null
    ): Job

    public fun recovery3(
        request: ReqRecoveryPassword,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope? = null
    ): Job

    public fun address(
        request: ReqAddress,
        responseFlow: MutableSharedFlow<Res<List<ResAddressItem>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun address(
        request: ReqCoordinate,
        responseFlow: MutableSharedFlow<Res<List<ResAddressItem>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun getUpdateCart(
        request: ReqCart? = null,
        responseFlow: MutableSharedFlow<Res<ResCart>>
    ): Job

    public fun orderNew(
        request: ReqOrder,
        responseFlow: MutableSharedFlow<Res<ResOrder>>,
        scope: CoroutineScope? = null
    ): Job

    public fun getOrders(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResOrder>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun getOrdersStatuses(
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResOrdersStatusItem>>>,
        scope: CoroutineScope? = null
    ): Job

    public fun orderCancel(
        orderId: String,
        responseFlow: MutableSharedFlow<Res<ResOrderCancel>>,
        scope: CoroutineScope? = null
    ): Job
}

public class RepoNetwork(
    @DIDeliveryServiceNetwork private val service: RetrofitService<DeliveryConnector, DeliveryAPI, IAppErrors>,
    private val handler: CoroutineExceptionHandler,
    @DIAppPoints private val points: IAppFlowPoints,
    @DIAppSharedPreferences private val prefs: IAppSharedPreferences,
    @DIAppScope public override val scope: CoroutineScope,
    @DIAppErrors private val errors: IAppErrors
) : IRepoNetwork {
    // Auth token
    private fun getTokenAsBearer(): String = "bearer ${prefs.accessToken}"

    /**
     * Login - https://sbdelivery.docs.apiary.io/reference/0/0/login POST
     */
    public override fun login(
        request: ReqLogin,
        responseFlow: MutableSharedFlow<Res<ResLogin>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun login() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_LOGIN_FAILED()),
                F(402, errors.E_LOGIN_ERROR()),
                S(201, errors.E_SUCCESS_LOGIN()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.login(request) }
        )
    }

    /**
     * Register - https://sbdelivery.docs.apiary.io/reference/0/1/register POST
     */
    public override fun register(
        request: ReqRegister,
        responseFlow: MutableSharedFlow<Res<ResRegister>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun register() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_REG_FAILED()),
                F(400, errors.E_REG_EXISTS()),
                S(201, errors.E_SUCCESS_REGISTRATION()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.register(request) }
        )
    }

    /**
     * Recovery email - https://polls.apiblueprint.org/auth/recovery/email POST
     */
    public override fun recovery1(
        request: ReqRecoveryEmail,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun recovery() Email invoke")

        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_RECOVERY_EMAIL_FAILED()),
                F(201, errors.E_RECOVERY_EMAIL_FAILED()),
                F(400, errors.E_RECOVERY_EMAIL_LESS_TIME()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.recovery(request) }
        )
    }

    /**
     * Recovery code - https://polls.apiblueprint.org/auth/recovery/code POST
     */
    public override fun recovery2(
        request: ReqRecoveryCode,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun recovery() Code invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_RECOVERY_CODE_FAILED()),
                F(201, errors.E_RECOVERY_CODE_FAILED()),
                F(400, errors.E_RECOVERY_CODE_WRONG()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.recovery(request) }
        )
    }

    /**
     * Recovery password - https://polls.apiblueprint.org/auth/recovery/password POST
     */
    public override fun recovery3(
        request: ReqRecoveryPassword,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun recovery() Password invoke")

        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_RECOVERY_PASSWORD_FAILED()),
                F(201, errors.E_RECOVERY_PASSWORD_FAILED()),
                F(402, errors.E_RECOVERY_PASSWORD_EXPIRED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.recovery(request) }
        )
    }

    /**
     * Refresh - https://polls.apiblueprint.org/auth/refresh POST
     */
    public fun refresh(
        request: ReqRecoveryToken,
        responseFlow: MutableSharedFlow<Res<ResRecoveryToken>>
    ): Job {
        logd("fun refresh() token invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_REFRESH_TOKEN_FAILED()),
                F(402, errors.E_REFRESH_TOKEN_EXPIRED()),
                S(201, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.refresh(request) }
        )
    }

    /**
     * Profile - https://polls.apiblueprint.org/profile GET
     */
    public fun profile(responseFlow: MutableSharedFlow<Res<ResUserProfile>>): Job {
        logd("fun profile() get invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_PROFILE_FAILED()),
                F(401, errors.E_PROFILE_FAILED()),
                F(402, errors.E_PROFILE_FAILED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.profile(getTokenAsBearer()) }
        )
    }

    /**
     * Profile - https://polls.apiblueprint.org/profile PUT
     */
    public override fun profile(
        request: ReqUserProfile,
        responseFlow: MutableSharedFlow<Res<ResUserProfile>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun profile() edit invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_PROFILE_FAILED()),
                F(401, errors.E_PROFILE_FAILED()),
                F(402, errors.E_PROFILE_FAILED()),
                S(202, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.profile(request, getTokenAsBearer()) }
        )
    }

    /**
     * Change password - https://polls.apiblueprint.org/profile/password PUT
     */
    public override fun changePassword(
        request: ReqNewPassword,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun changePassword() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_PASSWORD_CHANGE_FAILED()),
                F(400, errors.E_PASSWORD_CHANGE_FAILED()),
                F(401, errors.E_PASSWORD_CHANGE_FAILED()),
                F(402, errors.E_PASSWORD_CHANGE_FAILED()),
                S(202, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.changePassword(request, getTokenAsBearer()) }
        )
    }

    /**
     * Favorite - https://polls.apiblueprint.org/favorite GET
     */
    public fun favorite(
        responseFlow: MutableSharedFlow<Res<List<ResFavoriteItem>>>,
        offset: Int = 0,
        limit: Int = 10,
        date: Date
    ): Job {
        logd("fun favorite() get invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_FAVORITE_FAILED()),
                F(401, errors.E_FAVORITE_FAILED()),
                F(402, errors.E_FAVORITE_FAILED()),
                N(304, errors.E_NOT_MODIFIED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.favorite(offset, limit, getTokenAsBearer(), date) }
        )
    }

    /**
     * Favorite - https://polls.apiblueprint.org/favorite PUT
     */
    public fun favorite(
        request: ReqFavorite,
        responseFlow: MutableSharedFlow<Res<Unit>>
    ): Job {
        logd("fun favorite() put invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_FAVORITE_FAILED()),
                F(401, errors.E_FAVORITE_FAILED()),
                F(402, errors.E_FAVORITE_FAILED()),
                S(202, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.favorite(request, getTokenAsBearer()) }
        )
    }

    /**
     * getRecommended - https://polls.apiblueprint.org/main/recommend GET
     */
    public override fun getRecommended(
        responseFlow: MutableSharedFlow<Res<Set<String>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun getRecommended() invoke")

        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_RECOMMENDED_FAILED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getRecommended() }
        )
    }

    /**
     * Categories - https://polls.apiblueprint.org/categories GET
     */
    private fun getCategories(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResCategoryItem>>>
    ): Job {
        logd("fun getCategories() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_CATEGORIES_FAILED()),
                N(304, errors.E_NOT_MODIFIED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getCategories(offset, limit, date) }
        )
    }

    public override fun loadAllCategories(lastModified: Long): Flow<List<ResCategoryItem>> =
        flow {
            logd("Flow getCategoriesAll started")
            val limit = 100
            val offset = AtomicInteger(0)
            val tmp = MutableSharedFlow<Res<List<ResCategoryItem>>>(replay = 0)
            getCategories(
                offset = offset.getAndIncrement(),
                limit = limit,
                date = Date(lastModified),
                responseFlow = tmp
            )
            tmp.collect {
                if (it is Res.Success) {
                    it.payload?.let { cates ->
                        emit(cates)
                        if (cates.size == limit) {
                            getCategories(
                                offset = offset.getAndIncrement(),
                                limit = limit,
                                date = Date(lastModified),
                                tmp
                            )
                        }
                    }
                } else {
                    emit(emptyList())
                }
            }
        }

    /**
     * Dishes - https://polls.apiblueprint.org/dishes?offset=0&limit=10 GET
     */
    private fun getDishes(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResDishItem>>>
    ): Job {
        logd("fun getDishes() invoke. Last modified date = $date")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_DISHES_FAILED()),
                N(304, errors.E_NOT_MODIFIED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getDishes(offset, limit, date) }
        )
    }

    public override fun loadAllDishes(lastModified: Long): Flow<List<ResDishItem>> = flow {
        logd("Flow getDishesAll started")
        val limit = 1
        val offset = AtomicInteger(0)
        val tmp = MutableSharedFlow<Res<List<ResDishItem>>>(replay = 0)
        getDishes(
            offset = offset.getAndIncrement(),
            limit = limit,
            date = Date(lastModified),
            tmp
        )
        tmp.collect {
            if (it is Res.Success) {
                it.payload?.let { dishes ->
                    emit(dishes)
                    if (dishes.size == limit) {
                        getDishes(
                            offset = offset.getAndIncrement(),
                            limit = limit,
                            date = Date(lastModified),
                            tmp
                        )
                    }
                }
            }
        }
    }

    /**
     * Reviews - https://polls.apiblueprint.org/reviews/dishId?offset=0&limit=10 GET
     */
    public override fun getReviews(
        dishId: String,
        offset: Int,
        limit: Int,
        responseFlow: MutableSharedFlow<Res<List<ResReviewsItem>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun getReviews() invoke")
        return service.send(
            out = responseFlow, handler = handler,
            match = setOf(
                F(0, errors.E_GET_REVIEWS_FAILED()),
                N(304, errors.E_NOT_MODIFIED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getReviews(dishId, offset, limit) }
        )
    }

    /**
     * Reviews - https://polls.apiblueprint.org/reviews/dishId POST
     */
    public override fun addReview(
        request: ReqReview,
        dishId: String,
        responseFlow: MutableSharedFlow<Res<Unit>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun addReview() invoke")

        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_ADD_REVIEWS_FAILED()),
                F(400, errors.E_ADD_REVIEWS_FAILED()),
                F(401, errors.E_ADD_REVIEWS_FAILED()),
                S(201, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.addReview(dishId, request, getTokenAsBearer()) }
        )
    }

    /**
     * getUpdateCart - https://polls.apiblueprint.org/cart GET or PUT
     */
    public override fun getUpdateCart(
        request: ReqCart?,
        responseFlow: MutableSharedFlow<Res<ResCart>>
    ): Job {
        logd("fun getUpdateCart() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_OR_UPDATE_CART_FAILED()),
                F(400, errors.E_GET_OR_UPDATE_CART_FAILED()),
                F(401, errors.E_GET_OR_UPDATE_CART_FAILED()),
                F(402, errors.E_GET_OR_UPDATE_CART_FAILED()),
                S(200, errors.E_SUCCESS()),
                S(202, errors.E_SUCCESS()),
            ),
            scope = scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) }
        ) {

            request?.let {
                service.api.cart(token = getTokenAsBearer(), request)
            } ?: run {
                service.api.cart(token = getTokenAsBearer())
            }
        }
    }

    /**
     * Address - https://polls.apiblueprint.org/address/input POST
     */
    public override fun address(
        request: ReqAddress,
        responseFlow: MutableSharedFlow<Res<List<ResAddressItem>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun address() check by text invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                S(200, errors.E_SUCCESS()),
                F(0, errors.E_CHECK_ADDRESS_FAILED())
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.address(request) }
        )
    }

    /**
     * Address - https://polls.apiblueprint.org/address/coordinate POST
     */
    public override fun address(
        request: ReqCoordinate,
        responseFlow: MutableSharedFlow<Res<List<ResAddressItem>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun address() check by coordinates invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_CHECK_ADDRESS_FAILED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.address(request) }
        )
    }

    /**
     * Order - https://polls.apiblueprint.org/orders/new POST
     */
    public override fun orderNew(
        request: ReqOrder,
        responseFlow: MutableSharedFlow<Res<ResOrder>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun orderNew() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_ORDER_NEW_FAILED()),
                F(400, errors.E_ORDER_NEW_FAILED()),
                F(401, errors.E_ORDER_NEW_FAILED()),
                F(402, errors.E_ORDER_NEW_FAILED()),
                S(201, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.orderNew(token = getTokenAsBearer(), request) }
        )
    }

    /**
     * Orders - https://polls.apiblueprint.org/orders? GET
     */
    public override fun getOrders(
        offset: Int,
        limit: Int,
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResOrder>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun getOrders() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_ORDERS_FAILED()),
                F(401, errors.E_GET_ORDERS_FAILED()),
                F(402, errors.E_GET_ORDERS_FAILED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getOrders(offset, limit, getTokenAsBearer(), date) }
        )
    }

    /**
     * Orders - https://polls.apiblueprint.org/orders? GET
     */
    public override fun getOrdersStatuses(
        date: Date,
        responseFlow: MutableSharedFlow<Res<List<ResOrdersStatusItem>>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun getOrdersStatus() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_GET_ORDERS_STATUSES_FAILED()),
                S(200, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = { service.api.getOrdersStatuses(date) }
        )
    }

    /**
     * orderCancel - https://polls.apiblueprint.org/orders/cancel PUT
     */
    public override fun orderCancel(
        orderId: String,
        responseFlow: MutableSharedFlow<Res<ResOrderCancel>>,
        scope: CoroutineScope?
    ): Job {
        logd("fun getUpdateCart() invoke")
        return service.send(
            out = responseFlow,
            handler = handler,
            match = setOf(
                F(0, errors.E_ORDER_CANCEL_FAILED()),
                F(400, errors.E_ORDER_CANCEL_FAILED()),
                F(401, errors.E_ORDER_CANCEL_FAILED()),
                F(402, errors.E_ORDER_CANCEL_FAILED()),
                S(202, errors.E_SUCCESS()),
            ),
            scope = scope ?: this.scope,
            onStart = { points.progressBar(true) },
            onEnd = { points.progressBar(false) },
            api = {
                service.api.orderCancel(
                    token = getTokenAsBearer(),
                    order = ReqOrderCancel(orderId = orderId)
                )
            }
        )
    }
}
