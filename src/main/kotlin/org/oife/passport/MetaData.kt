package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.serialization.Serializable
import java.io.InputStream
import java.time.Year

interface RenderableDocument {
    val filledHtml: String
    val fontMap: Map<String, FSSupplier<InputStream>>
    val pdfFileName: String
}

data class SinglePdfDocument(
    val documentResource: DocumentResource,
    val metaInfo: SinglePassportMeta,
) : RenderableDocument {
    val passportContent: String by lazy {
        documentResource.contentMap.getValue(metaInfo.markdownFilename).toHtml()
    }

    override val filledHtml: String by lazy {
        documentResource.htmlTemplate.toFilledHtml(toHtmlReplacements())
    }

    override val fontMap: Map<String, FSSupplier<InputStream>>
        get() = mapOf(metaInfo.font.familyName to documentResource.fontMap.getValue(metaInfo.font.familyName))

    override val pdfFileName: String
        get() = metaInfo.pdfFileName
}

data class CombinedPdfDocument(val documentResource: CombinedDocumentResource) : RenderableDocument {
    fun languageFontStyles(): String = buildString {
        documentResource.passportConfigs
            .distinctBy { it.font }
            .forEach {
            val langCss = """
                .lang-${it.font.toCssClass(it.languageCode)} {
                    font-family: "${it.font.familyName}", sans-serif;
                    direction: ${it.font.direction};
                    text-align: ${it.font.toTextAlign()};
                }
            """.trimIndent()
            appendLine(langCss)
        }
    }

    fun articleContents(): String = buildString {
        val sortedConfig = documentResource.passportConfigs.sortedBy { it.languageCode }
        sortedConfig.forEachIndexed { index, config ->
            appendLine(
                documentResource.articleTemplate.toFilledHtml(
                    mapOf(
                        Placeholder.LANGUAGE_CODE to config.languageCode,
                        Placeholder.LOCALIZED_TITLE to config.localizedTitle,
                        Placeholder.TITLE to config.title,
                        Placeholder.FONT_TYPE to config.font.toCssClass(languageCode = config.languageCode),
                        Placeholder.BODY to documentResource.contentMap.getValue(config.markdownFilename).toHtml(),
                        Placeholder.PAGE_BREAK_AFTER to if (index == sortedConfig.lastIndex) "" else Placeholder.PAGE_BREAK_AFTER,
                        Placeholder.DIRECTION to config.direction
                    )
                )
            )
        }
    }

    fun indexItemsContent(): String = buildString {
        documentResource.passportConfigs
            .sortedBy { it.languageCode }
            .forEach { config ->
                appendLine(
                    documentResource.indexTemplate.toFilledHtml(
                        mapOf(
                            Placeholder.LANGUAGE_CODE to config.languageCode,
                            Placeholder.FONT_TYPE to config.font.toCssClass(languageCode = config.languageCode),
                            Placeholder.LOCALIZED_TITLE to config.localizedTitle,
                            Placeholder.TITLE to config.title
                        )
                    )
                )
            }
    }

    override val filledHtml: String
        get() = documentResource.htmlTemplate.toFilledHtml(toHtmlReplacements())
    override val fontMap: Map<String, FSSupplier<InputStream>>
        get() = documentResource.fontMap
    override val pdfFileName: String
        get() = "all-passport-combined.pdf"
}

fun SinglePdfDocument.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.PASSPORT_CONTENT to passportContent,
    Placeholder.VERSION to documentResource.version,
    Placeholder.YEAR to Year.now().toString()
) + metaInfo.toHtmlReplacements()

fun CombinedPdfDocument.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.PASSPORT_INDEX_ITEMS to indexItemsContent(),
    Placeholder.PASSPORT_ARTICLE_ITEMS to articleContents(),
    Placeholder.LANGUAGE_FONT_STYLES to languageFontStyles(),
    Placeholder.VERSION to documentResource.version,
    Placeholder.YEAR to Year.now().toString(),
    Placeholder.HEADER_TITLE to "OIFE Passport combined",
    Placeholder.LANG to "en"
)

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
