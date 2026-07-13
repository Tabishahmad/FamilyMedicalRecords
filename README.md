# Family Medical Records

An Android app for storing and managing medical documents (prescriptions, test reports) under separate patient profiles — built for organizing healthcare records for a family in one place.

## Features
- Create and manage patient profiles (name, age, gender, notes)
- Upload documents (PDF or image), tag with doctor name, report type, notes
- Offline-first: saved to internal storage, synced to Firebase Storage for backup and multi-device access
- Filter by doctor, report type, or date range
- Preview, share, and delete documents
- **AI explanation of reports** — extracts text via ML Kit OCR, sends it to the Gemini API, and returns a plain-language explanation with an editable prompt

## Architecture
Multi-module Clean Architecture + MVVM:
```
:app            → Compose UI entry point, navigation
:presentation   → ViewModels, Compose screens
:domain         → UseCases, repository interfaces
:data           → Repository impls, Room, Firebase, Gemini API client
:core           → Shared utilities
```

## Tech stack
`Kotlin` `Jetpack Compose` `Room` `Firebase Storage` `ML Kit (Text Recognition)` `Gemini API` `Hilt`

## Running it
```bash
git clone https://github.com/Tabishahmad/FamilyMedicalRecords.git
```
Open in Android Studio, add your Gemini API key to `local.properties`:
```properties
GEMINI_API_KEY=your-api-key-here
```
Build and run.

## Roadmap
Currently single-user. Multi-user support (Firebase Auth + per-user data scoping under `users/{userId}/...`) is a planned extension — PRs welcome.

## Author
Built by [Tabish Ahmad](https://www.linkedin.com/in/tabish-ahmad-427a5923/).
Write-up: [How I Built a Medical Help App Using Jetpack Compose](https://medium.com/@tabish.dev.work/how-i-built-a-medical-help-app-using-jetpack-compose-f6fd18b8f789)
