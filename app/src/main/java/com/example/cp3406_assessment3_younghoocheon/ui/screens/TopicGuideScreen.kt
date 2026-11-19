package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cp3406_assessment3_younghoocheon.data.TopicGuide
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.BackHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.ErrorCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.LoadingCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sand
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.TopicGuideState
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.TopicGuideViewModel
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.appViewModel

/**
 * Background reading for one topic. The text comes from the Wikipedia REST API and is saved in Room,
 * so it also opens offline. From here the student can read the full article or go straight to practice.
 */
@Composable
fun TopicGuideScreen(topic: String, onBack: () -> Unit, onPractice: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val viewModel: TopicGuideViewModel = appViewModel { TopicGuideViewModel(it.topicGuideRepository, topic) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { BackHeader(title = "About " + topic, onBack = onBack) }
        item { MutedText("A short background from Wikipedia") }
        item {
            NoticeBox("This screen asks Wikipedia for a summary, so Wikipedia can see your IP address. The text is kept on this device for offline use. Wikipedia text is available under CC BY-SA 4.0.")
        }
        when (current) {
            TopicGuideState.Loading -> item {
                LoadingCard(title = "Loading summary", message = "Asking Wikipedia about " + topic)
            }
            is TopicGuideState.Ready -> {
                item {
                    GuideCard(guide = current.guide, onOpen = { uriHandler.openUri(current.guide.summary.pageUrl) })
                }
                item {
                    PillButton(
                        text = "Practice " + topic,
                        onClick = onPractice,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            TopicGuideState.Failed -> item {
                ErrorCard(
                    title = "Could not load the summary",
                    message = "Check your connection and try again. You can still practice this topic.",
                    onRetry = viewModel::load,
                )
            }
        }
    }
}

@Composable
private fun GuideCard(guide: TopicGuide, onOpen: () -> Unit) {
    val summary = guide.summary
    AppCard(modifier = Modifier.fillMaxWidth()) {
        TopicTag(
            text = if (guide.fromCache) "Saved copy" else "Loaded from Wikipedia",
            color = if (guide.fromCache) Sand else Mint,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = summary.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (summary.description.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            MutedText(summary.description)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = summary.extract,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(20.dp))
        PillButton(text = "Read on Wikipedia", onClick = onOpen, modifier = Modifier.fillMaxWidth())
    }
}
