package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.serialization.Serializable
import java.io.InputStream


@Serializable
data class PassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val title: String,
    val localizedTitle: String = "",
    val font: FontMeta = FontMeta(),
)

@Serializable
data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "Noto Sans",
    val direction: String = "ltr",
)

data class PdfDocumentInput(
    val filledHtml: String,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val pdfFileName: String,
)

fun PassportMeta.pdfFileName(): String =
    markdownFilename.removeSuffix(".md") + ".pdf"

fun PassportMeta.direction(): String =
    font.direction

fun PassportMeta.isLocalizedTitleSame(): Boolean =
    title == localizedTitle

fun PassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.LANG to languageCode,
    Placeholder.HEADER_TITLE to if (isLocalizedTitleSame()) title else "$localizedTitle - $title",
    Placeholder.FONT_FAMILY to font.familyName,
    Placeholder.DIRECTION to direction()
)

fun FontMeta.toCssClass(languageCode: String): String = if (this == FontMeta()) "default" else languageCode

fun FontMeta.toTextAlign(): String = if (this.direction == "ltr") "left" else "right"
