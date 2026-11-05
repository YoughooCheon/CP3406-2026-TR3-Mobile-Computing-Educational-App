package com.example.cp3406_assessment3_younghoocheon.ui.theme

import androidx.compose.ui.graphics.Color

val Lime = Color(0xFFD5F53B)
val Ink = Color(0xFF10140F)
val Night = Color(0xFF0E110E)
val Plum = Color(0xFF3A1F3F)
val SurfaceDark = Color(0xFF1B211C)
val SurfaceRaised = Color(0xFF283029)
val TextPrimary = Color(0xFFF1F4EF)
val TextMuted = Color(0xFFA9B2A9)
val OutlineDark = Color(0xFF3A433B)
val Mint = Color(0xFFA8F0C6)
val Sky = Color(0xFFBFE3FF)
val Peach = Color(0xFFFFC2A8)
val Sand = Color(0xFFF5D9A0)
val Danger = Color(0xFFFF7A70)

fun topicColor(topic: String): Color = when (topic) {
    "Privacy" -> Sky
    "Access" -> Mint
    "Memory" -> Peach
    else -> Lime
}