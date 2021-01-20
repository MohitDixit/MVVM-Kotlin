package com.kotlin.mvvm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    val utils: Utils
) :
    ViewModel() {

    lateinit var textDescription: String
    lateinit var imageUrl: String
    private var isLoading: Boolean = false
    var orderData = OrderData()

    val orderListResult by lazy { MutableLiveData<List<OrderData>>() }
    private val orderListError by lazy { MutableLiveData<Throwable>() }
    private val orderListLoader by lazy { MutableLiveData<Boolean>() }
    private val isListShow by lazy { MutableLiveData<Boolean>() }
    private val isErrorShow by lazy { MutableLiveData<Boolean>() }

    fun orderListResult(): LiveData<List<OrderData>> {
        return orderListResult
    }

    fun orderListError(): LiveData<Throwable> {
        return orderListError
    }

    fun setOrderValue(orderData: OrderData) {
        this.imageUrl = orderData.imageUrl.toString()
        this.textDescription = orderData.description + " at " + orderData.location?.address
        this.orderData = orderData
    }

    fun orderListLoader(): LiveData<Boolean> {
        return orderListLoader
    }

    fun deleteOrderDB() {
        orderListRepository.getEmptyDb()
    }
    fun insertAfterApiSuccess(list: List<OrderData>) {
        orderListRepository.insertAfterApiSuccess(list)
    }

    fun insertAfterPullToRefresh(list: List<OrderData>) {
        orderListRepository.insertAfterPullToRefresh(list)
    }

    fun loadOrderList(offset: Int, limit: Int, isFromDB: Boolean) {

        viewModelScope.launch {
            try {
                var orders: List<OrderData>? = null
                orderListRepository.getDataFromApi(offset, limit).doAfterSuccess {
                    orders = it
                }/*getOrderList(offset, limit, isFromDB)*/
                orderListResult.postValue(orders)
                if (isFromDB && orders!!.isEmpty() && !isLoading) {
                    if (utils.isConnectedToInternet()) {
                        isLoading = true

                        loadOrderList(offset, limit, false)
                    }
                } else if (orders!!.isNotEmpty()) {
                    isLoading = false
                } else if (!isFromDB && orders!!.isEmpty() && isLoading) {
                    isLoading = false
                }
                setViewIndicators(isOK = true)
            } catch (e: Throwable) {
                orderListError.postValue(e)
                setViewIndicators(isOK = false)
            } finally {
                setLoadingIndicators(isLoad = false)
                orderListLoader.postValue(false)

            }
        }
    }

    private fun setViewIndicators(isOK: Boolean) {
        isListShow.postValue(isOK)
        isErrorShow.postValue(!isOK)
    }

    private fun setLoadingIndicators(isLoad: Boolean) {
        isLoading = isLoad
        when (isLoad) {
            true -> isErrorShow.postValue(!isLoad)
        }
    }
}