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
): RenderableDocument {
    val bodyHtml: String by lazy {
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

fun SinglePdfDocument.toHtmlReplacements(): Map<String, String> = mapOf(
    "body" to bodyHtml,
    "version" to documentResource.version,
    "year" to Year.now().toString()
) + metaInfo.toHtmlReplacements()

@Serializable
data class SinglePassportMeta(
    val markdownFilename: String,
    val languageCode: String,
    val documentTitle: String,
    val font: FontType = FontType.DEFAULT,
) {
    val pdfFileName: String
        get() = markdownFilename.removeSuffix(".md") + ".pdf"

    val direction: String
        get() = if (font.toFontMeta().rtl) "rtl" else "ltr"
}

fun SinglePassportMeta.toHtmlReplacements(): Map<String, String> = mapOf(
    "lang" to languageCode,
    "title" to documentTitle,
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
    ARABIC,
    INDIAN,
    GEORGIAN,
    JAPANESE,
    CHINESE
}

fun FontType.toFontMeta(): FontMeta = when (this) {
    FontType.DEFAULT -> FontMeta()
    FontType.ARABIC -> FontMeta(
        fileName = "NotoNaskhArabic-Regular.ttf",
        familyName = "Noto Naskh Arabic",
        rtl = true
    )
    FontType.INDIAN -> FontMeta(
        fileName = "NotoSansGujarati-Regular.ttf",
        familyName = "Noto Sans Gujarati"
    )
    FontType.GEORGIAN -> FontMeta(
        fileName = "NotoSansGeorgian-Regular.ttf",
        familyName = "Noto Sans Georgian",
    )
    FontType.JAPANESE -> FontMeta(
        fileName = "NotoSansJP-Regular.ttf",
        familyName = "Noto Sans JP",
    )
    FontType.CHINESE -> FontMeta(
        fileName = "NotoSansSC-Regular.ttf",
        familyName = "Noto Sans SC"
    )
}
