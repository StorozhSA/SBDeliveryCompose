package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable

@Entity(tableName = "register")
public data class EUser(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "access_token")
    val accessToken: String,
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10340300000001L
    }
}

@Dao
public interface IDaoEUser : IRoomDao<EUser> {

    @Query("SELECT COUNT(id) FROM register")
    public fun recordsCount(): Int

    @Query("SELECT * FROM register")
    public fun get(): List<EUser>

    @Query("SELECT * FROM register WHERE id =:id")
    public fun get(id: String): List<EUser>

    @Transaction
    @Query("DELETE FROM register")
    public fun delete()

    @Transaction
    public fun upsert(obj: List<EUser>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
