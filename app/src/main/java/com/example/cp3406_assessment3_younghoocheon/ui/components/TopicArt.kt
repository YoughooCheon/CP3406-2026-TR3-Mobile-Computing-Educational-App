package com.example.cp3406_assessment3_younghoocheon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.R

/** The illustration for a topic, or null for a topic that has none (like "All"). */
@DrawableRes
fun topicArt(topic: String): Int? = when (topic) {
    "Privacy" -> R.drawable.topic_privacy
    "Access" -> R.drawable.topic_access
    "Memory" -> R.drawable.topic_memory
    else -> null
}

/**
 * The picture of a topic on a soft tile in the topic colour.
 * A topic without a picture keeps the plain icon badge, so every screen can call this for any topic.
 * The picture is decorative, so it has no content description.
 */
@Composable
fun TopicArt(
    topic: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val art = topicArt(topic)
    if (art == null) {
        IconBadge(icon = topicIcon(topic), color = color, size = size, modifier = modifier)
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.3f))
                .background(color.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(art),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(size * 0.78f),
            )
        }
    }
}