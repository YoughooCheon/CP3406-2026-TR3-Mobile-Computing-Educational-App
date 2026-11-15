package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconBadge
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.components.topicIcon
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.OverviewViewModel

/** Practice tab: pick a topic, then choose flashcards or the Word Builder puzzle. */
@Composable
fun PracticeHubScreen(
    onStartFlashcards: (String) -> Unit,
    onStartBuilder: (String) -> Unit,
) {
    val container = appContainer()
    val viewModel: OverviewViewModel = viewModel(
        factory = viewModelFactory {
            initializer { OverviewViewModel(container.repository, container.settingsStore) }
        },
    )
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ScreenTitle("Practice")
                MutedText("Pick a topic and a way to practice")
            }
        }
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Cards per session",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText("Shorter sessions are easier to fit into a busy day")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5 to "5", 10 to "10", 0 to "All").forEach { (value, label) ->
                        TagChip(
                            text = label,
                            selected = settings.cardsPerSession == value,
                            onClick = { viewModel.setCardsPerSession(value) },
                        )
                    }
                }
            }
        }
        items(SampleData.topics, key = { it }) { topic ->
            TopicPracticeCard(
                topic = topic,
                cardCount = cards.forTopic(topic).size,
                onFlashcards = { onStartFlashcards(topic) },
                onBuilder = { onStartBuilder(topic) },
            )
        }
    }
}

@Composable
private fun TopicPracticeCard(
    topic: String,
    cardCount: Int,
    onFlashcards: () -> Unit,
    onBuilder: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            IconBadge(icon = topicIcon(topic), color = topicColor(topic))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (topic == "All") "All topics" else topic,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText("$cardCount cards")
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PillButton(
                text = "Flashcards",
                onClick = onFlashcards,
                modifier = Modifier.weight(1f),
                showArrow = false,
            )
            PillButton(
                text = "Word builder",
                onClick = onBuilder,
                modifier = Modifier.weight(1f),
                container = MaterialTheme.colorScheme.surfaceVariant,
                content = MaterialTheme.colorScheme.onSurface,
                showArrow = false,
            )
        }
    }
}