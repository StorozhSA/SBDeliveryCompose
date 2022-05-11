package ru.skillbranch.sbdelivery.models.database.domains

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable

@Entity(tableName = "dishes")
public data class EDish(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10000000000001L
    }
}

@Dao
public interface IDaoEDish : IRoomDao<EDish> {

    @Query("SELECT * FROM dishes ORDER BY `name` ASC")
    public fun getAll(): List<EDish>

    @Query("SELECT * FROM dishes WHERE id =:id ORDER BY `name` ASC")
    public fun getById(id: String): EDish

    @Query("""SELECT DISTINCT * FROM dishes AS d WHERE d.active=1 AND d.favorite=1 ORDER BY `name` ASC""")
    public fun getAllFavorites(): List<VDish>

    @Query("SELECT MAX(updated_at) FROM dishes")
    public fun getLastModDate(): Long

    @Query("SELECT * FROM dishes WHERE old_price IS NOT NULL")
    public fun isPromotionalDish(): Boolean

    @Query("DELETE FROM dishes")
    public fun delete()

    @Transaction
    public fun upsert(obj: List<EDish>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@DatabaseView(
    """
select 
d.id, 
d.name, 
d.description, 
d.image, 
d.old_price, 
d.price, 
d.rating, 
d.comments_count, 
d.likes, 
d.category, 
d.active, 
d.created_at, 
d.updated_at, 
d.favorite 
from dishes AS d
union 
select 
d.id, 
d.name, 
d.description, 
d.image, 
d.old_price, 
d.price, 
d.rating, 
d.comments_count, 
d.likes, 
'1' as category, 
d.active, 
d.created_at, 
d.updated_at, 
d.favorite 
from dishes AS d where d.old_price > 0"""
)
public data class VDish(
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10000000000002L
    }
}

@Dao
public interface IDaoVDish {

    @RawQuery(observedEntities = [VDish::class])
    public fun getByParentIdViewDataSource(simpleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, VDish>

    @RawQuery(observedEntities = [VDish::class])
    public fun getByParentIdViewPagingSource(simpleSQLiteQuery: SimpleSQLiteQuery): PagingSource<Int, VDish>

    @RawQuery(observedEntities = [VDish::class])
    public fun getByParentIdDishView(simpleSQLiteQuery: SimpleSQLiteQuery): Flow<List<VDish>>

    @Query("SELECT * FROM VDish WHERE UPPER(name) LIKE UPPER(:text) ORDER BY `name` ASC")
    public fun find(text: String): Flow<List<VDish>>

    @Query("""SELECT DISTINCT * FROM VDish AS d LEFT JOIN dishes_recomm AS r ON d.id = r.id WHERE r.id IS NOT NULL AND d.active=1 AND d.category != 1 ORDER BY d.rating""")
    public fun getAllRecommendation(): Flow<List<VDish>>

    @Query("""SELECT DISTINCT * FROM VDish AS d WHERE d.active=1 AND d.category != 1 AND d.rating >= :minRating ORDER BY d.rating LIMIT 10 """)
    public fun getAllBest(minRating: Double): Flow<List<VDish>>

    @Query("""SELECT DISTINCT * FROM VDish AS d WHERE d.id IN (SELECT DISTINCT id FROM VDish AS sd WHERE sd.active=1 AND sd.category != 1 AND sd.image IS NOT NULL ORDER BY sd.likes DESC LIMIT 10) ORDER BY d.likes LIMIT 10""")
    public fun getAllPopular(): Flow<List<VDish>>

    @Query("""SELECT * FROM VDish AS d WHERE d.id=:dishId """)
    public fun getById(dishId: String): Flow<VDish>

    @Query("""SELECT DISTINCT * FROM VDish AS d WHERE d.active=1 AND d.category != 1 AND d.favorite=1 ORDER BY `name` ASC""")
    public fun getAllFavorites(): Flow<List<VDish>>

    @Query("""SELECT DISTINCT * FROM VDish AS d WHERE d.active=1 AND d.category != 1 AND d.favorite=1 ORDER BY `name` ASC""")
    public fun getAllFavoritesPagingSource(): PagingSource<Int, VDish>
}

@Entity(tableName = "dishes_recomm")
public data class EDishRecommendation(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10000000000003L
    }
}

@Dao
public interface IDaoEDishesRecommendation : IRoomDao<EDishRecommendation> {

    @Query("SELECT id FROM dishes_recomm ORDER BY `id` ASC")
    public fun getAll(): Flow<List<String>>

    @Query("DELETE FROM dishes_recomm")
    public fun delete()

    @Transaction
    public fun upsert(obj: List<EDishRecommendation>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
