package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.serialization.Serializable
import java.io.InputStream
import java.time.Year

data class PdfDocument(
    val version: String,
    val contentMarkdown: String,
    val metaInfo: SinglePassportMeta,
    val htmlTemplate: String,
    val font: FSSupplier<InputStream>
) {
    val bodyHtml: String by lazy {
        contentMarkdown.toHtml()
    }

    val filledHtml: String by lazy {
        htmlTemplate.toFilledHtml(toHtmlReplacements())
    }
}

fun PdfDocument.toHtmlReplacements(): Map<String, String> = mapOf(
    "body" to bodyHtml,
    "version" to version,
    "year" to Year.now().toString()
) + metaInfo.toHtmlReplacements()

@Serializable
data class SinglePassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val documentTitle: String,
    val font: FontType = FontType.DEFAULT,
) {
    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"

    val direction: String
        get() = if (font.toFontMeta().rtl) "rtl" else "ltr"
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    "lang" to languageCode,
    "title" to documentTitle,
    "font-family" to font.toFontMeta().familyName,
    "rtl" to direction
)

data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "NotoSans",
    val rtl: Boolean = false,
)

@Serializable
enum class FontType {
    DEFAULT,
    ARABIC,
    INDIAN,
    GEORGIAN,
    JAPANESE,
    CHINESE
}

fun FontType.toFontMeta(): FontMeta = when (this) {
    FontType.DEFAULT -> FontMeta()
    FontType.ARABIC -> FontMeta(
        fileName = "NotoNaskhArabic-Regular.ttf",
        familyName = "Noto Naskh Arabic",
        rtl = true
    )
    FontType.INDIAN -> FontMeta(
        fileName = "NotoSansGujarati-Regular.ttf",
        familyName = "Noto Sans Gujarati"
    )
    FontType.GEORGIAN -> FontMeta(
        fileName = "NotoSansGeorgian-Regular.ttf",
        familyName = "Noto Sans Georgian",
    )
    FontType.JAPANESE -> FontMeta(
        fileName = "NotoSansJP-Regular.ttf",
        familyName = "Noto Sans JP",
    )
    FontType.CHINESE -> FontMeta(
        fileName = "NotoSansSC-Regular.ttf",
        familyName = "Noto Sans SC"
    )
}
