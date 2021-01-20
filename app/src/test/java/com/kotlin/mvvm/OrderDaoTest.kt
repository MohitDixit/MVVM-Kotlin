package com.kotlin.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.repository.OrderDao
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.ui.main.MainActivityViewModel
import com.kotlin.mvvm.util.Utils
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.IOException
import javax.inject.Inject


class OrderDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    @Inject
    lateinit var orderDao: OrderDao

    @Mock
    lateinit var repository: OrderListRepository

    @Mock
    lateinit var utils: Utils

    private val offset: Int = 0
    private val limit : Int = 20

    private lateinit var mainActivityViewModel: MainActivityViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        orderDao = Mockito.mock(OrderDao::class.java)
        repository = Mockito.mock(OrderListRepository::class.java)
        mainActivityViewModel = MainActivityViewModel(repository, utils)

    }

    @Test
    @Throws(Exception::class)
    fun insert() {

        Runnable {

            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(loadJSONFromAssets(), listType)
            runBlockingTest {
                Mockito.`when`(repository.getDataFromApi(offset, limit)).thenAnswer {
                    return@thenAnswer Single.just(orderList)
                }
            }

            orderDao.insert(orderList)

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)

            verify(orderDao).insert(orderList)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAll() {

       Runnable {

           val listType = object : TypeToken<List<OrderData>>() {
           }.type

           val orderList: List<OrderData> = Gson().fromJson(loadJSONFromAssets(), listType)
           Mockito.`when`(this.repository.getOrderListFromDb(offset, limit)).thenAnswer {
               return@thenAnswer Single.just(orderList)
           }

           orderDao.getAll(offset, limit)

           Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
           Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)

           verify(orderDao).getAll(offset, limit)
       }
    }

    @Test
    @Throws(Exception::class)
    fun empty() {

        Runnable {

            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(loadJSONFromAssets(), listType)
            Mockito.`when`(this.repository.getOrderListFromDb(offset, limit)).thenAnswer {
                return@thenAnswer Single.just(orderList)
            }

            orderDao.emptyTable()

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(0, this.mainActivityViewModel.orderListResult.value?.size)

            verify(orderDao).emptyTable()
        }

    }

    fun loadJSONFromAssets(): String? {
        var json: String? = null
        try {
            val classLoader = this.javaClass.classLoader
            val inputStream = classLoader?.getResourceAsStream("mockrepojson.json")
            val size = inputStream?.available()
            val buffer = size?.let { ByteArray(it) }
            inputStream?.read(buffer)
            inputStream?.close()

            json = buffer?.let { String(it, Charsets.UTF_8) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}