# MemoryDesk

A flashcard and puzzle app that helps university students remember what they study.
Built for **CP3406 Mobile Computing, Assessment 3 (Education App)** at James Cook University Singapore, with Kotlin, Jetpack Compose and Material Design 3.

| | |
|---|---|
| Author | Younghoo Cheon |
| Subject | CP3406 Mobile Computing, 2026 Trimester 3 |
| Platform | Android (minSdk 26, targetSdk 35, compileSdk 35) |
| Language and UI | Kotlin, Jetpack Compose, Material Design 3 |
| Build | Gradle (Kotlin DSL), JDK 17, Android Studio |

## 1. Purpose and target audience

University students study from lecture slides and long readings, and most of what they read is forgotten within days unless it is recalled. MemoryDesk turns short study questions into a daily routine of a few minutes.

| Question | Answer |
|---|---|
| Who is it for? | University students, especially first and second year IT students who need to remember concepts for exams |
| Which problem does it solve? | Forgetting what was studied, and passive re-reading that does not build memory |
| How does it help memory retention? | **Active recall.** The student sees a question, tries to answer, flips the card and rates how well they remembered (Again / Hard / Good / Easy) |
| How does it help analytical thinking? | **Word Builder.** The words of an answer are shuffled and the student rebuilds the sentence, which forces them to think about how the idea is structured |
| How does it keep the student motivated? | Study streak, weekly chart, topic mastery and achievement badges, without guilt prompts or penalties |
| What does it keep private? | Everything stays on the device. There is no account, no analytics and no advertising |

## 2. Features

| Area | What it does |
|---|---|
| Welcome | A three-page introduction (swipe or Next, Skip at any time) that explains flashcards, Word Builder and privacy. It is shown once on the first launch |
| Home | Today's progress, daily goal, deck shortcuts, a featured lecture and topic cards with artwork |
| Practice hub | The student chooses the activity (Flashcards or Word Builder), the topic and the session length. It shows today's numbers and the mastery of each topic |
| Activity 1: Flashcards | Flip-card review with a four-level rating. Every rating is saved as a review |
| Activity 2: Word Builder | Tap the shuffled words to rebuild the answer of a card, with check and reset. Every check is saved as a review |
| Topic guide | A short Wikipedia summary for each topic (Privacy, Access, Memory). It is saved in Room so it also opens offline, and links to the full article and to practice |
| My cards | Create your own cards (200 character limit, validation) and delete them |
| Progress | Study streak, weekly chart, Good-or-Easy percentage, mastery per topic and achievement badges |
| Lectures | Loads the title and thumbnail of a Stanford Online lecture from the YouTube oEmbed API, with loading, error, retry and a saved copy |
| Explore | Directory of IT staff and programs at JCU Singapore with search and filters, plus a shortcut that opens a Google Scholar search in the browser |
| Profile | Nickname, achievements and shortcuts to My cards, Settings and the Privacy Center |
| Settings | Cards per session, daily goal, nickname, text size (S/M/L), high contrast, reduce motion and reminder switches. All saved with DataStore |
| Privacy Center | Shows what is stored and what is never collected, exports all data as JSON and deletes all data |

## 3. Screens and navigation

The bottom bar has five top-level tabs. Their state is saved and restored when the student switches tabs.

| Tab | Route | Opens |
|---|---|---|
| Home | `home` | Today overview, shortcuts, featured lecture |
| Explore | `explore` | Staff and program directory |
| Practice | `practice_hub` | Activity, topic and length chooser |
| Progress | `stats` | Statistics and badges |
| Profile | `profile` | Nickname and links to other screens |

Full-screen routes (no bottom bar):

| Route | Arguments | Purpose |
|---|---|---|
| `welcome` | none | First-launch introduction |
| `practice/{topic}` | topic | Flashcard session |
| `builder/{topic}` | topic | Word Builder session |
| `topic_guide/{topic}` | topic | Wikipedia summary of a topic |
| `staff/{id}` | id | Staff member details |
| `program/{id}` | id | Program details |
| `department`, `learn`, `cards`, `add_card`, `settings`, `privacy` | none | Supporting screens |

