package com.example.mycode_lm_ng.repository

import android.graphics.Movie
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mycode_lm_ng.api.ApiService
import com.example.mycode_lm_ng.api.model.OrderData
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val apiService: ApiService) {

    fun getDataFromApi(): Observable<List<OrderData>/*IpAddress*/> = apiService.getJsonResponse()

    @Database(entities = arrayOf(Order::class), version = 1)

    abstract class AppDatabase : RoomDatabase() {

        abstract fun orderDao(): OrderDao

    }
}