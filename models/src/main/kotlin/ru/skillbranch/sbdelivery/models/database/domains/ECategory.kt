package ru.skillbranch.sbdelivery.models.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.skillbranch.common.database.IRoomDao
import java.io.Serializable

@Entity(tableName = "category")
public data class ECategory(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "parent")
    val parent: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
) : Comparable<ECategory>, IId<String>, Serializable {
    override fun compareTo(other: ECategory): Int = order - other.order

    public companion object {
        private const val serialVersionUID = 10000000000004L
    }
}

@Dao
public interface IDaoECategory : IRoomDao<ECategory> {

    @Query("SELECT * FROM category WHERE active=1 AND parent=:parentId ORDER BY `order` ASC")
    public fun getByParentIdFlow(parentId: String): Flow<List<ECategory>>

    @Query("SELECT MAX(updated_at) FROM category")
    public fun getLastModDate(): Long

    @Query("DELETE FROM category")
    public fun delete()

    @Transaction
    public fun upsert(obj: List<ECategory>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@DatabaseView(
    """
select  cat.*, count(dishes.id) as is_dish_root from category as cat left join dishes on cat.id=dishes.category group by cat.id 
union 
select '1' as id, 'Акции' as name, 0 as 'order', '' as icon, 'root' as parent, count(dishes.id) > 0  as 'active', 0 as 'created_at', 0 as 'updated_at', count(dishes.id) as is_dish_root from dishes where old_price > 0
"""
)
public data class VCategory(
    //@PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "parent")
    val parent: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "is_dish_root")
    val isDishRoot: Boolean
) : Comparable<VCategory>, IId<String>, Serializable {
    override fun compareTo(other: VCategory): Int = order - other.order

    public companion object {
        private const val serialVersionUID = 10000000000005L
    }
}

@Dao
public interface IDaoVCategory {
    @Query("SELECT * FROM VCategory WHERE active=1 AND parent=:parentId ORDER BY `order` ASC")
    public fun getByParentIdFlow(parentId: String): Flow<List<VCategory>>

    @Query("SELECT * FROM VCategory WHERE UPPER(name) LIKE UPPER(:text) ORDER BY `name` ASC")
    public fun find(text: String): Flow<List<VCategory>>

    @Query("SELECT * FROM VCategory WHERE id=:categoryId LIMIT 1")
    public fun getCategory(categoryId: String): Flow<VCategory>
}
