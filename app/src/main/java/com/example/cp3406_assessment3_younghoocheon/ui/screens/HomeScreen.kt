package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.DAILY_GOAL
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.Avatar
import com.example.cp3406_assessment3_younghoocheon.ui.components.InfoPill
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.components.Thumbnail
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.StatsViewModel

@Composable
fun HomeScreen(
    onStartPractice: (String) -> Unit,
    onOpenLearn: () -> Unit,
) {
    val container = appContainer()
    val viewModel: StatsViewModel = viewModel(
        factory = viewModelFactory { initializer { StatsViewModel(container.repository) } },
    )
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    var topic by rememberSaveable { mutableStateOf(SampleData.topics.first()) }
    val topicCards = SampleData.cardsFor(topic)
    val dueCount = topicCards.count { it.id !in stats.reviewedTodayIds }
    val allDone = dueCount == 0
    val featured = SampleData.links.first { it.kind == "Lecture" }
    val ink = MaterialTheme.colorScheme.onPrimary
    val lime = MaterialTheme.colorScheme.primary
    val goalProgress = stats.reviewedToday.toFloat() / DAILY_GOAL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(initial = "Y")
            Column(Modifier.weight(1f)) {
                MutedText("Welcome back")
                Text(
                    text = "Ready to recall?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (stats.streakDays > 0) {
                InfoPill(
                    icon = Icons.Filled.Star,
                    text = if (stats.streakDays == 1) "1 day" else "${stats.streakDays} days",
                )
            } else {
                InfoPill(icon = Icons.Filled.Lock, text = "On device")
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SampleData.topics.forEach { name ->
                TagChip(text = name, selected = name == topic, onClick = { topic = name })
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth(), containerColor = lime) {
            Text(
                text = if (allDone) "TODAY" else "DUE TODAY",
                style = MaterialTheme.typography.labelMedium,
                color = ink,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = when {
                    allDone -> "All done"
                    dueCount == 1 -> "1 card"
                    else -> "$dueCount cards"
                },
                style = MaterialTheme.typography.displaySmall,
                color = ink,
            )
            Text(
                text = if (allDone) {
                    "You reviewed every card in this topic today. Nice work."
                } else {
                    "A short session. No timer, no penalty for skipping."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = ink,
            )
            Spacer(Modifier.height(20.dp))
            PillButton(
                text = if (allDone) "Practice again" else "Start review",
                onClick = { onStartPractice(topic) },
                container = ink,
                content = lime,
            )
        }

        SectionHeader("Continue learning")
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenLearn,
            onClickLabel = "Open Learn",
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Thumbnail(
                    url = featured.thumbnailUrl,
                    label = featured.shortTitle,
                    modifier = Modifier.size(width = 112.dp, height = 72.dp),
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = featured.shortTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    MutedText(featured.subtitle)
                }
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Daily goal",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                MutedText("${stats.reviewedToday.coerceAtMost(DAILY_GOAL)} of $DAILY_GOAL cards")
            }
            Spacer(Modifier.height(12.dp))
            AppProgressBar(progress = goalProgress)
            Spacer(Modifier.height(12.dp))
            MutedText("Rest days are fine. Nothing is lost.")
        }
    }
}