package com.kotlin.mvvm.repository


import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.util.Utils
import io.reactivex.Observable
import timber.log.Timber
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
        val observableFromDb = getOrderListFromDb(/*limit, offset*/)

        return if (hasConnection) Observable.concatArrayEager(observableFromApi, observableFromDb)
        else observableFromDb
    }


    fun getDataFromApi(offset: Int, limit: Int): Observable<List<OrderData>> {
        return apiInterface.getJsonResponse(offset, limit)
            .doAfterNext() {
                Timber.e(it.size.toString())
                for (item in it) {
                    OrderDao.insert(item)
                }
            }
    }

    private fun getOrderListFromDb(/*limit: Int, offset: Int*/): Observable<List<OrderData>> {
        return OrderDao.getAll(/*limit, offset*/)
            .toObservable()
            .doAfterNext() {
                Timber.e(it.size.toString())
                Log.e("list has size", it.size.toString())
            }
    }

    @Database(entities = arrayOf(OrderData::class), version = 2)

    abstract class AppDatabase : RoomDatabase() {

        abstract fun orderDao(): OrderDao

    }


}