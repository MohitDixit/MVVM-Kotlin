package com.kotlin.mvvm.repository

import androidx.room.*
import com.kotlin.mvvm.api.model.OrderData
import io.reactivex.Single

@Dao
interface OrderDao {

    @Query("SELECT * FROM `orderdata` limit :limit offset :offset")
    fun getAll(offset: Int, limit: Int): Single<List<OrderData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: OrderData)

    @Query("DELETE FROM `orderdata`")
    fun emptyTable(): Single<Int>
}