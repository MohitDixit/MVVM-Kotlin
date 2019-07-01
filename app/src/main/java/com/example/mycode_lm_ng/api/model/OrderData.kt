package com.example.mycode_lm_ng.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OrderData : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("imageUrl")
    @Expose
    var imageUrl: String? = null
    @SerializedName("location")
    @Expose
    var location: Location? = null

    inner class Location :Serializable {

        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null
        @SerializedName("address")
        @Expose
        var address: String? = null

    }
}

