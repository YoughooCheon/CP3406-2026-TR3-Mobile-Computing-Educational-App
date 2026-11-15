package com.example.cp3406_assessment3_younghoocheon.data

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** How well the learner remembered a card. */
enum class Rating(val value: Int, val label: String) {
    Again(0, "Again"),
    Hard(1, "Hard"),
    Good(2, "Good"),
    Easy(3, "Easy");

    companion object {
        fun fromValue(value: Int): Rating = entries.firstOrNull { it.value == value } ?: Again
    }
}

data class DayCount(val label: String, val count: Int)

/** Everything the Home, Progress and Profile screens need, computed from raw review rows. */
data class StudyStats(
    val reviewedToday: Int = 0,
    val reviewedTodayIds: Set<Int> = emptySet(),
    val totalReviewed: Int = 0,
    val retentionPercent: Int? = null,
    val streakDays: Int = 0,
    val week: List<DayCount> = emptyList(),
    val mastery: Map<String, Float?> = emptyMap(),
)

/** Pure function object so the statistics logic can be unit tested without Android. */
object StatsCalculator {

    fun build(reviews: List<ReviewEntity>, today: LocalDate = LocalDate.now()): StudyStats {
        val todayEpoch = today.toEpochDay()
        val perDay = reviews.groupingBy { it.dayEpoch }.eachCount()
        val todayReviews = reviews.filter { it.dayEpoch == todayEpoch }
        val goodCount = reviews.count { it.rating >= Rating.Good.value }

        return StudyStats(
            reviewedToday = todayReviews.size,
            reviewedTodayIds = todayReviews.map { it.cardId }.toSet(),
            totalReviewed = reviews.size,
            retentionPercent = if (reviews.isEmpty()) null else goodCount * 100 / reviews.size,
            streakDays = streak(perDay, todayEpoch),
            week = lastSevenDays(perDay, todayEpoch),
            mastery = mastery(reviews),
        )
    }

    /** Counts consecutive study days. Today may still be empty without breaking the streak. */
    fun streak(perDay: Map<Long, Int>, todayEpoch: Long): Int {
        var streak = 0
        var day = if (perDay.containsKey(todayEpoch)) todayEpoch else todayEpoch - 1
        while (perDay.containsKey(day)) {
            streak += 1
            day -= 1
        }
        return streak
    }

    private fun lastSevenDays(perDay: Map<Long, Int>, todayEpoch: Long): List<DayCount> =
        (6 downTo 0).map { offset ->
            val date = LocalDate.ofEpochDay(todayEpoch - offset)
            DayCount(
                label = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.ENGLISH),
                count = perDay[todayEpoch - offset] ?: 0,
            )
        }

    private fun mastery(reviews: List<ReviewEntity>): Map<String, Float?> =
        SampleData.topics.drop(1).associateWith { topic ->
            val topicReviews = reviews.filter { it.topic == topic }
            if (topicReviews.isEmpty()) {
                null
            } else {
                topicReviews.count { it.rating >= Rating.Good.value }.toFloat() / topicReviews.size
            }
        }
}