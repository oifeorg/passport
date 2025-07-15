package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val outputDir = File("generated").apply { mkdirs() }
    val htmlTemplate = loadResourceText("/templates/passport-content.html")
    val passportFiles: Map<String, PassportMetaData> = mapOf(
        "en-english.md" to PassportMetaData("en", "OIFE Passport"),
        "fr-french.md" to PassportMetaData("fr", "Passeport OIFE"),
        "de-german.md" to PassportMetaData("de", "OIFE-Passport"),
        "ar-arabic.md" to PassportMetaData("ar", "جواز سفر OIFE", font = arabicFontMeta)
    )
    passportFiles.forEach { (markdownFilename, metadata) ->
        val outputFile = File(outputDir, markdownFilename.removeSuffix(".md") + ".pdf")
        val markdown = loadResourceText("/data/$markdownFilename")
        val bodyHtml = markdownToHtml(markdown)
        val filledHtml = fillHtmlTemplate(htmlTemplate, metadata, bodyHtml)
        renderPdfToFile(
            html = filledHtml,
            fontSupplier = fontSupplier("/fonts/${metadata.font.fileName}"),
            outputFile = outputFile,
            fontFamily = metadata.font.familyName
        ).fold(
            onSuccess = { logger.info("✅ PDF generated at: ${it.absolutePath}") },
            onFailure = { logger.error("❌ PDF generation failed", it) }
        )
    }
}