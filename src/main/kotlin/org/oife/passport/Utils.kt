package org.oife.passport

import java.nio.file.Path
import kotlin.io.path.pathString

@JvmInline
value class StatusMessage(private val title: String) {
    operator fun invoke(path: Path) = "$title → ${path.pathString}"
    operator fun invoke(detail: String) = "$title: $detail"
    override fun toString(): String = title
}

object Messages {
    val ResourceLoaded = StatusMessage("📦 Resource loaded")
    val ResourceNotFound = StatusMessage("❌ Resource not found")
    val CombinedPdfGenerated = StatusMessage("✅ Combined PDF created")
    val PdfGenerated = StatusMessage("✅ PDF created")
    val UnexpectedError = StatusMessage("❌ Unexpected error")
}



