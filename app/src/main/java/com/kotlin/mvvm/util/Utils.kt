package com.kotlin.mvvm.util

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kotlin.mvvm.R
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject


class Utils @Inject constructor(private val app: Application) {


    fun buildGsonConverterFactory(): GsonConverterFactory {
        val gson = Gson()
        return GsonConverterFactory.create(gson)
    }

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

    fun buildRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    companion object {
        @JvmStatic
        fun loadJSONFromAssets(utils: Utils): String? {
            var json: String? = null
            try {
                val classLoader = utils.javaClass.classLoader
                val inputStream = classLoader?.getResourceAsStream("mockrepojson.json")
                val size = inputStream?.available()
                val buffer = size?.let { ByteArray(it) }
                inputStream?.read(buffer)
                inputStream?.close()

                json = buffer?.let { String(it, Charsets.UTF_8) }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return json
        }

    }


    internal fun showNetworkAlert(context: Context) {

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(context.getString(R.string.error_no_internet))
            .setCancelable(false)

            .setPositiveButton(context.getString(R.string.connect_wifi)) { _, _ ->
                context.startActivity(
                    Intent(
                        Settings.ACTION_WIFI_SETTINGS
                    )
                )
            }

            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(context.getString(R.string.app_name))
        alert.show()
    }

}