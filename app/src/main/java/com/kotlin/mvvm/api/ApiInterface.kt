package com.kotlin.mvvm.api

import retrofit2.http.GET
import com.kotlin.mvvm.api.model.OrderData
import io.reactivex.Single
import retrofit2.http.Query

interface ApiInterface {

    @GET("/deliveries")
    fun getJsonResponse(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<List<OrderData>>
}