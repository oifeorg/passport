package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class PdfDocumentFilledHtmlTest : StringSpec({

    val passport = PassportMeta(
        markdownFilename = "test.md",
        languageCode = "en",
        title = "My Passport",
        localizedTitle = "My test",
    )

    "should correctly fill all placeholders in single passport HTML template" {
        val defaultFont = FontMeta()
        val documentResource = SinglePassport(
            loadResourceContent(Template.PASSPORT_SINGLE),
            emptyList(),
            contentMap = mapOf("test.md" to "# Hello"),
            fontMap = mapOf(defaultFont.familyName to loadTestFont(defaultFont)),
            version = "v1.0.0",
        )

        with(documentResource.toRenderable(passport).filledHtml) {
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

    "should correctly fill all placeholders in combined passport HTML template" {
        val defaultFont = FontMeta()
        val documentResource = loadCombinedPassport(
            SinglePassport(
                loadResourceContent(Template.PASSPORT_COMBINED),
                listOf(passport),
                contentMap = mapOf("test.md" to "# Hello"),
                fontMap = mapOf(defaultFont.familyName to loadTestFont(defaultFont)),
                version = "v1.0.0",
            )
        )

        with(documentResource.toRenderable().filledHtml) {
            shouldContain("lang=\"en\"")
            shouldContain("dir=\"ltr\"")
            shouldContain("<title>OIFE Passport combined</title>")
            shouldContain(".lang-default")
            shouldContain("© OIFE 2025 v1.0.0") // Assumes year is hardcoded in your `toHtmlReplacements()`
            shouldContain("<h1>Hello</h1>")
            shouldNotContain("{{")
            shouldNotContain("}}")
        }
    }
})
