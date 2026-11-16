package com.example.cp3406_assessment3_younghoocheon.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Reads and writes [AppSettings] using Preferences DataStore. */
class SettingsStore(private val context: Context) : SettingsSource {

    private object Keys {
        val dailyReminder = booleanPreferencesKey("daily_reminder")
        val breakReminders = booleanPreferencesKey("break_reminders")
        val highContrast = booleanPreferencesKey("high_contrast")
        val reduceMotion = booleanPreferencesKey("reduce_motion")
        val textSize = stringPreferencesKey("text_size")
        val dailyGoal = intPreferencesKey("daily_goal")
        val cardsPerSession = intPreferencesKey("cards_per_session")
        val nickname = stringPreferencesKey("nickname")
    }

    override val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            dailyReminder = prefs[Keys.dailyReminder] ?: false,
            breakReminders = prefs[Keys.breakReminders] ?: true,
            highContrast = prefs[Keys.highContrast] ?: false,
            reduceMotion = prefs[Keys.reduceMotion] ?: false,
            textSize = prefs[Keys.textSize] ?: "M",
            dailyGoal = prefs[Keys.dailyGoal] ?: DEFAULT_DAILY_GOAL,
            cardsPerSession = prefs[Keys.cardsPerSession] ?: 0,
            nickname = prefs[Keys.nickname] ?: "Student",
        )
    }

    suspend fun setDailyReminder(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.dailyReminder] = value }
    }

    suspend fun setBreakReminders(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.breakReminders] = value }
    }

    suspend fun setHighContrast(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.highContrast] = value }
    }

    suspend fun setReduceMotion(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.reduceMotion] = value }
    }

    suspend fun setTextSize(value: String) {
        context.settingsDataStore.edit { it[Keys.textSize] = value }
    }

    suspend fun setDailyGoal(value: Int) {
        context.settingsDataStore.edit { it[Keys.dailyGoal] = value }
    }

    suspend fun setCardsPerSession(value: Int) {
        context.settingsDataStore.edit { it[Keys.cardsPerSession] = value }
    }

    suspend fun setNickname(value: String) {
        context.settingsDataStore.edit { it[Keys.nickname] = value.trim().take(MAX_NICKNAME_LENGTH) }
    }

    override suspend fun clear() {
        context.settingsDataStore.edit { it.clear() }
    }
}