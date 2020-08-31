package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class MessageData {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("message")
    lateinit var message: String
    @SerializedName("sender_id")
    lateinit var senderID: String
    @SerializedName("resever_id")
    lateinit var receiverID: String
    @SerializedName("created_at")
    lateinit var createdDate: String
}