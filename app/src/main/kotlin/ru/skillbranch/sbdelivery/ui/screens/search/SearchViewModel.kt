package ru.skillbranch.sbdelivery.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.domain.toUiCategoryItem
import ru.skillbranch.sbdelivery.data.domain.toUiDishItem
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.database.domains.VCategory
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature.State
import javax.inject.Inject

@HiltViewModel
public class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints private val points: IAppFlowPoints,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private var searchJobDishes: Job? = null
    private var searchJobCategories: Job? = null

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch(Dispatchers.IO) {

            // Наблюдаем за flow eventSearchMsg. Внешние сообщения для SearchViewModel
            launch {
                points.eventSearchMsg.i().collectLatest { mutate(it) }
            }

            //
            if (state().input.isNotBlank()) mutate(Search(state().input))
        }
    }

    /*override fun onStop() {
        super.onStop()
        searchJobDishes?.cancel()
        searchJobCategories?.cancel()
    }*/

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is SetCategories -> copy(categories = msg.categories) to emptySet()
                is SetDishes -> copy(dishes = msg.dishes) to emptySet()
                is Search -> copy(input = msg.input) to setOf(
                    Eff.SearchCategories,
                    Eff.SearchDishes
                )
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
                is NavigateToCategory -> this to setOf(Eff.NavigateToCategory(msg.category))
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.SearchDishes -> {
                searchJobDishes?.cancel()
                searchJobDishes = viewModelScope.launch(currentCoroutineContext()) {
                    database.searchDishes(state().input)
                        .distinctUntilChanged()
                        .collectLatest {
                            mutate(SetDishes(it.map(VDish::toUiDishItem).toSet()))
                        }
                }
            }
            is Eff.SearchCategories -> {
                searchJobCategories?.cancel()
                searchJobCategories = viewModelScope.launch(currentCoroutineContext()) {
                    database.searchCategories(state().input)
                        .distinctUntilChanged()
                        .collectLatest {
                            mutate(SetCategories(it.map(VCategory::toUiCategoryItem).toSet()))
                        }
                }
            }
            is Eff.ToggleLike -> try {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.toggleFavoriteDish(
                        id = effect.id,
                        isFavorite = effect.isFavorite
                    )
                }
            } catch (ex: Exception) {
                UnKillable {
                    delay(PROGRESS_BAR_TIMEOUT)
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
            } catch (ex: Exception) {
                UnKillable { mutate(AddToBasketComplete) }
            }
            is Eff.NavigateToCategory -> points.navigate(
                Nav.To.Route(
                    destination = MenuFeature.target + "?catId=${effect.category.id}",
                    /*args = bundleOf("catId" to effect.category.id)*/
                )
            )
        }
    }
}
