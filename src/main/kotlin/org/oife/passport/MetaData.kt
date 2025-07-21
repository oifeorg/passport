package org.oife.passport

import kotlinx.serialization.Serializable


@Serializable
data class SinglePassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val title: String,
    val localizedTitle: String = "",
    val font: FontMeta = FontMeta(),
) {
    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"

    val direction: String
        get() = font.direction
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.LANG to languageCode,
    Placeholder.HEADER_TITLE to if (localizedTitle == title) title else "$localizedTitle - $title",
    Placeholder.FONT_FAMILY to font.familyName,
    Placeholder.DIRECTION to direction
)

@Serializable
data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "Noto Sans",
    val direction: String = "ltr",
)

fun FontMeta.toCssClass(languageCode: String): String = if (this == FontMeta()) "default" else languageCode

fun FontMeta.toTextAlign(): String = if (this.direction == "ltr") "left" else "right"
