package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
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


data class SinglePassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val documentTitle: String,
    val font: FontMeta = defaultFont,
) {
    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"

    val direction: String
        get() = if (font.rtl) "rtl" else "ltr"
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    "lang" to languageCode,
    "title" to documentTitle,
    "font-family" to font.familyName,
    "rtl" to direction
)

data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "NotoSans",
    val rtl: Boolean = false,
)

val defaultFont = FontMeta()

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

val japaneseFont = FontMeta(
    fileName = "NotoSansJP-Regular.ttf",
    familyName = "NotoSansJP",
)

fun singlePassportConfigs() = listOf(
    SinglePassportMeta("ar-arabic.md", "ar", "جواز سفر OIFE", font = arabicFont),
    SinglePassportMeta("da-danish.md", "da", "OIFE Passport"),
    SinglePassportMeta("de-german.md", "de", "OIFE-Passport"),
    SinglePassportMeta("el-greek.md", "el", "OIFE Passport"),
    SinglePassportMeta("en-english.md", "en", "OIFE Passport"),
    SinglePassportMeta("es-spanish.md", "es", "OIFE Passport"),
    SinglePassportMeta("fl-finnish.md", "fl", "OIFE Passport"),
    SinglePassportMeta("fr-french.md", "fr", "Passeport OIFE"),
    SinglePassportMeta("gu-indian-gujarati.md", "gu", "Passeport OIFE", font = indianFont),
    SinglePassportMeta("hr-croatian.md", "hr", "Passeport OIFE"),
    SinglePassportMeta("it-italian.md", "it", "Passeport OIFE"),
    SinglePassportMeta("ka-georgian.md", "ka", "Passeport OIFE", font = georgianFont),
    SinglePassportMeta("ja-japanese.md", "ja", "Passeport OIFE", font = japaneseFont),
    SinglePassportMeta("nb-norwegian-bokmal.md", "nb", "Passeport OIFE"),
    SinglePassportMeta("nl-dutch.md", "nl", "Passeport OIFE"),
    SinglePassportMeta("pl-polish.md", "pl", "Passeport OIFE"),
    SinglePassportMeta("pt-portugues.md", "pt", "Passeport OIFE"),
    SinglePassportMeta("ro-romanian.md", "ro", "Passeport OIFE"),
    SinglePassportMeta("ru-russian.md", "ru", "Passeport OIFE"),
    SinglePassportMeta("sl-slovenian.md", "sl", "Passeport OIFE"),
    SinglePassportMeta("sv-swedish.md", "sv", "Passeport OIFE"),
    SinglePassportMeta("tr-turkish.md", "tr", "Passeport OIFE"),
    SinglePassportMeta("uk-ukrainian.md", "uk", "Passeport OIFE"),
    SinglePassportMeta("zh-chinese.md", "zh", "Passeport OIFE", font = chineseFont),
)
