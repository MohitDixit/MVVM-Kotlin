package com.kotlin.mvvm.repository

import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.util.Utils
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.schedulers.Schedulers
import io.reactivex.Completable


@Singleton
class OrderListRepository @Inject constructor(
    private val apiInterface: ApiInterface,
    private val orderDao: OrderDao,
    private val utils: Utils
) {

    fun getOrderList(offset: Int, limit: Int, isFromDB: Boolean): Observable<List<OrderData>> {
        val hasConnection = utils.isConnectedToInternet()
        var observableFromApi: Observable<List<OrderData>>? = null
        if (!isFromDB) {
            if (hasConnection) {
                observableFromApi = getDataFromApi(offset, limit)
            }
        }
        val observableFromDb = getOrderListFromDb(offset, limit)

        return if (!isFromDB && hasConnection) Observable.concatArrayEager(observableFromApi, observableFromDb)
        else observableFromDb

    }

    internal fun getDataFromApi(offset: Int, limit: Int): Observable<List<OrderData>> {
        return apiInterface.getJsonResponse(offset, limit)
            .doAfterNext {
                if (it.isNotEmpty()) {
                    orderDao.insert(it)
                }
            }
    }

    private fun getOrderListFromDb(offset: Int, limit: Int): Observable<List<OrderData>> {
        return orderDao.getAll(offset, limit)
            .toObservable()

    }

    fun getEmptyDb() {
        Completable.fromAction {
            orderDao.emptyTable()
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}