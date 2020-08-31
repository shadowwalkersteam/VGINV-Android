package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectDetails {
//    @SerializedName("id")
//    lateinit var id: String
    @SerializedName("id")
    lateinit var projectID: String
    @SerializedName("Project")
    lateinit var projects: ProjectData

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("title")
    lateinit var title: String
    @SerializedName("auth")
    lateinit var auth: String
    @SerializedName("description")
    lateinit var description: String
    @SerializedName("budget")
    lateinit var budget: String
    @SerializedName("investment")
    lateinit var investment: String
    @SerializedName("image")
    lateinit var image: String
    @SerializedName("dep_id")
    lateinit var dep_id: String
    @SerializedName("ProjectAssets")
    lateinit var projectAssets: ArrayList<ProjectAssets>
    @SerializedName("ProjectLikes")
    lateinit var projectLikes: ArrayList<ProjectLikes>
    @SerializedName("ProjectComments")
    lateinit var projectComments: ArrayList<ProjectComments>
}