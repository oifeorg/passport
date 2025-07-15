package org.oife.passport

data class PassportMetaData(
    val markdownFilename: String,
    val languageCode: String,
    val documentTitle: String,
    val font: FontMeta = FontMeta(),
) {
    val markdownContent: String by lazy {
        loadResourceText("/data/$markdownFilename")
    }

    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"
}

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
    fileName = "NotoSansSC-Regular.ttf",
    familyName = "NotoSansSC"
)

val georgianFont = FontMeta(
    fileName = "NotoSansGeorgian-Regular.ttf",
    familyName = "NotoSansGeorgian",
)

val passports = listOf(
    PassportMetaData("ar-arabic.md", "ar", "جواز سفر OIFE", font = arabicFont),
    PassportMetaData("da-danish.md", "da", "OIFE Passport"),
    PassportMetaData("de-german.md", "de", "OIFE-Passport"),
    PassportMetaData("el-greek.md", "el", "OIFE Passport"),
    PassportMetaData("en-english.md", "en", "OIFE Passport"),
    PassportMetaData("es-spanish.md", "es", "OIFE Passport"),
    PassportMetaData("fl-finnish.md", "fl", "OIFE Passport"),
    PassportMetaData("fr-french.md", "fr", "Passeport OIFE"),
    PassportMetaData("gu-indian-gujarati.md", "gu", "Passeport OIFE", font = indianFont),
    PassportMetaData("hr-croatian.md", "hr", "Passeport OIFE"),
    PassportMetaData("it-italian.md", "it", "Passeport OIFE"),
    PassportMetaData("ka-georgian.md", "ka", "Passeport OIFE", font = georgianFont),
    PassportMetaData("nb-norwegian-bokmal.md", "nb", "Passeport OIFE"),
    PassportMetaData("nl-dutch.md", "nl", "Passeport OIFE"),
    PassportMetaData("pl-polish.md", "pl", "Passeport OIFE"),
    PassportMetaData("pt-portugues.md", "pt", "Passeport OIFE"),
    PassportMetaData("ro-romanian.md", "ro", "Passeport OIFE"),
    PassportMetaData("ru-russian.md", "ru", "Passeport OIFE"),
    PassportMetaData("sl-slovenian.md", "sl", "Passeport OIFE"),
    PassportMetaData("sv-swedish.md", "sv", "Passeport OIFE"),
    PassportMetaData("tr-turkish.md", "tr", "Passeport OIFE"),
    PassportMetaData("uk-ukrainian.md", "uk", "Passeport OIFE"),
    PassportMetaData("zh-chinese.md", "zh", "Passeport OIFE", font = chineseFont),
)
