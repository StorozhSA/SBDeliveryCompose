package ru.skillbranch.sbdelivery.ui.screens.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import ru.skillbranch.common.Nav
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.common.extension.UnKillable
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.sbdelivery.CAT_ROOT_UI
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.PROGRESS_BAR_TIMEOUT
import ru.skillbranch.sbdelivery.data.IRepoDataBase
import ru.skillbranch.sbdelivery.data.domain.toUiCategoryItem
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.models.database.domains.ECartItem
import ru.skillbranch.sbdelivery.models.database.domains.VCategory
import ru.skillbranch.sbdelivery.ui.screens.appbar.AppBarFeature.Msg.SetPosition
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.Msg.*
import ru.skillbranch.sbdelivery.ui.screens.menu.MenuFeature.State
import javax.inject.Inject

@HiltViewModel
public class MenuViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: State,
    @DIAppPoints public val points: IAppFlowPoints,
    public val database: IRepoDataBase,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    init {
        mutate(ClickCategory(state().currentCategory))
    }

    override fun onStart() {
        super.onStart()
        jobOnStart?.cancel()
        jobOnStart = viewModelScope.launch(Dispatchers.IO) {

            appBarTitle()

            // Наблюдаем за Navigation flow stateNav
            launch {
                points.stateNav.i().collectLatest {
                    if ((it as? Nav.Position)?.destination == MenuFeature.target) {
                        appBarTitle()
                    }
                }
            }

            // Наблюдаем за flow eventMenuMsg. Внешние сообщения для MenuViewModel
            launch {
                points.eventMenuMsg.i().collectLatest {
                    mutate(it)
                }
            }
        }
    }

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is ClickCategory -> copy(
                    stackCategories = stackCategories.apply { add(currentCategory) },
                    currentCategory = msg.category,
                    //categories = emptyList(),
                    //dishes = emptySet()
                ) to setOf(Eff.ClickCategory)
                is SetCategories -> copy(categories = msg.categories) to emptySet()
                is SetDishes -> copy(dishes = msg.dishes) to emptySet()
                is PopCategory -> copy(
                    currentCategory = stackCategories.removeLastOrNull() ?: currentCategory,
                    tabIndex = 0,
                    categories = emptyList(),
                    dishes = emptyFlow()
                ) to setOf(Eff.ClickCategory)
                is GetDishes -> this to setOf(Eff.GetDishes(msg.category))
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
                is OpenCategory -> copy(isFar = true) to setOf(Eff.OpenCategory(msg.categoryId))
                is ChangeSortBy -> {
                    if (sortBy == msg.sortBy) {
                        copy(sortOrder = !sortOrder) to setOf(Eff.ClickCategory)
                    } else {
                        copy(sortBy = msg.sortBy) to setOf(Eff.ClickCategory)
                    }
                }
                is ChangeTab -> copy(tabIndex = msg.tab) to setOf(Eff.ClickCategory)
            }
        }
    }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.ClickCategory -> withTimeout(PROGRESS_BAR_TIMEOUT) {
                // AppBar
                appBarTitle()
                // Список категорий или блюд
                if (state().currentCategory.isDishRoot) {
                    // Если категория содержит блюда, грузим в состояние блюда
                    mutate(GetDishes(state().currentCategory))
                } else {
                    // Если категория НЕ содержит блюда, грузим в состояние подкатегории
                    database.getSubCategoriesView(state().currentCategory.id)
                        .distinctUntilChanged()
                        .collectLatest {
                            if (it.isNotEmpty()) {
                                mutate(SetCategories(it.map(VCategory::toUiCategoryItem)))
                                mutate(GetDishes(it[state().tabIndex].toUiCategoryItem()))
                            }
                        }
                }
            }
            is Eff.GetDishes -> withTimeout(PROGRESS_BAR_TIMEOUT) {
                mutate(
                    SetDishes(
                        Pager(PAGING_CONFIG) {
                            database.getDishesPagingSource(
                                catId = effect.category.id,
                                sortBy = state().sortBy,
                                sortOrder = state().sortOrder
                            )
                        }.flow.cachedIn(viewModelScope)
                    )
                )
            }
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
            is Eff.OpenCategory -> if (effect.categoryId != "root") {
                withTimeout(PROGRESS_BAR_TIMEOUT) {
                    database.getCategory(effect.categoryId).collectLatest {
                        mutate(ClickCategory(it.toUiCategoryItem()))
                    }
                }
            }
        }
    }

    private suspend fun appBarTitle() {
        if (state().currentCategory.id == CAT_ROOT_UI.id) {
            // Если корневая категоря, то выводим надпись Меню в AppBar
            points.eventAppBarMsg.m().emit(
                SetPosition(position = MenuFeature.target)
            )
        } else {
            // Если не корневая категоря, то выводим эту категорию в AppBar
            points.eventAppBarMsg.m().emit(
                SetPosition(
                    position = MenuFeature.target + ".cat",
                    title = state().currentCategory.name
                )
            )
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
