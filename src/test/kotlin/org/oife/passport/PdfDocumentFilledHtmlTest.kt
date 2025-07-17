package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class PdfDocumentFilledHtmlTest : StringSpec({

    "should correctly fill all placeholders in the HTML template" {
        val document = PdfDocument(
            version = "v1.0.0",
            contentMarkdown = "# Hello",
            metaInfo = SinglePassportMeta(
                markdownFilename = "test.md",
                languageCode = "en",
                documentTitle = "My Passport",
                font = defaultFont.copy(familyName = "NotoSans")
            ),
            htmlTemplate = loadResourceContent("/templates/passport-single.html"),
            font = { ByteArray(0).inputStream() }
        )

        with(document.filledHtml) {
            shouldContain("lang=\"en\"")
            shouldContain("dir=\"ltr\"")
            shouldContain("<title>My Passport</title>")
            shouldContain("font-family: NotoSans")
            shouldContain("© OIFE 2025, v1.0.0") // Assumes year is hardcoded in your `toHtmlReplacements()`
            shouldContain("<h1>Hello</h1>")
            shouldNotContain("{{")
            shouldNotContain("}}")
        }
    }
})
