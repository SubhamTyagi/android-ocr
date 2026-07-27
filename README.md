# OCR - Character Recognizer

[![F-Droid](https://img.shields.io/f-droid/v/io.github.subhamtyagi.ocr?logo=f-droid&style=flat-square)](https://f-droid.org/packages/io.github.subhamtyagi.ocr/)
[![Translation Status](https://hosted.weblate.org/widgets/android-ocr/-/android-ocr/svg-badge.svg)](https://hosted.weblate.org/projects/android-ocr)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)

A modern, privacy-focused Android application for Optical Character Recognition (OCR), capable of extracting text from images in over 120 languages.

This app is powered by **Tesseract 5** via the [Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) library, ensuring high accuracy and performance.

---

## 🚀 Features

*   **Offline Recognition**: Process images entirely on-device for maximum privacy.
*   **Multi-Language Support**: Recognize 120+ [languages](https://tesseract-ocr.github.io/tessdoc/Data-Files) and process multiple languages in a single image.
*   **Specialized Extraction**: Recognize Math equations.
*   **Seamless Integration**: Share images directly from your gallery to the app.
*   **Crop & Select**: Precise control over which part of the image to recognize.
*   **Clipboard Integration**: One-tap copy of recognized text.

---

## 🛠 Tech Stack

The project leverages modern Android development tools and best practices:

*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for a modern, declarative UI.
*   **Architecture**: MVVM with [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection.
*   **Database**: [Room](https://developer.android.com/training/data-storage/room) for local data persistence.
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/) for efficient image processing.
*   **OCR Engine**: [Tesseract 5](https://github.com/tesseract-ocr/tesseract) via [Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android).
*   **Concurrency**: Kotlin Coroutines and Flow.

---

## 📸 Screenshots

| Home | Result | Settings | Languages |
|:---:|:---:|:---:|:---:|
| ![HOME](fastlane/metadata/android/en-US/images/phoneScreenshots/01.png?raw=true) | ![RESULT](fastlane/metadata/android/en-US/images/phoneScreenshots/02.png?raw=true) | ![SETTINGS](fastlane/metadata/android/en-US/images/phoneScreenshots/03.png?raw=true) | ![LANGUAGES](fastlane/metadata/android/en-US/images/phoneScreenshots/04.png?raw=true) |



---

## 📂 Project Structure

*   `:app`: The main application module containing the UI and business logic.
*   `:cropper`: A dedicated module for image cropping functionality.

---

## 🤝 Contributing

Contributions are welcome! Whether it's reporting a bug, suggesting a feature, or submitting a Pull Request:

### Translation
Help us reach more users by translating the app on [Weblate](https://hosted.weblate.org/projects/android-ocr).

---

## 📜 Licenses & Credits

### Libraries
*   **Tesseract Engine**: [Apache 2.0](https://github.com/tesseract-ocr/tesseract/blob/master/LICENSE)
*   **Tesseract4Android**: [Apache 2.0](https://github.com/adaptech-cz/Tesseract4Android/blob/master/LICENSE)

### Contributors
*   [Shubham Tyagi](https://github.com/SubhamTyagi) - I
*   [Hannes Gehrold](https://github.com/h4n23s) - Old UI Design
*   [urlordjames](https://github.com/urlordjames) - Code Contributor

### Graphics
*   App icon designed by [Nucleus-ffm](https://github.com/nucleus-ffm).
*   Original icon concept by [mondstern](https://mastodon.technology/@mondstern).

