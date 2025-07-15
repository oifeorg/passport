package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val lang = "en"
    val title = "OIFE Passport"

    val html = loadResourceText("/templates/passport-content.html") ?: return
    val markdownFileName = "en-english.md"
    val markdown = loadResourceText("/data/$markdownFileName") ?: return
    val bodyHtml = markdown.toHtmlFromMarkdown()

    val filledHtml = html
        .replace("{{lang}}", lang)
        .replace("{{title}}", title)
        .replace("{{body}}", bodyHtml)

    val fontFile = extractResourceToTempFile("/fonts/NotoSans-Light.ttf", "font-", ".ttf")
    val outputDir = File("generated").apply { mkdirs() }
    val outputFilename = markdownFileName.removeSuffix(".md") + ".pdf"
    val outputFile = File(outputDir, outputFilename)

    runCatching {
        FileOutputStream(outputFile).use { out ->
            PdfRendererBuilder()
                .useFont(fontFile, "NotoSansLight")
                .withHtmlContent(filledHtml, null)
                .toStream(out)
                .run()
        }
        logger.info("✅ PDF generated at: ${outputFile.absolutePath}")
    }.onFailure {
        logger.error("❌ PDF generation failed", it)
    }
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

fun extractResourceToTempFile(path: String, prefix: String, suffix: String): File {
    val stream = getResourceStream(path)
        ?: throw IllegalArgumentException("❌ Resource not found: $path")

    return try {
        val tempFile = Files.createTempFile(prefix, suffix).toFile().apply { deleteOnExit() }
        stream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        tempFile
    } catch (ex: Exception) {
        logger.error("❌ Failed to extract resource: $path", ex)
        throw ex
    }
}

fun String.toHtmlFromMarkdown(): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parsed = MarkdownParser(flavour).buildMarkdownTreeFromString(this)
    return HtmlGenerator(this, parsed, flavour).generateHtml()
}