package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class Notifications {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("notifiable_id")
    lateinit var notificationID: String
    @SerializedName("notifiable_type")
    lateinit var notificationType: String
    @SerializedName("type")
    lateinit var type: String
    @SerializedName("data")
    lateinit var notificationData: String
}