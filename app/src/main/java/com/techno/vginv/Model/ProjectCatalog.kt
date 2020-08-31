package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectCatalog {
    @SerializedName("ProjectsCatalog")
    lateinit var projectCatalog: ArrayList<ProjectDetails>

    @SerializedName("DealsCatalog")
    lateinit var dealsCatalog: ArrayList<ProjectDetails>

}