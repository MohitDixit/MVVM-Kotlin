package com.kotlin.mvvm.repository

import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.schedulers.Schedulers
import io.reactivex.Completable
import io.reactivex.Single


@Singleton
class OrderListRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val orderDao: OrderDao,
    private val utils: Utils
) {

    fun getOrderList(offset: Int, limit: Int, isFromDB: Boolean): Single<List<OrderData>> {
        val hasConnection = utils.isConnectedToInternet()
        var observableFromApi: Single<List<OrderData>>? = null
        if (!isFromDB) {
            if (hasConnection) {
                observableFromApi = getDataFromApi(offset, limit)
            }
        }
        val observableFromDb = getOrderListFromDb(offset, limit)

        return (if (!isFromDB && hasConnection) observableFromApi
        else observableFromDb)!!

    }

    internal fun getDataFromApi(offset: Int, limit: Int): Single<List<OrderData>> {
        return apiInterface.getJsonResponse(offset, limit).doAfterSuccess {
            if (it.isNotEmpty()) {
                orderDao.insert(it)
            }
        }
    }

    private fun getOrderListFromDb(offset: Int, limit: Int): Single<List<OrderData>> {
        return orderDao.getAll(offset, limit)
    }

    fun getEmptyDb() {
        Completable.fromAction(orderDao::emptyTable)
            .subscribeOn(Schedulers.single())
            .subscribe()
    }

    fun insertAfterPullToRefresh(list: List<OrderData>) {
        Completable.fromRunnable {
            orderDao.insert(list)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}