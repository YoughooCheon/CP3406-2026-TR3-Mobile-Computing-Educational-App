package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.DayCount
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.StatsViewModel

@Composable
fun StatsScreen() {
    val container = appContainer()
    val viewModel: StatsViewModel = viewModel(
        factory = viewModelFactory { initializer { StatsViewModel(container.repository) } },
    )
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val ink = MaterialTheme.colorScheme.onPrimary
    val lime = MaterialTheme.colorScheme.primary
    val maxCount = (stats.week.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ScreenTitle("Progress")

        if (stats.totalReviewed == 0) {
            NoticeBox("No reviews yet. Finish a practice session and your numbers will appear here.")
        }

        AppCard(modifier = Modifier.fillMaxWidth(), containerColor = lime) {
            Text("STREAK", style = MaterialTheme.typography.labelMedium, color = ink)
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (stats.streakDays == 1) "1 day" else "${stats.streakDays} days",
                style = MaterialTheme.typography.displaySmall,
                color = ink,
            )
            Text(
                text = "Missed a day? It is only a record. No guilt prompts.",
                style = MaterialTheme.typography.bodyMedium,
                color = ink,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                stats.week.forEach { day ->
                    DayDot(day = day.label, done = day.count > 0, modifier = Modifier.weight(1f))
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Good or Easy",
                value = stats.retentionPercent?.let { "$it%" } ?: "-",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Reviewed",
                value = stats.totalReviewed.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Cards reviewed this week",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Cards reviewed per day over the last seven days"
                    },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                stats.week.forEachIndexed { i, day ->
                    WeekBar(
                        day = day,
                        fraction = day.count.toFloat() / maxCount,
                        highlight = i == stats.week.lastIndex,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Topic mastery",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            MutedText("Share of reviews you rated Good or Easy")
            Spacer(Modifier.height(16.dp))
            stats.mastery.entries.forEachIndexed { i, entry ->
                if (i > 0) Spacer(Modifier.height(14.dp))
                MasteryRow(label = entry.key, value = entry.value)
            }
        }
    }
}

@Composable
private fun WeekBar(day: DayCount, fraction: Float, highlight: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = day.count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height((90 * fraction).dp.coerceAtLeast(4.dp))
                .background(
                    color = if (highlight) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                ),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = day.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DayDot(day: String, done: Boolean, modifier: Modifier = Modifier) {
    val ink = MaterialTheme.colorScheme.onPrimary
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (done) ink else ink.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            if (done) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(text = day, style = MaterialTheme.typography.labelSmall, color = ink)
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        MutedText(label)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun MasteryRow(label: String, value: Float?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            MutedText(if (value == null) "No data yet" else "${(value * 100).toInt()}%")
        }
        AppProgressBar(
            progress = value ?: 0f,
            track = MaterialTheme.colorScheme.outline,
        )
    }
}