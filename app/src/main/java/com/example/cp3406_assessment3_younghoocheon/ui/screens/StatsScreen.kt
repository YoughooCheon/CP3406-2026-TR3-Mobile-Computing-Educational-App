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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle

private val sampleWeek = listOf(
    "M" to 0.4f, "T" to 0.55f, "W" to 0.5f, "T" to 0.7f, "F" to 0.65f, "S" to 0.85f, "S" to 0.8f,
)

@Composable
fun StatsScreen() {
    val ink = MaterialTheme.colorScheme.onPrimary
    val lime = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ScreenTitle("Progress")
        NoticeBox("Prototype: all numbers below are sample data.")

        AppCard(modifier = Modifier.fillMaxWidth(), containerColor = lime) {
            Text("STREAK", style = MaterialTheme.typography.labelMedium, color = ink)
            Spacer(Modifier.height(8.dp))
            Text("5 days", style = MaterialTheme.typography.displaySmall, color = ink)
            Text(
                text = "Missed a day? It is only a record. No guilt prompts.",
                style = MaterialTheme.typography.bodyMedium,
                color = ink,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sampleWeek.forEachIndexed { i, (day, _) ->
                    DayDot(day = day, done = i >= 2, modifier = Modifier.weight(1f))
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Retention", "82%", Modifier.weight(1f))
            StatCard("Reviewed", "126", Modifier.weight(1f))
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Accuracy this week",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Accuracy by day this week, sample data"
                    },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                sampleWeek.forEachIndexed { i, (day, value) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height((100 * value).dp)
                                .background(
                                    color = if (i == sampleWeek.lastIndex) lime
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                                ),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Topic mastery",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            MasteryRow("Privacy and data", 0.78f)
            Spacer(Modifier.height(14.dp))
            MasteryRow("Accessibility", 0.52f)
            Spacer(Modifier.height(14.dp))
            MasteryRow("Memory techniques", 0.64f)
        }
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
private fun MasteryRow(label: String, value: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            MutedText("${(value * 100).toInt()}%")
        }
        AppProgressBar(
            progress = value,
            track = MaterialTheme.colorScheme.outline,
        )
    }
}