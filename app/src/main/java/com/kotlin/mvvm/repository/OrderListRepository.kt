package com.kotlin.mvvm.repository

import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.util.Utils
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OrderListRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val OrderDao: OrderDao,
    private val utils: Utils
) {


    fun getOrderList(offset: Int, limit: Int): Observable<List<OrderData>> {
        val hasConnection = utils.isConnectedToInternet()
        var observableFromApi: Observable<List<OrderData>>? = null
        if (hasConnection) {
            observableFromApi = getDataFromApi(offset, limit)
        }
        val observableFromDb = getOrderListFromDb()

        return if (hasConnection) Observable.concatArrayEager(observableFromApi, observableFromDb)
        else observableFromDb
    }


    private fun getDataFromApi(offset: Int, limit: Int): Observable<List<OrderData>> {
        return apiInterface.getJsonResponse(offset, limit)
            .doAfterNext {
                for (item in it) {
                    OrderDao.insert(item)
                }
            }
    }

    private fun getOrderListFromDb(): Observable<List<OrderData>> {
        return OrderDao.getAll()
            .toObservable()

    }


}