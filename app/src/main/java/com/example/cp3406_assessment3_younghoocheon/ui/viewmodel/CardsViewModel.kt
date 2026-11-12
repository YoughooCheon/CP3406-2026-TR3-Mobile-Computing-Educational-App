package com.example.cp3406_assessment3_younghoocheon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardsViewModel(private val repository: StudyRepository) : ViewModel() {
    val cards: StateFlow<List<Flashcard>> = repository.observeCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SampleData.cards)

    fun addCard(front: String, back: String, topic: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addCard(front, back, topic)
            onDone()
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch { repository.deleteCard(card) }
    }
}