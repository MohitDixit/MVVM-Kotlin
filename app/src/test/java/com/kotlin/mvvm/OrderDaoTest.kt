package com.kotlin.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.repository.OrderDao
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.util.Utils
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import javax.inject.Inject


class OrderDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    @Inject
    lateinit var orderDao: OrderDao

    @Mock
    lateinit var repository: OrderListRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        orderDao = Mockito.mock(OrderDao::class.java)
        repository = Mockito.mock(OrderListRepository::class.java)

    }


    @Test
    @Throws(Exception::class)
    fun insert() {

        val listType = object : TypeToken<List<OrderData>>() {
        }.type

        val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(), listType)
        Mockito.`when`(this.repository.getDataFromApi(BuildConfig.offset_mock, BuildConfig.limit)).thenAnswer {
            return@thenAnswer Observable.just(orderList)
        }

        orderDao.insert(orderList)

        verify(orderDao).insert(orderList)
    }

    @Test
    @Throws(Exception::class)
    fun getAll() {

        val listType = object : TypeToken<List<OrderData>>() {
        }.type

        val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(), listType)
        Mockito.`when`(this.repository.getOrderList(BuildConfig.offset_mock, BuildConfig.limit, true)).thenAnswer {
            return@thenAnswer Observable.just(orderList)
        }

        orderDao.getAll(BuildConfig.offset_mock, BuildConfig.limit)

        verify(orderDao).getAll(BuildConfig.offset_mock, BuildConfig.limit)
    }

    @Test
    @Throws(Exception::class)
    fun empty() {

        val listType = object : TypeToken<List<OrderData>>() {
        }.type

        val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(), listType)
        Mockito.`when`(this.repository.getOrderList(BuildConfig.offset_mock, BuildConfig.limit, true)).thenAnswer {
            return@thenAnswer Observable.just(orderList)
        }

        orderDao.emptyTable()

        verify(orderDao).emptyTable()

    }


}