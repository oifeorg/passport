package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val outputDir = File("generated").apply { mkdirs() }
    val singleHtmlTemplateFile = loadResourceText("/templates/passport-single.html")
    val fontSupplierMap = buildFontSupplierMap()

    passportFiles.forEach { (markdownFilename, metadata) ->
        val replacements = mapOf(
            "{{lang}}" to metadata.languageCode,
            "{{title}}" to metadata.documentTitle,
            "{{font-family}}" to metadata.font.familyName,
            "{{body}}" to loadResourceText("/data/$markdownFilename").fromMarkdownToHtml(),
            "{{rtl}}" to if (metadata.font.rtl) "rtl" else "ltr"
        )
        renderPdfToFile(
            filledHtml = singleHtmlTemplateFile.toFilledHtml(replacements),
            fontSupplier = fontSupplierMap[metadata.font.fileName]
                ?: error("❌ No font supplier for ${metadata.font.fileName}"),
            outputFile = File(outputDir, markdownFilename.removeSuffix(".md") + ".pdf"),
            fontFamily = metadata.font.familyName
        ).fold(
            onSuccess = { logger.info("✅ PDF generated at: ${it.absolutePath}") },
            onFailure = { logger.error("❌ PDF generation failed", it) }
        )
    }
}