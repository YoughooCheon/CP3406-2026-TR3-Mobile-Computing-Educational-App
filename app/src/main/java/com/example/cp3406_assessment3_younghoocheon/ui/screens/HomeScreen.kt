package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.forTopic
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppProgressBar
import com.example.cp3406_assessment3_younghoocheon.ui.components.Avatar
import com.example.cp3406_assessment3_younghoocheon.ui.components.IconBadge
import com.example.cp3406_assessment3_younghoocheon.ui.components.InfoPill
import com.example.cp3406_assessment3_younghoocheon.ui.components.FacultyStrip
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.components.Thumbnail
import com.example.cp3406_assessment3_younghoocheon.ui.components.topicIcon
import com.example.cp3406_assessment3_younghoocheon.ui.theme.topicColor
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.OverviewViewModel

/** Landing page: greeting, today's progress, decks, daily goal, quick practice and faculty shortcuts. */
@Composable
fun HomeScreen(
    onStartPractice: (String) -> Unit,
    onOpenPracticeHub: () -> Unit,
    onOpenExplore: () -> Unit,
    onOpenStaff: (String) -> Unit,
    onOpenLearn: () -> Unit,
    onOpenCards: () -> Unit,
    onAddCard: () -> Unit,
) {
    val container = appContainer()
    val viewModel: OverviewViewModel = viewModel(
        factory = viewModelFactory {
            initializer { OverviewViewModel(container.repository, container.settingsStore) }
        },
    )
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }

    val dueCount = cards.count { it.id !in stats.reviewedTodayIds }
    val goal = settings.dailyGoal
    val goalProgress = stats.reviewedToday.toFloat() / goal

    if (showGoalDialog) {
        GoalDialog(
            current = goal,
            onDismiss = { showGoalDialog = false },
            onSelect = {
                viewModel.setDailyGoal(it)
                showGoalDialog = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        HomeHeader(nickname = settings.nickname, streakDays = stats.streakDays)

        HeroCard(
            dueCount = dueCount,
            reviewedToday = stats.reviewedToday,
            goal = goal,
            progress = goalProgress,
            onContinue = { onStartPractice("All") },
        )

        SectionHeader("My decks")
        SampleData.topics.drop(1).forEach { topic ->
            DeckRow(
                topic = topic,
                cardCount = cards.forTopic(topic).size,
                mastery = stats.mastery[topic],
                onClick = { onStartPractice(topic) },
            )
        }

        GoalCard(
            reviewedToday = stats.reviewedToday,
            goal = goal,
            progress = goalProgress,
            onEditGoal = { showGoalDialog = true },
        )

        SectionHeader("Quick practice")
        QuickPracticeRow(onStartPractice = onStartPractice, onMore = onOpenPracticeHub)

        SectionHeader("Meet the faculty")
        FacultyStrip(onOpenStaff = onOpenStaff)
        PillButton(
            text = "Explore IT at JCU",
            onClick = onOpenExplore,
            modifier = Modifier.fillMaxWidth(),
            container = MaterialTheme.colorScheme.surfaceVariant,
            content = MaterialTheme.colorScheme.onSurface,
            showArrow = false,
        )

        SectionHeader("Featured lecture")
        LectureCard(onOpenLearn = onOpenLearn)

        CardsShortcut(
            total = cards.size,
            custom = cards.count { it.custom },
            onAddCard = onAddCard,
            onOpenCards = onOpenCards,
        )
    }
}

@Composable
private fun HomeHeader(nickname: String, streakDays: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(initial = nickname)
        Column(Modifier.weight(1f)) {
            Text(
                text = "Hello, $nickname",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            MutedText("What do you want to learn today?")
        }
        if (streakDays > 0) {
            InfoPill(
                icon = Icons.Filled.Star,
                text = if (streakDays == 1) "1 day" else "$streakDays days",
            )
        }
    }
}

@Composable
private fun HeroCard(
    dueCount: Int,
    reviewedToday: Int,
    goal: Int,
    progress: Float,
    onContinue: () -> Unit,
) {
    val ink = MaterialTheme.colorScheme.onPrimary
    val lime = MaterialTheme.colorScheme.primary
    val allDone = dueCount == 0

    AppCard(modifier = Modifier.fillMaxWidth(), containerColor = lime) {
        Text(
            text = "PICK UP WHERE YOU LEFT OFF",
            style = MaterialTheme.typography.labelMedium,
            color = ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when {
                allDone -> "All done today"
                dueCount == 1 -> "1 card due"
                else -> "$dueCount cards due"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = ink,
        )
        Spacer(Modifier.height(12.dp))
        AppProgressBar(progress = progress, color = ink, track = ink.copy(alpha = 0.15f))
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${reviewedToday.coerceAtMost(goal)} of $goal cards today. No timer, no penalty for skipping.",
            style = MaterialTheme.typography.bodySmall,
            color = ink,
        )
        Spacer(Modifier.height(20.dp))
        PillButton(
            text = if (allDone) "Practice again" else "Continue learning",
            onClick = onContinue,
            container = ink,
            content = lime,
        )
    }
}

@Composable
private fun DeckRow(topic: String, cardCount: Int, mastery: Float?, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        onClickLabel = "Practice $topic",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            IconBadge(icon = topicIcon(topic), color = topicColor(topic))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText(
                    if (mastery == null) {
                        "$cardCount cards, not started"
                    } else {
                        "$cardCount cards, ${(mastery * 100).toInt()}% good or easy"
                    },
                )
                AppProgressBar(progress = mastery ?: 0f, track = MaterialTheme.colorScheme.outline)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GoalCard(reviewedToday: Int, goal: Int, progress: Float, onEditGoal: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Today's goal",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onEditGoal) { Text("Edit goal") }
        }
        MutedText("Review $goal cards")
        Spacer(Modifier.height(12.dp))
        AppProgressBar(progress = progress)
        Spacer(Modifier.height(8.dp))
        MutedText("${reviewedToday.coerceAtMost(goal)} / $goal cards. Rest days are fine.")
    }
}

@Composable
private fun QuickPracticeRow(onStartPractice: (String) -> Unit, onMore: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SampleData.topics.forEach { topic ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClickLabel = "Practice $topic", role = Role.Button) { onStartPractice(topic) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconBadge(icon = topicIcon(topic), color = topicColor(topic), size = 56.dp)
                Text(
                    text = topic,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
    TextButton(onClick = onMore) { Text("More ways to practice") }
}

@Composable
private fun LectureCard(onOpenLearn: () -> Unit) {
    val featured = SampleData.links.first { it.kind == "Lecture" }
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpenLearn,
        onClickLabel = "Open lectures",
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
}

@Composable
private fun CardsShortcut(total: Int, custom: Int, onAddCard: () -> Unit, onOpenCards: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "My cards",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        MutedText("$total cards, $custom made by you")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PillButton(
                text = "Add card",
                onClick = onAddCard,
                modifier = Modifier.weight(1f),
                showArrow = false,
            )
            PillButton(
                text = "View all",
                onClick = onOpenCards,
                modifier = Modifier.weight(1f),
                container = MaterialTheme.colorScheme.surfaceVariant,
                content = MaterialTheme.colorScheme.onSurface,
                showArrow = false,
            )
        }
    }
}

@Composable
private fun GoalDialog(current: Int, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MutedText("How many cards would you like to review each day?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 5, 10).forEach { option ->
                        TagChip(
                            text = option.toString(),
                            selected = option == current,
                            onClick = { onSelect(option) },
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}