### Deep links

The app opens directly on a screen from outside the app. They are declared in `AndroidManifest.xml` and in `AppNavHost`.

| Link | Opens |
|---|---|
| `memorydesk://topic/Privacy` | Topic guide for Privacy (also `Access`, `Memory`) |
| `memorydesk://practice` | Practice hub |
| `memorydesk://learn` | Lectures |

Try one on a running emulator:

```
adb shell am start -a android.intent.action.VIEW -d "memorydesk://topic/Privacy"
```

## 4. Architecture

```
Compose UI (ui/screens, ui/components)
      |  state / events
ViewModels (ui/viewmodel)          StateFlow + viewModelScope
      |
Repositories (data)                StudyRepository, VideoRepository, TopicGuideRepository
      |                                  |
Room (reviews, cards,   DataStore    YouTube oEmbed, Wikipedia REST
topic summaries)                     (HttpURLConnection)
```

| Layer | Responsibility | Main classes |
|---|---|---|
| UI | Draws state, sends events, no business logic | `HomeScreen`, `PracticeHubScreen`, `PracticeScreen`, `WordBuilderScreen`, `TopicGuideScreen`, `StatsScreen` and others |
| ViewModel | Holds screen state in `StateFlow`, calls the repository in `viewModelScope` | `PracticeViewModel`, `WordBuilderViewModel`, `LearnViewModel`, `TopicGuideViewModel`, `CardsViewModel`, `SettingsViewModel`, `OverviewViewModel` |
| Repository | Single source of truth, joins database, settings and network | `StudyRepository`, `VideoRepository`, `WikipediaRepository`, `TopicGuideRepository` |
| Data sources | Storage and network | Room DAOs, `SettingsStore` (DataStore), `HttpURLConnection` |
| Pure logic | No Android dependencies, easy to test | `StatsCalculator`, `WordPuzzle`, `Achievements`, `ExploreSearch` |

Design decisions:

- **MVVM with a Repository.** Screens only observe `StateFlow` and send events. `StudyRepository` is the single source of truth for cards, reviews and statistics.
- **Manual dependency injection.** `AppContainer` (created in `MemoryDeskApp`) owns the database, `SettingsStore` and repositories. Every screen creates its ViewModel with one shared helper, `appViewModel { ... }`, instead of repeating factory code.
- **Interfaces for testing.** The repositories and ViewModels depend on the small interfaces `SettingsSource`, `VideoSource` and `SummarySource`, so tests can replace storage and network with fakes.
- **Shared UI parts.** Loading and error cards (`StatusCards.kt`) and topic artwork (`TopicArt.kt`) are reused across screens.

### Package structure

| Package | Content |
|---|---|
| `data` | Entities, DAOs, `AppDatabase`, repositories, settings, sample content and pure logic |
| `ui/screens` | One file per screen |
| `ui/components` | Reusable composables (`Common`, `Widgets`, `StatusCards`, `TopicArt`) |
| `ui/navigation` | `AppNavHost`, routes, bottom bar and deep links |
| `ui/viewmodel` | ViewModels and the `appViewModel` helper |
| `ui/theme` | Colours, typography and Material 3 theme |

## 5. Data and networking

### Local storage

| Store | Content | Notes |
|---|---|---|
| Room `reviews` | Every rating with topic, time and day | Statistics are computed from these rows with Kotlin Flows, so every screen updates automatically |
| Room `cards` | Cards created by the student | 200 character limit, validated before saving |
| Room `topic_summaries` | Saved Wikipedia summaries (title, description, extract, page URL, time fetched) | Added in version 3 |
| DataStore (Preferences) | Nickname, daily goal, cards per session, text size, high contrast, reduce motion, reminder switches | Read as a `Flow` through `SettingsSource` |

Room database `memorydesk.db` is at **version 3**. The migrations 1 to 2 and 2 to 3 are included, so an app that is already installed keeps its data. Every schema version is exported to `app/schemas`.

### Network (advanced API)

