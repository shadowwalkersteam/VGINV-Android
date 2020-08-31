package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class PollQuestions {
    @SerializedName("id")
    lateinit var id: String
    @SerializedName("answer")
    lateinit var answer: String
    @SerializedName("question_id")
    lateinit var questionId: String
}