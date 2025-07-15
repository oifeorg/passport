package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun renderPdfToFile(
    filledHtml: String,
    fontSupplier: FSSupplier<InputStream>,
    outputFile: File,
    fontFamily: String
): Result<File> = runCatching {
    FileOutputStream(outputFile).use { out ->
        PdfRendererBuilder()
            .useFont(fontSupplier, fontFamily)
            .withHtmlContent(filledHtml, null)
            .toStream(out)
            .run()
    }
    outputFile
}