| API | Used for | Details |
|---|---|---|
| Wikipedia REST summary (`/api/rest_v1/page/summary/{title}`) | Topic guide | `WikipediaRepository` sends a `User-Agent`, uses 8 second timeouts, runs on a background dispatcher and returns a `Result`. Disambiguation pages are rejected. Only the three known topics (Privacy, Access control, Memory) are ever sent to the network |
| YouTube oEmbed | Featured lecture | `VideoRepository` returns a `Result`. `LearnViewModel` turns it into `Loading`, `Success` or `Failed` |

Offline behaviour of the topic guide (`TopicGuideRepository`):

| Situation | What the student sees |
|---|---|
| Saved copy is less than 7 days old | The saved copy, no network call |
| Saved copy is older than 7 days | A fresh copy is fetched and saved |
| Network fails, saved copy exists | The old copy, marked as a saved copy |
| Network fails, nothing saved | An error card with a Retry button |

## 6. Ethical and professional design

These decisions follow the ACS Code of Ethics (public interest, quality of life, honesty, competence, professionalism) and the ethical issues researched in Assessment 2.

| Concern | Decision in the app |
|---|---|
| Privacy and data minimisation | No account, no analytics, no ads. Only a nickname, your own cards, review ratings, settings and saved Wikipedia text are stored, on the device only. Backups are disabled (`allowBackup=false`) |
| Transparency | The Privacy Center lists what is stored and what is never collected. The Lectures and topic guide screens warn that YouTube and Wikipedia can see the device's IP address. Wikipedia text is credited and shown with its CC BY-SA 4.0 licence |
| User control | Export all data as JSON and delete all data at any time. Deleting also removes the saved Wikipedia summaries |
| Persuasive design | No guilt prompts, no timers, no penalty for skipping. Badges are only ever unlocked, never taken away. A missed day is "only a record" |
| Accessibility and inclusiveness | Text size setting, high contrast, reduce motion, content descriptions and semantic roles for screen readers, and large touch targets |
| Safe, age-appropriate content | Built-in cards are short, neutral study questions. Cards you create stay on the device and are never shared unless you export them yourself |

## 7. Testing

| Type | Location | Needs a device | What it covers |
|---|---|---|---|
| Local unit tests | `app/src/test` | No | Statistics and streaks (`StatsCalculatorTest`), Word Builder logic (`WordPuzzleTest`), search (`ExploreSearchTest`), models, achievements and settings defaults (`ModelTest`), repository operations with fake DAOs (`StudyRepositoryTest`), the Practice, Word Builder and Learn ViewModels (`ViewModelTest`), Wikipedia parsing, the saved-copy and offline rules and the Topic guide ViewModel (`TopicGuideTest`) |
| Compose UI tests | `app/src/androidTest` | Yes (emulator) | Reusable components (button, chip, progress bar, nickname dialog) in `ComponentTest`, and a walk through the app (welcome, navigation, one flashcard session) in `AppFlowTest` |

Fakes (`FakeDataSources`, fake DAOs, fake `SummarySource`) replace Room, DataStore and the network, so unit tests run in seconds on the computer.

Run them:

```
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

In Android Studio: right-click `app/src/test`, then **Run 'Tests in ...'**.

## 8. Build and run

1. Open the project in Android Studio (JDK 17) and wait for the Gradle sync to finish.
2. Select the `app` configuration and an emulator or device (Android 8.0 or newer).
3. Press **Run**. The first launch shows the three-page welcome introduction.
4. The Wikipedia and YouTube features need an internet connection. Everything else works offline.

## 9. Assessment mapping

| Criterion | Where to look |
|---|---|
| Code quality | Small files by role, shared helpers (`appViewModel`, `StatusCards`), pure logic separated from Android code |
| Design | Material 3 theme, welcome introduction, topic artwork, text size and high contrast settings |
| Navigation | Five-tab bottom bar with saved state, routes with arguments, deep links (section 3) |
| Architecture | MVVM, Repository, manual DI, interfaces for tests (section 4) |
| Advanced API | Wikipedia REST with a Room cache and offline fallback, YouTube oEmbed (section 5) |
| Unit testing | Section 7 |
| GitHub | Commit history, this README |
| Reflection | Submitted separately as a PDF based on Gibbs' Reflective Cycle |
