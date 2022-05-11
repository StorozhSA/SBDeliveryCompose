package ru.skillbranch.sbdelivery.ui.screens.favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.favorites.FavoritesFeature.State
import javax.inject.Inject

@HiltViewModel
public class FavoritesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {
    private val dbFavorites: Flow<PagingData<VDish>> by lazy {
        Pager(PAGING_CONFIG) { database.getAllFavoriteDishesPagingSource() }
            .flow
            .cachedIn(viewModelScope)
    }

    init {
        mutate(SetPayload(dbFavorites))
    }


    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
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
                is SetPayload -> copy(payload = msg.payload) to emptySet()
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
                UnKillable { mutate(ToggleFavoriteComplete) }
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

    public companion object {
        private val PAGING_CONFIG = PagingConfig(
            pageSize = 6,
            prefetchDistance = 2,
            enablePlaceholders = false
        )
    }
}
