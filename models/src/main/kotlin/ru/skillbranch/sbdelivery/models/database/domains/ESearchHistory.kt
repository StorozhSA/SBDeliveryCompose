package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable
import java.util.*

@Entity(tableName = "history")
public data class ESearchHistory(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Long = Date().time
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10340001000001L
    }
}

@Dao
public interface IDaoESearchHistory : IRoomDao<ESearchHistory> {

    @Query("SELECT COUNT(id) FROM history")
    public fun recordsCount(): Int

    @Query("SELECT id FROM history ORDER BY created_at DESC LIMIT 5")
    public fun getHistory(): Flow<List<String>>

    @Transaction
    @Query("DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY created_at DESC LIMIT 5)")
    public fun cleanOldItems()

    @Transaction
    @Query("DELETE FROM history WHERE id =:id")
    public fun delete(id: String)

    @Transaction
    public fun upsert(obj: List<ESearchHistory>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
