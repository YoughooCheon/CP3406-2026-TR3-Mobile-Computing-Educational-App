package com.example.cp3406_assessment3_younghoocheon.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    private object Keys {
        val dailyReminder = booleanPreferencesKey("daily_reminder")
        val breakReminders = booleanPreferencesKey("break_reminders")
        val highContrast = booleanPreferencesKey("high_contrast")
        val reduceMotion = booleanPreferencesKey("reduce_motion")
        val textSize = stringPreferencesKey("text_size")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            dailyReminder = prefs[Keys.dailyReminder] ?: false,
            breakReminders = prefs[Keys.breakReminders] ?: true,
            highContrast = prefs[Keys.highContrast] ?: false,
            reduceMotion = prefs[Keys.reduceMotion] ?: false,
            textSize = prefs[Keys.textSize] ?: "M",
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

    suspend fun clear() {
        context.settingsDataStore.edit { it.clear() }
    }
}