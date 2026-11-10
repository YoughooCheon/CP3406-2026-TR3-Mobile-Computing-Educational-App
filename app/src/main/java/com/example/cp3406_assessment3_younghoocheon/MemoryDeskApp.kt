package com.example.cp3406_assessment3_younghoocheon

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cp3406_assessment3_younghoocheon.data.AppDatabase
import com.example.cp3406_assessment3_younghoocheon.data.SettingsStore
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.create(context)
    val settingsStore = SettingsStore(context)
    val repository = StudyRepository(database.reviewDao(), settingsStore)
}

class MemoryDeskApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

@Composable
fun appContainer(): AppContainer =
    (LocalContext.current.applicationContext as MemoryDeskApp).container