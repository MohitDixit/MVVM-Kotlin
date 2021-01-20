package com.kotlin.mvvm.bindings

import android.widget.ImageView
import com.squareup.picasso.Picasso
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.MarkerOptions
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.R
import com.kotlin.mvvm.ui.main.MainActivityViewModel


const val imgDimen = 200

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Picasso.get().load(url).resize(
            imgDimen,
            imgDimen
        ).into(imageView)
    } else {
        Picasso.get().load(R.drawable.no_image_found).resize(
            imgDimen,
            imgDimen
        ).into(imageView)
    }
}

@BindingAdapter("initMap")
fun initMap(mapView: MapView, mainActivityViewModel: MainActivityViewModel) {
    mapView.onCreate(Bundle())
    mapView.getMapAsync { googleMap ->
        val orderData = mainActivityViewModel.orderData
        val latLng = orderData.location?.lat?.toDouble()?.let {
            orderData.location?.lng?.toDouble()?.let { it1 ->
                LatLng(it, it1)
            }
        }
        val markerOptions = latLng?.let { MarkerOptions().position(it) }?.title(orderData.location?.address)
        markerOptions?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
        googleMap.addMarker(markerOptions)
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(BuildConfig.zoom).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        googleMap.uiSettings?.isZoomControlsEnabled = true
    }
}
@BindingAdapter("visibility")
fun showHideView(view: View, isShow: Boolean) {
    view.visibility = if (isShow) {
        View.VISIBLE
    } else View.GONE
}