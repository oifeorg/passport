package org.oife.passport

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.time.Year


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

fun DocumentResource.toRenderable(meta: SinglePassportMeta) = RenderableData(
    filledHtml = htmlTemplate.toFilledHtml(
        mapOf(
            Placeholder.PASSPORT_CONTENT to contentMap.getValue(meta.markdownFilename).toHtml(),
            Placeholder.VERSION to version,
            Placeholder.YEAR to Year.now().toString()
        ) + meta.toHtmlReplacements()
    ),
    fontMap = fontMap,
    pdfFileName = meta.pdfFileName()
)

fun CombinedDocumentResource.toRenderable() = RenderableData(
    filledHtml = htmlTemplate.toFilledHtml(
        mapOf(
            Placeholder.PASSPORT_INDEX_ITEMS to renderIndexItems(
                configs = passportConfigs,
                indexTemplate = indexTemplate
            ),
            Placeholder.PASSPORT_ARTICLE_ITEMS to renderArticlesContents(
                configs = passportConfigs,
                contentMap = contentMap,
                articleTemplate = articleTemplate
            ),
            Placeholder.LANGUAGE_FONT_STYLES to renderFontStyles(passportConfigs),
            Placeholder.VERSION to version,
            Placeholder.YEAR to Year.now().toString(),
            Placeholder.HEADER_TITLE to "OIFE Passport combined",
            Placeholder.LANG to "en"
        )
    ),
    fontMap = fontMap,
    pdfFileName = Pdf.TEMP_COMBINED
)
