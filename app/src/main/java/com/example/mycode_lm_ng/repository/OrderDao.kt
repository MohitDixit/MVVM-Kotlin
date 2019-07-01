package com.example.mycode_lm_ng.repository

import android.graphics.Movie
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.google.android.gms.tasks.Task
import androidx.room.Delete



@Dao

interface OrderDao {



    @Query("SELECT * FROM `order`")

    fun getAll(): List<Order>



    @Insert

    fun insert(item: Order)

    @Delete
    fun delete(item: Order)

    @Query("DELETE FROM `order`")
    fun nukeTable()
}