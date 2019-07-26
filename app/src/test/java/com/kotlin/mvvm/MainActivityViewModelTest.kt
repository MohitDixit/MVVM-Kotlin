package com.kotlin.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.ui.main.MainActivityViewModel
import com.kotlin.mvvm.util.Utils
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.io.IOException


class MainActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainActivityViewModel: MainActivityViewModel

    @Mock
    lateinit var utils: Utils

    @Mock
    lateinit var repository: OrderListRepository

    private val offset: Int = 0
    private val limit : Int = 20


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = mock(OrderListRepository::class.java)
        utils = mock(Utils::class.java)
        this.mainActivityViewModel = MainActivityViewModel(repository, utils)
    }

    @Test
    fun fetchRepositories_API_success() {

        Runnable {
            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(loadJSONFromAssets(), listType)
            Mockito.`when`(this.repository.getDataFromApi(offset, limit)).thenAnswer {
                return@thenAnswer Single.just(orderList)
            }

            this.mainActivityViewModel.loadOrderList(offset, limit, isFromDB = false)

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)
            verify(mainActivityViewModel).loadOrderList(offset, limit, isFromDB = false)
        }
    }

    @Test
    fun fetchRepositories_DB_success() {

        Runnable {
            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(loadJSONFromAssets(), listType)
            Mockito.`when`(this.repository.getDataFromApi(offset, limit)).thenAnswer {
                return@thenAnswer Single.just(orderList)
            }

            this.mainActivityViewModel.loadOrderList(offset, limit, isFromDB = true)

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)
            verify(mainActivityViewModel).loadOrderList(offset, limit, isFromDB = true)
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