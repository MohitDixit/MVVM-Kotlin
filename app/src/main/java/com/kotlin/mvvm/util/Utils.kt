package com.kotlin.mvvm.util

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class Utils @Inject constructor(private val app: Application) {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Suppress("DEPRECATION", "NAME_SHADOWING")
    fun isConnectedToInternet(): Boolean {
        val connectivity = app.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val networkInfo: Network?
        val info: Array<Network>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkInfo = connectivity.activeNetwork
            info = connectivity.allNetworks
            for (i in info.indices) {
                if (info[i] == networkInfo) {
                    return true
                }
            }
        } else {
            val networkInfo = connectivity.activeNetworkInfo
            info = connectivity.allNetworks
            for (i in info.indices) {
                if (info[i] == networkInfo) {
                    return true
                }
            }
        }

        return false
    }

    fun buildGsonConverterFactory(): GsonConverterFactory {
        val gson = Gson()
        return GsonConverterFactory.create(gson)
    }

    fun buildRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

}