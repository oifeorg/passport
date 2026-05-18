package org.oife.passport

import kotlinx.serialization.Serializable

@Serializable
data class PassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val title: String,
    val localizedTitle: String = "",
    val font: FontMeta = FontMeta(),
) {
    val fileName: String get() = markdownFilename.removeSuffix(".md") + ".pdf"
    val direction: String get() = font.direction
    val isLocalizedTitleSame: Boolean get() = title == localizedTitle
    val headerTitle: String get() = if (isLocalizedTitleSame) title else "$localizedTitle - $title"
}

@Serializable
data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "Noto Sans",
    val direction: String = "ltr",
)
