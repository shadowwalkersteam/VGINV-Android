package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class RoomMessages {
    @SerializedName("data")
    lateinit var roomsChats: ArrayList<RoomsChat>
}