package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
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
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky

/** First screen: an illustration on a lime circle with floating rating tags, then the pitch and a start button. */
@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val lime = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Row(
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
            )
        }

        HeroArt(modifier = Modifier.weight(1f).fillMaxWidth())

        Text(
            text = buildAnnotatedString {
                append("Learn Smarter,\n")
                withStyle(SpanStyle(color = lime)) { append("Remember") }
                append(" More")
            },
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp, lineHeight = 44.sp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(12.dp))
        MutedText("Short review sessions, no streak pressure, and everything stays on your device.")
        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Dot(active = true)
                Dot(active = false)
                Dot(active = false)
            }
            PillButton(text = "Get Started", onClick = onGetStarted)
        }
    }
}

@Composable
private fun HeroArt(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clearAndSetSemantics { }) {
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

@Composable
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(width = if (active) 24.dp else 8.dp, height = 8.dp)
            .clip(CircleShape)
            .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
    )
}