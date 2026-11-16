package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cp3406_assessment3_younghoocheon.R
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Ink
import com.example.cp3406_assessment3_younghoocheon.ui.theme.LocalReduceMotion
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky
import kotlinx.coroutines.launch

private const val PAGE_COUNT = 3

/**
 * First screen: a three page introduction that can be swiped or stepped with the Next button.
 * Page 1 is the pitch, page 2 shows the two ways to practice and page 3 explains privacy.
 * Skip and Get Started both lead to the Home screen.
 */
@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val lime = MaterialTheme.colorScheme.primary
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()
    val reduceMotion = LocalReduceMotion.current
    val isLast = pagerState.currentPage == PAGE_COUNT - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.logo_mark),
                contentDescription = null,
                colorFilter = ColorFilter.tint(lime),
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = buildAnnotatedString {
                    append("Memory")
                    withStyle(SpanStyle(color = lime)) { append("Desk") }
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            if (!isLast) {
                TextButton(onClick = onGetStarted) { Text("Skip") }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { page ->
            IntroPage(page = page)
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PageDots(
                count = PAGE_COUNT,
                current = pagerState.currentPage,
                modifier = Modifier.weight(1f),
            )
            PillButton(
                text = if (isLast) "Get Started" else "Next",
                onClick = {
                    if (isLast) {
                        onGetStarted()
                    } else {
                        scope.launch {
                            val target = pagerState.currentPage + 1
                            if (reduceMotion) {
                                pagerState.scrollToPage(target)
                            } else {
                                pagerState.animateScrollToPage(target)
                            }
                        }
                    }
                },
            )
        }
    }
}

/** One page of the introduction: an illustration that fills the free space, then a headline and a short text. */
@Composable
private fun IntroPage(page: Int) {
    val lime = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxSize()) {
        val artModifier = Modifier.weight(1f).fillMaxWidth()
        when (page) {
            0 -> PitchArt(artModifier)
            1 -> PracticeArt(artModifier)
            else -> PrivacyArt(artModifier)
        }
        Text(
            text = pageTitle(page, lime),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp, lineHeight = 44.sp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(12.dp))
        MutedText(pageText(page))
    }
}

private fun pageTitle(page: Int, highlight: Color): AnnotatedString = buildAnnotatedString {
    val (before, accent, after) = when (page) {
        0 -> Triple("Learn Smarter,\n", "Remember", " More")
        1 -> Triple("Two Ways to\n", "Practice", "")
        else -> Triple("Private by\n", "Design", "")
    }
    append(before)
    withStyle(SpanStyle(color = highlight)) { append(accent) }
    append(after)
}

private fun pageText(page: Int): String = when (page) {
    0 -> "Short review sessions, no streak pressure, and everything stays on your device."
    1 -> "Flip cards to recall an answer, or rebuild it word by word in the Word Builder puzzle."
    else -> "No account and no ads. Your cards and progress stay on this device, and you can export or delete them any time."
}

/** Page 1: the student illustration on a lime circle with floating rating tags. */
@Composable
private fun PitchArt(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clearAndSetSemantics { }) {
        ArtBackdrop()
        Image(
            painter = painterResource(R.drawable.welcome_hero),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxHeight(0.96f),
        )
        FloatingTag("Good", Mint, -8f, Modifier.align(Alignment.TopStart).padding(top = 28.dp))
        FloatingTag("Easy", Sky, 7f, Modifier.align(Alignment.CenterEnd).padding(bottom = 60.dp))
        FloatingTag("Again", Peach, -5f, Modifier.align(Alignment.BottomStart).padding(bottom = 90.dp))
    }
}

/** Page 2: a flashcard and a Word Builder card, drawn with Compose so no extra images are needed. */
@Composable
private fun PracticeArt(modifier: Modifier = Modifier) {
    val lime = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.clearAndSetSemantics { }) {
        ArtBackdrop()
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp)
                .fillMaxWidth(0.72f)
                .graphicsLayer { rotationZ = -4f }
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "QUESTION",
                style = MaterialTheme.typography.labelMedium,
                color = lime,
            )
            Text(
                text = "What is active recall?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MutedText("Tap the card to flip")
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.8f)
                .graphicsLayer { rotationZ = 3f }
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "WORD BUILDER",
                style = MaterialTheme.typography.labelMedium,
                color = lime,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WordTile("Recall", Mint)
                WordTile("before", Sky)
                WordTile("looking", Peach)
            }
        }
    }
}

/** Page 3: a lock on the lime circle with tags for the privacy choices. */
@Composable
private fun PrivacyArt(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clearAndSetSemantics { }) {
        ArtBackdrop()
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(132.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Ink),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
            )
        }
        FloatingTag("No account", Mint, -8f, Modifier.align(Alignment.TopStart).padding(top = 28.dp))
        FloatingTag("No ads", Sky, 7f, Modifier.align(Alignment.CenterEnd).padding(bottom = 60.dp))
        FloatingTag("Export or delete", Peach, -5f, Modifier.align(Alignment.BottomStart).padding(bottom = 90.dp))
    }
}

/** The lime circle and the small mint circle behind every illustration. Call it first inside a Box. */
@Composable
private fun BoxScope.ArtBackdrop() {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
    )
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(84.dp)
            .clip(CircleShape)
            .background(Mint.copy(alpha = 0.35f)),
    )
}

@Composable
private fun WordTile(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(text = text, color = Ink, style = MaterialTheme.typography.labelLarge, maxLines = 1)
    }
}

@Composable
private fun FloatingTag(text: String, color: Color, rotation: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation }
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text = text, color = Ink, style = MaterialTheme.typography.labelLarge)
    }
}

/** Page indicator. Screen readers hear "Page 2 of 3" instead of three unlabeled dots. */
@Composable
private fun PageDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "Page ${current + 1} of $count"
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(count) { index -> Dot(active = index == current) }
    }
}

@Composable
private fun Dot(active: Boolean) {
    val reduceMotion = LocalReduceMotion.current
    val width by animateDpAsState(
        targetValue = if (active) 24.dp else 8.dp,
        animationSpec = if (reduceMotion) snap() else tween(durationMillis = 250),
        label = "dotWidth",
    )
    Box(
        modifier = Modifier
            .size(width = width, height = 8.dp)
            .clip(CircleShape)
            .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
    )
}