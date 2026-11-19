package com.example.cp3406_assessment3_younghoocheon.data

/** A topic summary and whether it came from the copy saved on the device. */
data class TopicGuide(val summary: TopicSummary, val fromCache: Boolean)

/**
 * Gives the topic guide its text. A saved copy younger than seven days is used without a network call.
 * Otherwise Wikipedia is asked and the answer is saved in Room. If the network fails, an older saved
 * copy is still shown, so the guide works offline.
 */
class TopicGuideRepository(
    private val dao: TopicSummaryDao,
    private val source: SummarySource,
    private val now: () -> Long = System::currentTimeMillis,
) {
    suspend fun load(topic: String): Result<TopicGuide> {
        val title = TopicPages.titleFor(topic)
            ?: return Result.failure(IllegalArgumentException("Unknown topic: " + topic))
        val saved = dao.get(title)
        if (saved != null && now() - saved.fetchedAt < MAX_AGE_MS) {
            return Result.success(TopicGuide(saved.toSummary(), fromCache = true))
        }
        return source.fetch(title).fold(
            onSuccess = { summary ->
                dao.upsert(summary.toEntity(fetchedAt = now()))
                Result.success(TopicGuide(summary, fromCache = false))
            },
            onFailure = { error ->
                if (saved != null) Result.success(TopicGuide(saved.toSummary(), fromCache = true))
                else Result.failure(error)
            },
        )
    }

    companion object {
        const val MAX_AGE_MS = 7L * 24 * 60 * 60 * 1000
    }
}

private fun TopicSummaryEntity.toSummary() = TopicSummary(title, description, extract, pageUrl)

private fun TopicSummary.toEntity(fetchedAt: Long) =
    TopicSummaryEntity(title = title, description = description, extract = extract, pageUrl = pageUrl, fetchedAt = fetchedAt)
