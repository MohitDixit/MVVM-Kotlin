package com.example.mycode_lm_ng.ui.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycode_lm_ng.R
import com.example.mycode_lm_ng.R.layout.order_description
import com.example.mycode_lm_ng.api.model.OrderData
import com.example.mycode_lm_ng.databinding.FirstScreenBinding
import com.example.mycode_lm_ng.databinding.OrderDescriptionBinding
import com.example.mycode_lm_ng.util.OrderAdapter
import com.google.android.gms.maps.*
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import dagger.android.DaggerActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.order_list_item.view.*
import java.util.*
import javax.inject.Inject

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions





class DescriptionActivity: DaggerActivity(), OnMapReadyCallback {
    var tempObj : List<OrderData>?=null
    var tempPos: Int?=0
    override fun onMapReady(p0: GoogleMap?) {

        // create marker
        val marker =
            tempPos?.let {
                tempObj?.get(it)?.location?.lng?.let { tempObj!!.get(tempPos!!).location!!.lat?.let { it1 -> LatLng(it1, it) } }?.let {
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
            .target(tempPos?.let { tempObj?.get(it)?.location?.lat?.let { tempObj!!.get(tempPos!!).location?.lng?.let { it1 ->
                LatLng(it,
                    it1
                )
            } } }).zoom(12f).build()
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
        setContentView(com.example.mycode_lm_ng.R.layout.order_description)
        AndroidInjection.inject(this);
        initDataBinding()

    }

    private fun initDataBinding() {
        // val activityMainBinding = DataBindingUtil.setContentView(this, R.layout.first_screen)
        var descriptionOrderBinding : OrderDescriptionBinding = DataBindingUtil.setContentView(this, R.layout.order_description)
        // mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        descriptionOrderBinding.setMainActivityViewModel(mainActivityViewModel)
        setUpViews(descriptionOrderBinding)

    }

    private fun setUpViews(descriptionOrderBinding: OrderDescriptionBinding) {

        val imageView = descriptionOrderBinding.orderImage
        val textView = descriptionOrderBinding.orderDescription
        val obj = intent.extras?.get("order_list") as List<OrderData>
        val pos = intent.extras?.get("position") as Int
        textView.setText(obj.get(0/*pos*/).description)
        Picasso.with(this).load(obj.get(0/*pos*/).imageUrl).resize(120, 60).into(imageView);


      //  val supportMapFragment = findViewById(com.example.mycode_lm_ng.R.id.map) as SupportMapFragment
        val mapView = MapFragment.newInstance()// fragmentManager.findFragmentById(R.id.mapFragment) as MapView
        tempObj = obj
        tempPos = 0//pos
        val googleMap = mapView.getMapAsync(this)
        fragmentManager.beginTransaction().replace(R.id.map, mapView).commit();
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }


}