package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable

@Entity(tableName = "cart")
public data class ECartItem(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "amount")
    val amount: Int = 0,
    @ColumnInfo(name = "price")
    val price: Int = 0
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10000300000004L
    }
}

public data class CartItemJoined(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "amount")
    val amount: Int = 0,
    @ColumnInfo(name = "price")
    val price: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "image")
    val image: String = ""
) : IId<String>, Serializable {
    public companion object {
        private const val serialVersionUID = 10000400000004L
    }
}

@Dao
public interface IDaoECart : IRoomDao<ECartItem> {
    @Query("SELECT  count(c.id) FROM cart as c")
    public fun count(): Int

    @Query("SELECT  c.id, c.amount, c.price, d.name, d.image FROM cart AS c LEFT JOIN dishes AS d ON c.id=d.id WHERE c.amount > 0 ORDER BY d.name ASC")
    public fun getCartFlow(): Flow<List<CartItemJoined>>

    @Query("SELECT  c.id, c.amount, c.price, d.name, d.image FROM cart AS c LEFT JOIN dishes AS d ON c.id=d.id WHERE c.amount > 0 ORDER BY d.name ASC")
    public fun getCart(): List<CartItemJoined>

    @Query("SELECT DISTINCT  c.id, c.amount, c.price, d.name, d.image FROM cart AS c LEFT JOIN dishes AS d ON c.id=d.id WHERE c.id = :id")
    public fun getCartItem(id: String): List<CartItemJoined>

    @Query("DELETE FROM cart")
    public fun delete(): Int

    @Query("DELETE FROM cart WHERE id=:id")
    public fun delete(id: String): Int

    @Transaction
    public fun upsert(obj: List<ECartItem>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
