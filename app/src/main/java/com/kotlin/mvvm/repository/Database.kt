package com.kotlin.mvvm.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotlin.mvvm.api.model.OrderData

@Database(entities = [OrderData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
