package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.data.Flashcard
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconCircleButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sand
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor

@Composable
fun PracticeScreen(topic: String, onClose: () -> Unit) {
    val cards = SampleData.cardsFor(topic)
    var index by rememberSaveable { mutableIntStateOf(0) }
    var showAnswer by rememberSaveable { mutableStateOf(false) }
    val finished = index >= cards.size
    val progress = index.toFloat() / cards.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconCircleButton(Icons.Filled.Close, "Close practice", onClose)
            Spacer(Modifier.weight(1f))
            Text(
                text = if (finished) "Done" else "Card ${index + 1} of ${cards.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        AppProgressBar(progress = progress)

        if (finished) {
            FinishedPanel(onClose = onClose, modifier = Modifier.weight(1f))
        } else {
            val card = cards[index]
            TopicTag(text = card.topic, color = topicColor(card.topic))
            FlipCard(
                card = card,
                showAnswer = showAnswer,
                onFlip = { showAnswer = !showAnswer },
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
            if (showAnswer) {
                RatingRow(
                    onRate = {
                        index += 1
                        showAnswer = false
                    },
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillButton(
                        text = "Back",
                        onClick = {
                            if (index > 0) index -= 1
                            showAnswer = false
                        },
                        modifier = Modifier.weight(1f),
                        container = MaterialTheme.colorScheme.surfaceVariant,
                        content = MaterialTheme.colorScheme.onSurface,
                        showArrow = false,
                    )
                    PillButton(
                        text = "Show answer",
                        onClick = { showAnswer = true },
                        modifier = Modifier.weight(2f),
                    )
                }
            }
            MutedText("You rate yourself. No timer and no penalty for skipping.")
        }
    }
}

@Composable
private fun FlipCard(
    card: Flashcard,
    showAnswer: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (showAnswer) 180f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "flip",
    )
    val backVisible = rotation > 90f
    val container = if (backVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (backVisible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(32.dp))
            .background(container)
            .clickable(onClickLabel = "Flip card", onClick = onFlip),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = if (backVisible) 180f else 0f }
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (backVisible) "ANSWER" else "QUESTION",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (backVisible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (backVisible) card.back else card.front,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
                if (!backVisible) MutedText("Tap the card to flip")
            }
        }
    }
}

@Composable
private fun RatingRow(onRate: () -> Unit) {
    val ratings = listOf("Again" to Peach, "Hard" to Sand, "Good" to Mint, "Easy" to Lime)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ratings.forEach { (label, color) ->
            PillButton(
                text = label,
                onClick = onRate,
                modifier = Modifier.weight(1f),
                container = color,
                showArrow = false,
            )
        }
    }
}

@Composable
private fun FinishedPanel(onClose: () -> Unit, modifier: Modifier = Modifier) {
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
            text = "Session complete",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        MutedText("No streak pressure. Come back whenever you like.")
        PillButton(text = "Back to Home", onClick = onClose)
    }
}