package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.R
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.Avatar
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconBadge
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicArt
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.OverviewViewModel

// Pictures for the two activities. They live in res/drawable-nodpi.
// The activity pictures are portrait (842 x 1264), so they are shown at exactly that ratio and never cropped.
private val FlashcardsImage = R.drawable.practice_flashcards
private val BuilderImage = R.drawable.practice_builder
private const val ACTIVITY_IMAGE_RATIO = 842f / 1264f

/**
 * Practice tab: today's numbers, the two learning activities with a short explanation,
 * the session length, per-topic decks with mastery, and a few tips on why the activities work.
 */
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
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val dueToday = cards.count { it.id !in stats.reviewedTodayIds }
    val topics = SampleData.topics.drop(1)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ScreenTitle("Practice")
                    MutedText("Pick a way to practice and a topic")
                }
                Avatar(
                    initial = "",
                    size = 72.dp,
                    color = MaterialTheme.colorScheme.primary,
                    image = R.drawable.avatar_student,
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(value = dueToday.toString(), label = "Due today", modifier = Modifier.weight(1f))
                StatTile(value = stats.reviewedToday.toString(), label = "Done today", modifier = Modifier.weight(1f))
                StatTile(value = settings.dailyGoal.toString(), label = "Daily goal", modifier = Modifier.weight(1f))
            }
        }

        item { SectionHeader("Choose an activity") }
        item {
            ActivityCard(
                image = FlashcardsImage,
                tag = "ACTIVE RECALL",
                tagColor = Lime,
                title = "Flashcards",
                description = "Recall the answer, flip the card, then rate how well you knew it.",
                buttonText = "Start flashcards",
                filled = true,
                onStart = { onStartFlashcards("All") },
            )
        }
        item {
            ActivityCard(
                image = BuilderImage,
                tag = "PUZZLE",
                tagColor = Sky,
                title = "Word builder",
                description = "The words of an answer are shuffled. Tap them in the right order.",
                buttonText = "Start word builder",
                filled = false,
                onStart = { onStartBuilder("All") },
            )
        }

        item { SectionHeader("Session length") }
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

        item { SectionHeader("Practice by topic") }
        items(topics, key = { it }) { topic ->
            val topicCards = cards.forTopic(topic)
            TopicPracticeCard(
                topic = topic,
                cardCount = topicCards.size,
                dueCount = topicCards.count { it.id !in stats.reviewedTodayIds },
                mastery = stats.mastery[topic],
                onFlashcards = { onStartFlashcards(topic) },
                onBuilder = { onStartBuilder(topic) },
            )
        }

        item { SectionHeader("Why it works") }
        item { TipsCard() }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        MutedText(label)
    }
}

/**
 * A card for one activity: the picture on the left at its own ratio (so nothing is cropped),
 * the tag, title and short text on the right, and the start button across the bottom.
 * [filled] makes the button lime, otherwise it is the quieter grey one.
 */
@Composable
private fun ActivityCard(
    @DrawableRes image: Int,
    tag: String,
    tagColor: Color,
    title: String,
    description: String,
    buttonText: String,
    filled: Boolean,
    onStart: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(124.dp)
                    .aspectRatio(ACTIVITY_IMAGE_RATIO)
                    .clip(RoundedCornerShape(20.dp)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TopicTag(text = tag, color = tagColor)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText(description)
            }
        }
        Spacer(Modifier.height(16.dp))
        if (filled) {
            PillButton(text = buttonText, onClick = onStart, modifier = Modifier.fillMaxWidth())
        } else {
            PillButton(
                text = buttonText,
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                container = MaterialTheme.colorScheme.surfaceVariant,
                content = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TopicPracticeCard(
    topic: String,
    cardCount: Int,
    dueCount: Int,
    mastery: Float?,
    onFlashcards: () -> Unit,
    onBuilder: () -> Unit,
) {
    val color = topicColor(topic)

    AppCard(modifier = Modifier.fillMaxWidth(), glow = color) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TopicArt(topic = topic, color = color, size = 88.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText("$cardCount cards, $dueCount due today")
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mastery",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            MutedText(if (mastery == null) "Not started" else "${(mastery * 100).toInt()}% Good or Easy")
        }
        Spacer(Modifier.height(8.dp))
        AppProgressBar(
            progress = mastery ?: 0f,
            color = color,
            track = MaterialTheme.colorScheme.outline,
        )
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

@Composable
private fun TipsCard() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        TipRow(
            icon = Icons.Filled.Refresh,
            color = Peach,
            title = "Recall before you peek",
            text = "Trying to remember an answer first makes it stick better than reading it again.",
        )
        Spacer(Modifier.height(16.dp))
        TipRow(
            icon = Icons.Filled.DateRange,
            color = Mint,
            title = "Little and often",
            text = "Short sessions on several days are easier to keep up than one long session.",
        )
        Spacer(Modifier.height(16.dp))
        TipRow(
            icon = Icons.Filled.Create,
            color = Sky,
            title = "Put it in order",
            text = "Rebuilding an answer word by word trains you to notice how an idea is put together.",
        )
    }
}

@Composable
private fun TipRow(icon: ImageVector, color: Color, title: String, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(icon = icon, color = color, size = 44.dp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MutedText(text)
        }
    }
}