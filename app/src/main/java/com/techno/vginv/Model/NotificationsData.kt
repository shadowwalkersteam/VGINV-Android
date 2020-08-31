package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class NotificationsData {
    @SerializedName("sender")
    lateinit var sender: String
    @SerializedName("sender_name")
    lateinit var sender_name: String
}