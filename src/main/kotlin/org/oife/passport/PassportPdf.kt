package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")
const val OUTPUT_DIR_NAME = "generated"
private val outputDir = File(OUTPUT_DIR_NAME).apply { mkdirs() }

fun renderPdfToFile(
    filledHtml: String,
    metadata: PassportMetaData,
    outputFile: File = File(outputDir, metadata.pdfFileName)
): Result<File> = runCatching {
    FileOutputStream(outputFile).use { out ->
        PdfRendererBuilder()
            .useFont(fontSupplierMap[metadata.font.fileName], metadata.font.familyName)
            .withHtmlContent(filledHtml, null)
            .toStream(out)
            .run()
    }
    outputFile
}

fun generateAllSinglePassports() {
    passports.forEach { it: PassportMetaData ->
        renderPdfToFile(singleHtmlTemplateFile.toFilledHtml(it.toHtmlReplacements()), it)
            .onSuccess { file -> logger.info("✅ PDF generated at: ${file.absolutePath}") }
            .onFailure { logger.error("❌ PDF generation failed", it) }
    }
}
