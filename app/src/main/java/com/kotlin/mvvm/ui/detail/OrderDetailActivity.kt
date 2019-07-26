package com.kotlin.mvvm.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.R
import com.kotlin.mvvm.model.OrderData
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.databinding.OrderDetailBinding
import com.kotlin.mvvm.ui.main.MainActivityViewModel
import com.kotlin.mvvm.ui.main.MainActivityViewModelFactory

class OrderDetailActivity : AppCompatActivity() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var orderDetailActivityViewModel: OrderDetailActivityViewModel
    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
    @Inject
    lateinit var apiInterface: ApiInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_detail)
        AndroidInjection.inject(this)
        initDataBinding()
    }

    private fun initDataBinding() {
        val orderDetailBinding: OrderDetailBinding = setContentView(this, R.layout.order_detail)
        mainActivityViewModel =
            ViewModelProviders.of(this, mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
        orderDetailActivityViewModel =
            ViewModelProviders.of(this, mainActivityViewModelFactory).get(OrderDetailActivityViewModel::class.java)
        orderDetailBinding.mainActivityViewModel = mainActivityViewModel
        setUpViews(orderDetailBinding)
    }

    private fun setUpViews(orderDetailBinding: OrderDetailBinding) {
        val toolbar = orderDetailBinding.toolbar
        toolbar.title = this.getString(R.string.order_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val orderData = intent.extras?.get(this.getString(R.string.order_list)) as List<OrderData>
        mainActivityViewModel.setOrderValue(orderData[0])
        orderDetailActivityViewModel.setOrderValue(orderData[0])
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}