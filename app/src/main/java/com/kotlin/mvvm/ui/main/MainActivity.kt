package com.kotlin.mvvm.ui.main

import android.R.color.*
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.AndroidInjection
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject
import androidx.recyclerview.widget.DividerItemDecoration
import com.kotlin.mvvm.R
import com.kotlin.mvvm.R.color.colorPrimary
import com.kotlin.mvvm.ui.adapter.OrderAdapter
import com.kotlin.mvvm.util.PaginationScrollListener

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val orderAdapter by lazy { OrderAdapter(ArrayList()) }
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
    @Inject
    lateinit var utils: Utils

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var offset: Int = 0
    private var isRefresh: Boolean = false
    private var isFromDB: Boolean = true
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        initDataBinding()
        loadData(true)
    }

    private fun initDataBinding() {
        val activityMainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityViewModel =
            ViewModelProvider(this, mainActivityViewModelFactory).get(
                MainActivityViewModel::class.java
            )
        activityMainBinding.mainViewModel = mainActivityViewModel
        setUpViews()
    }

    private fun setUpViews() {
        recyclerView = orderListView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        mainActivityViewModel.orderListResult().observe(this,
            Observer { orderListData ->

                if (orderListData != null && orderListData.isNotEmpty()) {
                    isLoading = false
                    orderAdapter.removeDummyItem()
                    mainActivityViewModel.insertAfterApiSuccess(orderListData)
                    if (isRefresh) {
                        mainActivityViewModel.deleteOrderDB()
                        mainActivityViewModel.insertAfterPullToRefresh(orderListData)
                        isLastPage = false
                    }
                    val itemPosition = orderAdapter.itemCount
                    recyclerView.adapter = orderAdapter
                    orderAdapter.addOrders(orderListData, isRefresh)
                    if (!isRefresh) {
                        recyclerView.scrollToPosition(itemPosition)
                    }
                    if (!isFromDB) {
                        showNoOrderViews(false)
                    }
                    progressBar.visibility = View.GONE
                } else if (!utils.isConnectedToInternet()) {
                    isLoading = false
                    progressBar.visibility = View.GONE
                    orderAdapter.removeDummyItem()
                    utils.showNetworkAlert(this)
                    showNoOrderViews(false)
                } else if (isFromDB && orderListData.isEmpty()) {
                    isFromDB = false
                } else if (!isFromDB && orderListData.isEmpty()) {
                    isLoading = false
                    showNoOrderViews(true)
                    progressBar.visibility = View.GONE
                    orderAdapter.removeDummyItem()
                }
            })

        mainActivityViewModel.orderListError().observe(this, Observer {
            if (it != null) {
                showErrorAlert()
                isLoading = false
            }
        })

        mainActivityViewModel.orderListLoader().observe(this, Observer {
            if (it == false) {
                mSwipeRefreshLayout.isRefreshing = false
            }

        })

        recyclerView.addOnScrollListener(object :
            PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                if (!isLastPage() && !isLoading()) {
                    isRefresh = false
                    recyclerView.post {
                        orderAdapter.addDummyItem()
                    }
                    loadData(true)
                }
            }
        })

        retry_btn.setOnClickListener {
            if (utils.isConnectedToInternet()) {
                progressBar.visibility = View.VISIBLE
                noOrderTextView.visibility = View.GONE
                retry_btn.visibility = View.GONE
                isRefresh = true
                loadData(false)
            } else {
                utils.showNetworkAlert(this)
            }
        }

        mSwipeRefreshLayout = swipe_container.apply {
            setOnRefreshListener(this@MainActivity)
            setColorSchemeResources(
                colorPrimary,
                holo_green_dark,
                holo_orange_dark,
                holo_blue_dark
            )
        }

    }

    private fun loadData(isFromDB: Boolean) {
        this.isFromDB = isFromDB
        if (!isLoading) {
            isLoading = true
            progressBar.visibility = if (orderAdapter.itemCount == 0) View.VISIBLE else View.GONE
            offset = if (isRefresh) 0 else orderAdapter.itemCount
            mainActivityViewModel.loadOrderList(
                offset,
                resources.getInteger(R.integer.limit),
                isFromDB
            )
        }
    }

    override fun onRefresh() {
        when (utils.isConnectedToInternet()) {
            true -> loadData(false).also { isRefresh = true }
            false -> utils.showNetworkAlert(this).also { mSwipeRefreshLayout.isRefreshing = false }
        }
    }

    private fun showNoOrderViews(showToast: Boolean) {
        if (orderAdapter.itemCount == 0) {
            noOrderTextView.visibility = View.VISIBLE
            retry_btn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            noOrderTextView.visibility = View.GONE
            retry_btn.visibility = View.GONE
            if (showToast) {
                isLastPage = true
                Toast.makeText(this, getString(R.string.no_more_order), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showErrorAlert() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.error_retry_string))
            setCancelable(false)
            setPositiveButton(getString(R.string.proceed)) { _, _ ->
                isRefresh = false
                orderAdapter.removeDummyItem()
                if (orderAdapter.itemCount == 0) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    recyclerView.post { orderAdapter.addDummyItem() }
                }
                loadData(false)
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
                showNoOrderViews(false)
                orderAdapter.removeDummyItem()
            }
            create().apply {
                setTitle(getString(R.string.app_name))
                show()
            }
        }
    }
}

