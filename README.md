# OIFE Passports

[![Build & Test](https://github.com/oifeorg/passport/actions/workflows/build-test.yml/badge.svg)](https://github.com/oifeorg/passport/actions/workflows/build-test.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Kotest](https://img.shields.io/badge/Kotest-purple.svg?style=flat)](https://kotest.io)

This repository contains a Kotlin project that generates individual OIFE Passports as PDF files. The core content is
written in Markdown and stored in the [data](src/main/resources/data) folder.

## 🌐 How to Add a New Language

1. Add a new Markdown file to [resources/data](src/main/resources/data) using the naming convention `languageCode-englishLanguageName.md`.
2. If needed, add a custom Noto Sans font file to [resources/fonts](src/main/resources/fonts). This is only required if the default font does not support the language.
3. Add a language entry to [passport-config.json](src/main/resources/passport-config.json). You can omit the `font` property if the default font (`Noto Sans`) is sufficient.

## 🛠 How It Works

### Single OIFE Passport Generation

1. Each Markdown file is converted to HTML using [JetBrains Markdown](https://github.com/JetBrains/markdown).
2. The template [passport-single.html](src/main/resources/templates) is loaded, and variables like `{{body}}` are replaced using string substitution.
3. A PDF is generated with [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf) and saved to the `generated` folder.

### Combined OIFE Passport Generation

1. All Markdown files are converted to HTML.
2. The combined template [passport-combined.html](src/main/resources/templates) is loaded. Template variables such as `{{passport-index-items}}` and `{{passport-article-items}}` are replaced.
3. A combined PDF is rendered with [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf).
4. The final PDF is created by merging this with the front and back covers and saved to the `generated` folder.


## 🎯 Implemented features

- [x] Automatic PDF generation triggered by a pull request (PR) merge using GitHub Actions or a similar CI tool.
- [x] Generation of a combined PDF with all OIFE Passports, including a clickable index.
- [x] GitHub workflow for building and executing tests.
- [x] Language-based styling and font loading.
- [x] Basic support for RTL (right-to-left) languages such as Arabic.
- [x] Dependabot configuration

## 🚀 Possible Features

- [ ] Improve the combined passport layout with new front and back covers
- [ ] Add page numbers in the index (to do this, we'd need to preload all PDFs and compute the total pages per language)

## 🧪 Requirements

- JDK 21 or higher
- Gradle (if not using the wrapper)

## ▶️ Run the generator

To generate the PDFs, run:

```bash
./gradlew run
# or with a version number 
./gradlew run --args=v1.10.0
```

## 📦 Libraries and Dependencies

This project uses a set of publicly available libraries—mostly open source—from trusted projects in the Kotlin and Java ecosystems, including:

- **JetBrains Markdown** – for converting Markdown to HTML
- **OpenHTMLtoPDF** – for rendering HTML to PDF
- **kotlinx.coroutines** and **kotlinx.serialization** – for async and structured data
- **Kotest** and **MockK** – for testing
- **Logback** – for logging

These libraries are used in accordance with their respective licenses (Apache 2.0, MIT, BSD, MPL, LGPL, or EPL).  
See [LICENSE](LICENSE) and project dependencies for more information.

## 📝 License

This repository is **dual-licensed**:

- **Source code** (`*.kt`, `*.html`, etc.): Licensed under the [Apache License 2.0](LICENSE)
- **Markdown content and cover PDF files** (`src/main/resources/data/` and subfolders): © 2025 OIFE. All rights reserved
- **Generated passport PDF files** (available via GitHub Releases or published platforms): © 2025 OIFE. All rights reserved

The Markdown content, cover PDFs, and generated passport PDFs are **not open source**.  
They may **not** be copied, modified, redistributed, or reused without **explicit written permission** from [OIFE](https://oife.org).