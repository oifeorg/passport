package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class PdfDocumentFilledHtmlTest : StringSpec({

    "should correctly fill all placeholders in the HTML template" {
        val defaultFont = FontMeta()
        val document = SinglePdfDocument(
            metaInfo = SinglePassportMeta(
                markdownFilename = "test.md",
                languageCode = "en",
                title = "My Passport",
                localizedTitle = "My test"
            ),
            documentResource = DocumentResource(
                loadResourceContent(Template.PASSPORT_SINGLE),
                emptyList(),
                contentMap = mapOf("test.md" to "# Hello"),
                fontMap = mapOf(defaultFont.familyName to loadTestFont(defaultFont)),
                version = "v1.0.0",
            ),
        )

        with(document.filledHtml) {
            shouldContain("lang=\"en\"")
            shouldContain("dir=\"ltr\"")
            shouldContain("<title>My test - My Passport</title>")
            shouldContain("font-family: Noto Sans")
            shouldContain("© OIFE 2025 v1.0.0") // Assumes year is hardcoded in your `toHtmlReplacements()`
            shouldContain("<h1>Hello</h1>")
            shouldNotContain("{{")
            shouldNotContain("}}")
        }
    }
})
