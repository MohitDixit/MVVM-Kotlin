package com.kotlin.mvvm.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mvvm.api.model.OrderData


import android.content.Context
import android.content.Intent

import com.squareup.picasso.Picasso
import java.io.Serializable

import java.util.ArrayList


class OrderAdapter(private val orderList: List<OrderData>, private val context: Context) :
    RecyclerView.Adapter<OrderAdapter.ItemViewHolder>() {

    private var orderListItems = ArrayList<OrderData>()

    init {
        this.orderListItems = orderList as ArrayList<OrderData>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.kotlin.mvvm.R.layout.order_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(orderList[position], context, position)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun addOrders(cryptoCurrencies: List<OrderData>) {
        val initPosition = orderList.size
        orderListItems.addAll(cryptoCurrencies)
        notifyItemRangeInserted(initPosition, orderListItems.size)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var orderList: List<OrderData>? = null
        var position: Int? = 0

        override fun onClick(p0: View?) {
            val clickOrder =
                Intent(p0?.context, DescriptionActivity::class.java).putExtra("order_list", orderList as Serializable)
                    .putExtra("position", position)
            p0?.context?.startActivity(clickOrder)
        }

        fun bindItems(
            model: OrderData,
            context: Context,
            position: Int
        ) {
            val txtDescription =
                itemView.findViewById(com.kotlin.mvvm.R.id.order_description) as TextView
            val imgOrder = itemView.findViewById(com.kotlin.mvvm.R.id.order_image) as ImageView

            txtDescription.text = model.description
            Picasso.with(context).load(model.imageUrl).resize(120, 60).into(imgOrder)

            this.orderList = mutableListOf(model)
            this.position = position
            itemView.setOnClickListener(this)


        }
    }
}