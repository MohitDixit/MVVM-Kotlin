package com.kotlin.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.repository.OrderListRepository
import com.kotlin.mvvm.ui.MainActivityViewModel
import com.kotlin.mvvm.util.Utils
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.ExecutorService


class MainActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var executor: ExecutorService? = null

    @Mock
    lateinit var utils: Utils

    @Mock
    lateinit var repository: OrderListRepository


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = mock(OrderListRepository::class.java)
        utils = mock(Utils::class.java)
        executor = mock(ExecutorService::class.java)
        this.mainActivityViewModel = MainActivityViewModel(repository, utils)
    }


    @Test
    fun fetchRepositories_API_success() {

        Runnable {
            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(utils), listType)
            Mockito.`when`(this.repository.getDataFromApi(BuildConfig.offset_mock, BuildConfig.limit)).thenAnswer {
                return@thenAnswer Observable.just(orderList)
            }

            val obs: androidx.lifecycle.Observer<List<OrderData>> =
                mock(androidx.lifecycle.Observer::class.java) as androidx.lifecycle.Observer<List<OrderData>>

            this.mainActivityViewModel.orderListResult.observeForever(obs)

            this.mainActivityViewModel.loadOrderList(BuildConfig.offset_mock, BuildConfig.limit, isFromDB = false)

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)
            verify(mainActivityViewModel).loadOrderList(BuildConfig.offset_mock, BuildConfig.limit, isFromDB = false)
        }
    }

    @Test
    fun fetchRepositories_DB_success() {

        Runnable {
            val listType = object : TypeToken<List<OrderData>>() {
            }.type

            val orderList: List<OrderData> = Gson().fromJson(Utils.loadJSONFromAssets(utils), listType)
            Mockito.`when`(this.repository.getDataFromApi(BuildConfig.offset_mock, BuildConfig.limit)).thenAnswer {
                return@thenAnswer Observable.just(orderList)
            }

            val obs: androidx.lifecycle.Observer<List<OrderData>> =
                mock(androidx.lifecycle.Observer::class.java) as androidx.lifecycle.Observer<List<OrderData>>

            this.mainActivityViewModel.orderListResult.observeForever(obs)

            this.mainActivityViewModel.loadOrderList(BuildConfig.offset_mock, BuildConfig.limit, isFromDB = true)

            Assert.assertNotNull(this.mainActivityViewModel.orderListResult.value)
            Assert.assertEquals(orderList, this.mainActivityViewModel.orderListResult.value)
            verify(mainActivityViewModel).loadOrderList(BuildConfig.offset_mock, BuildConfig.limit, isFromDB = true)
        }
    }


}