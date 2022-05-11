package ru.skillbranch.sbdelivery.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.IRepoNetwork
import ru.skillbranch.sbdelivery.data.domain.toUiDishItem
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.screens.DishesUiState
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.home.HomeFeature.State
import javax.inject.Inject


@HiltViewModel
public class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    private val network: IRepoNetwork,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {
    private val stopTimeout = 5000L
    private val dbRecomm = database.getAllRecommendationDishes().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout),
        replay = 1
    )
    private val dbBest = database.getAllBestDishes(3.0).shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout),
        replay = 1
    )
    private val dbPopular = database.getAllPopularDishes().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout),
        replay = 1
    )
    private val netRecomm = MutableSharedFlow<RetrofitService.Res<Set<String>>>(replay = 0)

    @OptIn(FlowPreview::class)
    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch {

            // Загрузка рекоммендованные из сети и сохрание в базу
            launch(Dispatchers.IO) {
                logd("netRecomm working in thread ${Thread.currentThread().name}")
                netRecomm.collectLatest {
                    if (it is RetrofitService.Res.Success) {
                        database.saveRecommendationDishes(it)
                    }
                }
            }

            launch(Dispatchers.IO) {
                network.getRecommended(netRecomm)
            }

            // Загрузка рекомендаций из базы
            launch {
                logd("dbRecomm working in thread ${Thread.currentThread().name}")
                dbRecomm.debounce(1000).collectLatest {
                    mutate(ShowRecommended(it.map(VDish::toUiDishItem).toSet()))
                }
            }

            // Загрузка лучшие из базы
            launch {
                logd("dbBest working in thread ${Thread.currentThread().name}")
                dbBest.debounce(1000).collectLatest {
                    mutate(ShowBest(it.map(VDish::toUiDishItem).toSet()))
                }
            }

            // Загрузка популярные из базы
            launch {
                logd("dbPopular working in thread ${Thread.currentThread().name}")
                dbPopular.debounce(1000).collectLatest {
                    mutate(ShowPopular(it.map(VDish::toUiDishItem).toSet()))
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is ShowRecommended -> copy(
                    recommended = if (msg.dishes.isEmpty()) DishesUiState.Empty
                    else DishesUiState.Value(msg.dishes)
                ) to emptySet()
                is ShowBest -> copy(
                    best = if (msg.dishes.isEmpty()) DishesUiState.Empty
                    else DishesUiState.Value(msg.dishes)
                ) to emptySet()
                is ShowPopular -> copy(
                    popular = if (msg.dishes.isEmpty()) DishesUiState.Empty
                    else DishesUiState.Value(msg.dishes)
                ) to emptySet()
                is ToggleLike -> if (isToggleFavoriteInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isToggleFavoriteInProcess = true
                    ) to setOf(Eff.ToggleLike(msg.id, msg.isFavorite))
                }
                is ToggleFavoriteComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isToggleFavoriteInProcess = false
                ) to emptySet()
                is NavigateToDish -> this to setOf(Eff.NavigateToDish(msg.routeToDish))
                is AddToBasketComplete -> copy(
                    isLoading = isLoading.decZero(),
                    isBasketAddInProcess = false
                ) to emptySet()
                is AddToBasket -> if (isBasketAddInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isBasketAddInProcess = true
                    ) to setOf(Eff.AddToBasket(msg.id))
                }
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.ToggleLike -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.toggleFavoriteDish(
                        id = effect.id,
                        isFavorite = effect.isFavorite
                    )
                }
            } finally {
                UnKillable {
                    mutate(ToggleFavoriteComplete)
                }
            }
            is Eff.NavigateToDish -> points.navigate(
                Nav.To.Route(
                    destination = effect.routeToDish,
                    options = NavOptions.Builder().setLaunchSingleTop(true).build()
                )
            )
            is Eff.AddToBasket -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.getDishById(dishId = effect.id).collectLatest {
                        logd("--- updated database.getDishById(effect.id).collectLatest ---")
                        database.saveCart(
                            listOf(
                                ECartItem(
                                    id = effect.id,
                                    amount = 1,
                                    price = it.price
                                )
                            )
                        )
                    }
                }
            } finally {
                UnKillable { mutate(AddToBasketComplete) }
            }
        }
    }
}
