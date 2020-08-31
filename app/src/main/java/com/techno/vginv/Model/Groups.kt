package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class Groups {
    @SerializedName("data")
    lateinit var groupsData: ArrayList<GroupChats>
}