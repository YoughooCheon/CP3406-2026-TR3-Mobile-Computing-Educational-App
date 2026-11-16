package com.example.cp3406_assessment3_younghoocheon

import com.example.cp3406_assessment3_younghoocheon.data.Achievements
import com.example.cp3406_assessment3_younghoocheon.data.AppSettings
import com.example.cp3406_assessment3_younghoocheon.data.CUSTOM_ID_OFFSET
import com.example.cp3406_assessment3_younghoocheon.data.DEFAULT_DAILY_GOAL
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.Rating
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.StudyStats
import com.example.cp3406_assessment3_younghoocheon.data.VideoInfo
import com.example.cp3406_assessment3_younghoocheon.data.VideoRepository
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import kotlin.collections.plus

class ModelTest {

    // Achievements

    private fun stats(total: Int = 0, streak: Int = 0, mastery: Map<String, Float?> = emptyMap()) =
        StudyStats(totalReviewed = total, streakDays = streak, mastery = mastery)

    private fun unlocked(stats: StudyStats, cards: List<Flashcard> = SampleData.cards) =
        Achievements.evaluate(stats, cards).filter { it.unlocked }.map { it.id }

    @Test
    fun achievements_noneUnlockedAtTheStart() {
        assertTrue(unlocked(stats(mastery = mapOf("Privacy" to null))).isEmpty())
    }

    @Test
    fun achievements_unlockByReviewCount() {
        assertEquals(listOf("first"), unlocked(stats(total = 1)))
        assertEquals(listOf("first", "ten"), unlocked(stats(total = 10)))
    }

    @Test
    fun achievements_streakNeedsThreeDays() {
        assertFalse("streak3" in unlocked(stats(streak = 2)))
        assertTrue("streak3" in unlocked(stats(streak = 3)))
    }

    @Test
    fun achievements_explorerNeedsEveryTopicReviewed() {
        assertFalse("explorer" in unlocked(stats(mastery = mapOf("A" to 1f, "B" to null))))
        assertTrue("explorer" in unlocked(stats(mastery = mapOf("A" to 1f, "B" to 0f))))
        assertFalse("explorer" in unlocked(stats(mastery = emptyMap())))
    }

    @Test
    fun achievements_makerNeedsACustomCard() {
        val mine = SampleData.cards + Flashcard(1001, "Q", "A", "Privacy", custom = true)

        assertFalse("maker" in unlocked(stats(), SampleData.cards))
        assertTrue("maker" in unlocked(stats(), mine))
    }

    // Settings

    @Test
    fun textScale_followsTheChosenSize() {
        assertEquals(0.9f, AppSettings(textSize = "S").textScale)
        assertEquals(1f, AppSettings(textSize = "M").textScale)
        assertEquals(1.2f, AppSettings(textSize = "L").textScale)
        assertEquals(1f, AppSettings(textSize = "unknown").textScale)
    }

    @Test
    fun defaultSettings_areGentle() {
        val defaults = AppSettings()

        assertEquals(DEFAULT_DAILY_GOAL, defaults.dailyGoal)
        assertEquals(0, defaults.cardsPerSession)
        assertFalse(defaults.dailyReminder)
        assertFalse(defaults.highContrast)
    }

    // Ratings and cards

    @Test
    fun rating_fromValueFallsBackToAgain() {
        assertEquals(Rating.Easy, Rating.fromValue(3))
        assertEquals(Rating.Again, Rating.fromValue(99))
    }

    @Test
    fun forTopic_allKeepsEverythingAndTopicFilters() {
        assertEquals(SampleData.cards, SampleData.cards.forTopic("All"))
        assertTrue(SampleData.cards.forTopic("Memory").all { it.topic == "Memory" })
        assertEquals(2, SampleData.cards.forTopic("Memory").size)
    }

    @Test
    fun sampleCards_haveUniqueIdsBelowTheCustomOffset() {
        val ids = SampleData.cards.map { it.id }

        assertEquals(ids.size, ids.toSet().size)
        assertTrue(ids.all { it < CUSTOM_ID_OFFSET })
    }

    @Test
    fun parseOEmbed_readsTheThreeFields() {
        val body = """{"title":"Lecture","author_name":"Stanford Online","thumbnail_url":"https://img/x.jpg"}"""

        val info = VideoRepository.parseOEmbed(body)

        assertEquals(VideoInfo("Lecture", "Stanford Online", "https://img/x.jpg"), info)
    }

    @Test(expected = Exception::class)
    fun parseOEmbed_throwsWhenAFieldIsMissing() {
        VideoRepository.parseOEmbed("""{"title":"Only a title"}""")
    }
}