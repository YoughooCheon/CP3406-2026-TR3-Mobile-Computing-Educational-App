# MemoryDesk

A flashcard and puzzle app that helps university students remember what they study.
Built for **CP3406 Assessment 3 (Education App)** at James Cook University Singapore with Kotlin, Jetpack Compose and Material Design 3.

The target audience is university students. The app supports **memory retention** with active recall (flip cards rated Again / Hard / Good / Easy) and **analytical thinking** with a Word Builder puzzle (put the shuffled words of an answer back in order). Progress is kept on the device only.

## Features

| Area | What it does |
|---|---|
| Landing page | Welcome screen, then a Home screen with today's progress, decks, daily goal, shortcuts and a featured lecture |
| Activity 1: Flashcards | Flip-card review with a 4-level rating. Each rating is saved as a review |
| Activity 2: Word Builder | Tap shuffled words to rebuild a card's answer. Each check is saved as a review |
| My cards | Create your own cards (200 character limit, validation) and delete them |
| Statistics | Study streak, weekly chart, Good-or-Easy percentage, mastery per topic and achievement badges |
| Settings | Cards per session, daily goal, nickname, text size (S/M/L), high contrast and reduce motion. All saved with DataStore |
| Lectures | Loads the title and thumbnail of a Stanford Online lecture from the YouTube oEmbed API, with loading, error, retry and a saved copy |
| Explore | Directory of IT staff and programs at JCU Singapore with search and filters, plus a shortcut that opens a Google Scholar search in the browser |
| Privacy Center | Shows what is stored, exports all data as JSON, deletes all data |

## Architecture

```
Compose UI (ui/screens, ui/components)
      |  state / events
ViewModels (ui/viewmodel)          StateFlow + viewModelScope
      |
Repositories (data)                StudyRepository, VideoRepository
      |                                  |
Room (reviews, cards)  DataStore    YouTube oEmbed (HttpURLConnection)
```

- **MVVM with a Repository.** Screens only observe `StateFlow` and send events. `StudyRepository` is the single source of truth for cards, reviews and statistics.
- **Dependency injection.** Manual constructor injection. `AppContainer` (created in `MemoryDeskApp`) owns the database, `SettingsStore` and repositories, and every ViewModel is created through a `viewModelFactory` that receives its dependencies. The repository and ViewModels depend on the small interfaces `SettingsSource` and `VideoSource`, so tests can replace storage and network with fakes.
- **Pure model logic.** `StatsCalculator`, `WordPuzzle`, `Achievements` and `ExploreSearch` have no Android dependencies, which keeps them easy to unit test.
- **Navigation.** Navigation Compose with a bottom bar for five top-level tabs (state saved and restored), plus full-screen routes with arguments for practice, staff and program details.

### Data and networking

- **Room** (`memorydesk.db`, version 2): `reviews` (every rating with topic, time and day) and `cards` (user-created cards). A migration from version 1 to 2 is included and the schema is exported to `app/schemas`. Statistics are computed from the stored reviews with Kotlin Flows, so every screen updates automatically.
- **DataStore (Preferences):** all settings.
- **Network:** `VideoRepository` calls the public YouTube oEmbed endpoint on a background dispatcher with timeouts and returns a `Result`. `LearnViewModel` turns it into `Loading`, `Success` or `Failed` states.

## Ethical and professional design

These decisions follow the ACS Code of Ethics (public interest, quality of life, honesty, competence, professionalism) and the ethical issues researched in Assessment 2.

| Concern | Decision in the app |
|---|---|
| Privacy and data minimisation | No account, no analytics, no ads. Only a nickname, your own cards, review ratings and settings are stored, on the device only. Backups are disabled (`allowBackup=false`) |
| Transparency | The Privacy Center lists what is stored and what is never collected. The Lectures screen warns that YouTube can see the device's IP address |
| User control | Export all data as JSON and delete all data at any time |
| Persuasive design | No guilt prompts, no timers, no penalty for skipping. Badges are only ever unlocked, never taken away. A missed day is "only a record" |
| Accessibility and inclusiveness | Text size setting, high contrast, reduce motion, content descriptions and semantic roles for screen readers, and large touch targets |
| Safe, age-appropriate content | Built-in cards are short, neutral study questions. Cards you create stay on the device and are never shared unless you export them yourself |

## Build and run

1. Open the project in Android Studio (JDK 17) and let Gradle sync.
2. Run the `app` configuration on an emulator or device (minSdk 26, targetSdk 35).

## Tests

- **Local unit tests** (`app/src/test`, no device needed): statistics and streaks, Word Builder logic, search, achievements, settings defaults, repository operations with fake DAOs, and the Practice, Word Builder and Learn ViewModels.

  ```
  ./gradlew testDebugUnitTest
  ```

- **Compose UI tests** (`app/src/androidTest`, need an emulator or device): reusable components (button, chip, progress bar, nickname dialog) and a walk through the app (welcome, navigation, one flashcard session).

  ```
  ./gradlew connectedDebugAndroidTest
  ```

## Credits and AI declaration

- Lecture data: YouTube oEmbed API. Lecture: Stanford Online (YouTube). The Explore screen opens Google Scholar searches in the browser.
- University information in the Explore screen is summarised from the JCU Singapore Information Technology booklet (SIN07/26).
- Libraries: Jetpack Compose, Navigation, Room, DataStore, Coil (all AndroidX / open source).
- Use of generative AI is described in the *Declaration of AI-Generated Material* submitted with this assignment.