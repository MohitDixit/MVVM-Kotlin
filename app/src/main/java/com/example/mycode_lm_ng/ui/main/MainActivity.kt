package com.example.mycode_lm_ng.ui.main

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.mycode_lm_ng.R
import dagger.android.DaggerActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycode_lm_ng.databinding.FirstScreenBinding
import com.example.mycode_lm_ng.util.OrderAdapter
import dagger.android.AndroidInjection

import javax.inject.Inject
import android.content.Intent
import com.google.android.gms.tasks.Task
import android.os.AsyncTask

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AlertDialog
import com.example.mycode_lm_ng.api.model.OrderData
import com.example.mycode_lm_ng.repository.Order
import com.example.mycode_lm_ng.repository.OrderDBClient
import com.example.mycode_lm_ng.repository.OrderDBClient.getInstance
import com.example.mycode_lm_ng.util.PaginationScrollListener


class MainActivity: DaggerActivity() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject lateinit var mainActivityViewModel: MainActivityViewModel

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mycode_lm_ng.R.layout.first_screen)
        AndroidInjection.inject(this);
        initDataBinding();
    }

    private fun initDataBinding() {
       // val activityMainBinding = DataBindingUtil.setContentView(this, R.layout.first_screen)
        var activityMainBinding : FirstScreenBinding = DataBindingUtil.setContentView(this, com.example.mycode_lm_ng.R.layout.first_screen)
        // mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        activityMainBinding.setMainActivityViewModel(mainActivityViewModel)
        setUpViews(activityMainBinding)

    }

    private fun setUpViews(activityMainBinding: FirstScreenBinding) {

         val recyclerView = activityMainBinding.orderListView
         recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
           // Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()
            activityMainBinding.progressBar.setVisibility(View.VISIBLE)

            compositeDisposable.add(mainActivityViewModel.showDataFromApi()
                .subscribeBy(onComplete = {
                    //Log.d("MainActivity", OrderData.)
                    // Toast.makeText(this,"Success"+mainActivityViewModel.showDataFromApi().,Toast.LENGTH_SHORT).show();
                }, onError = {
                    // Log.d("MainActivity", it.message)
                    activityMainBinding.progressBar.setVisibility(View.GONE)
                    Toast.makeText(this,"Some Network Error Occurred",Toast.LENGTH_SHORT).show();
                },onNext = {
                    // Toast.makeText(this,"Success"+it.get(0).description,Toast.LENGTH_SHORT).show();
                    activityMainBinding.progressBar.setVisibility(View.GONE)
                    val adapter : OrderAdapter = OrderAdapter(it,this)
                    recyclerView.adapter = adapter


                } ))

        } else {
            //Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_LONG).show()
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Internet Not Connected,Do you want to see offline results ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        dialog, id -> //finish()
                    activityMainBinding.progressBar.setVisibility(View.VISIBLE)
                class GetOrders : AsyncTask<Void, Void, List<Order>>() {

                    override fun doInBackground(vararg voids: Void): List<Order>? {

                        /*List<Order> orderList = */return OrderDBClient
                                .getInstance(this@MainActivity)
                            .getAppDatabase()
                            .orderDao()
                            .getAll();

                    }

                    override fun onPostExecute(orders: List<Order>) {
                        super.onPostExecute(orders)
                        val tempModels = ArrayList<OrderData>()
                        for (i in orders) {
                            val orderData = OrderData()
                            orderData.id = i.order_id.toInt()
                            orderData.description = i.description
                            orderData.imageUrl = i.imageUrl
                            val location = OrderData().Location()
                             location.lat = i.lat.toDouble()
                             location.lng = i.lng.toDouble()
                             location.address = i.address
                            orderData.location = location
                            tempModels.add(orderData!!)
                        }

                        activityMainBinding.progressBar.setVisibility(View.GONE)
                        if(tempModels?.size==0){
                            Toast.makeText(this@MainActivity,"No Data Available Now", Toast.LENGTH_SHORT).show()
                        }else{
                            val adapter : OrderAdapter = tempModels?.let { OrderAdapter(it,this@MainActivity) }!!
                            recyclerView.adapter = adapter
                        }

                    }
                }
                    val st = GetOrders()
                    st.execute()

                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                    finish()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("AlertDialogExample")
            // show alert dialog
            alert.show()
        }


        recyclerView?.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call loadmore items to get more data
               // getMoreItems()
                isLoading = false

              //  recyclerView.adapter.addData(list)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }



}

