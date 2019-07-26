package com.kotlin.mvvm.util

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.mvvm.R
import javax.inject.Inject


class Utils @Inject constructor(private val context: Context) {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Suppress("DEPRECATION", "NAME_SHADOWING")
    fun isConnectedToInternet(): Boolean {
        val connectivity = context.getSystemService(
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

    internal fun showNetworkAlert(activity: AppCompatActivity) {

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage(context.getString(R.string.error_no_internet))
            .setCancelable(false)

            .setPositiveButton(context.getString(R.string.connect_wifi)) { _, _ ->
                context.startActivity(
                    Intent(
                        Settings.ACTION_WIFI_SETTINGS
                    ).addFlags(FLAG_ACTIVITY_NEW_TASK)
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