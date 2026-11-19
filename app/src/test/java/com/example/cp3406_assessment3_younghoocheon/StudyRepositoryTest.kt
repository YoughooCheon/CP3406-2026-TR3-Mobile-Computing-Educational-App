package com.example.cp3406_assessment3_younghoocheon.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StudyRepositoryTest {

    private lateinit var reviewDao: FakeReviewDao
    private lateinit var cardDao: FakeCardDao
    private lateinit var settings: FakeSettingsSource
    private lateinit var repository: StudyRepository

    @Before
    fun setUp() {
        reviewDao = FakeReviewDao()
        cardDao = FakeCardDao()
        settings = FakeSettingsSource(AppSettings(nickname = "Hu"))
        repository = StudyRepository(reviewDao, cardDao, settings)
    }

    @Test
    fun observeCards_startsWithTheBuiltInCardsOnly() = runBlocking {
        val cards = repository.observeCards().first()

        assertEquals(SampleData.cards, cards)
        assertTrue(cards.none { it.custom })
    }

    @Test
    fun addCard_appearsAsACustomCardWithAnOffsetId() = runBlocking {
        repository.addCard("What is a Repository?", "A single source of truth for data.", "Memory")

        val added = repository.observeCards().first().single { it.custom }

        assertEquals("What is a Repository?", added.front)
        assertEquals("Memory", added.topic)
        assertTrue(added.id > CUSTOM_ID_OFFSET)
        assertEquals(SampleData.cards.size + 1, repository.observeCards().first().size)
    }

    @Test
    fun deleteCard_removesCustomCardsButNeverBuiltInOnes() = runBlocking {
        repository.addCard("Q", "A", "Privacy")
        val custom = repository.observeCards().first().single { it.custom }

        repository.deleteCard(SampleData.cards.first())
        assertEquals(SampleData.cards.size + 1, repository.observeCards().first().size)

        repository.deleteCard(custom)
        assertEquals(SampleData.cards, repository.observeCards().first())
    }

    @Test
    fun recordReview_isReflectedInTheStatistics() = runBlocking {
        val card = SampleData.cards.first()

        repository.recordReview(card, Rating.Good)
        repository.recordReview(card, Rating.Again)

        val stats = repository.observeStats().first()
        assertEquals(2, stats.totalReviewed)
        assertEquals(2, stats.reviewedToday)
        assertEquals(setOf(card.id), stats.reviewedTodayIds)
        assertEquals(50, stats.retentionPercent)
        assertEquals(1, stats.streakDays)
    }

    @Test
    fun deleteAll_clearsReviewsCardsAndSettings() = runBlocking {
        repository.addCard("Q", "A", "Privacy")
        repository.recordReview(SampleData.cards.first(), Rating.Easy)

        repository.deleteAll()

        assertEquals(SampleData.cards, repository.observeCards().first())
        assertEquals(0, repository.observeStats().first().totalReviewed)
        assertEquals(1, settings.clearCount)
        assertEquals("Student", settings.settings.first().nickname)
    }

    @Test
    fun exportJson_containsEverythingThatIsStored() = runBlocking {
        repository.addCard("My question", "My answer", "Access")
        repository.recordReview(SampleData.cards.first(), Rating.Hard)

        val json = repository.exportJson()

        assertTrue(Regex("\"front\"\\s*:\\s*\"My question\"").containsMatchIn(json))
        assertTrue(Regex("\"rating\"\\s*:\\s*\"Hard\"").containsMatchIn(json))
        assertTrue(Regex("\"nickname\"\\s*:\\s*\"Hu\"").containsMatchIn(json))
        assertTrue("customCards" in json && "reviews" in json && "settings" in json)
    }

    @Test
    fun exportJson_doesNotLeakAnythingWhenEmpty() = runBlocking {
        val json = repository.exportJson()

        assertFalse("My question" in json)
        assertTrue("exportedAt" in json)
    }
}
