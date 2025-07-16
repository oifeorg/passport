# OIFE Passports

[![Kotlin](https://img.shields.io/badge/Kotlin-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

This repository contains a Kotlin project that generates individual OIFE Passports as PDF files. The core content is written in Markdown and stored in the [data](src/main/resources/data) folder.

## 🛠 How it works (single OIFE Passport PDF generation)

1. Each Markdown file is converted to HTML using the [intellij-markdown](https://github.com/JetBrains/markdown) library.
2. The HTML template file [passport-single.html](src/main/resources/templates) is loaded, and template variables like `{{body}}` are replaced using simple string substitution.
3. A PDF is generated using [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf) and saved in the [generated](generated) folder.

## 🚀 Planned Features

- [ ] Generation of a combined PDF with all OIFE Passports, including a clickable index.
- [ ] Automatic PDF generation triggered by a pull request (PR) merge using GitHub Actions or a similar CI tool.
- [ ] GitHub workflow for building and executing tests.
- [x] Language-based styling and font loading.
- [x] Basic support for RTL (right-to-left) languages such as Arabic.
- [X] Dependabot configuration
- [ ] Add screenshot of sample PDF in the README.

## 🧪 Requirements

- JDK 21 or higher
- Gradle (if not using the wrapper)

## ▶️ Run the generator

To generate the PDFs, run:

```bash
./gradlew run
