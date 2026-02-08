# Africa Quiz

An Android quiz game that tests your knowledge of African capital cities, featuring an interactive 3D globe powered by [Globe.gl](https://globe.gl/).

Pick from 54 countries, watch the globe spin to each capital, and see how well you really know the continent.

## Screenshots

<p align="center">
  <img src="screenshots/01_home.png" width="200" alt="Home screen" />
  <img src="screenshots/02_quiz.png" width="200" alt="Quiz with 3D globe" />
  <img src="screenshots/03_answer_feedback.png" width="200" alt="Answer feedback" />
  <img src="screenshots/04_results.png" width="200" alt="Results screen" />
</p>

**Home** &mdash; Toggle the countdown timer and start a round | **Quiz** &mdash; Spin the 3D globe and pick from 4 choices | **Feedback** &mdash; Correct answer in green, wrong in red, globe flies to the capital | **Results** &mdash; Score breakdown with full answer review

## Features

- 10 questions per round, randomly selected from all 54 African countries
- 4 choices per question: 1 correct capital + 2 other African capitals + 1 major non-capital city
- Interactive 3D globe (Globe.gl in a WebView) animates to each capital with a red dot marker
- Optional 15-second countdown timer
- Green/red button feedback with auto-advance
- Results screen with score circle, performance message, and answer-by-answer review
- Fully offline &mdash; no internet permission, all assets bundled

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM with StateFlow |
| 3D Globe | [Globe.gl](https://globe.gl/) in WebView via `WebViewAssetLoader` |
| Data | Python scraper (BeautifulSoup) with hardcoded fallback |
| Min SDK | 24 (Android 7.0) |
| Build | AGP 8.13, Gradle 9.3.1 |

## Getting Started

### Build

```bash
./gradlew assembleDebug
```

### Install on emulator/device

```bash
./gradlew installDebug
```

### Regenerate quiz data

The JSON is pre-bundled, but you can re-scrape from Wikipedia:

```bash
cd scripts
pip install -r requirements.txt
python scrape_african_capitals.py
```

### Capture screenshots

```bash
python3 scripts/take_screenshots.py          # full run (boots emulator)
python3 scripts/take_screenshots.py --skip-boot  # emulator already running
```

## Project Structure

```
app/src/main/java/com/mufradat/africaquiz/
├── MainActivity.kt
├── data/                    # Data layer
│   ├── model/               # Country, QuizData
│   ├── local/               # AssetJsonParser (Gson)
│   └── repository/          # QuizRepository
├── domain/                  # Domain layer
│   ├── model/               # Question, QuizResult
│   └── usecase/             # GenerateQuizUseCase, CalculateScoreUseCase
├── ui/                      # Presentation layer
│   ├── theme/               # Dark theme (gold/green/navy)
│   ├── navigation/          # NavGraph (Home -> Quiz -> Results)
│   ├── components/          # GlobeWebView, AnswerButton, CountdownTimer
│   └── screens/             # HomeScreen, QuizScreen, ResultsScreen
└── util/                    # Constants
```

## License

[MIT](LICENSE)
