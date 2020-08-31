package com.techno.vginv.utils

import android.util.Log
import com.techno.vginv.Core.ConstantStrings
import com.techno.vginv.Model.ProjectAssets
import okhttp3.*
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException


object CloudDataService {

    private var cookie = ""

    @JvmStatic
    fun userLogin(jsonObject: JSONObject,onComplete:(()->Unit)?) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.LOGIN_URL
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("LOGIN API", "LOGIN ERROR")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("LOGIN API", res.toString())
                    cookie = response.headers["Set-Cookie"]!!.split(";")[0]
                    if (res.has("token")) {
                        SharedPrefManager.write(SharedPrefManager.TOKEN, res.getString("token"))
                    }
                    if (res.has("success")) {
                        val status = res.getString("success")
                        if (status == "true") {
                            SharedPrefManager.write(SharedPrefManager.IS_LOGGEDIN, true)
                        }
                    }
                    try {
                        response.body!!.close()
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                    onComplete?.invoke()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getUserProfile(token: String, callback: (JSONObject) -> Unit): JSONObject? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.PROFILE_URL, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = JSONObject(it.body!!.string())
                    callback(res)
                }
            }, error = {
                Log.e("GET PROFILE", "Error thrown in get profile api")
                callback(JSONObject())
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getUserSession(token: String, callback: (JSONObject) -> Unit): JSONObject? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.PROFILE_URL, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = JSONObject(it.body!!.string())
                    callback(res)
                }
            }, error = {
                Log.e("GET PROFILE", "Error thrown in get profile api")
                callback(JSONObject())
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getFriendProfile(token: String, id: String, callback: (JSONObject) -> Unit): JSONObject? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.FRIENDS_PROFILE + id, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = JSONObject(it.body!!.string())
                    callback(res)
                }
            }, error = {
                Log.e("GET friend PROFILE", "Error thrown in get friend profile api")
                callback(JSONObject())
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getAllUsersChats(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_CHATS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Chats", "Error thrown in get chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        try {
//            val response = OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_CHATS, headers = mapOf("Authorization" to token))
//            if (response.isSuccessful) {
//                return response.body!!.string()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return ""
    }

    @JvmStatic
    fun getMessages(token: String, userID: Int): String? {
        try {
            val response = OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.FRIEND_MESSAGES + userID, headers = mapOf("Authorization" to token, "Cookie" to cookie))
                if (response.isSuccessful) {
                return response.body!!.string()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun sendMessage(token: String, jsonObject: JSONObject) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.SEND_MESSAGE
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getAllGroupChats(token: String, callback: (String) -> Unit): String? {
        try {
            var extension = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, ""))
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.GROUP_CHATS + "/" + extension.toLowerCase(), headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun sendGroupMessage(token: String, jsonObject: JSONObject) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.SEND_GROUPMESSAGE
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendMessageAttachment(url: String, token: String, imagepath: String, created: String, isFile: Boolean, senderType: String, callback: (String) -> Unit) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
                    MEDIA_TYPE_JPEG = fileExt[fileExt.size - 1].toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "*/*".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("created_at", created)
                    .addFormDataPart("sender_type", senderType)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    callback("")
                    val res = response.body!!.string()
                    Log.v("Send Message", res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendVoiceMessageGroup(url: String, token: String, imagepath: String, created: String, isFile: Boolean, senderType: String) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("created_at", created)
                    .addFormDataPart("sender_type", senderType)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendMessageAttachmentFriend(url: String, token: String, imagepath: String, created: String, friendID: String, isFile: Boolean, callback: (String) -> Unit) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
                    MEDIA_TYPE_JPEG = fileExt[fileExt.size - 1].toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "*/*".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("created_at", created)
                    .addFormDataPart("friend_id", friendID)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    callback("")
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendVoiceMessage(url: String, token: String, imagepath: String, created: String, friendID: String, isFile: Boolean) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("created_at", created)
                    .addFormDataPart("friend_id", friendID)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getFriends(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.USER_FRIENDS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getAllUsers(token: String, type: String, callback: (String) -> Unit): String? {
        try {
//            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_PEOPLE, headers = mapOf("Authorization" to token, "Cookie" to "connect.sid=s%3AynSiYhP0-iWurVOIzloLO4IbgQE2Wssg.kYX4BlmknPIyXf53HkKSsa1qr%2FrAZOhFG%2FqOWw1RUPw"), data = {
//                if (it.isSuccessful) {
//                    val res = it.body!!.string()
//                    callback(res)
//                }
//            }, error = {
//                Log.e("GET Group Chats", "Error thrown in get group chats api")
//                callback("")
//            })

            Thread(Runnable {
                val client = OkHttpClient()

//                val url = HttpUrl.Builder()
//                        .host(ConstantStrings.URLS.Cloud.ALL_PEOPLE)
//                        .addQueryParameter("type", type)
//                        .build()

                val request: Request = Request.Builder()
                        .url(ConstantStrings.URLS.Cloud.ALL_PEOPLE + "?type=" + type)
                        .get()
                        .addHeader("Authorization", token)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie", cookie)
                        .build()

                val response = client.newCall(request).execute()
                val res = response.body!!.string()
                callback(res)

            }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun sendFriendRequest(token: String, jsonObject: JSONObject, callback: (Boolean) -> Unit): Boolean? {
        var status = false
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.USER_ADD_FRIEND
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Send Friend request", "Send friend request Failed")
                        status = false;
                        callback(status)
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Friend Request", res.toString())
                    if (res.has("msg")) {
                        val message = res.getString("msg")
                        if (message.contains("Request has been sent successfully", ignoreCase = true)) {
                            status = true;
                            callback(status)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return status
    }

    @JvmStatic
    fun getNotifications(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.NOTIFICATIONS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun sendRequest(token: String, data : String, callback: (String) -> Unit): String? {
        try {
            val body = create(null, byteArrayOf())
            val url = ConstantStrings.URLS.Cloud.ACCEPT_REQUEST + data
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookie)
                    .header("Authorization", token)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("request api Send", "request Send Failed")
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    Log.v("Send request", res)
                    callback(res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getAllPosts(token: String, callback: (JSONObject) -> Unit): JSONObject? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_POSTS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = JSONObject(it.body!!.string())
                    callback(res)
                }
            }, error = {
                callback(JSONObject())
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun changePassword(token: String, jsonObject: JSONObject, callback: (String) -> Unit): String? {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.CHANGE_PASSWORD
            val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    var result = ""
                    if (res.has("msg")) {
                        result = res.getString("msg")
                    }
                    if (result.contains("password changed successfully", ignoreCase = true)) {
                        callback(result)
                    } else {
                        callback("")
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun ChangeProfile(token: String, jsonObject: JSONObject, imagepath: String, callback: (String) -> Unit): String? {
        try {
            if (!imagepath.isEmpty()) {
                val image = File(imagepath)
                var MEDIA_TYPE_JPEG: MediaType? = "image/png".toMediaTypeOrNull()

                val multipartBuilder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", imagepath, create(MEDIA_TYPE_JPEG, image))
                        .addFormDataPart("first_name", jsonObject.getString("first_name"))
                        .addFormDataPart("last_name", jsonObject.getString("last_name"))
                        .addFormDataPart("email", jsonObject.getString("email"))
                        .addFormDataPart("phone", jsonObject.getString("phone"))
                        .addFormDataPart("description", jsonObject.getString("description"))
                        .addFormDataPart("position", jsonObject.getString("position"))
                        .addFormDataPart("city_id", jsonObject.getString("city_id"))

                if (jsonObject.has("departments")) {
                    try {
                        multipartBuilder.addFormDataPart("departments[0]", jsonObject.getString("departments").replace("[","").replace("]",""))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val requestBody: RequestBody = multipartBuilder.build()
                var request: Request = Request.Builder()
                        .url(ConstantStrings.URLS.Cloud.CHANGE_PROFILE)
                        .header("Content-Type", "multipart/form-data")
                        .header("Authorization", token)
                        .put(requestBody).build()

                val call = OkhttpClient.getClient().newCall(request).execute()
                if (call.isSuccessful) {
                    val res = JSONObject(call.body!!.string())
                    var result = ""
                    if (res.has("msg")) {
                        result = res.getString("msg")
                    }
                    if (result.contains("profile updated successfully", ignoreCase = true)) {
                        callback(result)
                    } else {
                        callback("")
                    }
                }
            } else {
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val url = ConstantStrings.URLS.Cloud.CHANGE_PROFILE
                val request = Request.Builder()
                        .url(url)
                        .put(body)
                        .header("Content-Type", "application/json")
                        .header("Authorization", token)
                        .header("Cookie", cookie)
                        .build()

                val call = OkhttpClient.getClient().newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        try {
                            callback("")
                        } catch (e1: Exception) {
                            e1.printStackTrace()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val res = JSONObject(response.body!!.string())
                            var result = ""
                            if (res.has("msg")) {
                                result = res.getString("msg")
                            }
                            if (result.contains("profile updated successfully", ignoreCase = true)) {
                                callback(result)
                            } else {
                                callback("")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback("")
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }





//        try {
//            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
//            val url = ConstantStrings.URLS.Cloud.CHANGE_PROFILE
//            val request = Request.Builder()
//                    .url(url)
//                    .put(body)
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", token)
//                    .header("Cookie", cookie)
//                    .build()
//
//            val call = OkhttpClient.getClient().newCall(request)
//            call.enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    try {
//                        callback("")
//                    } catch (e1: Exception) {
//                        e1.printStackTrace()
//                    }
//                }
//
//                @Throws(IOException::class)
//                override fun onResponse(call: Call, response: Response) {
//                    val res = JSONObject(response.body!!.string())
//                    var result = ""
//                    if (res.has("msg")) {
//                        result = res.getString("msg")
//                    }
//                    if (result.contains("profile updated successfully", ignoreCase = true)) {
//                        callback(result)
//                    } else {
//                        callback("")
//                    }
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return ""
    }

    @JvmStatic
    fun getCountries(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_COUNTRIES, headers = mapOf("Authorization" to token), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getAllDepartment(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.DEPARTMENTS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getAllPollsData(token: String, callback: (String) -> Unit): String? {
        try {
            println("Polls cookie is: " + cookie)
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.POLLS + "/" + SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, "")), headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun postPolls(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.POLLS
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    Log.v("POST POLLS",res)
                    callback(res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getCities(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ALL_CITIES, headers = mapOf("Authorization" to token), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun postProject(url: String,
                    token: String,
                    imagepath: String,
                    filePath: String,
                    title: String,
                    provider: String,
                    description: String,
                    cityid: Int,
                    countryid: Int,
                    budget: String,
                    investment: String,
                    departmentid: Int,
                    period: String,
                    created: String,
                    userType: String,
                    projectAssets: ArrayList<ProjectAssets>): Boolean? {
        try {
//            var fileExt = filePath.split(".")
//            val file = File(filePath)
//            var MEDIA_TYPE_FILE : MediaType? = null
//            if (fileExt.isNotEmpty()) {
//                MEDIA_TYPE_FILE = fileExt[fileExt.size - 1].toMediaTypeOrNull()
//            } else {
//                MEDIA_TYPE_FILE = "*/*".toMediaTypeOrNull()
//            }

            var requestBody : MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                    for (project in projectAssets) {
                        if (project.id == "0") {
                            val image = File(project.filePath)
                            var MEDIA_TYPE_JPEG : MediaType? = "image/*".toMediaTypeOrNull()
                            requestBody.addFormDataPart("images", project.filePath, create(MEDIA_TYPE_JPEG, image))
                        } else {
                            var fileExt = project.filePath.split(".")
                            val file = File(project.filePath)
                            var MEDIA_TYPE_FILE : MediaType? = null
                            if (fileExt.isNotEmpty()) {
                                MEDIA_TYPE_FILE = fileExt[fileExt.size - 1].toMediaTypeOrNull()
                            } else {
                                MEDIA_TYPE_FILE = "*/*".toMediaTypeOrNull()
                            }
                            requestBody .addFormDataPart("studies", project.filePath, create(MEDIA_TYPE_FILE, file))
                        }
                    }
            requestBody .addFormDataPart("title", title)
            requestBody .addFormDataPart("provider", provider)
            requestBody .addFormDataPart("description", description)
            requestBody .addFormDataPart("city_id", cityid.toString())
            requestBody.addFormDataPart("country_id", countryid.toString())
            requestBody.addFormDataPart("budget", budget)
            requestBody.addFormDataPart("investment", investment)
            requestBody.addFormDataPart("dep_id", departmentid.toString())
            requestBody.addFormDataPart("created_at", created)
            requestBody .addFormDataPart("type", userType);


            val newRequestBody: RequestBody = requestBody.build()

            var request : Request = Request.Builder()
                    .url("http://3.17.158.63:3637/projects")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .post(newRequestBody).build()

            val call = OkhttpClient.getClient().newCall(request).execute()
            if (call.isSuccessful) {
                val res = JSONObject(call.body!!.string())
                Log.v("Post Project", res.toString())
                if (res.has("success")) {
                    return res.getBoolean("success")
                }
            } else {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun toggleUser(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.USER_TOGGLE
            val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    var result = ""
                    if (res.has("msg")) {
                        result = res.getString("msg")
                    }
                    callback(result)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun switchToHmg(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.SWITCH_HMG
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    callback(res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getUnreadMessagesForFriend(token: String, friendID: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.UNREAD_MESSAGES + friendID + "/unread", headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    val jsonObject = JSONObject(res)
                    val jsonArray = jsonObject.getJSONArray("data")
                    callback(jsonArray.length().toString())
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getAllUnreadMessages(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.UNREAD_MESSAGES + "unread", headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    val jsonObject = JSONObject(res)
                    callback(jsonObject.getString("data"))
                }
            }, error = {
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getCookies() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("email",SharedPrefManager.read(SharedPrefManager.EMAIL, "support@support.com"))
            jsonObject.put("password",SharedPrefManager.read(SharedPrefManager.PASSWORD, "12345678"))
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.LOGIN_URL
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("LOGIN API", "LOGIN ERROR")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    cookie = response.headers["Set-Cookie"]!!.split(";")[0]
                    try {
                        response.body!!.close()
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun postInvestment(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.INVEST
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    callback(res.getString("msg"))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun postLike(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.LIKE
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    callback(res.getString("msg"))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun postComment(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.COMMENT
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    callback(res.getString("msg"))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getAllRooms(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ROOMS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getRoomMessages(token: String, roomID: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ROOMS + "/" + roomID + "/messages", headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun sendRoomMessage(token: String, jsonObject: JSONObject) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.SEND_ROOM_MESSAGE
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @JvmStatic
    fun createRoom(token: String, imagepath: String, created: String, name: String, description: String, arrayData: ArrayList<Int>, callback: (String) -> Unit) {
        try {
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? =  "image/png".toMediaTypeOrNull()

            val multipartBuilder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", name)
                    .addFormDataPart("description", description)
                    .addFormDataPart("createdAt", created)

            for (i in 0 until arrayData.size) {
                multipartBuilder.addFormDataPart("roomMembers[$i]", arrayData[i].toString())
            }

            if (!imagepath.isEmpty()) {
                multipartBuilder.addFormDataPart("groupIcon", imagepath, create(MEDIA_TYPE_JPEG, file))
            }

            val requestBody: RequestBody = multipartBuilder.build()

            var request : Request = Request.Builder()
                    .url(ConstantStrings.URLS.Cloud.ROOMS)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                        callback("Room Created")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Create Room", res.toString())
                    callback("Room Created")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getAllUsersForRoom(token: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.USER_FRIENDS, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
                Log.e("GET Group Chats", "Error thrown in get group chats api")
                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getRoomInfo(token: String, roomID: String, callback: (String) -> Unit): String? {
        try {
            OkhttpClient.getClient().request(ConstantStrings.URLS.Cloud.ROOMS + "/" + roomID, headers = mapOf("Authorization" to token, "Cookie" to cookie), data = {
                if (it.isSuccessful) {
                    val res = it.body!!.string()
                    callback(res)
                }
            }, error = {
//                callback("")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun deleteRoom(token: String, roomID: String) {
        try {
            val body = JSONObject().toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.ROOMS + "/" + roomID
            val request = Request.Builder()
                    .url(url)
                    .delete(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    Log.v("Delete room", res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun deleteMembers(token: String, roomID: String, memberID: String) {
        try {
            val body = JSONObject().toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.ROOMS + "/" + roomID + "/" + memberID
            val request = Request.Builder()
                    .url(url)
                    .delete(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    Log.v("Delete member", res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun updateRoom(token: String, imagepath: String, name: String, description: String, roomID: String) {
        try {
            if (imagepath.isEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("name", name)
                jsonObject.put("description", description)
                jsonObject.put("roomId", roomID)
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                        .url(ConstantStrings.URLS.Cloud.ROOMS)
                        .put(body)
                        .header("Content-Type", "application/json")
                        .header("Authorization", token)
                        .build()
                val call = OkhttpClient.getClient().newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        try {
                            Log.v("Message Send", "Message Send Failed")
                        } catch (e1: Exception) {
                            e1.printStackTrace()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val res = JSONObject(response.body!!.string())
                        Log.v("Create Room", res.toString())
                    }
                })
            } else {
                val file = File(imagepath)
                var MEDIA_TYPE_JPEG : MediaType? =  "image/png".toMediaTypeOrNull()

                val multipartBuilder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", name)
                        .addFormDataPart("description", description)
                        .addFormDataPart("groupIcon", imagepath, create(MEDIA_TYPE_JPEG, file))
                        .addFormDataPart("roomId", roomID)

                val requestBody: RequestBody = multipartBuilder.build()

                var request : Request = Request.Builder()
                        .url(ConstantStrings.URLS.Cloud.ROOMS)
                        .header("Accept", "application/json")
                        .header("Content-Type", "multipart/form-data")
                        .header("Authorization",token)
                        .header("Cookie",cookie)
                        .put(requestBody).build()

                val call = OkhttpClient.getClient().newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        try {
                            Log.v("Message Send", "Message Send Failed")
                        } catch (e1: Exception) {
                            e1.printStackTrace()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val res = JSONObject(response.body!!.string())
                        Log.v("Create Room", res.toString())
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun addMembers(token: String, roomID: String, arrayData: JSONArray) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("roomMembers", arrayData)
            jsonObject.put("roomId", roomID)

            val mediaType = "application/json".toMediaTypeOrNull()
            val body: RequestBody = create(mediaType, jsonObject.toString())
            val request = Request.Builder()
                    .url(ConstantStrings.URLS.Cloud.ROOMS_ADD_MEMBERS)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie",cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Create Room", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun unFriendUser(token: String, jsonObject: JSONObject, callback: (String) -> Unit) {
        try {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val url = ConstantStrings.URLS.Cloud.DELETE_MEMBER
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .header("Cookie", cookie)
                    .build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    callback(res.getString("msg"))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendMessageAttachmentRoom(url: String, token: String, imagepath: String, created: String, isFile: Boolean, roomID: String, callback: (String) -> Unit) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
//                    MEDIA_TYPE_JPEG = fileExt[fileExt.size - 1].toMediaTypeOrNull()
                    MEDIA_TYPE_JPEG = "*/*".toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "*/*".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("createdAt", created)
                    .addFormDataPart("roomId", roomID)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                        callback("")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    callback("")
                    val res = response.body!!.string()
                    Log.v("Send Message", res)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendVoiceMessageRoom(url: String, token: String, imagepath: String, created: String, isFile: Boolean, roomID: String) {
        try {
            var fileExt = imagepath.split(".")
            val file = File(imagepath)
            var MEDIA_TYPE_JPEG : MediaType? = null
            if (isFile) {
                if (fileExt.isNotEmpty()) {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                } else {
                    MEDIA_TYPE_JPEG = "audio/mp3".toMediaTypeOrNull()
                }
            } else {
                MEDIA_TYPE_JPEG = "image/png".toMediaTypeOrNull()
            }
            val requestBody : RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", imagepath, create(MEDIA_TYPE_JPEG, file))
                    .addFormDataPart("createdAt", created)
                    .addFormDataPart("roomId", roomID)
                    .build()
            var request : Request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data")
                    .header("Authorization",token)
                    .header("Cookie",cookie)
                    .post(requestBody).build()

            val call = OkhttpClient.getClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    try {
                        Log.v("Message Send", "Message Send Failed")
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val res = JSONObject(response.body!!.string())
                    Log.v("Send Message", res.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}