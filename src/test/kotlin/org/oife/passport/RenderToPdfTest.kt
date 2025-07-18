package org.oife.passport


import com.openhtmltopdf.extend.FSSupplier
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import java.io.InputStream
import kotlin.io.path.createTempFile
import kotlin.io.path.extension
import kotlin.io.path.fileSize

class RenderToPdfTest : StringSpec({

    "should render a valid PDF file for a document" {
        val font = loadTestFont(FontType.DEFAULT.toFontMeta())
        val document = SinglePdfDocument(
            version = "test-version",
            contentMarkdown = "# Hello PDF",
            metaInfo = SinglePassportMeta(
                markdownFilename = "test.md",
                languageCode = "en",
                documentTitle = "Test Document"
            ),
            htmlTemplate = "<html><body>{{body}}</body></html>",
            documentResource = DocumentResource(
                "", emptyList(),
                contentMap = emptyMap(),
                fontMap = emptyMap(),
                version = "v1.0.0",
            ),
            font = font
        )

        val outputPath = createTempFile("test-passport-", ".pdf").apply { toFile().deleteOnExit() }

        with(renderToPdf(document, outputPath)) {
            shouldExist()
            fileSize() shouldBeGreaterThan 100L
            extension shouldBe "pdf"
        }
    }
})

private suspend fun loadTestFont(font: FontMeta): FSSupplier<InputStream> {
    val path = "/fonts/${font.fileName}"
    val bytes = loadResourceBytes(path)
    return FSSupplier { bytes.inputStream() }
}