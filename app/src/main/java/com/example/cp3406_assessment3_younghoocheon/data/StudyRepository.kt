package com.example.cp3406_assessment3_younghoocheon.data

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class StudyRepository(
    private val dao: ReviewDao,
    private val settingsStore: SettingsStore,
) {
    fun observeStats(): Flow<StudyStats> = dao.observeAll().map { buildStats(it) }

    suspend fun recordReview(card: Flashcard, rating: Rating) {
        dao.insert(
            ReviewEntity(
                cardId = card.id,
                topic = card.topic,
                rating = rating.value,
                reviewedAt = System.currentTimeMillis(),
                dayEpoch = LocalDate.now().toEpochDay(),
            ),
        )
    }

    suspend fun deleteAll() {
        dao.deleteAll()
        settingsStore.clear()
    }

    suspend fun exportJson(): String {
        val reviews = dao.getAll()
        val settings = settingsStore.settings.first()
        val reviewArray = JSONArray()
        reviews.forEach { review ->
            reviewArray.put(
                JSONObject()
                    .put("cardId", review.cardId)
                    .put("topic", review.topic)
                    .put("rating", Rating.fromValue(review.rating).label)
                    .put("reviewedAt", review.reviewedAt),
            )
        }
        val settingsObject = JSONObject()
            .put("dailyReminder", settings.dailyReminder)
            .put("breakReminders", settings.breakReminders)
            .put("highContrast", settings.highContrast)
            .put("reduceMotion", settings.reduceMotion)
            .put("textSize", settings.textSize)
        return JSONObject()
            .put("exportedAt", System.currentTimeMillis())
            .put("reviews", reviewArray)
            .put("settings", settingsObject)
            .toString(2)
    }

    private fun buildStats(reviews: List<ReviewEntity>): StudyStats {
        val today = LocalDate.now().toEpochDay()
        val perDay = reviews.groupingBy { it.dayEpoch }.eachCount()
        val todayReviews = reviews.filter { it.dayEpoch == today }
        val goodCount = reviews.count { it.rating >= Rating.Good.value }

        var streak = 0
        var day = if (perDay.containsKey(today)) today else today - 1
        while (perDay.containsKey(day)) {
            streak += 1
            day -= 1
        }

        val week = (6 downTo 0).map { offset ->
            val date = LocalDate.ofEpochDay(today - offset)
            DayCount(
                label = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.ENGLISH),
                count = perDay[today - offset] ?: 0,
            )
        }

        val mastery = SampleData.topics.drop(1).associateWith { topic ->
            val topicReviews = reviews.filter { it.topic == topic }
            if (topicReviews.isEmpty()) {
                null
            } else {
                topicReviews.count { it.rating >= Rating.Good.value }.toFloat() / topicReviews.size
            }
        }

        return StudyStats(
            reviewedToday = todayReviews.size,
            reviewedTodayIds = todayReviews.map { it.cardId }.toSet(),
            totalReviewed = reviews.size,
            retentionPercent = if (reviews.isEmpty()) null else goodCount * 100 / reviews.size,
            streakDays = streak,
            week = week,
            mastery = mastery,
        )
    }
}