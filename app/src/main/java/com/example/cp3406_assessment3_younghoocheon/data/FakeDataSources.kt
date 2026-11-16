package com.example.cp3406_assessment3_younghoocheon.data

import com.example.cp3406_assessment3_younghoocheon.data.AppSettings
import com.example.cp3406_assessment3_younghoocheon.data.CardDao
import com.example.cp3406_assessment3_younghoocheon.data.CardEntity
import com.example.cp3406_assessment3_younghoocheon.data.ReviewDao
import com.example.cp3406_assessment3_younghoocheon.data.ReviewEntity
import com.example.cp3406_assessment3_younghoocheon.data.SettingsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory stand-in for [ReviewDao] so repository and ViewModel logic can run on the JVM. */
class FakeReviewDao : ReviewDao {
    private val rows = MutableStateFlow<List<ReviewEntity>>(emptyList())

    override suspend fun insert(review: ReviewEntity) {
        rows.value = rows.value + review.copy(id = rows.value.size + 1L)
    }

    override fun observeAll(): Flow<List<ReviewEntity>> = rows.map { list -> list.sortedBy { it.reviewedAt } }
    override suspend fun getAll(): List<ReviewEntity> = rows.value.sortedBy { it.reviewedAt }
    override suspend fun deleteAll() {
        rows.value = emptyList()
    }
}

/** In-memory stand-in for [CardDao]. */
class FakeCardDao : CardDao {
    private val rows = MutableStateFlow<List<CardEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(card: CardEntity) {
        rows.value = rows.value + card.copy(id = nextId++)
    }

    override fun observeAll(): Flow<List<CardEntity>> = rows.map { list -> list.sortedByDescending { it.createdAt } }
    override suspend fun getAll(): List<CardEntity> = rows.value.sortedByDescending { it.createdAt }
    override suspend fun deleteById(id: Long) {
        rows.value = rows.value.filterNot { it.id == id }
    }

    override suspend fun deleteAll() {
        rows.value = emptyList()
    }
}

/** Settings that live in memory. [clearCount] lets a test check that "delete all data" reached the settings too. */
class FakeSettingsSource(initial: AppSettings = AppSettings()) : SettingsSource {
    private val state = MutableStateFlow(initial)
    var clearCount = 0
        private set

    override val settings: Flow<AppSettings> = state

    override suspend fun clear() {
        clearCount += 1
        state.value = AppSettings()
    }

    fun update(settings: AppSettings) {
        state.value = settings
    }
}