package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class LastMessageModel {
    @SerializedName("last_message")
    lateinit var lastMessageObject: LastMessageData
}