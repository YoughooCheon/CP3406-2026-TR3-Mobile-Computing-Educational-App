package com.example.cp3406_assessment3_younghoocheon.data

const val DEFAULT_DAILY_GOAL = 5
const val MAX_NICKNAME_LENGTH = 20

/**
 * User preferences stored on the device with DataStore.
 * [cardsPerSession] uses 0 to mean "all cards".
 */
data class AppSettings(
    val dailyReminder: Boolean = false,
    val breakReminders: Boolean = true,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val textSize: String = "M",
    val dailyGoal: Int = DEFAULT_DAILY_GOAL,
    val cardsPerSession: Int = 0,
    val nickname: String = "Student",
) {
    val textScale: Float
        get() = when (textSize) {
            "S" -> 0.9f
            "L" -> 1.2f
            else -> 1f
        }
}