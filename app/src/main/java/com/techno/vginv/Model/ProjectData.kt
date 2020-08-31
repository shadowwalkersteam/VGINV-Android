package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectData {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("title")
    lateinit var title: String
    @SerializedName("description")
    lateinit var description: String
    @SerializedName("budget")
    lateinit var budget: String
    @SerializedName("investment")
    lateinit var investment: String
    @SerializedName("image")
    lateinit var image: String
    @SerializedName("ProjectAssets")
    lateinit var projectAssets: ArrayList<ProjectAssets>
}