package com.kotlin.mvvm.ui.main


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.reactivex.disposables.CompositeDisposable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.AndroidInjection
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.model.OrderData
import com.kotlin.mvvm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject
import androidx.recyclerview.widget.DividerItemDecoration
import com.kotlin.mvvm.R
import com.kotlin.mvvm.ui.adapter.OrderAdapter
import com.kotlin.mvvm.util.PaginationScrollListener

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val compositeDisposable by lazy { CompositeDisposable() }
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var orderAdapter = OrderAdapter(ArrayList())
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
    @Inject
    lateinit var utils: Utils

    lateinit var recyclerView: RecyclerView

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var offset: Int = 0
    private var isRefresh: Boolean = false
    private var isFromDB: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)
        initDataBinding()
        loadData(true)
    }

    private fun initDataBinding() {
        val activityMainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityViewModel =
            ViewModelProviders.of(this, mainActivityViewModelFactory).get(
                MainActivityViewModel::class.java
            )
        activityMainBinding.mainActivityViewModel = mainActivityViewModel
        setUpViews()
    }

    private fun setUpViews() {
        recyclerView = orderListView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        orderAdapter.setViewModel(mainActivityViewModel)

        mainActivityViewModel.orderListResult().observe(this,
            Observer<List<OrderData>> {

                if (it != null && it.isNotEmpty()) {
                    isLoading = false
                    orderAdapter.removeDummyItem()
                    if (isRefresh) {
                        mainActivityViewModel.deleteOrderDB()
                        mainActivityViewModel.insertAfterPullToRefresh(it)
                        isLastPage = false
                    }
                    val itemPosition = orderAdapter.itemCount
                    recyclerView.adapter = orderAdapter
                    orderAdapter.addOrders(it, isRefresh)
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
                } else if (isFromDB && it.isEmpty()) {
                    isFromDB = false
                } else if (!isFromDB && it.isEmpty()) {
                    isLoading = false
                    showNoOrderViews(true)
                    progressBar.visibility = View.GONE
                    orderAdapter.removeDummyItem()
                }
            })

        mainActivityViewModel.orderListError().observe(this, Observer<String> {
            if (it != null) {
                showErrorAlert()
                isLoading = false
            }
        })

        mainActivityViewModel.orderListLoader().observe(this, Observer<Boolean> {
            if (it == false) {
                mSwipeRefreshLayout?.isRefreshing = false
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

        mSwipeRefreshLayout = swipe_container as SwipeRefreshLayout
        mSwipeRefreshLayout?.setOnRefreshListener(this)
        mSwipeRefreshLayout?.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
    }

    private fun loadData(isFromDB: Boolean) {
        this.isFromDB = isFromDB
        if (!isLoading) {
            isLoading = true
            if (orderAdapter.itemCount == 0) {
                progressBar.visibility = View.VISIBLE
            } else progressBar.visibility = View.GONE

            val coRoutineScope = CoroutineScope(Job() + Dispatchers.Main)
            if (isRefresh) offset = 0 else offset = orderAdapter.itemCount
            coRoutineScope.launch {
                mainActivityViewModel.loadOrderList(
                    offset
                    , resources.getInteger(R.integer.limit), isFromDB
                )
            }
        }
    }

    override fun onRefresh() {
        if (utils.isConnectedToInternet()) {
            isRefresh = true
            loadData(false)
        } else {
            mSwipeRefreshLayout?.isRefreshing = false
            utils.showNetworkAlert(this)
        }
    }

    private fun showNoOrderViews(showToast: Boolean) {
        if (orderAdapter.itemCount == 0) {
            noOrderTextView.visibility = View.VISIBLE
            retry_btn.visibility = View.VISIBLE
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
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.error_retry_string))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                isRefresh = false
                orderAdapter.removeDummyItem()
                if (orderAdapter.itemCount == 0) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    recyclerView.post {
                        orderAdapter.addDummyItem()
                    }
                }
                loadData(false)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
                showNoOrderViews(false)
                orderAdapter.removeDummyItem()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.app_name))
        alert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}

