package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class Messages {
    @SerializedName("messages")
    lateinit var messages: ArrayList<MessageData>
}