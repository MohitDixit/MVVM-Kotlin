package com.example.mycode_lm_ng.ui.main

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycode_lm_ng.api.model.OrderData
import com.example.mycode_lm_ng.repository.Repository
import com.example.mycode_lm_ng.util.SchedulerProvider
import io.reactivex.Observable

class MainActivityViewModel(private val repository: Repository, private val schedulerProvider: SchedulerProvider) :
    ViewModel() {
    var winner: MutableLiveData<OrderData> = MutableLiveData()
    var orderData : OrderData?=null;
    var context: Context?=null;
    var textData:String?="My Orders"
    var textDetail:String?="Order Details"

    fun showDataFromApi(): Observable<List<OrderData>> = repository.getDataFromApi().
        compose(schedulerProvider.getSchedulersForObservable())

    fun getWinner(): LiveData<OrderData> {
        return winner
    }

    fun onClickedCellAt(position: Int) {
        Toast.makeText(context, "Position" + position + "is clicked", Toast.LENGTH_SHORT).show();
    }
}