package com.techno.vginv.utils

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

fun OkHttpClient.request(
        url: String,
        post: Map<String, String?> = mapOf(),
        query: Map<String, String?> = mapOf(),
        headers: Map<String, String?> = mapOf(),
        data: (Response) -> Unit = {},
        error: (Exception) -> Unit = {}
) {
    Thread {
        val request: Response
        try {
            request = request(url, post, query, headers)
            data(request)
        } catch (e: Exception) {
            error(e)
        }

    }.start()
}

fun OkHttpClient.request(
        url: String,
        post: Map<String, String?> = mapOf(),
        query: Map<String, String?> = mapOf(),
        headers: Map<String, String?> = mapOf()
): Response {
    return createCall(url, post, query, headers).execute()
}

fun OkHttpClient.createCall(url: String, post: Map<String, String?>, query: Map<String, String?>, headers: Map<String, String?>): Call {
    val reqBuilder = Request.Builder()
    val urlBuilder = url.toHttpUrlOrNull()!!.newBuilder()
    //headers
    for ((key, value) in headers) {
        reqBuilder.addHeader(key, value!!)
    }
    //query
    for ((key, value) in query) {
        urlBuilder.addQueryParameter(key, value ?: "")
    }
    reqBuilder.url(urlBuilder.build())
    //post data
    if (post.count() != 0) {
        val formBuilder = FormBody.Builder()
        for ((key, value) in post) {
            if (value!!.contains("+"))
            {
                formBuilder.add(key,value ?: "")
            }else {
                formBuilder.addEncoded(key, value ?: "")

            }
        }

        reqBuilder.post(formBuilder.build())
    }

    val request = reqBuilder.build()
    return newCall(request)

}

fun OkHttpClient.createCall(url: String): Call {
    val reqBuilder = Request.Builder()
    val urlBuilder = url.toHttpUrlOrNull()!!.newBuilder()

    reqBuilder.url(urlBuilder.build())

    val request = reqBuilder.build()
    return newCall(request)

}

fun OkHttpClient.localHost(
        url: String
):
        Response {
    return createCall(url).execute()
}