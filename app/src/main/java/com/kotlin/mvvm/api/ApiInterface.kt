package com.kotlin.mvvm.api

import com.kotlin.mvvm.model.OrderData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/deliveries")
    suspend fun getJsonResponse(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<List<OrderData>>
}