package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable

@Entity(tableName = "reviews")
public data class EReviews(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "dishId")
    val dishId: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10340000000001L
    }
}

@Dao
public interface IDaoEReviews : IRoomDao<EReviews> {

    @Query("SELECT * FROM reviews WHERE dishId =:dishId ORDER BY `date` DESC")
    public fun getReviewsByDish(dishId: String): Flow<List<EReviews>>

    @Query("DELETE FROM reviews")
    public fun delete()

    @Transaction
    public fun upsert(obj: List<EReviews>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
