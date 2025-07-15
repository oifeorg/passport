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
    fileName = "NotoSansGujarati-Regular.ttf",
    familyName = "NotoSansGujarati"
)

val chineseFont = FontMeta(
    "NotoSansSC-Regular.ttf",
    familyName = "NotoSansSC"
)

val georgianFont = FontMeta(
    fileName = "NotoSansGeorgian-Regular.ttf",
    familyName = "NotoSansGeorgian",
)

val passportFiles: Map<String, PassportMetaData> = mapOf(
    "ar-arabic.md" to PassportMetaData("ar", "جواز سفر OIFE", font = arabicFont),
    "da-danish.md" to PassportMetaData("da", "OIFE Passport"),
    "de-german.md" to PassportMetaData("de", "OIFE-Passport"),
    "el-greek.md" to PassportMetaData("el", "OIFE Passport"),
    "en-english.md" to PassportMetaData("en", "OIFE Passport"),
    "es-spanish.md" to PassportMetaData("es", "OIFE Passport"),
    "fl-finnish.md" to PassportMetaData("fl", "OIFE Passport"),
    "fr-french.md" to PassportMetaData("fr", "Passeport OIFE"),
    "gu-indian-gujarati.md" to PassportMetaData("gu", "Passeport OIFE", font = indianFont),
    "hr-croatian.md" to PassportMetaData("hr", "Passeport OIFE"),
    "it-italian.md" to PassportMetaData("it", "Passeport OIFE"),
    "ka-georgian.md" to PassportMetaData("ka", "Passeport OIFE", font = georgianFont),
    "nb-norwegian-bokmal.md" to PassportMetaData("nb", "Passeport OIFE"),
    "nl-dutch.md" to PassportMetaData("nl", "Passeport OIFE"),
    "pl-polish.md" to PassportMetaData("pl", "Passeport OIFE"),
    "pt-portugues.md" to PassportMetaData("pt", "Passeport OIFE"),
    "ro-romanian.md" to PassportMetaData("ro", "Passeport OIFE"),
    "ru-russian.md" to PassportMetaData("ru", "Passeport OIFE"),
    "sl-slovenian.md" to PassportMetaData("sl", "Passeport OIFE"),
    "sv-swedish.md" to PassportMetaData("sv", "Passeport OIFE"),
    "tr-turkish.md" to PassportMetaData("tr", "Passeport OIFE"),
    "uk-ukrainian.md" to PassportMetaData("uk", "Passeport OIFE"),
    "zh-chinese.md" to PassportMetaData("zh", "Passeport OIFE", font = chineseFont),
)
