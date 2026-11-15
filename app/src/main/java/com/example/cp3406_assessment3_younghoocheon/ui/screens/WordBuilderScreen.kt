package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.Tile
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconCircleButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.BuilderState
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.WordBuilderViewModel

/** Puzzle activity: tap the shuffled words in the right order to rebuild a card's answer. */
@Composable
fun WordBuilderScreen(topic: String, onClose: () -> Unit) {
    val container = appContainer()
    val viewModel: WordBuilderViewModel = viewModel(
        factory = viewModelFactory {
            initializer { WordBuilderViewModel(container.repository, container.settingsStore, topic) }
        },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val total = state.queue.size
    val finished = !state.loading && total > 0 && state.index >= total
    val progress = state.index.toFloat() / total.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconCircleButton(Icons.Filled.Close, "Close word builder", onClose)
            Spacer(Modifier.weight(1f))
            Text(
                text = when {
                    state.loading || total == 0 -> "Word builder"
                    finished -> "Done"
                    else -> "Puzzle ${state.index + 1} of $total"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }
        AppProgressBar(progress = progress)

        when {
            state.loading -> Spacer(Modifier.weight(1f))
            total == 0 -> EmptyPanel(onClose = onClose, modifier = Modifier.weight(1f))
            finished -> ResultPanel(
                correct = state.correctCount,
                total = total,
                onClose = onClose,
                modifier = Modifier.weight(1f),
            )
            else -> BuilderBody(
                state = state,
                onPick = viewModel::pick,
                onUnpick = viewModel::unpick,
                onCheck = viewModel::check,
                onNext = viewModel::next,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuilderBody(
    state: BuilderState,
    onPick: (Tile) -> Unit,
    onUnpick: (Tile) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val card = state.queue[state.index]
    val ready = state.pool.isEmpty() && state.chosen.isNotEmpty()
    val checked = state.result != null
    val tileColor = when (state.result) {
        true -> Mint
        false -> Peach
        null -> MaterialTheme.colorScheme.primary
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TopicTag(text = card.topic, color = topicColor(card.topic))
            AppCard(modifier = Modifier.fillMaxWidth()) {
                MutedText("Build the answer to")
                Spacer(Modifier.size(6.dp))
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 112.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(14.dp),
            ) {
                if (state.chosen.isEmpty()) {
                    MutedText("Tap the words below in the right order")
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.chosen.forEach { tile ->
                        WordTile(
                            text = tile.text,
                            container = tileColor,
                            content = MaterialTheme.colorScheme.onPrimary,
                            enabled = !checked,
                            onClick = { onUnpick(tile) },
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.pool.forEach { tile ->
                    WordTile(
                        text = tile.text,
                        container = MaterialTheme.colorScheme.surfaceVariant,
                        content = MaterialTheme.colorScheme.onSurface,
                        enabled = !checked,
                        onClick = { onPick(tile) },
                    )
                }
            }

            when (state.result) {
                true -> Text(
                    text = "Correct. Saved as Good.",
                    style = MaterialTheme.typography.titleSmall,
                    color = Mint,
                )
                false -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Not quite. Saved as Again. The correct answer is:",
                        style = MaterialTheme.typography.titleSmall,
                        color = Peach,
                    )
                    Text(
                        text = card.back,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                null -> Unit
            }
        }

        if (checked) {
            PillButton(
                text = if (state.index + 1 >= state.queue.size) "Finish" else "Next puzzle",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            PillButton(
                text = "Check",
                onClick = { if (ready) onCheck() },
                modifier = Modifier.fillMaxWidth(),
                container = if (ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                content = if (ready) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                showArrow = false,
            )
        }
    }
}

@Composable
private fun WordTile(
    text: String,
    container: Color,
    content: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = content, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun EmptyPanel(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No puzzles here yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        MutedText("Word builder needs cards whose answer has 3 to 14 words.")
        PillButton(text = "Back", onClick = onClose)
    }
}

@Composable
private fun ResultPanel(correct: Int, total: Int, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = "$correct of $total correct",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        MutedText("Your results are saved on this device.")
        PillButton(text = "Back to Practice", onClick = onClose)
    }
}