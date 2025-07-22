# OIFE Passports

[![Build & Test](https://github.com/oifeorg/passport/actions/workflows/build-test.yml/badge.svg)](https://github.com/oifeorg/passport/actions/workflows/build-test.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Kotest](https://img.shields.io/badge/Kotest-purple.svg?style=flat)](https://kotest.io)

This repository contains a Kotlin project that generates individual OIFE Passports as PDF files. The core content is
written in Markdown and stored in the [data](src/main/resources/data) folder.

## 🌐 How to add a new language

1. Add a new Markdown file to [resources/data](src/main/resources/data) using the naming convention `languageCode-englishLanguageName.md`.
2. If needed, add a custom Noto Sans font file to [resources/fonts](src/main/resources/fonts). This is only required if the standard font does not support the language.
3. Add a new language entry to [passport-config.json](src/main/resources/passport-config.json). You can omit the `font` property if the default font (`Noto Sans`) is sufficient.

## 🛠 How it works under the hood

### Single OIFE Passport generation

1. Each Markdown file is converted to HTML using [intellij-markdown](https://github.com/JetBrains/markdown).
2. The HTML template [passport-single.html](src/main/resources/templates) is loaded, and template variables like `{{body}}` are replaced using simple string substitution.
3. A PDF is generated with [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf) and saved to the `generated` folder.

### Combined OIFE Passport generation

1. Each Markdown file is converted to HTML using [intellij-markdown](https://github.com/JetBrains/markdown).
2. The combined HTML template [passport-combined.html](src/main/resources/templates) is loaded. Template placeholders like `{{passport-index-items}}` and `{{passport-article-items}}` are replaced using string substitution.
3. A combined PDF is created using [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf).
4. The combined PDF is merged with the front and back covers, and saved to the `generated` folder.

## 🎯 Implemented features

- [x] Automatic PDF generation triggered by a pull request (PR) merge using GitHub Actions or a similar CI tool.
- [x] Generation of a combined PDF with all OIFE Passports, including a clickable index.
- [x] GitHub workflow for building and executing tests.
- [x] Language-based styling and font loading.
- [x] Basic support for RTL (right-to-left) languages such as Arabic.
- [x] Dependabot configuration

## 🚀 Planned Features

- [ ] Make the combined passport more beautiful with creating new front and back cover.
- [ ] Page number in the index. The question is, if that is really needed? If yes, then we need to load all the
  passports in memory, find out the total page number of each language and implement calculated paging for the index.

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
