package com.example.mycode_lm_ng.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity

data class Order (



    @PrimaryKey(autoGenerate = true) var id: Int? = null,


    @ColumnInfo var order_id: String = "",
    @ColumnInfo var description: String = "",
    @ColumnInfo var imageUrl: String = "",
    @ColumnInfo var lat: String = "",
    @ColumnInfo var lng: String = "",
    @ColumnInfo var address: String = ""


)