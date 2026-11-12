package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.BackHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconCircleButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.CardsViewModel

@Composable
fun CardsScreen(onBack: () -> Unit, onAddCard: () -> Unit) {
    val container = appContainer()
    val viewModel: CardsViewModel = viewModel(
        factory = viewModelFactory { initializer { CardsViewModel(container.repository) } },
    )
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    var cardToDelete by remember { mutableStateOf<Flashcard?>(null) }

    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Delete this card?") },
            text = { Text(card.front) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCard(card)
                        cardToDelete = null
                    },
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) { Text("Cancel") }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { BackHeader(title = "My cards", onBack = onBack) }
        item { MutedText("${cards.size} cards, ${cards.count { it.custom }} made by you") }
        item {
            PillButton(
                text = "Add a card",
                onClick = onAddCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(cards, key = { it.id }) { card ->
            CardRow(card = card, onDelete = { cardToDelete = card })
        }
    }
}

@Composable
private fun CardRow(card: Flashcard, onDelete: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TopicTag(text = card.topic, color = topicColor(card.topic))
                    MutedText(if (card.custom) "Yours" else "Built-in")
                }
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText(card.back)
            }
            if (card.custom) {
                IconCircleButton(
                    icon = Icons.Filled.Delete,
                    contentDescription = "Delete card",
                    onClick = onDelete,
                )
            }
        }
    }
}