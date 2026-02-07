# African Capitals Quiz Game

## Build
```bash
./gradlew assembleDebug
```

## Run
Install on emulator/device:
```bash
./gradlew installDebug
```

## Data Generation
```bash
cd scripts
pip install -r requirements.txt
python scrape_african_capitals.py
```
This generates `app/src/main/assets/african_capitals.json`.

## Architecture
- MVVM with Jetpack Compose
- Globe.gl in WebView for 3D globe visualization
- Fully offline - no INTERNET permission needed
