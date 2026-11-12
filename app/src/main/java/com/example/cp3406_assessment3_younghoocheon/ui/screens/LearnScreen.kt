package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.LinkItem
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.Avatar
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.PlayBadge
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.Thumbnail
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sand
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.LearnState
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.LearnViewModel

@Composable
fun LearnScreen() {
    val uriHandler = LocalUriHandler.current
    val container = appContainer()
    val featured = SampleData.links.first { it.kind == "Lecture" }
    val others = SampleData.links.filter { it.url != featured.url }
    val viewModel: LearnViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LearnViewModel(container.videoRepository, featured.url) }
        },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ScreenTitle("Learn")
                MutedText("Free lectures from Stanford Online")
            }
        }
        item {
            NoticeBox("This screen asks YouTube for the lecture title and thumbnail, so YouTube can see your IP address. Links open in YouTube.")
        }
        when (current) {
            LearnState.Loading -> item { LoadingCard() }
            is LearnState.Success -> item {
                FeaturedLecture(
                    title = current.info.title,
                    author = current.info.author,
                    thumbnailUrl = current.info.thumbnailUrl,
                    tagText = "Loaded from YouTube",
                    tagColor = Mint,
                    onOpen = { uriHandler.openUri(featured.url) },
                )
            }
            LearnState.Failed -> {
                item { FailedCard(onRetry = viewModel::load) }
                item {
                    FeaturedLecture(
                        title = featured.shortTitle,
                        author = featured.subtitle,
                        thumbnailUrl = featured.thumbnailUrl,
                        tagText = "Saved copy",
                        tagColor = Sand,
                        onOpen = { uriHandler.openUri(featured.url) },
                    )
                }
            }
        }
        item { SectionHeader("Channels") }
        items(others, key = { it.url }) { link ->
            ChannelRow(link = link, onOpen = { uriHandler.openUri(link.url) })
        }
    }
}

@Composable
private fun LoadingCard() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
            )
            Column {
                Text(
                    text = "Loading lecture info",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText("Asking YouTube for the title and thumbnail")
            }
        }
    }
}

@Composable
private fun FailedCard(onRetry: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Could not load lecture info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(4.dp))
        MutedText("Check your connection and try again. A saved copy is shown below.")
        Spacer(Modifier.height(16.dp))
        PillButton(text = "Try again", onClick = onRetry, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun FeaturedLecture(
    title: String,
    author: String,
    thumbnailUrl: String?,
    tagText: String,
    tagColor: Color,
    onOpen: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
            Thumbnail(
                url = thumbnailUrl,
                label = title,
                modifier = Modifier.fillMaxSize(),
            )
            PlayBadge(modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.height(16.dp))
        TopicTag(text = tagText, color = tagColor)
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        MutedText(author)
        Spacer(Modifier.height(20.dp))
        PillButton(
            text = "Watch on YouTube",
            onClick = onOpen,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ChannelRow(link: LinkItem, onOpen: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen,
        onClickLabel = "Open in YouTube",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar(initial = link.shortTitle, color = Sky)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = link.shortTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText(link.subtitle)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Lime,
            )
        }
    }
}