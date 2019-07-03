package com.kotlin.mvvm.ui.main


import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mvvm.databinding.FirstScreenBinding
import dagger.android.AndroidInjection
import javax.inject.Inject
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.api.model.OrderData
import kotlinx.android.synthetic.main.first_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var orderAdapter = OrderAdapter(ArrayList(), this)

    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    private var offset: Int = 0
    private var limit: Int = 20
    private var currentPage: Int = 1
    private var LIST_SCROLLING: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kotlin.mvvm.R.layout.first_screen)
        AndroidInjection.inject(this);
        initDataBinding();

        loadData()

    }

    private fun initDataBinding() {

        val activityMainBinding: FirstScreenBinding =
            DataBindingUtil.setContentView(this, com.kotlin.mvvm.R.layout.first_screen)

        mainActivityViewModel = ViewModelProviders.of(this, mainActivityViewModelFactory).get(
            MainActivityViewModel::class.java
        )

        activityMainBinding.setMainActivityViewModel(mainActivityViewModel)

        setUpViews(activityMainBinding)

    }

    private fun setUpViews(activityMainBinding: FirstScreenBinding) {

        val recyclerView = activityMainBinding.orderListView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        activityMainBinding.progressBar.setVisibility(View.VISIBLE)


        mainActivityViewModel.orderListResult().observe(this,
            Observer<List<OrderData>> {
                if (it != null) {
                    val position = orderAdapter.itemCount
                    orderAdapter.addOrders(it)
                    recyclerView.adapter = orderAdapter
                    recyclerView.scrollToPosition(position - LIST_SCROLLING)
                }
            })

        mainActivityViewModel.orderListError().observe(this, Observer<String> {
            if (it != null) {
                Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()
            }
        })

        mainActivityViewModel.orderListLoader().observe(this, Observer<Boolean> {
            if (it == false) progressBar.visibility = View.GONE
        })


        recyclerView?.addOnScrollListener(object :
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

                    loadData()

                }

            }
        })
    }

    private fun loadData() {

        val job = Job()

        val coroutineScope = CoroutineScope(job + Dispatchers.Main)

        coroutineScope.launch {
            mainActivityViewModel.loadOrderList(offset, /*currentPage **/ limit)
            currentPage++
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }


}

