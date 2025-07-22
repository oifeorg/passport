package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.InputStream
import java.time.Year
import kotlin.collections.Map


fun String.toHtml(): String {
    val flavour = flavor()
    val tree = MarkdownParser(flavour).parse(IElementType("ROOT"), this)
    return HtmlGenerator(this, tree, flavour).generateHtml()
}

fun String.toFilledHtml(replacements: Map<String, String>): String =
    replacements.entries.fold(this) { acc, (key, value) ->
        acc.replace("{{${key}}}", value)
    }

private fun flavor(): CommonMarkFlavourDescriptor = GFMFlavourDescriptor()

fun renderFontStyles(passportConfigs: List<SinglePassportMeta>): String = buildString {
    passportConfigs
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

fun renderArticlesContents(
    configs: List<SinglePassportMeta>,
    contentMap: Map<String, String>,
    articleTemplate: String,
): String = buildString {
    val sorted = configs.sortedBy { it.languageCode }
    sorted.forEachIndexed { index, config ->
        appendLine(
            articleTemplate.toFilledHtml(
                mapOf(
                    Placeholder.LANGUAGE_CODE to config.languageCode,
                    Placeholder.LOCALIZED_TITLE to config.localizedTitle,
                    Placeholder.TITLE to config.title,
                    Placeholder.FONT_TYPE to config.font.toCssClass(config.languageCode),
                    Placeholder.BODY to contentMap.getValue(config.markdownFilename).toHtml(),
                    Placeholder.PAGE_BREAK_AFTER to if (index == sorted.lastIndex) "" else Placeholder.PAGE_BREAK_AFTER,
                    Placeholder.DIRECTION to config.direction(),
                    Placeholder.HIDDEN to if (config.isLocalizedTitleSame()) "hidden" else ""
                )
            )
        )
    }
}

fun renderIndexItems(configs: List<SinglePassportMeta>, indexTemplate: String): String = buildString {
    configs.sortedBy { it.languageCode }.forEach { config ->
        appendLine(
            indexTemplate.toFilledHtml(
                mapOf(
                    Placeholder.LANGUAGE_CODE to config.languageCode,
                    Placeholder.FONT_TYPE to config.font.toCssClass(config.languageCode),
                    Placeholder.LOCALIZED_TITLE to config.localizedTitle,
                    Placeholder.TITLE to config.title,
                    Placeholder.HIDDEN to if (config.isLocalizedTitleSame()) "hidden" else ""
                )
            )
        )
    }
}

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
        get() = documentResource.fontMap

    override val pdfFileName: String
        get() = metaInfo.pdfFileName()
}

fun DocumentResource.toRenderable(meta: SinglePassportMeta): RenderableData = RenderableData(
    filledHtml = htmlTemplate.toFilledHtml( mapOf(
        Placeholder.PASSPORT_CONTENT to contentMap.getValue(meta.markdownFilename).toHtml(),
        Placeholder.VERSION to version,
        Placeholder.YEAR to Year.now().toString()
    ) + meta.toHtmlReplacements()),
    fontMap = fontMap,
    pdfFileName = meta.pdfFileName()
)

data class CombinedPdfDocument(val documentResource: CombinedDocumentResource) : RenderableDocument {
    fun languageFontStyles(): String =
        renderFontStyles(documentResource.passportConfigs)

    fun articleContents(): String =
        renderArticlesContents(
            configs = documentResource.passportConfigs,
            contentMap = documentResource.contentMap,
            articleTemplate = documentResource.articleTemplate
        )

    fun indexItemsContent(): String =
        renderIndexItems(
            configs = documentResource.passportConfigs,
            indexTemplate = documentResource.indexTemplate
        )

    override val filledHtml: String
        get() = documentResource.htmlTemplate.toFilledHtml(toHtmlReplacements())
    override val fontMap: Map<String, FSSupplier<InputStream>>
        get() = documentResource.fontMap
    override val pdfFileName: String
        get() = Pdf.TEMP_COMBINED
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