package com.kotlin.mvvm.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.kotlin.mvvm.R
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.repository.AppDatabase
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject

class MainActivityViewModelFactory @Inject constructor(
    private val app: Application, private val apiInterface: ApiInterface, private val utils: Utils
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            val db =
                Room.databaseBuilder(app, AppDatabase::class.java, app.getString(R.string.order_data)).build()
            val orderListRepository = OrderListRepository(apiInterface, db.orderDao(), utils)
            return MainActivityViewModel(orderListRepository, utils) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}