package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

const val OUTPUT_DIR_NAME = "generated"
private val outputDir = File(OUTPUT_DIR_NAME).apply { mkdirs() }

suspend fun renderToPdf(
    document: PdfDocument,
    outputFile: File = File(outputDir, document.metaInfo.pdfFileName)
): File = withContext(Dispatchers.IO) {
    outputFile.outputStream().use { out ->
        PdfRendererBuilder().apply {
            useFont(document.font, document.metaInfo.font.familyName)
            withHtmlContent(
                document.filledHtml,
                null
            )
            toStream(out)
        }.run()
    }

    outputFile
}

