package ru.skillbranch.sbdelivery.ui.screens.appbar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.ext.base
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.ResourcesProvider
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.BarState.Default
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.BarState.Search
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.State
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
import ru.skillbranch.sbdelivery.ui.screens.search.SearchFeature
import javax.inject.Inject

@HiltViewModel
public class AppBarViewModel @Inject constructor(
    initialState: State,
    savedStateHandle: SavedStateHandle,
    public val res: ResourcesProvider,
    @DIAppPoints public val points: IAppFlowPoints,
    private val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler,
    private val resourcesProvider: ResourcesProvider
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    private val searchHistory by lazy { database.getSearchHistory() }
    private var searchHistoryJob: Job? = null

    init {
        // Наблюдаем за Navigation flow stateNav
        viewModelScope.launch {
            points.stateNav.i().collectLatest {
                if (it is Nav.Position) mutate(SetPosition(it.destination))
            }
        }

        // Наблюдаем за flow eventAppBarMsg
        viewModelScope.launch {
            points.eventAppBarMsg.i().collectLatest {
                mutate(it)
            }
        }

        // Наблюдаем за корзиной
        viewModelScope.launch {
            points.stateCartLocal.i().collectLatest {
                mutate(CartChanged(it.sumOf { item -> item.amount }))
            }
        }

        // Наблюдаем за историей поиска
        viewModelScope.launch {
            state.collectLatest {
                if (it.barState is Search) {
                    if (it.barState.input.isBlank() && it.barState.isSearch) {
                        searchHistoryJob = launch {
                            searchHistory.collectLatest { sh ->
                                mutate(SetSuggestions(sh.associateWith { 0 }))
                                //mutate(SetSuggestions(sh.map { item -> item to 0 }.toMap()))
                            }
                        }
                    } else {
                        searchHistoryJob?.cancel()
                        mutate(SetSuggestions(emptyMap()))
                    }
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is SetPosition -> {
                    when (msg.position.base()) {
                        HomeFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(msg.position),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = true
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = true
                            )
                        ) to emptySet()
                        CartFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(msg.position),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()

                        FavoritesFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(msg.position),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()

                        DishFeature.target -> copy(
                            position = msg.position,
                            title = res.getString(R.string.title_dish),
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        MenuFeature.target + ".cat" -> copy(
                            position = msg.position,
                            title = msg.title.ifBlank { this.appBarTitle(msg.position) },
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = true,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = true,
                                canCart = false
                            )
                        ) to emptySet()
                        MenuFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(msg.position),
                            barState = (barState as? Search)?.copy(isSearch = false) ?: Search(
                                isSearch = false
                            )
                        ) to emptySet()
                        SearchFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(MenuFeature.target),
                            barState = (barState as? Search)?.copy(isSearch = true) ?: Search(
                                isSearch = true
                            )
                        ) to emptySet()
                        RegistrationFeature.target -> copy(
                            position = msg.position,
                            title = resourcesProvider.getString(R.string.labelReg),
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        LoginFeature.target -> copy(
                            position = msg.position,
                            title = resourcesProvider.getString(R.string.labelLogin),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        ProfileFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(ProfileFeature.target),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        PassRecovery1Feature.target -> copy(
                            position = msg.position,
                            title = resourcesProvider.getString(R.string.labelRecoveryPassword),
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        OrderProcessingFeature.target -> copy(
                            position = msg.position,
                            title = resourcesProvider.getString(R.string.labelOrderProcessing),
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        OrderListFeature.target -> copy(
                            position = msg.position,
                            title = this.appBarTitle(OrderListFeature.target),
                            barState = (barState as? Default)?.copy(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = false,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        OrderFeature.target -> copy(
                            position = msg.position,
                            title = resourcesProvider.getString(R.string.labelOrder),
                            barState = (barState as? Default)?.copy(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            ) ?: Default(
                                canBack = true,
                                canSort = false,
                                canCart = false
                            )
                        ) to emptySet()
                        else -> {
                            this to emptySet()
                        }
                    }
                }
                is CartChanged -> copy(cartCount = msg.cartCount) to emptySet()
                is SearchToggle -> copy(
                    barState = (barState as? Search)
                        ?.copy(
                            isSearch = !barState.isSearch,
                            input = "",
                            canBack = !barState.isSearch,
                        )
                        ?: this.barState
                ) to setOf(Eff.SearchToggle)
                is OnInput -> copy(
                    barState = (barState as? Search)
                        ?.copy(
                            input = msg.input,
                            suggestions = emptyMap()
                        )
                        ?: this.barState
                ) to emptySet()
                is OnSubmit -> copy(
                    barState = (barState as? Search)
                        ?.copy(suggestions = emptyMap())
                        ?: this.barState
                ) to setOf(
                    Eff.OnSubmit
                )
                is SetSuggestions -> copy(
                    barState = (barState as? Search)
                        ?.copy(suggestions = msg.suggestions)
                        ?: this.barState
                ) to emptySet()
                is ChangeSortBy -> if ((barState as? Default)?.sortBy == msg.sortBy) {
                    copy(
                        barState = (barState as? Default)
                            ?.copy(sortOrder = !barState.sortOrder)
                            ?: this.barState
                    ) to setOf(Eff.ChangeSortBy(msg.sortBy))
                } else {
                    copy(
                        barState = (barState as? Default)
                            ?.copy(sortBy = msg.sortBy)
                            ?: this.barState
                    ) to setOf(Eff.ChangeSortBy(msg.sortBy))
                }

            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.SearchToggle -> if ((state().barState as? Search)?.isSearch == true) {
                points.eventNav.m().tryEmit(Nav.To.Route(SearchFeature.target))
            } else {
                points.eventNav.m().tryEmit(Nav.PopBackStack.Route())
            }

            is Eff.OnSubmit -> (state().barState as Search).apply {
                database.addSearchHistoryItem(input)
                points.eventSearchMsg.m().emit(SearchFeature.Msg.Search(input))
            }
            is Eff.ChangeSortBy -> points.eventMenuMsg.m()
                .emit(MenuFeature.Msg.ChangeSortBy(effect.sortBy))
        }
    }
}
