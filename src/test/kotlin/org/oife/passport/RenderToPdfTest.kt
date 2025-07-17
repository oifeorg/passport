package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.io.InputStream
import kotlin.io.path.createTempFile


class RenderToPdfTest : StringSpec({

    "should render a valid PDF file for a document" {
        val font = loadTestFont(defaultFont)
        val document = PdfDocument(
            version = "test-version",
            contentMarkdown = "# Hello PDF",
            metaInfo = SinglePassportMeta(
                markdownFilename = "test.md",
                languageCode = "en",
                documentTitle = "Test Document",
                font = defaultFont
            ),
            htmlTemplate = "<html><body>{{body}}</body></html>",
            font = font
        )

        val tempFile = createTempFile("test-passport-", ".pdf").toFile().apply { deleteOnExit() }

        renderToPdf(document, tempFile).also {
            it.shouldExist()
            it.length() shouldBeGreaterThan 100L
            it.extension shouldBe "pdf"
        }
    }
})

private suspend fun loadTestFont(font: FontMeta): FSSupplier<InputStream> {
    val path = "/fonts/${font.fileName}"
    val bytes = loadResourceBytes(path)
    return FSSupplier { bytes.inputStream() }
}