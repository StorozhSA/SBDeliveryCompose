package ru.skillbranch.common.database

import androidx.room.*

public interface IRoomDao<T : Any> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public fun insert(obj: List<T>): List<Long>

    @Transaction
    @Update
    public fun update(obj: List<T>)

    @Transaction
    @Delete
    public fun delete(obj: T)

    @Transaction
    @Delete
    public fun delete(obj: List<T>): Int
}
