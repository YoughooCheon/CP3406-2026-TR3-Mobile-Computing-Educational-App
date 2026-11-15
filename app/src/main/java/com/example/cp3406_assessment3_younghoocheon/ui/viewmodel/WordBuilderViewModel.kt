package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.Rating
import com.example.cp3406_assessment3_younghoocheon.data.SettingsStore
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import com.example.cp3406_assessment3_younghoocheon.data.Tile
import com.example.cp3406_assessment3_younghoocheon.data.WordPuzzle
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuilderState(
    val queue: List<Flashcard> = emptyList(),
    val index: Int = 0,
    val pool: List<Tile> = emptyList(),
    val chosen: List<Tile> = emptyList(),
    val result: Boolean? = null,
    val correctCount: Int = 0,
    val loading: Boolean = true,
)

/** Word Builder: put the shuffled words of a card's answer back in order. Each check is saved as a review. */
class WordBuilderViewModel(
    private val repository: StudyRepository,
    settingsStore: SettingsStore,
    topic: String,
) : ViewModel() {

    private val _state = MutableStateFlow(BuilderState())
    val state: StateFlow<BuilderState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val playable = repository.observeCards().first()
                .forTopic(topic)
                .filter { WordPuzzle.isPlayable(it.back) }
            val limit = settingsStore.settings.first().cardsPerSession
            val queue = if (limit > 0) playable.take(limit) else playable
            _state.value = deal(BuilderState(queue = queue, loading = false), 0)
        }
    }

    fun pick(tile: Tile) {
        _state.update { s ->
            if (s.result != null) s else s.copy(pool = s.pool - tile, chosen = s.chosen + tile)
        }
    }

    fun unpick(tile: Tile) {
        _state.update { s ->
            if (s.result != null) s else s.copy(chosen = s.chosen - tile, pool = s.pool + tile)
        }
    }

    fun check() {
        val s = _state.value
        val card = s.queue.getOrNull(s.index) ?: return
        if (s.result != null) return
        val correct = WordPuzzle.isCorrect(s.chosen, WordPuzzle.tokenize(card.back))
        viewModelScope.launch {
            repository.recordReview(card, if (correct) Rating.Good else Rating.Again)
        }
        _state.update {
            it.copy(result = correct, correctCount = it.correctCount + if (correct) 1 else 0)
        }
    }

    fun next() {
        _state.update { deal(it, it.index + 1) }
    }

    private fun deal(state: BuilderState, index: Int): BuilderState {
        val card = state.queue.getOrNull(index)
            ?: return state.copy(index = index, pool = emptyList(), chosen = emptyList(), result = null)
        val words = WordPuzzle.tokenize(card.back)
        return state.copy(
            index = index,
            pool = WordPuzzle.shuffledTiles(words, card.id.toLong()),
            chosen = emptyList(),
            result = null,
        )
    }
}