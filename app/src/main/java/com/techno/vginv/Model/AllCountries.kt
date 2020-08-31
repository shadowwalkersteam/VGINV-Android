package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllCountries {
    @SerializedName("data")
    lateinit var allCountries: ArrayList<CountriesData>
}