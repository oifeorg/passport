package org.oife.passport

import kotlinx.serialization.Serializable


@Serializable
data class SinglePassportMeta(
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

fun SinglePassportMeta.pdfFileName(): String =
    markdownFilename.removeSuffix(".md") + ".pdf"

fun SinglePassportMeta.direction(): String =
    font.direction

fun SinglePassportMeta.isLocalizedTitleSame(): Boolean =
    title == localizedTitle

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.LANG to languageCode,
    Placeholder.HEADER_TITLE to if (isLocalizedTitleSame()) title else "$localizedTitle - $title",
    Placeholder.FONT_FAMILY to font.familyName,
    Placeholder.DIRECTION to direction()
)

fun FontMeta.toCssClass(languageCode: String): String = if (this == FontMeta()) "default" else languageCode

fun FontMeta.toTextAlign(): String = if (this.direction == "ltr") "left" else "right"
