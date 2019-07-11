package com.kotlin.mvvm.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val orderListRepository: OrderListRepository,
    val utils: Utils
) :
    ViewModel() {

    var textDescription: String? = null
    var imageUrl: String? = null
    var offset: Int? = 0

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
        this.textDescription = orderData.description + BuildConfig.at_str + orderData.location?.address
    }

    fun setOffset(size: Int) {
        offset = size
    }

    fun deleteOrderDB() {
        orderListRepository.getEmptyDb()
    }

    fun loadOrderList(offset: Int, limit: Int, isFromDB: Boolean) {

        disposableObserver = object : DisposableObserver<List<OrderData>>() {
            override fun onComplete() {

            }

            override fun onNext(orders: List<OrderData>) {
                orderListResult.postValue(orders)
                orderListLoader.postValue(false)

                if (isFromDB && orders.isEmpty()) {
                    if (utils.isConnectedToInternet()) {
                        loadOrderList(offset, limit, false)
                    }
                }
            }

            override fun onError(e: Throwable) {
                orderListError.postValue(e.message)
                orderListLoader.postValue(false)
            }

        }

        orderListRepository.getOrderList(offset, limit, isFromDB)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(BuildConfig.timeout, TimeUnit.MILLISECONDS)
            .subscribe(disposableObserver)
    }

}