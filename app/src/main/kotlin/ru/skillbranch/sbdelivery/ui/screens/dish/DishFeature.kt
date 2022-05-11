package ru.skillbranch.sbdelivery.ui.screens.dish

import ru.skillbranch.common.elm.IFeature
import ru.skillbranch.sbdelivery.data.domain.UiReviews
import ru.skillbranch.sbdelivery.models.database.domains.EReviews
import ru.skillbranch.sbdelivery.models.database.domains.VDish
import ru.skillbranch.sbdelivery.ui.screens.DishUiState

public object DishFeature :
    IFeature<DishFeature.State, DishFeature.Msg, DishFeature.Eff> {
    override val target: String = "screen_dish"
    public fun target(id: String): String = "$target/$id"

    public data class State(
        override val isLoading: Int = 0,
        val dishId: String = "",
        val payload: DishUiState = DishUiState.Loading,
        val amount: Int = 1,
        val inCartAmount: Int = 0,
        val inCart: Boolean = false,
        val reviews: List<UiReviews> = emptyList(),
        val rating: Float = 0f,
        val isReviewLoaded: Boolean = false,
        val isReviewLoadingInProcess: Boolean = false,
        val isReviewDialogShow: Boolean = false,
        val isBasketAddInProcess: Boolean = false,
        val isToggleLikeInProcess: Boolean = false,
        val isIncrementInProcess: Boolean = false,
        val isDecrementInProcess: Boolean = false
    ) : IFeature.IState

    public sealed class Msg : IFeature.IMessage {
        public data class Increment(val dishId: String) : Msg()
        public data class IncrementComplete(val dishId: String) : Msg()
        public data class Decrement(val dishId: String) : Msg()
        public data class DecrementComplete(val dishId: String) : Msg()
        public data class SetAmount(val amount: Int) : Msg()
        public data class SetInCartAmount(val amount: Int) : Msg()
        public data class SetDish(val dish: VDish) : Msg()
        public object EmptyDish : Msg()
        public object AddToBasket : Msg()
        public object AddToBasketComplete : Msg()
        public object HideProgress : Msg()
        public data class ToggleLike(val dishId: String, val isFavorite: Boolean) : Msg()
        public object ToggleLikeComplete : Msg()
        public object UpdateReviews : Msg()
        public object UpdateReviewsComplete : Msg()
        public data class SetReviews(val reviews: List<EReviews>) : Msg()
        public data class SetDishId(val dishId: String) : Msg()
        public data class ReviewDialogOpen(val dishId: String) : Msg()
        public object ReviewDialogClose : Msg()
        public data class SendReview(val dishId: String, val rating: Int, val review: String) :
            Msg()
        public object SendReviewComplete : Msg()
        public object SendReviewFailure : Msg()
    }

    public sealed class Eff : IFeature.IEffect {
        public data class Increment(val dishId: String) : Eff()
        public data class Decrement(val dishId: String) : Eff()
        public object AddToBasket : Eff()
        public object SetDishId : Eff()
        public data class ToggleLike(val dishId: String, val isFavorite: Boolean) : Eff()
        public object UpdateReviews : Eff()
        public data class SendReview(val dishId: String, val rating: Int, val review: String) :
            Eff()
        public object SendReviewComplete : Eff()
    }
}
