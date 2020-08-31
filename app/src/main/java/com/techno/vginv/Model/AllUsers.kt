package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllUsers {
    @SerializedName("data")
    lateinit var allUsers: ArrayList<AllUsersData>
}