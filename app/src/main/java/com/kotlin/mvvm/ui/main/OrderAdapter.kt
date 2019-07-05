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
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.R
import com.squareup.picasso.Picasso
import java.io.Serializable
import java.util.ArrayList


class OrderAdapter(private val orderList: List<OrderData>, private val context: Context) :
    RecyclerView.Adapter<OrderAdapter.ItemViewHolder>() {

    private var mainActivityViewModel: MainActivityViewModel? = null

    private var orderListItems = ArrayList<OrderData>()

    init {
        this.orderListItems = orderList as ArrayList<OrderData>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(orderList[position], context, position, mainActivityViewModel)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun addOrders(orders: List<OrderData>) {
        val initPosition = orderList.size
        orderListItems.addAll(orders)
        notifyItemRangeInserted(initPosition, orderListItems.size)
    }

    fun setViewModel(mainActivityViewModel: MainActivityViewModel?) {
        this.mainActivityViewModel = mainActivityViewModel
    }


    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
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
            context: Context,
            position: Int,
            mainActivityViewModel: MainActivityViewModel?
        ) {
            val txtDescription =
                itemView.findViewById(com.kotlin.mvvm.R.id.order_description) as TextView
            val imgOrder = itemView.findViewById(com.kotlin.mvvm.R.id.order_image) as ImageView
            /*val orderData = OrderData()
            orderData.description =  model.description
            orderData.imageUrl = model.imageUrl*/
            txtDescription.text = model.description

            // mainActivityViewModel?.textDescription = model.description
            /// mainActivityViewModel?.imageUrl = model.imageUrl

            // binding.mainActivityViewModel = mainActivityViewModel


            /*  val activityMainBinding: OrderListItemBinding =
                  DataBindingUtil.setContentView(context as Activity, R.layout.order_list_item)


              val mainActivityViewModel = ViewModelProviders.of(context as FragmentActivity, MainActivityViewModelFactory(context,, Utils(context))).get(
                  MainActivityViewModel::class.java
              )
              activityMainBinding.mainActivityViewModel = mainActivityViewModel

              mainActivityViewModel.textDescription = model.description
              mainActivityViewModel.imageUrl = model.imageUrl*/




            Picasso.with(context).load(model.imageUrl).resize(120, 60).into(imgOrder)
            this.orderList = mutableListOf(model)
            this.position = position
            itemView.setOnClickListener(this)


        }
    }
}