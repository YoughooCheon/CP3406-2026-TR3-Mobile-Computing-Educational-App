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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val lime = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append("Memory")
                withStyle(SpanStyle(color = lime)) { append("Desk") }
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CardStack()
        }

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
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(width = if (active) 24.dp else 8.dp, height = 8.dp)
            .clip(CircleShape)
            .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
    )
}

@Composable
private fun CardStack() {
    Box(
        modifier = Modifier.clearAndSetSemantics { },
        contentAlignment = Alignment.Center,
    ) {
        StackCard(rotation = -10f, color = MaterialTheme.colorScheme.surfaceVariant, label = null)
        StackCard(rotation = 7f, color = MaterialTheme.colorScheme.secondary, label = null)
        StackCard(rotation = 0f, color = MaterialTheme.colorScheme.primary, label = "?")
    }
}

@Composable
private fun StackCard(rotation: Float, color: Color, label: String?) {
    Box(
        modifier = Modifier
            .size(width = 210.dp, height = 280.dp)
            .graphicsLayer { rotationZ = rotation }
            .clip(RoundedCornerShape(32.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 96.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displaySmall,
            )
        }
    }
}