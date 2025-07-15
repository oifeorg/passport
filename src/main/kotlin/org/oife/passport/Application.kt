package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    val outputDir = File("generated").apply { mkdirs() }
    val htmlTemplate = loadResourceText("/templates/passport-content.html")
    val passportFiles: Map<String, PassportMetaData> = mapOf(
        "ar-arabic.md" to PassportMetaData("ar", "جواز سفر OIFE", font = arabicFont),
        "da-danish.md" to PassportMetaData("en", "OIFE Passport"),
        "de-german.md" to PassportMetaData("de", "OIFE-Passport"),
        "el-greek.md" to PassportMetaData("en", "OIFE Passport"),
        "en-english.md" to PassportMetaData("en", "OIFE Passport"),
        "es-spanish.md" to PassportMetaData("en", "OIFE Passport"),
        "fl-finnish.md" to PassportMetaData("en", "OIFE Passport"),
        "fr-french.md" to PassportMetaData("fr", "Passeport OIFE"),
        "gu-indian-gujarati.md" to PassportMetaData("fr", "Passeport OIFE", font = indianFont),
        "hr-croatian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "it-italian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "ka-georgian.md" to PassportMetaData("fr", "Passeport OIFE", font = georgianFont),
        "nb-norwegian-bokmal.md" to PassportMetaData("fr", "Passeport OIFE"),
        "nl-dutch.md" to PassportMetaData("fr", "Passeport OIFE"),
        "pl-polish.md" to PassportMetaData("fr", "Passeport OIFE"),
        "pt-portugues.md" to PassportMetaData("fr", "Passeport OIFE"),
        "ro-romanian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "ru-russian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "sl-slovenian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "sv-swedish.md" to PassportMetaData("fr", "Passeport OIFE"),
        "tr-turkish.md" to PassportMetaData("fr", "Passeport OIFE"),
        "uk-ukrainian.md" to PassportMetaData("fr", "Passeport OIFE"),
        "zh-chinese.md" to PassportMetaData("fr", "Passeport OIFE", font = chineseFont),
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