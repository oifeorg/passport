# OIFE Passports

[![Build & Test](https://github.com/oifeorg/passport/actions/workflows/build-test.yml/badge.svg)](https://github.com/oifeorg/passport/actions/workflows/build-test.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Kotest](https://img.shields.io/badge/Kotest-purple.svg?style=flat)](https://kotest.io)

This repository contains a Kotlin project that generates individual OIFE Passports as PDF files. The core content is
written in Markdown and stored in the [data](src/main/resources/data) folder.

## 🛠 How it works (single OIFE Passport PDF generation)

1. Each Markdown file is converted to HTML using the [intellij-markdown](https://github.com/JetBrains/markdown) library.
2. The HTML template file [passport-single.html](src/main/resources/templates) is loaded, and template variables like
   `{{body}}` are replaced using simple string substitution.
3. A PDF is generated using [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf) and saved in the root folder
   `generated`.

## 🎯 Implemented features

- [x] Automatic PDF generation triggered by a pull request (PR) merge using GitHub Actions or a similar CI tool.
- [x] Generation of a combined PDF with all OIFE Passports, including a clickable index.
- [x] GitHub workflow for building and executing tests.
- [x] Language-based styling and font loading.
- [x] Basic support for RTL (right-to-left) languages such as Arabic.
- [x] Dependabot configuration

## 🚀 Planned Features

- [ ] Make the combined passport more beautiful.
- [ ] Design a nice front side for the passport.
- [ ] Page number in the index. The question is, if that is really needed. If yes, then we need to load all the
  passports in memory, find out the total page number of each language and implement calculated paging for the index.
- [ ] Add screenshot of sample PDF in the README.
- [ ] Add for more languages

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
