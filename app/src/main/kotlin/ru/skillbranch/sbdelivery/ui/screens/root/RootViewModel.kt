@file:Suppress(
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused"
)

package ru.skillbranch.sbdelivery.ui.screens.root

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.skillbranch.common.elm.ELMViewModel
import ru.skillbranch.sbdelivery.IAppFlowPoints
import ru.skillbranch.sbdelivery.IAppSharedPreferences
import ru.skillbranch.sbdelivery.hilt.DIAppDefaultExceptionHandler
import ru.skillbranch.sbdelivery.hilt.DIAppPoints
import ru.skillbranch.sbdelivery.hilt.DIAppSharedPreferences
import ru.skillbranch.sbdelivery.hilt.SecurityProxy
import ru.skillbranch.sbdelivery.ui.components.CartCount
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.Eff
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.Msg
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.Msg.CartChanged
import ru.skillbranch.sbdelivery.ui.screens.root.RootFeature.State
import javax.inject.Inject

@HiltViewModel
public class RootViewModel @Inject constructor(
    initialState: State,
    savedStateHandle: SavedStateHandle,
    @DIAppPoints public val points: IAppFlowPoints,
    @DIAppSharedPreferences public val appSharedPreferences: IAppSharedPreferences,
    @DIAppDefaultExceptionHandler handler: CoroutineExceptionHandler,
    public val security: SecurityProxy
) : ELMViewModel<State, Msg, Eff>(initialState, handler, points, savedStateHandle) {

    init {
        // Наблюдаем за корзиной
        viewModelScope.launch {
            points.stateCartLocal.i().collectLatest {
                mutate(CartChanged(CartCount(it.sumOf { item -> item.amount })))
            }
        }
    }

/*    private fun fillUserFio(): String {
        appSharedPreferences.apply { return "$userFirstName $userLastName" }
    }*/

    override fun reduce(msg: Msg): Pair<State, Set<Eff>> {
        state().apply {
            return when (msg) {
                is CartChanged -> copy(cartCount = msg.cartCount) to emptySet()
            }
        }
    }

    override suspend fun handle(effect: Eff) {}
}
