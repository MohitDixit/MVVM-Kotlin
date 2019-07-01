package com.example.mycode_lm_ng.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycode_lm_ng.api.model.OrderData


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.mycode_lm_ng.ui.main.DescriptionActivity
import com.squareup.picasso.Picasso
import java.io.Serializable
import android.widget.Toast
import com.example.mycode_lm_ng.ui.main.MainActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.Task
import android.os.AsyncTask
import com.example.mycode_lm_ng.repository.Order
import com.example.mycode_lm_ng.repository.OrderDBClient
import android.os.AsyncTask.execute
import androidx.core.content.ContextCompat.getSystemService


class OrderAdapter(val models: List<OrderData>,val context: Context) : RecyclerView.Adapter<OrderAdapter.ItemViewHolder>()

{

         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.example.mycode_lm_ng.R.layout.order_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderAdapter.ItemViewHolder, position: Int) {
        holder.bindItems(models[position],context,position)

    }

    override fun getItemCount(): Int {
        return models.size
    }

    fun ImageAdapter(context: Context){
        val inflater: LayoutInflater? = LayoutInflater.from(context)

    }
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tempModels: List<OrderData>?=null
        var pos : Int?=0

        override fun onClick(p0: View?) {
            var clickOrder = Intent(p0?.context, DescriptionActivity::class.java).putExtra("order_list", tempModels as Serializable).putExtra("position",pos)
            p0?.context?.startActivity(clickOrder)
        }

        fun bindItems(
            model: OrderData,
            context: Context,
            position: Int
        ) {
            val txtDescription = itemView.findViewById<TextView>(com.example.mycode_lm_ng.R.id.order_description) as TextView
            val imgOrder = itemView.findViewById<TextView>(com.example.mycode_lm_ng.R.id.order_image) as ImageView


            txtDescription.text = model.description

            Picasso.with(context).load(model.imageUrl).resize(120, 60).into(imgOrder);
            tempModels = mutableListOf(model)
            pos = position
            itemView.setOnClickListener(this)

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

                class SaveTask : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {

                        //creating a Order
                        val order = Order()
                        order.description = model.description.toString()
                        order.order_id = model.id.toString()
                        order.imageUrl = model.imageUrl.toString()
                        order.lat = model.location?.lat.toString()
                        order.lng = model.location?.lng.toString()
                        order.address = model.location?.address.toString()

                        //adding to database
                        OrderDBClient.getInstance(context).getAppDatabase()
                            .orderDao()
                            .insert(order)
                        return null
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                    }
                }

                class DeleteOrders : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {

                        OrderDBClient.getInstance(context).getAppDatabase()
                            .orderDao()
                            .nukeTable()
                        return null;
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                        val st = SaveTask()
                        st.execute()

                    }
                }

                if(position==0) {
                    val del = DeleteOrders()
                    del.execute()
                } else {
                    val st = SaveTask()
                    st.execute()
                }



            }

        }
    }
}