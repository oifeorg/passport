package org.oife.passport


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import kotlin.io.path.createTempFile
import kotlin.io.path.extension
import kotlin.io.path.fileSize

class RenderToPdfTest : StringSpec({

    "should render a valid PDF file for a document" {
        val document = SinglePdfDocument(
            metaInfo = SinglePassportMeta(
                markdownFilename = "test.md",
                languageCode = "en",
                title = "Test Document"
            ),
            documentResource = DocumentResource(
                "<html><body>{{passport-content}}</body></html>", emptyList(),
                contentMap = mapOf("test.md" to "# Hello"),
                fontMap = mapOf(FontType.DEFAULT.toFontMeta().familyName to loadTestFont(FontType.DEFAULT.toFontMeta())),
                version = "v1.0.0",
            ),
        )

        val outputPath = createTempFile("test-passport-", ".pdf").apply { toFile().deleteOnExit() }

        with(renderToPdf(document, outputPath)) {
            shouldExist()
            fileSize() shouldBeGreaterThan 100L
            extension shouldBe "pdf"
        }
    }
})
