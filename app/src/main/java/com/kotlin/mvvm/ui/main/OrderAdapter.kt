package com.kotlin.mvvm.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mvvm.api.model.OrderData
import android.content.Intent
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.R
import com.kotlin.mvvm.databinding.OrderListItemBinding
import java.io.Serializable
import java.util.ArrayList


class OrderAdapter(private val orderList: List<OrderData>) :
    RecyclerView.Adapter<OrderAdapter.ItemViewHolder>() {

    private var mainActivityViewModel: MainActivityViewModel? = null

    private var orderListItems = ArrayList<OrderData>()

    init {
        this.orderListItems = orderList as ArrayList<OrderData>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<OrderListItemBinding>(layoutInflater, R.layout.order_list_item, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(orderList[position], position, mainActivityViewModel)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun addOrders(orders: List<OrderData>) {
        val initPosition = orderList.size
        orderListItems.addAll(orders)
        Log.e("list size", orderListItems.size.toString())
        mainActivityViewModel?.setOffset(orderListItems.size)
        notifyItemRangeInserted(initPosition, orderListItems.size)
    }

    fun setViewModel(mainActivityViewModel: MainActivityViewModel?) {
        this.mainActivityViewModel = mainActivityViewModel
    }


    class ItemViewHolder(private val binding: OrderListItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private var orderList: List<OrderData>? = null
        private var position: Int? = 0


        override fun onClick(view: View?) {
            val clickOrder =
                Intent(view?.context, OrderDescriptionActivity::class.java).putExtra(
                    BuildConfig.order_list,
                    orderList as Serializable
                )

            view?.context?.startActivity(clickOrder)
        }

        fun bindItems(
            model: OrderData,
            position: Int,
            mainActivityViewModel: MainActivityViewModel?
        ) {

            mainActivityViewModel?.setOrderValue(model)

            binding.mainActivityViewModel = mainActivityViewModel
            binding.executePendingBindings()

            this.orderList = mutableListOf(model)
            this.position = position
            itemView.setOnClickListener(this)

        }
    }
}