package org.oife.passport

data class PassportMetaData(
    val languageCode: String,
    val documentTitle: String,
    val font: FontMeta = FontMeta(),
)

data class FontMeta(
    val fileName: String = "NotoSans-Light.ttf",
    val familyName: String = "NotoSansLight",
    val rtl: Boolean = false,
)

val arabicFontMeta = FontMeta(
    fileName = "NotoNaskhArabic-Regular.ttf",
    familyName = "NotoNaskhArabicRegular",
    rtl = true
)