package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class DepartmentsData {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("dep_en")
    lateinit var depName: String
    @SerializedName("dep_ar")
    lateinit var depArName: String
}