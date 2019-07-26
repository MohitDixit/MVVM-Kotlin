package com.kotlin.mvvm.ui.detail


import androidx.lifecycle.ViewModel
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject

class OrderDetailActivityViewModel @Inject constructor(
    val utils: Utils
) :
    ViewModel() {

    var textDescription: String? = null
    var imageUrl: String? = null
    var orderData= OrderData()

    fun setOrderValue(orderData: OrderData) {
        this.imageUrl = orderData.imageUrl
        this.textDescription = orderData.description + " at " + orderData.location?.address
        this.orderData = orderData
    }
}