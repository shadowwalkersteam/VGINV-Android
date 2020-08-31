package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GroupChatSender {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("first_name")
    lateinit var firstName: String
    @SerializedName("last_name")
    lateinit var lastName: String
    @SerializedName("image")
    lateinit var profilePicture: String
}