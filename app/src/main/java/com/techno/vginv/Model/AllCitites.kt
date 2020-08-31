package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllCitites {
    @SerializedName("data")
    lateinit var allCities: ArrayList<CitiesData>
}