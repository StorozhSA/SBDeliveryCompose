package ru.skillbranch.sbdelivery.ui.screens.dish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import ru.skillbranch.common.Notify
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.data.domain.toUiReviews
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.database.domains.EReviews
import ru.skillbranch.sbdelivery.models.network.domains.ReqReview
import ru.skillbranch.sbdelivery.models.network.domains.ResReviewsItem
import ru.skillbranch.sbdelivery.ui.screens.DishUiState
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.dish.DishFeature.State
import javax.inject.Inject

@HiltViewModel
public class DishViewModel @Inject constructor(
    public val savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints public val points: IAppFlowPoints,
    public val network: IRepoNetwork,
    public val database: IRepoDataBase,
    @DIAppSharedPreferences private val appSharedPreferences: IAppSharedPreferences,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val responseFlowAddReview = MutableSharedFlow<Res<Unit>>()
    private val responseFlowReview = MutableSharedFlow<Res<List<ResReviewsItem>>>()

    public fun setDish(dishId: String) {
        if (state().dishId != dishId) {
            mutate(SetDishId(dishId = dishId))
        }
    }

    override fun onStart() {
        super.onStart()

        if (state().dishId.isNotBlank()) {
            jobOnStart?.cancel()
            jobOnStart = viewModelScope.launch {

                // Получаем блюдо
                launch {
                    database.getDishById(state().dishId)
                        .distinctUntilChanged()
                        .collectLatest {
                            mutate(SetDish(it))
                        }
                }

                // Проверяем наличие в корзине и обновляем состояние если есть
                launch {
                    points.stateCartLocal.i().collectLatest {
                        val inCartAmount =
                            it.find { item -> item.id == state().dishId }?.amount ?: 0
                        mutate(SetInCartAmount(inCartAmount))
                        state().inCartAmount.apply {
                            if (this > 0 && state().amount != this) {
                                mutate(SetAmount(this))
                            }
                        }
                    }
                }

                // Подписываемся на отзывы блюда из базы
                launch {
                    database.getReviewsByDish(state().dishId)
                        .distinctUntilChanged()
                        .collectLatest {
                            mutate(SetReviews(it))
                        }
                }

                // Подписываемся на отзывы блюда из сети (RepoNetwork.getReviews()).
                launch {
                    responseFlowReview
                        .onEach { logd("Invoke observer points.eventUpdateDishReviews") }
                        .distinctUntilChanged()
                        .collectLatest {
                            mutate(UpdateReviewsComplete)

                            if (it is Res.Success) {
                                database.saveReviews(it)
                            }
                        }
                }

                // Обновляем отзывы с сервера. Запись в базу происходит см. App.kt
                mutate(UpdateReviews)

                launch {
                    responseFlowAddReview
                        .onEach { logd("Received AddReview data $it") }
                        .catch { mutate(SendReviewFailure) }
                        .collectLatest {
                            when (it) {
                                is Res.Success -> {
                                    logd("Received AddReview data success ${it.payload}")
                                    mutate(SendReviewComplete)
                                }
                                else -> {
                                    logd("Received AddReview data error ${it.appCode}")
                                    mutate(SendReviewFailure)
                                }
                            }
                        }
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is Increment -> if (isIncrementInProcess) emptyPairStub() else
                    copy(
                        isLoading = isLoading.inc(),
                        amount = amount.inc(),
                        isIncrementInProcess = true
                    ) to setOf(Eff.Increment(msg.dishId))
                is Decrement -> if (isDecrementInProcess) emptyPairStub() else
                    copy(
                        isLoading = isLoading.inc(),
                        amount = amount.decZero(),
                        isDecrementInProcess = true
                    ) to setOf(Eff.Decrement(msg.dishId))
                is SetInCartAmount -> copy(inCartAmount = msg.amount) to emptySet()
                is SetAmount -> copy(amount = msg.amount) to emptySet()
                is SetDishId -> copy(dishId = msg.dishId) to setOf(Eff.SetDishId)
                is SetDish -> copy(payload = DishUiState.Value(msg.dish)) to emptySet()
                is EmptyDish -> copy(payload = DishUiState.Loading) to emptySet()
                is HideProgress -> copy(isLoading = isLoading.decZero()) to emptySet()
                is IncrementComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isIncrementInProcess = false
                ) to emptySet()
                is DecrementComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isDecrementInProcess = false
                ) to emptySet()
                is AddToBasketComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isBasketAddInProcess = false
                ) to emptySet()
                is AddToBasket -> if (isBasketAddInProcess)
                    copy(inCart = true) to emptySet()
                else
                    copy(
                        isLoading = isLoading.inc(),
                        isBasketAddInProcess = true
                    ) to setOf(Eff.AddToBasket)
                is ToggleLike -> if (isToggleLikeInProcess) emptyPairStub() else
                    copy(
                        isLoading = isLoading.inc(),
                        isToggleLikeInProcess = true
                    ) to setOf(Eff.ToggleLike(msg.dishId, msg.isFavorite))
                is ToggleLikeComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isToggleLikeInProcess = false
                ) to emptySet()

                is SetReviews -> copy(reviews = msg.reviews.map(EReviews::toUiReviews)) to emptySet()
                is ReviewDialogOpen -> copy(isReviewDialogShow = true) to setOf()
                is ReviewDialogClose -> copy(isReviewDialogShow = false) to setOf()
                is SendReview -> copy(
                    isLoading = isLoading.inc(),
                    isReviewDialogShow = false
                ) to setOf(
                    Eff.SendReview(msg.dishId, msg.rating, msg.review)
                )
                is SendReviewComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isReviewDialogShow = false
                ) to setOf(Eff.SendReviewComplete)
                is SendReviewFailure -> copy(
                    isLoading = isLoading.decZero(),
                    isReviewDialogShow = false
                ) to emptySet()
                is UpdateReviews -> copy(isLoading = isLoading.inc()) to setOf(Eff.UpdateReviews)
                is UpdateReviewsComplete -> copy(isLoading = isLoading.decZero()) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.SetDishId -> this.onStart()
            is Eff.Increment -> {
                delay(PROGRESS_BAR_TIMEOUT)
                mutate(IncrementComplete(effect.dishId))
            }

            is Eff.Decrement -> {
                delay(PROGRESS_BAR_TIMEOUT)
                mutate(DecrementComplete(effect.dishId))
            }

            is Eff.AddToBasket -> if (state().payload is DishUiState.Value) {
                try {
                    withTimeout(PROGRESS_BAR_TIMEOUT) {
                        database.saveCart(
                            listOf(
                                ECartItem(
                                    id = (state().payload as DishUiState.Value).dish.id,
                                    amount = state().amount,
                                    price = (state().payload as DishUiState.Value).dish.price
                                )
                            )
                        )
                    }
                } finally {
                    UnKillable {
                        mutate(AddToBasketComplete)
                    }
                }
            }

            is Eff.ToggleLike -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.toggleFavoriteDish(
                        id = effect.dishId,
                        isFavorite = effect.isFavorite
                    )
                }
            } finally {
                UnKillable {
                    mutate(ToggleLikeComplete)
                }
            }

            is Eff.UpdateReviews -> viewModelScope.launch {
                network.getReviews(
                    dishId = state().dishId,
                    offset = 0,
                    limit = 10,
                    responseFlow = responseFlowReview,
                    scope = viewModelScope
                )
            }
            is Eff.SendReview -> {
                // If user is login
                if (appSharedPreferences.userId.isNotBlank()) {
                    network.addReview(
                        request = ReqReview(rating = effect.rating, text = effect.review),
                        dishId = effect.dishId,
                        responseFlow = responseFlowAddReview,
                        scope = viewModelScope
                    )
                } else {
                    mutate(SendReviewFailure)
                    points.notify(Notify.Text("User is not logged in")) //todo from resources
                }
            }
            is Eff.SendReviewComplete -> mutate(UpdateReviews)
        }
    }
}
