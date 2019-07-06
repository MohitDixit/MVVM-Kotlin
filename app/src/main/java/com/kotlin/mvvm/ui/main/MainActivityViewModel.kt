package com.kotlin.mvvm.ui.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository
) :
    ViewModel() {

    var textDescription: String? = null
    var imageUrl: String? = null

    var orderListResult: MutableLiveData<List<OrderData>> = MutableLiveData()
    var orderListError: MutableLiveData<String> = MutableLiveData()
    var orderListLoader: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var disposableObserver: DisposableObserver<List<OrderData>>

    fun orderListResult(): LiveData<List<OrderData>> {
        return orderListResult
    }

    fun orderListError(): LiveData<String> {
        return orderListError
    }

    fun orderListLoader(): LiveData<Boolean> {
        return orderListLoader
    }

    fun setOrderValue(orderData: OrderData) {
        this.imageUrl = orderData.imageUrl
        this.textDescription = orderData.description
    }

    fun loadOrderList(offset: Int, limit: Int) {

        disposableObserver = object : DisposableObserver<List<OrderData>>() {
            override fun onComplete() {

            }

            override fun onNext(orders: List<OrderData>) {
                orderListResult.postValue(orders)
                orderListLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                orderListError.postValue(e.message)
                orderListLoader.postValue(false)
            }
        }

        orderListRepository.getOrderList(offset, limit)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(400, TimeUnit.MILLISECONDS)
            .subscribe(disposableObserver)
    }

}