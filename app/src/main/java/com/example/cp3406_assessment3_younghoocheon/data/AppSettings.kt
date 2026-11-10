package com.example.cp3406_assessment3_younghoocheon.data

data class AppSettings(
    val dailyReminder: Boolean = false,
    val breakReminders: Boolean = true,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val textSize: String = "M",
) {
    val textScale: Float
        get() = when (textSize) {
            "S" -> 0.9f
            "L" -> 1.2f
            else -> 1f
        }
}