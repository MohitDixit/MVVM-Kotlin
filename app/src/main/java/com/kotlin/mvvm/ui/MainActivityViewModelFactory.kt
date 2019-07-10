package com.kotlin.mvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.repository.OrderDao
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject

class MainActivityViewModelFactory @Inject constructor(
     private val apiInterface: ApiInterface, private val utils: Utils, private val orderDao: OrderDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            val orderListRepository = OrderListRepository(apiInterface, orderDao, utils)
            return MainActivityViewModel(orderListRepository, utils) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}