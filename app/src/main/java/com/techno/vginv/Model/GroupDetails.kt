package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GroupDetails {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("name")
    lateinit var name: String
    @SerializedName("description")
    lateinit var description: String
    @SerializedName("image")
    lateinit var image: String
    @SerializedName("admin")
    lateinit var admin: String
    @SerializedName("RoomMembers")
    lateinit var groupMembers: ArrayList<GroupMembers>
    @SerializedName("MembersToAdd")
    lateinit var groupMembersToAdd: ArrayList<GroupMembersToAdd>
}