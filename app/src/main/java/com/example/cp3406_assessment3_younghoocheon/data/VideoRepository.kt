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

class VideoRepository {
    suspend fun fetch(videoUrl: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
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
                val json = JSONObject(body)
                VideoInfo(
                    title = json.getString("title"),
                    author = json.getString("author_name"),
                    thumbnailUrl = json.getString("thumbnail_url"),
                )
            } finally {
                connection.disconnect()
            }
        }
    }
}