package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val outputDir = File("generated").apply { mkdirs() }
    val htmlTemplate = loadResourceText("/templates/passport-single.html")
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
    passportFiles.forEach { (markdownFilename, metadata) ->
        val replacements = mapOf(
            "{{lang}}" to metadata.languageCode,
            "{{title}}" to metadata.documentTitle,
            "{{font-family}}" to metadata.font.familyName,
            "{{body}}" to loadResourceText("/data/$markdownFilename").fromMarkdownToHtml(),
            "{{rtl}}" to if (metadata.font.rtl) "rtl" else "ltr"
        )
        renderPdfToFile(
            filledHtml = htmlTemplate.toFilledHtml(replacements),
            fontSupplier = fontSupplier("/fonts/${metadata.font.fileName}"),
            outputFile = File(outputDir, markdownFilename.removeSuffix(".md") + ".pdf"),
            fontFamily = metadata.font.familyName
        ).fold(
            onSuccess = { logger.info("✅ PDF generated at: ${it.absolutePath}") },
            onFailure = { logger.error("❌ PDF generation failed", it) }
        )
    }
}