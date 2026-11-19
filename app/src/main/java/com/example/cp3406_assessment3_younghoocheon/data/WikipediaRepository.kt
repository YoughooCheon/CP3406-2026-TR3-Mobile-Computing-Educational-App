package com.example.cp3406_assessment3_younghoocheon.data

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** A short description of a topic taken from Wikipedia. */
data class TopicSummary(
    val title: String,
    val description: String,
    val extract: String,
    val pageUrl: String,
)

/** Where the topic guide gets summaries from. Tests replace it with a fake. */
interface SummarySource {
    suspend fun fetch(title: String): Result<TopicSummary>
}

/** Maps an app topic to the Wikipedia article that explains it. Unknown topics are never sent to the network. */
object TopicPages {
    private val titles = mapOf(
        "Privacy" to "Privacy",
        "Access" to "Access control",
        "Memory" to "Memory",
    )

    fun titleFor(topic: String): String? = titles[topic]
}

/** Fetches a page summary from the public Wikipedia REST API. */
class WikipediaRepository : SummarySource {

    override suspend fun fetch(title: String): Result<TopicSummary> = withContext(Dispatchers.IO) {
        runCatching {
            val endpoint = BASE_URL + URLEncoder.encode(title, "UTF-8").replace("+", "%20")
            val connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.setRequestProperty("Accept", "application/json")
            try {
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    error("HTTP " + connection.responseCode)
                }
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                parseSummary(body)
            } finally {
                connection.disconnect()
            }
        }
    }

    companion object {
        private const val BASE_URL = "https://en.wikipedia.org/api/rest_v1/page/summary/"
        private const val USER_AGENT = "MemoryDesk/1.0 (JCU Singapore student project)"

        /**
         * Turns a Wikipedia summary JSON body into [TopicSummary].
         * Throws if the title or extract is missing, or if the page is only a disambiguation list.
         */
        fun parseSummary(body: String): TopicSummary {
            val json = JSONObject(body)
            if (json.optString("type") == "disambiguation") error("Disambiguation page")
            val title = json.getString("title")
            val pageUrl = json.optJSONObject("content_urls")
                ?.optJSONObject("desktop")
                ?.optString("page")
                .orEmpty()
                .ifEmpty { "https://en.wikipedia.org/wiki/" + title.replace(' ', '_') }
            return TopicSummary(
                title = title,
                description = json.optString("description"),
                extract = json.getString("extract"),
                pageUrl = pageUrl,
            )
        }
    }
}
