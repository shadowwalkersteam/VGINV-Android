package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class CountriesData {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("short_name")
    lateinit var shortName: String
}