package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream

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
