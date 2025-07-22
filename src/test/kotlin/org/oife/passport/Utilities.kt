package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import java.io.InputStream

internal const val testMarkdownFile = "test.md"

suspend fun loadTestFont(font: FontMeta): FSSupplier<InputStream> {
    val path = "/fonts/${font.fileName}"
    val bytes = loadResourceBytes(path)
    return FSSupplier { bytes.inputStream() }
}


fun dummyPdfContent(): ByteArray = """
    %PDF-1.4
    1 0 obj <</Type /Catalog /Pages 2 0 R>> endobj
    2 0 obj <</Type /Pages /Kids [3 0 R] /Count 1>> endobj
    3 0 obj <</Type /Page /Parent 2 0 R /MediaBox [0 0 200 200]>> endobj
    trailer <</Root 1 0 R>> %%EOF
""".trimIndent().toByteArray()