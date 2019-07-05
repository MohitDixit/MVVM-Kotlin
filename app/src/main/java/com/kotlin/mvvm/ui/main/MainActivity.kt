package com.kotlin.mvvm.ui.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.reactivex.disposables.CompositeDisposable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.AndroidInjection
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.BuildConfig.*
import com.kotlin.mvvm.R
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.util.Utils
import javax.inject.Inject

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val compositeDisposable by lazy { CompositeDisposable() }
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var orderAdapter = OrderAdapter(ArrayList(), this)
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @Inject
    lateinit var apiInterface: ApiInterface

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var offset: Int = 0
    private var currentPage: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)
        initDataBinding()
        loadData()
    }

    private fun initDataBinding() {

        val activityMainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainActivityViewModel =
            ViewModelProviders.of(this, MainActivityViewModelFactory(this, apiInterface, Utils(this))).get(
                MainActivityViewModel::class.java
            )

        activityMainBinding.mainActivityViewModel = mainActivityViewModel
        setUpViews(activityMainBinding)

    }

    private fun setUpViews(activityMainBinding: ActivityMainBinding) {

        val toolbar = activityMainBinding.toolbar
        toolbar.title = BuildConfig.My_Orders
        setSupportActionBar(toolbar)

        val recyclerView = activityMainBinding.orderListView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        activityMainBinding.progressBar.visibility = View.VISIBLE

        orderAdapter.setViewModel(mainActivityViewModel)

        mainActivityViewModel.orderListResult().observe(this,
            Observer<List<OrderData>> {
                if (it != null) {
                    val position = orderAdapter.itemCount
                    orderAdapter.addOrders(it)
                    recyclerView.adapter = orderAdapter
                }
            })

        mainActivityViewModel.orderListError().observe(this, Observer<String> {
            if (it != null) {
                showErrorAlert()
            }
        })

        mainActivityViewModel.orderListLoader().observe(this, Observer<Boolean> {
            if (it == false) {
                progressBar.visibility = View.GONE
                progressBarBottom.visibility = View.GONE
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
                isLoading = true


                if (!isLastPage()) {
                    isLoading = false
                    offset++
                    progressBarBottom.visibility = View.VISIBLE

                    loadData()

                }
            }
        })

        mSwipeRefreshLayout = swipe_container as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        mSwipeRefreshLayout!!.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
    }

    private fun loadData() {

        val job = Job()
        val coroutineScope = CoroutineScope(job + Dispatchers.Main)

        coroutineScope.launch {
            mainActivityViewModel.loadOrderList(offset * limit, /*currentPage **/ limit)
            currentPage++
        }
    }

    override fun onRefresh() {
        loadData()
    }


    private fun showErrorAlert() {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Some error occurred, Do you want to reload ?")
            .setCancelable(false)

            .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                loadData()
            })

            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.app_name))
        alert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

}

