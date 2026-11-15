package com.example.cp3406_assessment3_younghoocheon.data

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
)

/**
 * Badges are only ever unlocked, never taken away, so they celebrate progress
 * without creating pressure to keep a streak alive.
 */
object Achievements {

    fun evaluate(stats: StudyStats, cards: List<Flashcard>): List<Achievement> = listOf(
        Achievement("first", "First step", "Review your first card", stats.totalReviewed >= 1),
        Achievement("ten", "Warming up", "Review 10 cards", stats.totalReviewed >= 10),
        Achievement("streak3", "Three days", "Study on 3 days in a row", stats.streakDays >= 3),
        Achievement(
            "explorer",
            "Topic explorer",
            "Review a card in every topic",
            stats.mastery.isNotEmpty() && stats.mastery.values.all { it != null },
        ),
        Achievement("maker", "Card maker", "Create your own card", cards.any { it.custom }),
    )
}