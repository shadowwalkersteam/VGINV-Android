package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class Rooms {
    @SerializedName("data")
    lateinit var allRooms: ArrayList<RoomDetails>
}