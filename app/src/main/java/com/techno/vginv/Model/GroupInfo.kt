package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GroupInfo {
    @SerializedName("data")
    lateinit var groupDetails: GroupDetails
}