package com.example.cp3406_assessment3_younghoocheon

import com.example.cp3406_assessment3_younghoocheon.data.AppSettings
import com.example.cp3406_assessment3_younghoocheon.data.Rating
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.VideoInfo
import com.example.cp3406_assessment3_younghoocheon.data.VideoSource
import com.example.cp3406_assessment3_younghoocheon.data.WordPuzzle
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.LearnState
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.LearnViewModel
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.PracticeViewModel
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.WordBuilderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first

class ViewModelTest {

    private lateinit var repository: StudyRepository
    private lateinit var settings: FakeSettingsSource

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        settings = FakeSettingsSource()
        repository = StudyRepository(FakeReviewDao(), FakeCardDao(), settings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // PracticeViewModel

    @Test
    fun practice_loadsOnlyTheChosenTopic() = runTest {
        val viewModel = PracticeViewModel(repository, settings, "Memory")

        val state = viewModel.state.value
        assertFalse(state.loading)
        assertEquals(2, state.queue.size)
        assertTrue(state.queue.all { it.topic == "Memory" })
    }

    @Test
    fun practice_respectsTheCardsPerSessionSetting() = runTest {
        settings.update(AppSettings(cardsPerSession = 3))

        val viewModel = PracticeViewModel(repository, settings, "All")

        assertEquals(3, viewModel.state.value.queue.size)
    }

    @Test
    fun practice_ratingSavesAReviewAndMovesOn() = runTest {
        val viewModel = PracticeViewModel(repository, settings, "All")
        val first = viewModel.state.value.queue.first()
        viewModel.revealAnswer()

        viewModel.rate(Rating.Good)

        assertEquals(1, viewModel.state.value.index)
        assertFalse(viewModel.state.value.showAnswer)
        assertEquals(setOf(first.id), repository.observeStats().first().reviewedTodayIds)
    }

    @Test
    fun practice_skipsCardsAlreadyReviewedToday() = runTest {
        repository.recordReview(SampleData.cards.first(), Rating.Easy)

        val viewModel = PracticeViewModel(repository, settings, "All")

        assertEquals(SampleData.cards.size - 1, viewModel.state.value.queue.size)
        assertTrue(viewModel.state.value.queue.none { it.id == SampleData.cards.first().id })
    }

    @Test
    fun practice_flipAndGoBackStayInRange() = runTest {
        val viewModel = PracticeViewModel(repository, settings, "All")

        viewModel.flip()
        assertTrue(viewModel.state.value.showAnswer)
        viewModel.flip()
        assertFalse(viewModel.state.value.showAnswer)
        viewModel.goBack()
        assertEquals(0, viewModel.state.value.index)
    }

    // WordBuilderViewModel

    @Test
    fun builder_onlyOffersCardsWithPlayableAnswers() = runTest {
        val viewModel = WordBuilderViewModel(repository, settings, "All")

        val queue = viewModel.state.value.queue
        assertTrue(queue.isNotEmpty())
        assertTrue(queue.all { WordPuzzle.isPlayable(it.back) })
    }

    @Test
    fun builder_pickAndUnpickMoveTilesBetweenPoolAndAnswer() = runTest {
        val viewModel = WordBuilderViewModel(repository, settings, "All")
        val tile = viewModel.state.value.pool.first()
        val poolSize = viewModel.state.value.pool.size

        viewModel.pick(tile)
        assertEquals(listOf(tile), viewModel.state.value.chosen)
        assertEquals(poolSize - 1, viewModel.state.value.pool.size)

        viewModel.unpick(tile)
        assertTrue(viewModel.state.value.chosen.isEmpty())
        assertEquals(poolSize, viewModel.state.value.pool.size)
    }

    private fun solveCurrentPuzzle(viewModel: WordBuilderViewModel) {
        val state = viewModel.state.value
        val words = WordPuzzle.tokenize(state.queue[state.index].back)
        val remaining = state.pool.toMutableList()
        for (word in words) {
            val tile = remaining.first { it.text == word }
            remaining.remove(tile)
            viewModel.pick(tile)
        }
    }

    @Test
    fun builder_correctOrderCountsAsGoodReview() = runTest {
        val viewModel = WordBuilderViewModel(repository, settings, "All")
        solveCurrentPuzzle(viewModel)

        viewModel.check()

        assertEquals(true, viewModel.state.value.result)
        assertEquals(1, viewModel.state.value.correctCount)
        assertEquals(1, repository.observeStats().first().totalReviewed)
        assertEquals(100, repository.observeStats().first().retentionPercent)
    }

    @Test
    fun builder_wrongOrderCountsAsAgainAndLocksTheBoard() = runTest {
        val viewModel = WordBuilderViewModel(repository, settings, "All")
        val state = viewModel.state.value
        val words = WordPuzzle.tokenize(state.queue[state.index].back)
        // Spell the answer with its first and last word swapped, which is never the right order here.
        val wrong = listOf(words.last()) + words.drop(1).dropLast(1) + words.first()
        val remaining = state.pool.toMutableList()
        for (word in wrong) {
            val tile = remaining.first { it.text == word }
            remaining.remove(tile)
            viewModel.pick(tile)
        }

        viewModel.check()
        val chosenAfterCheck = viewModel.state.value.chosen
        viewModel.unpick(chosenAfterCheck.first())

        assertEquals(false, viewModel.state.value.result)
        assertEquals(0, viewModel.state.value.correctCount)
        assertEquals(0, repository.observeStats().first().retentionPercent)
        assertEquals(chosenAfterCheck, viewModel.state.value.chosen)
    }

    @Test
    fun builder_nextDealsTheFollowingPuzzle() = runTest {
        val viewModel = WordBuilderViewModel(repository, settings, "All")
        solveCurrentPuzzle(viewModel)
        viewModel.check()

        viewModel.next()

        val state = viewModel.state.value
        assertEquals(1, state.index)
        assertNotNull(state.queue.getOrNull(1))
        assertEquals(null, state.result)
        assertTrue(state.chosen.isEmpty())
    }

    // LearnViewModel

    private class FakeVideoSource(private val results: MutableList<Result<VideoInfo>>) :
        VideoSource {
        var calls = 0
        override suspend fun fetch(videoUrl: String): Result<VideoInfo> {
            calls += 1
            return results.removeAt(0)
        }
    }

    private val info = VideoInfo("Title", "Author", "https://img/thumb.jpg")

    @Test
    fun learn_successShowsTheLoadedInfo() = runTest {
        val viewModel =
            LearnViewModel(FakeVideoSource(mutableListOf(Result.success(info))), "https://video")

        val state = viewModel.state.value

        assertTrue(state is LearnState.Success)
        assertEquals(info, (state as LearnState.Success).info)
    }

    @Test
    fun learn_failureThenRetryRecovers() = runTest {
        val source = FakeVideoSource(mutableListOf(Result.failure(RuntimeException("offline")), Result.success(info)))
        val viewModel = LearnViewModel(source, "https://video")
        assertTrue(viewModel.state.value is LearnState.Failed)

        viewModel.load()

        assertTrue(viewModel.state.value is LearnState.Success)
        assertEquals(2, source.calls)
    }
}