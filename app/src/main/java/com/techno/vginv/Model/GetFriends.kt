package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GetFriends {
    @SerializedName("data")
    lateinit var friendObject: ArrayList<FriendDetails>
}