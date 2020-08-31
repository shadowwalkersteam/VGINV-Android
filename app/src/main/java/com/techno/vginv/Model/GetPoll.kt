package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class GetPoll {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("question")
    lateinit var question: String
    @SerializedName("type")
    lateinit var type: String
    @SerializedName("PollQuestionAnswers")
    lateinit var pollQuestions: ArrayList<PollQuestions>
}