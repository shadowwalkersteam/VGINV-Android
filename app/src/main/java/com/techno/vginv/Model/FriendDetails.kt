package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class FriendDetails {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("first_name")
    lateinit var firstName: String
    @SerializedName("last_name")
    lateinit var lastName: String
    @SerializedName("email")
    lateinit var email: String
    @SerializedName("phone")
    lateinit var phone: String
    @SerializedName("description")
    lateinit var description: String
    @SerializedName("type")
    lateinit var type: String
    @SerializedName("position")
    lateinit var position: String
    @SerializedName("image")
    lateinit var image: String

}