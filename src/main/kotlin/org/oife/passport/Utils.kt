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

object Placeholder {
    const val LANG = "lang"
    const val HEADER_TITLE = "headerTitle"
    const val FONT_FAMILY = "fontFamily"
    const val DIRECTION = "direction"
    const val VERSION = "version"
    const val TITLE = "title"
    const val FONT_TYPE = "fontType"
    const val LOCALIZED_TITLE= "localizedTitle"
    const val LANGUAGE_CODE = "languageCode"
    const val BODY = "body"
    const val PAGE_BREAK_AFTER = "page-break-after"
    const val PASSPORT_CONTENT = "passport-content"
    const val YEAR = "year"
    const val PASSPORT_INDEX_ITEMS = "passport-index-items"
    const val PASSPORT_ARTICLE_ITEMS = "passport-article-items"
}



