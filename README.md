# 🏥 Medical Help App

A Jetpack Compose-powered Android app to help users store and manage their **medical documents** (prescriptions, test reports, etc.) under different **patient profiles**. Ideal for organizing healthcare records for families, parents, or yourself.

---

## ✨ Features

- 👤 **Create and manage patient profiles**
  - Name, age, gender, and notes
- 📄 **Upload and view documents** (PDF or Image)
  - Add doctor name, report type, and personal notes
  - Save in internal storage for offline access
- 🔍 **Smart filters**
  - Filter by doctor, report type, or date (e.g., Last 3 months)
- 🖼️ **Preview files and actions**
  - View, share (WhatsApp, email, etc.), delete
- 🤖 **AI Explanation of reports**
  - Extracts text from files and uses **Gemini API** to explain in simple terms
  - Editable prompt for full control
- 🔄 **Expandable for Multi-User Support** *(See below)*

---

## 🛠️ Tech Stack

- 🧩 **Jetpack Compose** – Modern UI toolkit
- 🧼 **Clean Architecture + MVVM** – Maintainable and testable code
- 🧱 **Multi-Module Architecture**
  - `:app`, `:presentation`, `:domain`, `:data`, `:core`
- 🏠 **Room Database** – Local storage
- ☁️ **Firebase (optional)** – For sync and authentication
- 📸 **ML Kit (Text Recognition)** – Extract text from images
- 🤖 **Gemini API (Google AI)** – Explain medical documents in plain language

---

## 🚀 Getting Started

1. **Clone the repo**
```bash
git clone https://github.com/yourname/MedicalHelpApp.git
```

2. **Open in Android Studio** (Arctic Fox or newer)

3. **Add your Gemini API Key**
- Add this to `local.properties`:
```properties
GEMINI_API_KEY=your-api-key-here
```
- And reference it in `build.gradle`:
```groovy
buildConfigField "String", "GEMINI_API_KEY", "\"${GEMINI_API_KEY}\""
```

4. **Run the app** on a real device or emulator

---

## 📷 AI Explanation Feature

1. User uploads an image or PDF.
2. App extracts text using ML Kit (for image) or PDF parser.
3. Text and prompt are sent to Gemini API.
4. User sees an easy-to-understand explanation.

---

## 🧠 Multi-User Data Separation *(To Be Added)*

Currently, the app is built for a **single user**. To support **multiple users**:

- Integrate Firebase Authentication
- Store all user data under their `userId`
- Update Room or Firebase DB logic to isolate:
  - Profiles: `users/{userId}/profiles`
  - Documents: `users/{userId}/documents`

> ✅ Developers are welcome to fork and add this capability.

---

## 🙌 Contribution

Feel free to use, modify, and extend this app! Whether you're a beginner learning Compose or a developer building a real medical app — this codebase is yours to build upon.

If you improve it (add auth, backup, export, etc.), feel free to send a PR! ❤️

---

## 📄 License

MIT License. Free to use with attribution.

---

## ✍️ Author

Built with ❤️ by [Your Name](https://medium.com/@tabish.dev.work)

Medium Article: [How I Built a Medical Help App Using Jetpack Compose](https://medium.com/@yourprofile)

---

Need help or want to collaborate? Feel free to reach out on [LinkedIn](https://www.linkedin.com/in/tabish-ahmad-427a5923/).
