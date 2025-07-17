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
        get() = if (fontForType(font).rtl) "rtl" else "ltr"
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    "lang" to languageCode,
    "title" to documentTitle,
    "font-family" to fontForType(font).familyName,
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

fun fontForType(type: FontType): FontMeta = when (type) {
    FontType.DEFAULT -> FontMeta()
    FontType.ARABIC -> FontMeta(
        fileName = "NotoNaskhArabic-Regular.ttf",
        familyName = "NotoNaskhArabic",
        rtl = true
    )
    FontType.INDIAN -> FontMeta(
        fileName = "NotoSansGujarati-Regular.ttf",
        familyName = "NotoSansGujarati"
    )
    FontType.GEORGIAN -> FontMeta(
        fileName = "NotoSansGeorgian-Regular.ttf",
        familyName = "NotoSansGeorgian",
    )
    FontType.JAPANESE -> FontMeta(
        fileName = "NotoSansJP-Regular.ttf",
        familyName = "NotoSansJP",
    )
    FontType.CHINESE -> FontMeta(
        fileName = "NotoSansSC-Regular.ttf",
        familyName = "NotoSansSC"
    )
}
