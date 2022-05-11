package ru.skillbranch.sbdelivery.data

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import okhttp3.internal.toImmutableList
import ru.skillbranch.common.AppException
import ru.skillbranch.common.extension.decZero
import ru.skillbranch.common.extension.logd
import ru.skillbranch.common.network.retrofit2.RetrofitService.Res.Success
import ru.skillbranch.sbdelivery.CAT_ROOT_UI
import ru.skillbranch.sbdelivery.IAppErrors
import ru.skillbranch.sbdelivery.common.SortBy
import ru.skillbranch.sbdelivery.ext.asECartItem
import ru.skillbranch.sbdelivery.hilt.DIAppErrors
import ru.skillbranch.sbdelivery.hilt.DIAppScope
import ru.skillbranch.sbdelivery.hilt.DIDeliveryServiceDataBase
import ru.skillbranch.sbdelivery.models.database.DeliveryDatabaseService
import ru.skillbranch.sbdelivery.models.database.domains.*
import ru.skillbranch.sbdelivery.models.network.FOR_DATA
import ru.skillbranch.sbdelivery.models.network.domains.ResCategoryItem
import ru.skillbranch.sbdelivery.models.network.domains.ResDishItem
import ru.skillbranch.sbdelivery.models.network.domains.ResReviewsItem
import kotlin.coroutines.CoroutineContext

public interface IRepoDataBase {
    public val scope: CoroutineScope
    public fun saveCategories(value: List<ResCategoryItem>): Job
    public fun saveDishes(value: List<ResDishItem>): Job
    public fun getLastModDateDishes(): Long
    public fun getAllPopularDishes(): Flow<List<VDish>>
    public fun getAllBestDishes(minRating: Double): Flow<List<VDish>>
    public fun getAllRecommendationDishes(): Flow<List<VDish>>
    public fun saveRecommendationDishes(value: Success<Set<String>>): Job
    public fun toggleFavoriteDish(id: String, isFavorite: Boolean): Job
    public fun getDishById(dishId: String): Flow<VDish>
    public fun saveCart(value: List<ECartItem>): Job
    public fun saveCartFromServer(value: List<ECartItem>): Job
    public fun getCartFlow(oe: MutableSharedFlow<List<CartItemJoined>>): Job
    public fun getCart(): List<CartItemJoined>
    public fun saveReviews(value: Success<List<ResReviewsItem>>): Job
    public fun saveReviews(value: List<ResReviewsItem>, dishId: String): Job
    public fun getReviewsByDish(dishId: String): Flow<List<EReviews>>
    public fun getAllFavoriteDishesPagingSource(): PagingSource<Int, VDish>
    public fun getSubCategoriesView(catId: String = CAT_ROOT_UI.id): Flow<List<VCategory>>
    public fun getDishesPagingSource(
        catId: String,
        sortBy: SortBy,
        sortOrder: Boolean
    ): PagingSource<Int, VDish>

    public fun getDishes(
        catId: String,
        sortBy: SortBy = SortBy.Alphabetically,
        sortOrder: Boolean = false
    ): Flow<List<VDish>>

    public fun addSearchHistoryItem(value: String): Job
    public fun getSearchHistory(): Flow<List<String>>
    public fun delete(id: String): Job
    public fun searchCategories(text: String): Flow<List<VCategory>>
    public fun searchDishes(text: String): Flow<List<VDish>>
    public fun getCategory(catId: String): Flow<VCategory>
    public fun incrementCartItem(dishId: String)
    public fun decrementCartItem(dishId: String)
    public fun removeCartItem(dishId: String): Job
    public fun saveOrdersWithDishes(value: List<EOrderWithDishes>): Job
    public fun saveOrdersStatuses(value: List<EOrderStatus>): Job
    public fun getOrdersWithDishes(): Flow<List<Order>>
    public fun getOrderWithDishes(orderId: String): Flow<Order>

}

