package com.techno.vginv.Core

object ConstantStrings {
    object URLS {
        object Cloud {
            @JvmField
            var BASE_URL = "http://3.17.158.63:3637"
            @JvmField
            var WEBSITE_BASE_URL = "https://app.vginv.com"
            @JvmField
            var WEBSITE_BASE_URL2 = "https://www.vginv.com/vg-admin/public"


            @JvmField
            var LOGIN_URL = BASE_URL + "/user/login"
            @JvmField
            var PROFILE_URL = BASE_URL + "/user/me"
            @JvmField
            var ALL_CHATS = BASE_URL + "/user/chats"
            @JvmField
            var FRIEND_MESSAGES = BASE_URL + "/user/chats/"
            @JvmField
            var SEND_MESSAGE = BASE_URL + "/user/chats/message"
            @JvmField
            var GROUP_CHATS = BASE_URL + "/group/chats"
            @JvmField
            var SEND_GROUPMESSAGE = BASE_URL + "/group/chats/message"
            @JvmField
            var GROUP_CHAT_FILE = BASE_URL + "/group/chats/file"
            @JvmField
            var USER_CHAT_FILE = BASE_URL + "/user/chats/file"
            @JvmField
            var USER_FRIENDS = BASE_URL + "/user/friends"
            @JvmField
            var USER_ADD_FRIEND = BASE_URL + "/user/friends"
            @JvmField
            var ALL_PEOPLE = BASE_URL + "/user/friends/add"
            @JvmField
            var NOTIFICATIONS = BASE_URL + "/user/notifications"
            @JvmField
            var ACCEPT_REQUEST = BASE_URL + "/request/"
            @JvmField
            var FRIENDS_PROFILE = BASE_URL + "/user/info/"
            @JvmField
            var ALL_POSTS = BASE_URL + "/posts"
            @JvmField
            var CHANGE_PASSWORD = BASE_URL + "/user/settings/password"
            @JvmField
            var CHANGE_PROFILE = BASE_URL + "/user/settings/profile"
            @JvmField
            var ALL_CITIES = BASE_URL + "/cities"
            @JvmField
            var ALL_COUNTRIES = BASE_URL + "/countries"
            @JvmField
            var POST_PROJECT = BASE_URL + "/projects"
            @JvmField
            var DEPARTMENTS = BASE_URL + "/departments"
            @JvmField
            var POLLS = BASE_URL + "/poll"
            @JvmField
            var USER_TOGGLE = BASE_URL + "/type/toggle"
            @JvmField
            var SWITCH_HMG = BASE_URL + "/user/hmg/request"
            @JvmField
            var UNREAD_MESSAGES = BASE_URL + "/user/chats/"
            @JvmField
            var INVEST = BASE_URL + "/projects/invest"
            @JvmField
            var LIKE = BASE_URL + "/projects/like"
            @JvmField
            var COMMENT = BASE_URL + "/projects/comment"
            @JvmField
            var ROOMS = BASE_URL + "/rooms"
            @JvmField
            var ROOMS_ADD_MEMBERS = BASE_URL + "/rooms/addMembers"
            @JvmField
            var SEND_ROOM_MESSAGE = BASE_URL + "/rooms/message"
            @JvmField
            var GET_ALL_USERS = BASE_URL + "/user/all/"
            @JvmField
            var DELETE_MEMBER = BASE_URL + "/user/unfriend"
        }
    }
}