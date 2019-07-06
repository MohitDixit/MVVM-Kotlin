package com.kotlin.mvvm.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.R
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.databinding.OrderDescriptionBinding
import com.google.android.gms.maps.*
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.util.Utils


class OrderDescriptionActivity : AppCompatActivity(), OnMapReadyCallback {

    private val compositeDisposable by lazy { CompositeDisposable() }
    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    private var orderList: List<OrderData>? = null
    private var position: Int? = 0

    @Inject
    lateinit var apiInterface: ApiInterface

    override fun onMapReady(googleMap: GoogleMap?) {

        val marker =
            position?.let { it ->
                orderList?.get(it)?.location?.lng?.let {
                    orderList!![position!!].location!!.lat?.let { it1 ->
                        LatLng(
                            it1.toDouble(),
                            it.toDouble()
                        )
                    }
                }?.let {
                    MarkerOptions().position(
                        it
                    ).title(orderList!![0].location!!.address)
                }
            }

        marker?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

        googleMap?.addMarker(marker)
        val cameraPosition = CameraPosition.Builder()
            .target(position?.let {
                orderList?.get(it)?.location?.lat?.let {
                    orderList!![position!!].location?.lng?.let { it1 ->
                        LatLng(
                            it.toDouble(),
                            it1.toDouble()
                        )
                    }
                }
            }).zoom(BuildConfig.zoom).build()
        googleMap?.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_description)
        AndroidInjection.inject(this)
        initDataBinding()

    }

    private fun initDataBinding() {

        val orderDescriptionActivity: OrderDescriptionBinding =
            DataBindingUtil.setContentView(this, R.layout.order_description)

        mainActivityViewModel =
            ViewModelProviders.of(this, MainActivityViewModelFactory(this, apiInterface, Utils(this))).get(
                MainActivityViewModel::class.java
            )
        orderDescriptionActivity.mainActivityViewModel = mainActivityViewModel
        setUpViews(orderDescriptionActivity)

    }

    private fun setUpViews(descriptionOrderBinding: OrderDescriptionBinding) {

        val toolbar = descriptionOrderBinding.toolbar
        toolbar.title = BuildConfig.Order_Details
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val orderData = intent.extras?.get(BuildConfig.order_list) as List<OrderData>

        mainActivityViewModel.setOrderValue(orderData[0])

        val mapView = MapFragment.newInstance()
        orderList = orderData
        position = 0
        mapView.getMapAsync(this)
        fragmentManager.beginTransaction().replace(R.id.map, mapView).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }


}