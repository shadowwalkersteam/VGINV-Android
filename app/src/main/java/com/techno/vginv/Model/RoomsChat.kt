package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class RoomsChat {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("message")
    lateinit var message: String
    @SerializedName("sender_id")
    lateinit var senderID: String
    @SerializedName("sender")
    lateinit var senderDetails: GroupChatSender

}