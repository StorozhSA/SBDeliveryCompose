package ru.skillbranch.sbdelivery.ui.screens

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.sbdelivery.data.domain.UiDishItem
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import java.io.Serializable

public sealed class DishesUiState : Serializable {
    public object Loading : DishesUiState()
    public object Empty : DishesUiState()
    public object Error : DishesUiState()
    public data class Value(val dishes: Set<UiDishItem>) : DishesUiState()
    public data class PagingValue(val dishes: Flow<PagingData<VDish>>) : DishesUiState()
}

public sealed class DishUiState : Serializable {
    public object Loading : DishUiState()
    public object Empty : DishUiState()
    public object Error : DishUiState()
    public data class Value(val dish: VDish) : DishUiState()
}
