package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectAssets {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("path")
    lateinit var path: String
    @SerializedName("filePath")
    lateinit var filePath: String
}