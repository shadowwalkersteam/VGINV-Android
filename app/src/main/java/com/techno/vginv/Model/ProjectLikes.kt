package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectLikes {
    @SerializedName("user_id")
    lateinit var id: String
    @SerializedName("project_id")
    lateinit var projectID: String
}