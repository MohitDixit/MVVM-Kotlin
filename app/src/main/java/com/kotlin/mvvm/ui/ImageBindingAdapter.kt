package com.kotlin.mvvm.ui

import android.widget.ImageView
import com.squareup.picasso.Picasso
import androidx.databinding.BindingAdapter
import com.kotlin.mvvm.BuildConfig

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    if (url != "") {
        Picasso.with(imageView.context).load(url).resize(BuildConfig.imgDimen, BuildConfig.imgDimen).into(imageView)
    }
}