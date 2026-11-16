package com.example.cp3406_assessment3_younghoocheon.data

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class VideoInfo(
    val title: String,
    val author: String,
    val thumbnailUrl: String,
)

/** Where the Learn screen gets lecture details from. Tests replace it with a fake. */
interface VideoSource {
    suspend fun fetch(videoUrl: String): Result<VideoInfo>
}

/** Fetches lecture details from the public YouTube oEmbed endpoint. */
class VideoRepository : VideoSource {

    override suspend fun fetch(videoUrl: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val endpoint = "https://www.youtube.com/oembed?format=json&url=" +
                    URLEncoder.encode(videoUrl, "UTF-8")
            val connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            try {
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    error("HTTP " + connection.responseCode)
                }
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                parseOEmbed(body)
            } finally {
                connection.disconnect()
            }
        }
    }

    companion object {
        /** Turns an oEmbed JSON body into [VideoInfo]. Throws if a required field is missing. */
        fun parseOEmbed(body: String): VideoInfo {
            val json = JSONObject(body)
            return VideoInfo(
                title = json.getString("title"),
                author = json.getString("author_name"),
                thumbnailUrl = json.getString("thumbnail_url"),
            )
        }
    }
}