package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ReceiverDetails {
    @SerializedName("first_name")
    lateinit var firstName: String
    @SerializedName("last_name")
    lateinit var lastName: String
    @SerializedName("type")
    lateinit var type: String
    @SerializedName("image")
    lateinit var profilePicture: String
}