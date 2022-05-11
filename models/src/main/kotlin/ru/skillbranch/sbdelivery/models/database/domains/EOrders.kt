package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao

// region Entities for support Orders
@Entity(tableName = "orders")
public data class EOrder(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "completed")
    val completed: Boolean,
    @ColumnInfo(name = "statusId")
    val statusId: String,
    @ColumnInfo(name = "total")
    val total: Int,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
) : IId<String>, java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10230000000001L
    }
}

@Entity(
    tableName = "orders_dishes",
    primaryKeys = ["orderId", "dishId"],
    foreignKeys = [
        ForeignKey(
            entity = EOrder::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
public data class EOrderDish(
    @ColumnInfo(name = "orderId")
    val orderId: String,
    @ColumnInfo(name = "dishId")
    val dishId: String,
    @ColumnInfo(name = "amount")
    val amount: Int,
    @ColumnInfo(name = "price")
    val price: Int
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10240000000001L
    }
}

public data class EOrderWithDishes(
    @Embedded val order: EOrder,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val dishes: List<EOrderDish>
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10740000000001L
    }
}

@Entity(tableName = "orders_status")
public data class EOrderStatus(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "cancelable")
    val cancelable: Boolean,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10740030500001L
    }
}
// endregion

// region DTO for support Orders
public data class Order(
    @Embedded val order: EOrder,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId",
        entity = EOrderDish::class
    )
    val dishes: List<Dish>,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id",
    )
    val status: EOrderStatus
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10740000500001L
    }
}

public data class Dish(
    val orderId: String,
    val dishId: String,
    val amount: Int,
    val price: Int,
    @Relation(
        parentColumn = "dishId",
        entityColumn = "id",
        entity = EDish::class
    )
    val additionalInfo: AdditionalInfo
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10740400000001L
    }
}

public data class AdditionalInfo(
    val name: String,
    val image: String
) : java.io.Serializable {
    public companion object {
        private const val serialVersionUID = 10740010000001L
    }
}
// endregion


@Dao
public interface IDaoEOrder : IRoomDao<EOrder> {

    @Transaction
    @Query("SELECT * FROM orders ORDER BY `createdAt` DESC")
    public fun getOrders(): Flow<List<EOrder>>

    @Transaction
    @Query("SELECT * FROM orders ORDER BY `createdAt` DESC")
    //@Query("select o.*, od.orderId, od.dishId, od.amount, od.price, d.name, d.image as dish_active  from orders as o left join orders_dishes as od on o.id=od.orderId left join dishes as d on od.dishId=d.id ORDER BY o.createdAt DESC")
    public fun getOrdersWithDishes(): Flow<List<Order>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orders.id = :orderId")
    public fun getOrder(orderId: String): Flow<EOrder>

    @Transaction
    @Query("SELECT * FROM orders WHERE orders.id = :orderId")
    public fun getOrderWithDishes(orderId: String): Flow<Order>

    @Transaction
    public fun upsert(obj: List<EOrder>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@Dao
public interface IDaoEOrderDish : IRoomDao<EOrderDish> {

    @Transaction
    public fun upsert(obj: List<EOrderDish>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@Dao
public interface IDaoEOrderStatus : IRoomDao<EOrderStatus> {

    @Transaction
    public fun upsert(obj: List<EOrderStatus>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }

    @Query("SELECT * FROM orders_status ORDER BY `createdAt` DESC")
    public fun getOrders(): Flow<List<EOrderStatus>>
}



