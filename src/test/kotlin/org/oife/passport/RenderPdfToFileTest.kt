package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldHaveExtension
import io.kotest.matchers.longs.shouldBeGreaterThan
import kotlin.io.path.createTempFile

class RenderPdfToFileTest : StringSpec({

    "should generate a valid PDF file from HTML" {
        val html = """
            <html>
              <head><style>body { font-family: NotoSans; }</style></head>
              <body><h1>Hello, world!</h1></body>
            </html>
        """.trimIndent()
        val metadata = PassportMetaData(
            markdownFilename = testMarkdownFile,
            languageCode = "en",
            documentTitle = "Hello PDF"
        )

        val output = createTempFile("passport", ".pdf")
            .toFile()
            .apply { deleteOnExit() }
            .let { tempFile ->
                renderPdfToFile(html, metadata, tempFile).getOrThrow()
            }

        with(output) {
            shouldExist()
            shouldHaveExtension("pdf")
            length().shouldBeGreaterThan(1000)
        }
    }
})