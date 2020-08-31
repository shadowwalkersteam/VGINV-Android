package com.techno.vginv

import com.techno.vginv.Model.*

object SharedInstance {
    @JvmStatic
    lateinit var allChats: AllChats

    @JvmStatic
    lateinit var userMessages: Messages

    @JvmStatic
    lateinit var groups: Groups

    @JvmStatic
    lateinit var getFriends: GetFriends

    @JvmStatic
    lateinit var allUsers: AllUsers

    @JvmStatic
    lateinit var allNotifications: AllNotifications

    @JvmStatic
    lateinit var allCities: AllCitites

    @JvmStatic
    lateinit var allCountries: AllCountries

    @JvmStatic
    lateinit var allDepartments: AllDepartments

    @JvmStatic
    lateinit var allPollsData: PollsData

    @JvmStatic
    lateinit var projectCatalog: ProjectCatalog

    @JvmStatic
    lateinit var rooms: Rooms

    @JvmStatic
    lateinit var allRoomUsers: AllUsers
}