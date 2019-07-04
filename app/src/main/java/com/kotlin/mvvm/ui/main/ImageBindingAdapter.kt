package com.kotlin.mvvm.ui.main

import android.widget.ImageView
import com.squareup.picasso.Picasso
import androidx.databinding.BindingAdapter

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    if (url != "") {
        Picasso.with(imageView.context).load(url).resize(200, 200).into(imageView)
    }
}