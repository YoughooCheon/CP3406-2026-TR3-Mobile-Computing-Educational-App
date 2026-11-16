package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.Rating
import com.example.cp3406_assessment3_younghoocheon.data.SettingsSource
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PracticeState(
    val queue: List<Flashcard> = emptyList(),
    val index: Int = 0,
    val showAnswer: Boolean = false,
    val loading: Boolean = true,
)

/** Runs one flashcard session. The queue is fixed when the session starts so it does not change while rating. */
class PracticeViewModel(
    private val repository: StudyRepository,
    settingsSource: SettingsSource,
    topic: String,
) : ViewModel() {

    private val _state = MutableStateFlow(PracticeState())
    val state: StateFlow<PracticeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val doneToday = repository.observeStats().first().reviewedTodayIds
            val all = repository.observeCards().first().forTopic(topic)
            val due = all.filter { it.id !in doneToday }
            val chosen = due.ifEmpty { all }
            val limit = settingsSource.settings.first().cardsPerSession
            val queue = if (limit > 0) chosen.take(limit) else chosen
            _state.value = PracticeState(queue = queue, loading = false)
        }
    }

    fun flip() {
        _state.update { it.copy(showAnswer = !it.showAnswer) }
    }

    fun revealAnswer() {
        _state.update { it.copy(showAnswer = true) }
    }

    fun goBack() {
        _state.update { it.copy(index = (it.index - 1).coerceAtLeast(0), showAnswer = false) }
    }

    fun rate(rating: Rating) {
        val current = _state.value
        val card = current.queue.getOrNull(current.index) ?: return
        viewModelScope.launch { repository.recordReview(card, rating) }
        _state.update { it.copy(index = it.index + 1, showAnswer = false) }
    }
}