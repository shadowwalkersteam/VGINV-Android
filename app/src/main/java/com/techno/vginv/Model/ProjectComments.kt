package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class ProjectComments {
    @SerializedName("user_id")
    lateinit var id: String
    @SerializedName("project_id")
    lateinit var projectID: String
    @SerializedName("comment")
    lateinit var comment: String
    @SerializedName("User")
    lateinit var commentsUsers: CommentsUsers
}