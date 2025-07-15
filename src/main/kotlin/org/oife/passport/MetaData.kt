package org.oife.passport

data class PassportMetaData(
    val languageCode: String,
    val documentTitle: String,
    val font: FontMeta = FontMeta(),
)

data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "NotoSans",
    val rtl: Boolean = false,
)

val arabicFont = FontMeta(
    fileName = "NotoNaskhArabic-Regular.ttf",
    familyName = "NotoNaskhArabic",
    rtl = true
)

val indianFont = FontMeta(
    fileName = "NotoSansGujarati-Thin.ttf",
    familyName = "NotoSansGujaratiThin"
)

val chineseFont = FontMeta(
    "NotoSansSC-Thin.ttf",
    familyName = "NotoSansSCThin"
)

val georgianFont = FontMeta(
    fileName = "NotoSansGeorgian-Thin.ttf",
    familyName = "NotoSansGeorgianThin",
)
