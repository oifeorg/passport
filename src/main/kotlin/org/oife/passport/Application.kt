package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val outputDir = File("generated").apply { mkdirs() }
    val htmlTemplate = loadResourceText("/templates/passport-content.html")
    val passportFiles: Map<String, Metadata> = mapOf(
        "en-english.md" to Metadata("en", "OIFE Passport"),
        "fr-french.md" to Metadata("fr", "Passeport OIFE"),
        "de-german.md" to Metadata("de", "OIFE-Passport"),
        "ar-arabic.md" to Metadata("ar", "جواز سفر OIFE")
    )
    passportFiles.forEach { (markdownFilename, metadata) ->
        val outputFile = File(outputDir, markdownFilename.removeSuffix(".md") + ".pdf")
        val markdown = loadResourceText("/data/$markdownFilename")
        val bodyHtml = markdownToHtml(markdown)
        val filledHtml = fillHtmlTemplate(htmlTemplate, metadata, bodyHtml)
        renderPdfToFile(
            html = filledHtml,
            fontSupplier = fontSupplier("/fonts/NotoSans-Light.ttf"),
            outputFile = outputFile
        ).fold(
            onSuccess = { logger.info("✅ PDF generated at: ${it.absolutePath}") },
            onFailure = { logger.error("❌ PDF generation failed", it) }
        )
    }
}