package com.example.cp3406_assessment3_younghoocheon.data

const val DAILY_GOAL = 5

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

data class StudyStats(
    val reviewedToday: Int = 0,
    val reviewedTodayIds: Set<Int> = emptySet(),
    val totalReviewed: Int = 0,
    val retentionPercent: Int? = null,
    val streakDays: Int = 0,
    val week: List<DayCount> = emptyList(),
    val mastery: Map<String, Float?> = emptyMap(),
)