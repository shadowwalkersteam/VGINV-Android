package com.techno.vginv.Model

import com.google.gson.annotations.SerializedName

class AllDepartments {
    @SerializedName("data")
    lateinit var allDepartments: ArrayList<DepartmentsData>
}