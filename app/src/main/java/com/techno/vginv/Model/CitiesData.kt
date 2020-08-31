package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class CitiesData {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("city_name")
    lateinit var cityName: String
    @SerializedName("country_id")
    lateinit var countryID: String
}