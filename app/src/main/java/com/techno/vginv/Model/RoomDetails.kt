package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class RoomDetails {
    @SerializedName("roomId")
    lateinit var id: String
    @SerializedName("message")
    lateinit var message: String
    @SerializedName("roomName")
    lateinit var roomName: String
    @SerializedName("roomImage")
    lateinit var roomImage: String
    @SerializedName("roomAdmin")
    lateinit var roomAdmin: String
    @SerializedName("roomType")
    lateinit var roomType: String
    @SerializedName("sender_id")
    lateinit var senderId: String
}