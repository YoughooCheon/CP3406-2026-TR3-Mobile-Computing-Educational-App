package com.example.cp3406_assessment3_younghoocheon.data

import kotlinx.coroutines.flow.Flow

/**
 * The part of the settings storage that the repository and the activity ViewModels depend on.
 * [SettingsStore] is the DataStore-backed implementation. Unit tests use a simple fake instead,
 * so the model code can be tested without Android.
 */
interface SettingsSource {
    val settings: Flow<AppSettings>
    suspend fun clear()
}