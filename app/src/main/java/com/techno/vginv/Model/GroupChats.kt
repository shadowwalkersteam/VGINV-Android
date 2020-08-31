package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GroupChats {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("message")
    lateinit var message: String
    @SerializedName("sender_type")
    lateinit var senderType: String
    @SerializedName("created_at")
    lateinit var createdAt: String
    @SerializedName("sender")
    lateinit var senderDetails: GroupChatSender
}