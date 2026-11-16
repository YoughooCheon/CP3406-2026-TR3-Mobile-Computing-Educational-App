package com.example.cp3406_assessment3_younghoocheon.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatsCalculatorTest {

    private val today: LocalDate = LocalDate.of(2026, 11, 16)
    private val todayEpoch = today.toEpochDay()

    private fun review(
        daysAgo: Long = 0,
        rating: Rating = Rating.Good,
        topic: String = "Privacy",
        cardId: Int = 1,
    ) = ReviewEntity(
        cardId = cardId,
        topic = topic,
        rating = rating.value,
        reviewedAt = (todayEpoch - daysAgo) * 86_400_000L,
        dayEpoch = todayEpoch - daysAgo,
    )

    @Test
    fun noReviews_givesEmptyStats() {
        val stats = StatsCalculator.build(emptyList(), today)
        assertEquals(0, stats.totalReviewed)
        assertEquals(0, stats.reviewedToday)
        assertEquals(0, stats.streakDays)
        assertNull(stats.retentionPercent)
        assertEquals(7, stats.week.size)
    }

    @Test
    fun retention_countsGoodAndEasyOnly() {
        val reviews = listOf(
            review(rating = Rating.Again),
            review(rating = Rating.Hard),
            review(rating = Rating.Good),
            review(rating = Rating.Easy),
        )
        val stats = StatsCalculator.build(reviews, today)
        assertEquals(50, stats.retentionPercent)
        assertEquals(4, stats.totalReviewed)
    }

    @Test
    fun reviewedToday_onlyCountsTodaysReviews() {
        val reviews = listOf(
            review(daysAgo = 0, cardId = 1),
            review(daysAgo = 0, cardId = 2),
            review(daysAgo = 1, cardId = 3),
        )
        val stats = StatsCalculator.build(reviews, today)
        assertEquals(2, stats.reviewedToday)
        assertEquals(setOf(1, 2), stats.reviewedTodayIds)
    }

    @Test
    fun streak_countsConsecutiveDays() {
        val perDay = mapOf(todayEpoch to 2, todayEpoch - 1 to 1, todayEpoch - 2 to 4)
        assertEquals(3, StatsCalculator.streak(perDay, todayEpoch))
    }

    @Test
    fun streak_survivesAnEmptyToday() {
        val perDay = mapOf(todayEpoch - 1 to 1, todayEpoch - 2 to 1)
        assertEquals(2, StatsCalculator.streak(perDay, todayEpoch))
    }

    @Test
    fun streak_stopsAtAGap() {
        val perDay = mapOf(todayEpoch to 1, todayEpoch - 2 to 1)
        assertEquals(1, StatsCalculator.streak(perDay, todayEpoch))
    }

    @Test
    fun streak_isZeroAfterTwoMissedDays() {
        val perDay = mapOf(todayEpoch - 2 to 5)
        assertEquals(0, StatsCalculator.streak(perDay, todayEpoch))
    }

    @Test
    fun mastery_isPerTopic() {
        val reviews = listOf(
            review(topic = "Privacy", rating = Rating.Good),
            review(topic = "Privacy", rating = Rating.Again),
            review(topic = "Access", rating = Rating.Easy),
        )
        val mastery = StatsCalculator.build(reviews, today).mastery
        assertEquals(0.5f, mastery["Privacy"])
        assertEquals(1f, mastery["Access"])
        assertNull(mastery["Memory"])
    }
}