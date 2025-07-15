package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val metadata = Metadata(lang = "en", title = "OIFE Passport")
    val markdownFile = "en-english.md"
    val outputFile = File("generated", markdownFile.removeSuffix(".md") + ".pdf")

    val template = loadResourceText("/templates/passport-content.html")

    val markdown = loadResourceText("/data/$markdownFile")

    val bodyHtml = markdownToHtml(markdown)
    val filledHtml = fillHtmlTemplate(template, metadata, bodyHtml)

    val result = renderPdfToFile(
        html = filledHtml,
        fontSupplier = fontSupplier("/fonts/NotoSans-Light.ttf"),
        outputFile = outputFile
    )

    result.fold(
        onSuccess = { logger.info("✅ PDF generated at: ${it.absolutePath}") },
        onFailure = { logger.error("❌ PDF generation failed", it) }
    )
}