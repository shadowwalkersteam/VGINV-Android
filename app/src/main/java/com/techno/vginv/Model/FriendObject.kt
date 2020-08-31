package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class FriendObject {
    @SerializedName("Friend")
    lateinit var friendDetails: FriendDetails
}