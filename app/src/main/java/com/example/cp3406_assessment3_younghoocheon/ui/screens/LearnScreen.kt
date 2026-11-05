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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
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
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky

@Composable
fun LearnScreen() {
    val uriHandler = LocalUriHandler.current
    val featured = SampleData.links.first { it.kind == "Lecture" }
    val others = SampleData.links.filter { it.url != featured.url }

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
            NoticeBox("Thumbnails load from YouTube, and links open in YouTube. YouTube may collect data when you watch.")
        }
        item {
            FeaturedLecture(link = featured, onOpen = { uriHandler.openUri(featured.url) })
        }
        item { SectionHeader("Channels") }
        items(others, key = { it.url }) { link ->
            ChannelRow(link = link, onOpen = { uriHandler.openUri(link.url) })
        }
    }
}

@Composable
private fun FeaturedLecture(link: LinkItem, onOpen: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
            Thumbnail(
                url = link.thumbnailUrl,
                label = link.shortTitle,
                modifier = Modifier.fillMaxSize(),
            )
            PlayBadge(modifier = Modifier.align(Alignment.Center))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = link.shortTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        MutedText(link.subtitle)
        Spacer(Modifier.height(8.dp))
        MutedText(link.title)
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