public class RepoDataBase(
    @DIDeliveryServiceDataBase private val service: DeliveryDatabaseService,
    handler: CoroutineExceptionHandler,
    @DIAppScope public override val scope: CoroutineScope,
    @DIAppErrors private val errors: IAppErrors
) : IRepoDataBase {

    private val workContext: CoroutineContext = (Dispatchers.IO + handler)

    // region Categories
    public override fun saveCategories(value: List<ResCategoryItem>): Job {
        return scope.launch(workContext) {
            service.daoECategory().upsert(
                value.map {
                    ECategory(
                        id = it.id,
                        name = it.name,
                        order = it.order,
                        icon = it.icon,
                        parent = it.parent,
                        active = it.active,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            )
        }
    }

    public fun getSubCategories(catId: String = CAT_ROOT_UI.id): Flow<List<ECategory>> =
        service.daoECategory().getByParentIdFlow(catId)

    public override fun getSubCategoriesView(catId: String): Flow<List<VCategory>> =
        service.daoVCategory().getByParentIdFlow(catId)

    public override fun getCategory(catId: String): Flow<VCategory> =
        service.daoVCategory().getCategory(catId)

    public fun getLastModDateCategory(): Long = service.daoECategory().getLastModDate()

    public override fun searchCategories(text: String): Flow<List<VCategory>> =
        service.daoVCategory().find("%$text%")

    // endregion

    // region Dishes
    public override fun getDishById(dishId: String): Flow<VDish> =
        service.daoVDish().getById(dishId = dishId)

    public override fun saveDishes(value: List<ResDishItem>): Job {
        return scope.launch(workContext) {
            service.daoEDish().upsert(
                value.map {
                    EDish(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        image = it.image,
                        oldPrice = it.oldPrice,
                        price = it.price,
                        rating = it.rating,
                        commentsCount = it.commentsCount,
                        likes = it.likes,
                        category = it.category,
                        active = it.active,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                        favorite = false
                    )
                }
            )
        }
    }

    public override fun getDishesPagingSource(
        catId: String,
        sortBy: SortBy,
        sortOrder: Boolean
    ): PagingSource<Int, VDish> {
        val d = when (sortOrder) {
            false -> "ASC"
            true -> "DESC"
        }
        val m = when (sortBy) {
            SortBy.Alphabetically -> "name"
            SortBy.Popularity -> "likes"
            SortBy.Rating -> "rating"
        }

        val q = "SELECT DISTINCT * FROM VDish WHERE active=1 AND category='$catId' ORDER BY $m $d"
        logd("invoke getDishes query = $q")
        return service.daoVDish().getByParentIdViewPagingSource(SimpleSQLiteQuery(q))
    }

    public override fun getDishes(
        catId: String,
        sortBy: SortBy,
        sortOrder: Boolean
    ): Flow<List<VDish>> {
        val d = when (sortOrder) {
            false -> "ASC"
            true -> "DESC"
        }
        val m = when (sortBy) {
            SortBy.Alphabetically -> "name"
            SortBy.Popularity -> "likes"
            SortBy.Rating -> "rating"
        }

        val q = "SELECT DISTINCT * FROM VDish WHERE active=1 AND category='$catId' ORDER BY $m $d"
        logd("invoke getDishes query = $q")
        return service.daoVDish().getByParentIdDishView(SimpleSQLiteQuery(q))
    }

    public override fun getLastModDateDishes(): Long = service.daoEDish().getLastModDate()

    public override fun searchDishes(text: String): Flow<List<VDish>> =
        service.daoVDish().find("%$text%")
    // endregion

    // region Search history
    public override fun getSearchHistory(): Flow<List<String>> =
        service.daoESearchHistory().getHistory()

    public override fun addSearchHistoryItem(value: String): Job {
        return scope.launch(workContext) {
            service.runInTransaction {
                service.daoESearchHistory().cleanOldItems()
                service.daoESearchHistory().upsert(
                    listOf(ESearchHistory(id = value))
                )
            }
        }
    }

    public override fun delete(id: String): Job {
        return scope.launch(workContext) {
            service.daoESearchHistory().delete(id)
        }
    }
    //endregion

    //region Recommendation dishes
    public override fun saveRecommendationDishes(value: Success<Set<String>>): Job {
        return scope.launch(workContext) {
            value.payload?.let {
                service.runInTransaction {
                    service.daoEDishesRecommendation().delete()
                    service.daoEDishesRecommendation().upsert(
                        it.map { item ->
                            EDishRecommendation(id = item)
                        }.toImmutableList()
                    )
                }
            }
        }
    }

    public fun cleanRecommendationDishes(): Job {
        logd("Invoke cleanRecommendationDishes()")
        return scope.launch(workContext) {
            service.daoEDishesRecommendation().delete()
        }
    }

    public override fun getAllRecommendationDishes(): Flow<List<VDish>> =
        service.daoVDish().getAllRecommendation()

    public override fun getAllBestDishes(minRating: Double): Flow<List<VDish>> =
        service.daoVDish().getAllBest(minRating)

    public override fun getAllPopularDishes(): Flow<List<VDish>> =
        service.daoVDish().getAllPopular()
    //endregion

    //region Favorite dishes
/*    public fun saveFavoriteDishes(value: Set<ResFavoriteItem>): Job {
        return scope.launch(workContext) {
            service.runInTransaction {
                //todo
                //  service.dishDao().upsert(
                // value.payload.map { item -> DishFavorite(id = item) }.toImmutableList()
                //  )
            }
        }
    }*/

    public override fun toggleFavoriteDish(id: String, isFavorite: Boolean): Job {
        return scope.launch(workContext) {
            service.runInTransaction {
                service.daoEDish().getById(id).let {
                    service.daoEDish().upsert(
                        listOf(it.copy(favorite = isFavorite))
                    )
                }
            }
        }
    }

    public override fun getAllFavoriteDishesPagingSource(): PagingSource<Int, VDish> =
        service.daoVDish().getAllFavoritesPagingSource()

    //endregion

    //region Reviews dishes
    public override fun saveReviews(value: Success<List<ResReviewsItem>>): Job {
        return scope.launch(workContext) {
            value.payload?.let { payload ->
                service.daoEReviews().upsert(
                    payload.map {
                        EReviews(
                            id = it.id,
                            dishId = value.infoLowLevel.httpHeaders[FOR_DATA] ?: "",
                            author = it.author,
                            text = it.text,
                            date = it.date,
                            rating = it.rating,
                            active = it.active,
                            createdAt = it.createdAt,
                            updatedAt = it.updatedAt
                        )
                    }
                )
            }
        }
    }

    public override fun saveReviews(value: List<ResReviewsItem>, dishId: String): Job {
        return scope.launch(workContext) {
            service.daoEReviews().upsert(
                value.map {
                    EReviews(
                        id = it.id,
                        dishId = dishId,
                        author = it.author,
                        text = it.text,
                        date = it.date,
                        rating = it.rating,
                        active = it.active,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            )
        }
    }

    public override fun getReviewsByDish(dishId: String): Flow<List<EReviews>> =
        service.daoEReviews().getReviewsByDish(dishId)
    //endregion

    //region Cart
    public override fun getCartFlow(oe: MutableSharedFlow<List<CartItemJoined>>): Job =
        scope.launch(workContext) {
            oe.emitAll(service.daoECart().getCartFlow())
        }

    public override fun getCart(): List<CartItemJoined> = service.daoECart().getCart()

    public override fun saveCart(value: List<ECartItem>): Job = scope.launch(workContext) {
        service.runInTransaction {
            service.daoECart().upsert(value)
        }
    }

    public override fun saveCartFromServer(value: List<ECartItem>): Job =
        scope.launch(workContext) {
            service.runInTransaction {
                service.daoECart().delete()
                service.daoECart().upsert(value)
            }
        }

    public override fun removeCartItem(dishId: String): Job = scope.launch(workContext) {
        service.daoECart().delete(id = dishId).let {
            //  if (it == 0) throw AppException(errors.E_NOT_FOUND().toString())
        }
    }

    public override fun incrementCartItem(dishId: String) {
        service.daoECart().getCartItem(id = dishId).let {
            if (it.isNotEmpty()) {
                service.daoECart()
                    .upsert(listOf(it.first().copy(amount = it.first().amount.inc()).asECartItem()))
            } else {
                throw AppException(errors.E_NOT_FOUND().toString())
            }
        }
    }

    public override fun decrementCartItem(dishId: String) {
        service.daoECart().getCartItem(id = dishId).let {
            if (it.isNotEmpty()) {
                service.daoECart()
                    .upsert(
                        listOf(
                            it.first().copy(amount = it.first().amount.decZero()).asECartItem()
                        )
                    )
            } else {
                throw AppException(errors.E_NOT_FOUND().toString())
            }
        }
    }
    //endregion

    // region Orders
    public override fun saveOrdersWithDishes(value: List<EOrderWithDishes>): Job {
        return scope.launch(workContext) {
            service.runInTransaction {
                // Save parents
                service.daoEOrder().upsert(value.map(EOrderWithDishes::order))

                // Save children
                value.forEach {
                    service.daoEOrderDish().upsert(it.dishes)
                }
            }
        }
    }

    public override fun getOrdersWithDishes(): Flow<List<Order>> =
        service.daoEOrder().getOrdersWithDishes()

    public override fun getOrderWithDishes(orderId: String): Flow<Order> =
        service.daoEOrder().getOrderWithDishes(orderId)

    public override fun saveOrdersStatuses(value: List<EOrderStatus>): Job {
        return scope.launch(workContext) {
            service.daoEOrderStatus().upsert(value)
        }

    }
    // endregion
}

