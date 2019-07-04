package com.kotlin.mvvm.api.model

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import androidx.room.Embedded

@Entity
class OrderData : Serializable {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null

    @SerializedName("description")
    @Expose
    @ColumnInfo(name = "description")
    var description: String? = null

    @SerializedName("imageUrl")
    @Expose
    @ColumnInfo(name = "imageUrl")
    var imageUrl: String? = null

    @SerializedName("location")
    @Expose
    @Embedded
    var location: Location? = null

    class Location : Serializable {

        @SerializedName("lat")
        @Expose
        var lat: String? = null

        @SerializedName("lng")
        @Expose
        var lng: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null
    }
}

