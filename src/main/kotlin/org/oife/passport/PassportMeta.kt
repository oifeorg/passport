package org.oife.passport

import kotlinx.serialization.Serializable


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

fun PassportMeta.pdfFileName(): String =
    markdownFilename.removeSuffix(".md") + ".pdf"

fun PassportMeta.direction(): String =
    font.direction

fun PassportMeta.isLocalizedTitleSame(): Boolean =
    title == localizedTitle

fun PassportMeta.headerTitle(): String =
    if (isLocalizedTitleSame()) title else "$localizedTitle - $title"