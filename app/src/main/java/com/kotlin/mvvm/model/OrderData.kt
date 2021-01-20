package com.kotlin.mvvm.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderData(@PrimaryKey var id:Int = 1, val userId:Int = 1, var description:String ="description", var imageUrl:String ="", @Embedded var location:Location = Location("","",""))
data class Location(var lat:String="", var lng:String="", var address:String="")

