package com.kotlin.mvvm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mvvm.model.OrderData
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.kotlin.mvvm.databinding.OrderListItemBinding
import com.kotlin.mvvm.databinding.ProgressBarBinding
import com.kotlin.mvvm.ui.detail.OrderDetailActivity
import com.kotlin.mvvm.ui.main.MainActivityViewModel
import java.io.Serializable
import java.util.ArrayList


class OrderAdapter(private val orderList: List<OrderData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mainActivityViewModel: MainActivityViewModel? = null

    private var orderListItems = ArrayList<OrderData>()

    init {
        this.orderListItems = orderList as ArrayList<OrderData>
    }

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val layoutInflater = LayoutInflater.from(parent.context)

            val binding =
                DataBindingUtil.inflate<OrderListItemBinding>(
                    layoutInflater,
                    com.kotlin.mvvm.R.layout.order_list_item,
                    parent,
                    false
                )
            return ItemViewHolder(binding)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                DataBindingUtil.inflate<ProgressBarBinding>(
                    layoutInflater,
                    com.kotlin.mvvm.R.layout.progress_bar,
                    parent,
                    false
                )
            return ProgressViewHolder(binding)

        } else {
            throw RuntimeException("The type has to be ONE or TWO")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bindItems(orderList[position], mainActivityViewModel)
        } else if (holder is ProgressViewHolder) {
            holder.bindItems()
        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (orderListItems[position].id == 999) {
            return VIEW_TYPE_LOADING
        } else {
            return VIEW_TYPE_ITEM
        }
    }

    fun addOrders(orders: List<OrderData>, isRefresh: Boolean) {
        val initPosition = orderList.size
        if (isRefresh) {
            orderListItems.clear()
        }
        orderListItems.addAll(orders)
        notifyItemRangeInserted(initPosition, orderListItems.size)
    }


    fun addDummyItem() {
        val orderData = OrderData()
        orderData.id = 999
        orderListItems.add(orderData)
        notifyItemInserted(orderList.size - 1)
    }

    fun removeDummyItem() {
        if (orderListItems.size != 0) {
            orderListItems.removeAt(orderList.size - 1)
            notifyItemRemoved(orderList.size - 1)
        }
    }

    fun setViewModel(mainActivityViewModel: MainActivityViewModel?) {
        this.mainActivityViewModel = mainActivityViewModel
    }


    class ProgressViewHolder(private val binding: ProgressBarBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems() {
            binding.progressbar.isIndeterminate = true
        }

    }

    class ItemViewHolder(private val binding: OrderListItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private var orderList: List<OrderData>? = null
        private var isClicked: Boolean = false


        override fun onClick(view: View?) {

            val clickOrder =
                Intent(view?.context, OrderDetailActivity::class.java).putExtra(
                    view?.context?.getString(com.kotlin.mvvm.R.string.order_list),
                    orderList as Serializable
                )

            if (!isClicked) {
                view?.context?.startActivity(clickOrder)
                isClicked = true
            }
        }

        fun bindItems(
            model: OrderData,
            mainActivityViewModel: MainActivityViewModel?
        ) {

            mainActivityViewModel?.setOrderValue(model)

            binding.mainActivityViewModel = mainActivityViewModel
            binding.executePendingBindings()

            this.orderList = mutableListOf(model)
            itemView.setOnClickListener(this)

        }
    }
}