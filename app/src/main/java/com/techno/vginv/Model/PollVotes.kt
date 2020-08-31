package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class PollVotes {
    @SerializedName("answer_id")
    lateinit var answerId: String
    @SerializedName("count")
    lateinit var count: String
    @SerializedName("total")
    lateinit var total: String
}