package com.kotlin.mvvm.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.kotlin.mvvm.R
import com.kotlin.mvvm.api.model.OrderData
import com.kotlin.mvvm.databinding.OrderDescriptionBinding
import com.google.android.gms.maps.*
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions


class DescriptionActivity : AppCompatActivity(), OnMapReadyCallback {
    var orderList: List<OrderData>? = null
    var position: Int? = 0
    @Inject
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    override fun onMapReady(p0: GoogleMap?) {

        // create marker
        val marker =
            position?.let {
                orderList?.get(it)?.location?.lng?.let {
                    orderList!!.get(position!!).location!!.lat?.let { it1 ->
                        LatLng(
                            it1.toDouble(),
                            it.toDouble()
                        )
                    }
                }?.let {
                    MarkerOptions().position(
                        it
                    ).title("Order Location")
                }
            }

        // Changing marker icon
        marker?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

        // adding marker
        p0?.addMarker(marker)
        val cameraPosition = CameraPosition.Builder()
            .target(position?.let {
                orderList?.get(it)?.location?.lat?.let {
                    orderList!!.get(position!!).location?.lng?.let { it1 ->
                        LatLng(
                            it.toDouble(),
                            it1.toDouble()
                        )
                    }
                }
            }).zoom(12f).build()
        p0?.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )
        p0?.getUiSettings()?.setZoomControlsEnabled(true)
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kotlin.mvvm.R.layout.order_description)
        AndroidInjection.inject(this);
        initDataBinding()

    }

    private fun initDataBinding() {

        val descriptionOrderBinding: OrderDescriptionBinding =
            DataBindingUtil.setContentView(this, R.layout.order_description)
        mainActivityViewModel = ViewModelProviders.of(this, mainActivityViewModelFactory).get(
            MainActivityViewModel::class.java
        )
        descriptionOrderBinding.setMainActivityViewModel(mainActivityViewModel)
        setUpViews(descriptionOrderBinding)

    }

    private fun setUpViews(descriptionOrderBinding: OrderDescriptionBinding) {

        val imageView = descriptionOrderBinding.orderImage
        val textView = descriptionOrderBinding.orderDescription
        val obj = intent.extras?.get("order_list") as List<OrderData>

        mainActivityViewModel.textDescription = obj.get(0).description

        Picasso.with(this).load(obj.get(0).imageUrl).resize(120, 60).into(imageView);


        val mapView = MapFragment.newInstance()// fragmentManager.findFragmentById(R.id.mapFragment) as MapView
        orderList = obj
        position = 0
        mapView.getMapAsync(this)
        fragmentManager.beginTransaction().replace(R.id.map, mapView).commit();
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }


}