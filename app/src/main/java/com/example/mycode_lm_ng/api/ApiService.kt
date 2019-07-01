package com.example.mycode_lm_ng.api

import retrofit2.http.GET
import com.example.mycode_lm_ng.api.model.OrderData
import io.reactivex.Observable

interface ApiService {

    @GET("/deliveries")
    fun getJsonResponse(): Observable<List<OrderData>/*IpAddress*/>
}