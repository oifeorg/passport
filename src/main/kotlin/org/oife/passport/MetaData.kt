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
        get() = mapOf(metaInfo.font.toFontMeta().familyName to documentResource.fontMap.getValue(metaInfo.font.toFontMeta().familyName))

    override val pdfFileName: String
        get() = metaInfo.pdfFileName
}

data class CombinedPdfDocument(val documentResource: CombinedDocumentResource) : RenderableDocument {
    fun articleContents(): String = buildString {
        val sortedConfig = documentResource.passportConfigs.sortedBy { it.languageCode }
        sortedConfig.forEachIndexed { index, config ->
            appendLine(
                documentResource.articleTemplate.toFilledHtml(
                    mapOf(
                        Placeholder.LANGUAGE_CODE to config.languageCode,
                        Placeholder.LOCALIZED_TITLE to config.localizedTitle,
                        Placeholder.TITLE to config.title,
                        Placeholder.FONT_TYPE to config.font.toString().lowercase(),
                        Placeholder.BODY to documentResource.contentMap.getValue(config.markdownFilename).toHtml(),
                        Placeholder.PAGE_BREAK_AFTER to if (index == sortedConfig.lastIndex) "" else Placeholder.PAGE_BREAK_AFTER
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
                        Placeholder.FONT_TYPE to config.font.toString().lowercase(),
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
    Placeholder.VERSION to documentResource.version,
    Placeholder.YEAR to Year.now().toString()
)

@Serializable
data class SinglePassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val title: String,
    val localizedTitle: String = "",
    val font: FontType = FontType.DEFAULT,
) {
    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"

    val direction: String
        get() = if (font.toFontMeta().rtl) "rtl" else "ltr"
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    Placeholder.LANG to languageCode,
    Placeholder.HEADER_TITLE to if (localizedTitle == title) title else "$localizedTitle - $title",
    Placeholder.FONT_FAMILY to font.toFontMeta().familyName,
    Placeholder.DIRECTION to direction
)

data class FontMeta(
    val fileName: String = "NotoSans-Regular.ttf",
    val familyName: String = "Noto Sans",
    val rtl: Boolean = false,
)

@Serializable
enum class FontType {
    DEFAULT,
    AR,
    GU,
    KA,
    JA,
    ZH
}

fun FontType.toFontMeta(): FontMeta = when (this) {
    FontType.DEFAULT -> FontMeta()
    FontType.AR -> FontMeta(
        fileName = "NotoNaskhArabic-Regular.ttf",
        familyName = "Noto Naskh Arabic",
        rtl = true
    )

    FontType.GU -> FontMeta(
        fileName = "NotoSansGujarati-Regular.ttf",
        familyName = "Noto Sans Gujarati"
    )

    FontType.KA -> FontMeta(
        fileName = "NotoSansGeorgian-Regular.ttf",
        familyName = "Noto Sans Georgian",
    )

    FontType.JA -> FontMeta(
        fileName = "NotoSansJP-Regular.ttf",
        familyName = "Noto Sans JP",
    )

    FontType.ZH -> FontMeta(
        fileName = "NotoSansSC-Regular.ttf",
        familyName = "Noto Sans SC"
    )
}
