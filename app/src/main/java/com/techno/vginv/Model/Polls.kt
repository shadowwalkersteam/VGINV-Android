package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class Polls {
    @SerializedName("poll")
    lateinit var getAllPolls: ArrayList<GetPoll>
    @SerializedName("pollVotes")
    lateinit var pollVotes: ArrayList<PollVotes>
}