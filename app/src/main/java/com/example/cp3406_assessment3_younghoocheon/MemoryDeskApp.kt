package com.example.cp3406_assessment3_younghoocheon

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cp3406_assessment3_younghoocheon.data.AppDatabase
import com.example.cp3406_assessment3_younghoocheon.data.SettingsStore
import com.example.cp3406_assessment3_younghoocheon.data.StudyRepository
import com.example.cp3406_assessment3_younghoocheon.data.TopicGuideRepository
import com.example.cp3406_assessment3_younghoocheon.data.VideoRepository
import com.example.cp3406_assessment3_younghoocheon.data.WikipediaRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.create(context)
    val settingsStore = SettingsStore(context)
    private val topicSummaryDao = database.topicSummaryDao()
    val repository = StudyRepository(
        reviewDao = database.reviewDao(),
        cardDao = database.cardDao(),
        settingsSource = settingsStore,
        clearCache = { topicSummaryDao.deleteAll() },
    )
    val videoRepository = VideoRepository()
    val topicGuideRepository = TopicGuideRepository(topicSummaryDao, WikipediaRepository())
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
