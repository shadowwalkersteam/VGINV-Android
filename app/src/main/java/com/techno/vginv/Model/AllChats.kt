package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllChats {
    @SerializedName("data")
    lateinit var chats: ArrayList<LastMessageData>
}