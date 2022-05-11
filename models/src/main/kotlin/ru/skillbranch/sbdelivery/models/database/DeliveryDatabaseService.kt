package ru.skillbranch.sbdelivery.models.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.common.SingletonHolder
import ru.skillbranch.common.database.IRoomDatabase
import ru.skillbranch.common.database.converters.ConverterQuotedStringVsList
import ru.skillbranch.sbdelivery.models.database.domains.*

public interface IDeliveryDatabaseService : IRoomDatabase {
    public fun daoECart(): IDaoECart
    public fun daoECategory(): IDaoECategory
    public fun daoEDish(): IDaoEDish
    public fun daoEDishesRecommendation(): IDaoEDishesRecommendation
    public fun daoEReviews(): IDaoEReviews
    public fun daoESearchHistory(): IDaoESearchHistory
    public fun daoEUser(): IDaoEUser
    public fun daoVCategory(): IDaoVCategory
    public fun daoVDish(): IDaoVDish
    public fun daoEOrder(): IDaoEOrder
    public fun daoEOrderDish(): IDaoEOrderDish
    public fun daoEOrderStatus(): IDaoEOrderStatus
}

@Database(
    entities = [
        ECartItem::class,
        ECategory::class,
        EDish::class,
        EDishRecommendation::class,
        EReviews::class,
        ESearchHistory::class,
        EUser::class,
        EOrder::class,
        EOrderDish::class,
        EOrderStatus::class
    ],
    views = [
        VCategory::class,
        VDish::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(ConverterQuotedStringVsList::class)
public abstract class DeliveryDatabaseService : RoomDatabase(), IDeliveryDatabaseService {
    public abstract override fun daoECart(): IDaoECart
    public abstract override fun daoECategory(): IDaoECategory
    public abstract override fun daoEDish(): IDaoEDish
    public abstract override fun daoEDishesRecommendation(): IDaoEDishesRecommendation
    public abstract override fun daoEReviews(): IDaoEReviews
    public abstract override fun daoESearchHistory(): IDaoESearchHistory
    public abstract override fun daoEUser(): IDaoEUser
    public abstract override fun daoVCategory(): IDaoVCategory
    public abstract override fun daoVDish(): IDaoVDish
    public abstract override fun daoEOrder(): IDaoEOrder
    public abstract override fun daoEOrderDish(): IDaoEOrderDish
    public abstract override fun daoEOrderStatus(): IDaoEOrderStatus

    public companion object : SingletonHolder<DeliveryDatabaseService, Context>({
        if (it.getSharedPreferences(it.packageName, MODE_PRIVATE).getBoolean("dbIsRAM", false)) {
            Room.inMemoryDatabaseBuilder(it, DeliveryDatabaseService::class.java)
                .build()
        } else {
            Room.databaseBuilder(it, DeliveryDatabaseService::class.java, it.packageName + ".db")
                .build()
        }
    })
}
