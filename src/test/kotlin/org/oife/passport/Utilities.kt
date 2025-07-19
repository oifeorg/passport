package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import java.io.InputStream

internal const val testMarkdownFile = "test.md"
internal const val testTemplate = "test.html"

suspend fun loadTestFont(font: FontMeta): FSSupplier<InputStream> {
    val path = "/fonts/${font.fileName}"
    val bytes = loadResourceBytes(path)
    return FSSupplier { bytes.inputStream() }
}