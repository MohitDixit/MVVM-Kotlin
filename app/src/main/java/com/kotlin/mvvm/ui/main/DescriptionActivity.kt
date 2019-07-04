package com.kotlin.mvvm.ui.main

import android.os.Bundle
import android.view.View
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


class DescriptionActivity : AppCompatActivity(), OnMapReadyCallback {

    private val compositeDisposable by lazy { CompositeDisposable() }
    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    private var orderList: List<OrderData>? = null
    private var position: Int? = 0

    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    override fun onMapReady(p0: GoogleMap?) {

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
                    ).title(orderList!!.get(0).location!!.address)
                }
            }

        marker?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

        p0?.addMarker(marker)
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
        p0?.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )
        p0?.uiSettings?.isZoomControlsEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_description)
        AndroidInjection.inject(this)
        initDataBinding()

    }

    private fun initDataBinding() {

        val descriptionOrderBinding: OrderDescriptionBinding =
            DataBindingUtil.setContentView(this, R.layout.order_description)
        mainActivityViewModel = ViewModelProviders.of(this, mainActivityViewModelFactory).get(
            MainActivityViewModel::class.java
        )
        descriptionOrderBinding.mainActivityViewModel = mainActivityViewModel
        setUpViews(descriptionOrderBinding)

    }

    private fun setUpViews(descriptionOrderBinding: OrderDescriptionBinding) {

        val toolbar = descriptionOrderBinding.toolbar
        toolbar.title = BuildConfig.Order_Details
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val obj = intent.extras?.get(BuildConfig.order_list) as List<OrderData>

        mainActivityViewModel.textDescription = obj[0].description
        mainActivityViewModel.imageUrl = obj[0].imageUrl

        val mapView = MapFragment.newInstance()
        orderList = obj
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