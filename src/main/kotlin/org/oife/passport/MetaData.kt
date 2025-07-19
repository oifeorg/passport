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
                        "languageCode" to config.languageCode,
                        "localizedTitle" to config.localizedTitle,
                        "title" to config.title,
                        "fontType" to config.font.toString().lowercase(),
                        "body" to documentResource.contentMap.getValue(config.markdownFilename).toHtml(),
                        "page-break-after" to if (index == sortedConfig.lastIndex) "" else "page-break-after"
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
                    mapOf<String, String>(
                        "languageCode" to config.languageCode,
                        "fontType" to config.font.toString().lowercase(),
                        "localizedTitle" to config.localizedTitle,
                        "title" to config.title
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
    "passport-content" to passportContent,
    "version" to documentResource.version,
    "year" to Year.now().toString()
) + metaInfo.toHtmlReplacements()

fun CombinedPdfDocument.toHtmlReplacements(): Map<String, String> = mapOf(
    "passport-index-items" to indexItemsContent(),
    "article-items" to articleContents(),
    "version" to documentResource.version,
    "year" to Year.now().toString()
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
    "lang" to languageCode,
    "headerTitle" to if (localizedTitle == title) title else "$localizedTitle - $title",
    "font-family" to font.toFontMeta().familyName,
    "rtl" to direction
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
