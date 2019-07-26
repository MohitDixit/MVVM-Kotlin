package com.kotlin.mvvm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    val utils: Utils
) :
    ViewModel() {

    var textDescription: String? = null
    var imageUrl: String? = null
    var isLoading: Boolean = false
    var orderData= OrderData()

    var orderListResult: MutableLiveData<List<OrderData>> = MutableLiveData()
    var orderListError: MutableLiveData<String> = MutableLiveData()
    var orderListLoader: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var disposableObserver: DisposableSingleObserver<List<OrderData>>


    fun orderListResult(): LiveData<List<OrderData>> {
        return orderListResult
    }

    fun orderListError(): LiveData<String> {
        return orderListError
    }

    fun setOrderValue(orderData: OrderData) {
        this.imageUrl = orderData.imageUrl
        this.textDescription = orderData.description + " at " + orderData.location?.address
        this.orderData = orderData
    }

    fun orderListLoader(): LiveData<Boolean> {
        return orderListLoader
    }

    fun deleteOrderDB() {
        orderListRepository.getEmptyDb()
    }

    fun insertAfterPullToRefresh(list: List<OrderData>) {
        orderListRepository.insertAfterPullToRefresh(list)
    }

    fun loadOrderList(offset: Int, limit: Int, isFromDB: Boolean) {

        disposableObserver = object : DisposableSingleObserver<List<OrderData>>() {
            override fun onSuccess(orders: List<OrderData>) {
                orderListResult.postValue(orders)
                orderListLoader.postValue(false)

                if (isFromDB && orders.isEmpty() && !isLoading) {
                    if (utils.isConnectedToInternet()) {
                        isLoading = true

                        loadOrderList(offset, limit, false)
                    }
                } else if (orders.isNotEmpty()) {
                    isLoading = false
                } else if (!isFromDB && orders.isEmpty() && isLoading) {
                    isLoading = false
                }

            }

            override fun onError(e: Throwable) {
                orderListError.postValue(e.message)
                orderListLoader.postValue(false)
                isLoading = false
            }

        }

        orderListRepository.getOrderList(offset, limit, isFromDB)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableObserver)
    }

}