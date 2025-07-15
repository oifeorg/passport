package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val lang = "en"
    val title = "OIFE Passport"
    val header = "Welcome"

    val html = loadResourceText("/templates/passport-content.html") ?: return
    val body = loadResourceText("/data/en-english.txt") ?: return

    val filledHtml = html
        .replace("{{lang}}", lang)
        .replace("{{title}}", title)
        .replace("{{header}}", header)
        .replace("{{body}}", body)

    val fontFile = extractResourceToTempFile("/fonts/NotoSans-Regular.ttf", "font-", ".ttf") ?: return
    val outputFile = File("output.pdf")

    runCatching {
        FileOutputStream(outputFile).use { out ->
            PdfRendererBuilder()
                .useFont(fontFile, "NotoSans")
                .withHtmlContent(filledHtml, null)
                .toStream(out)
                .run()
        }
        logger.info("✅ PDF generated at: ${outputFile.absolutePath}")
    }.onFailure {
        logger.error("❌ PDF generation failed", it)
    }

    logger.info("✅ PDF generated at: ${outputFile.absolutePath}")
}

fun getResourceStream(path: String): java.io.InputStream? {
    return object {}.javaClass.getResourceAsStream(path) ?: run {
        logger.error("❌ Resource not found: $path")
        null
    }
}

fun loadResourceText(path: String): String? =
    getResourceStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }

fun extractResourceToTempFile(path: String, prefix: String, suffix: String): File? {
    val stream = getResourceStream(path) ?: return null

    return runCatching {
        val tempFile = Files.createTempFile(prefix, suffix).toFile().apply { deleteOnExit() }
        stream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        tempFile
    }.onFailure {
        logger.error("❌ Failed to extract resource: $path", it)
    }.getOrNull()
}