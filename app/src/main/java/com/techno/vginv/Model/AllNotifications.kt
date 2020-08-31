package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllNotifications {
    @SerializedName("data")
    lateinit var allNotifications: ArrayList<Notifications>
}