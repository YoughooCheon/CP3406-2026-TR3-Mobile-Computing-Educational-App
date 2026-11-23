package com.example.cp3406_assessment3_younghoocheon.data

import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.TopicGuideState
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.TopicGuideViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TopicGuideTest {

    private val privacy = TopicSummary(
        title = "Privacy",
        description = "Ability to keep information to yourself",
        extract = "Privacy is the ability of an individual to seclude themselves.",
        pageUrl = "https://en.wikipedia.org/wiki/Privacy",
    )

    private var time = 10_000_000_000L
    private lateinit var dao: FakeTopicSummaryDao

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeTopicSummaryDao()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun repository(source: FakeSummarySource) = TopicGuideRepository(dao, source, now = { time })

    // Parsing the Wikipedia answer

    @Test
    fun parseSummary_readsTitleDescriptionExtractAndPageUrl() {
        val body = """
            {"type":"standard","title":"Privacy","description":"Ability to keep information to yourself",
             "extract":"Privacy is the ability of an individual to seclude themselves.",
             "content_urls":{"desktop":{"page":"https://en.wikipedia.org/wiki/Privacy"}}}
        """.trimIndent()

        assertEquals(privacy, WikipediaRepository.parseSummary(body))
    }

    @Test
    fun parseSummary_usesAFallbackLinkAndAnEmptyDescriptionWhenMissing() {
        val summary = WikipediaRepository.parseSummary("""{"title":"Access control","extract":"Text."}""")

        assertEquals("", summary.description)
        assertEquals("https://en.wikipedia.org/wiki/Access_control", summary.pageUrl)
    }

    @Test
    fun parseSummary_rejectsDisambiguationPages() {
        val body = """{"type":"disambiguation","title":"Access","extract":"Access may refer to:"}"""

        assertTrue(runCatching { WikipediaRepository.parseSummary(body) }.isFailure)
    }

    @Test
    fun parseSummary_failsWhenTheExtractIsMissing() {
        assertTrue(runCatching { WikipediaRepository.parseSummary("""{"title":"Privacy"}""") }.isFailure)
    }

    // Topic to article

    @Test
    fun topicPages_mapsTheAppTopicsAndNothingElse() {
        assertEquals("Privacy", TopicPages.titleFor("Privacy"))
        assertEquals("Access control", TopicPages.titleFor("Access"))
        assertEquals("Memory", TopicPages.titleFor("Memory"))
        assertNull(TopicPages.titleFor("All"))
        assertNull(TopicPages.titleFor("../etc/passwd"))
    }

    // Repository: network the first time, the saved copy afterwards

    @Test
    fun load_asksWikipediaAndSavesTheAnswerWhenNothingIsSaved() = runTest {
        val source = FakeSummarySource(listOf(Result.success(privacy)))

        val guide = repository(source).load("Privacy").getOrThrow()

        assertEquals(privacy, guide.summary)
        assertFalse(guide.fromCache)
        assertEquals(1, source.calls)
        assertEquals(time, dao.rows.getValue("Privacy").fetchedAt)
    }

    @Test
    fun load_usesAFreshSavedCopyWithoutTheNetwork() = runTest {
        val source = FakeSummarySource(listOf(Result.success(privacy)))
        val repository = repository(source)
        repository.load("Privacy")

        time += TopicGuideRepository.MAX_AGE_MS - 1
        val guide = repository.load("Privacy").getOrThrow()

        assertTrue(guide.fromCache)
        assertEquals(1, source.calls)
    }

    @Test
    fun load_refreshesASavedCopyOlderThanSevenDays() = runTest {
        val newer = privacy.copy(extract = "A newer text.")
        val source = FakeSummarySource(listOf(Result.success(privacy), Result.success(newer)))
        val repository = repository(source)
        repository.load("Privacy")

        time += TopicGuideRepository.MAX_AGE_MS + 1
        val guide = repository.load("Privacy").getOrThrow()

        assertFalse(guide.fromCache)
        assertEquals("A newer text.", guide.summary.extract)
        assertEquals(2, source.calls)
        assertEquals("A newer text.", dao.rows.getValue("Privacy").extract)
    }

    @Test
    fun load_fallsBackToAnOldSavedCopyWhenTheNetworkFails() = runTest {
        val source = FakeSummarySource(listOf(Result.success(privacy), Result.failure(IOException("offline"))))
        val repository = repository(source)
        repository.load("Privacy")

        time += TopicGuideRepository.MAX_AGE_MS + 1
        val guide = repository.load("Privacy").getOrThrow()

        assertTrue(guide.fromCache)
        assertEquals(privacy, guide.summary)
    }

    @Test
    fun load_failsWhenOfflineAndNothingIsSaved() = runTest {
        val source = FakeSummarySource(listOf(Result.failure(IOException("offline"))))

        assertTrue(repository(source).load("Privacy").isFailure)
        assertTrue(dao.rows.isEmpty())
    }

    @Test
    fun load_neverSendsAnUnknownTopicToTheNetwork() = runTest {
        val source = FakeSummarySource(listOf(Result.success(privacy)))

        assertTrue(repository(source).load("Something else").isFailure)
        assertEquals(0, source.calls)
    }

    @Test
    fun load_asksForTheMappedArticleTitle() = runTest {
        val source = FakeSummarySource(listOf(Result.success(privacy.copy(title = "Access control"))))

        repository(source).load("Access")

        assertEquals("Access control", source.lastTitle)
    }

    // ViewModel

    @Test
    fun viewModel_showsTheGuideOnceLoaded() = runTest {
        val viewModel = TopicGuideViewModel(repository(FakeSummarySource(listOf(Result.success(privacy)))), "Privacy")

        val state = viewModel.state.value
        assertTrue(state is TopicGuideState.Ready)
        assertEquals(privacy, (state as TopicGuideState.Ready).guide.summary)
    }

    @Test
    fun viewModel_showsFailedThenRecoversOnRetry() = runTest {
        val source = FakeSummarySource(listOf(Result.failure(IOException("offline")), Result.success(privacy)))
        val viewModel = TopicGuideViewModel(repository(source), "Privacy")
        assertEquals(TopicGuideState.Failed, viewModel.state.value)

        viewModel.load()

        assertTrue(viewModel.state.value is TopicGuideState.Ready)
    }

    // Delete all data also clears the saved summaries

    @Test
    fun deleteAll_runsTheCacheClearHook() = runTest {
        var cleared = 0
        val studyRepository = StudyRepository(
            FakeReviewDao(),
            FakeCardDao(),
            FakeSettingsSource(),
            clearCache = { cleared += 1 },
        )

        studyRepository.deleteAll()

        assertEquals(1, cleared)
    }
}

/** In-memory stand-in for [TopicSummaryDao]. */
class FakeTopicSummaryDao : TopicSummaryDao {
    val rows = mutableMapOf<String, TopicSummaryEntity>()

    override suspend fun upsert(summary: TopicSummaryEntity) {
        rows[summary.title] = summary
    }

    override suspend fun get(title: String): TopicSummaryEntity? = rows[title]

    override suspend fun deleteAll() {
        rows.clear()
    }
}

/** Answers with the given results in order (the last one repeats) and counts how often it was asked. */
class FakeSummarySource(results: List<Result<TopicSummary>>) : SummarySource {
    private val queue = results.toMutableList()
    var calls = 0
        private set
    var lastTitle: String? = null
        private set

    override suspend fun fetch(title: String): Result<TopicSummary> {
        calls += 1
        lastTitle = title
        return if (queue.size > 1) queue.removeAt(0) else queue.first()
    }
}