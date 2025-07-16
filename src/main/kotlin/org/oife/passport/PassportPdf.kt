package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream

const val OUTPUT_DIR_NAME = "generated"
private val outputDir = File(OUTPUT_DIR_NAME).apply { mkdirs() }

fun renderPdfToFile(
    filledHtml: String,
    metadata: PassportMetaData
): Result<File> = runCatching {
    val outputFile = File(outputDir, metadata.pdfFileName)
    FileOutputStream(outputFile).use { out ->
        PdfRendererBuilder()
            .useFont(fontSupplierMap[metadata.font.fileName], metadata.font.familyName)
            .withHtmlContent(filledHtml, null)
            .toStream(out)
            .run()
    }
    outputFile
}
