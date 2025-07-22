package org.oife.passport

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.time.Year

fun FontMeta.toCssClass(languageCode: String): String = if (this == FontMeta()) "default" else languageCode

fun FontMeta.toTextAlign(): String = if (this.direction == "ltr") "left" else "right"

private val flavour = GFMFlavourDescriptor()

fun String.renderHtml(): String = HtmlGenerator(this, markDownParser(flavour), flavour).generateHtml()

private fun String.markDownParser(flavour: GFMFlavourDescriptor): ASTNode =
    MarkdownParser(flavour).parse(IElementType("ROOT"), this)

fun String.replacePlaceholders(replacements: Map<String, String>): String =
    replacements.entries.fold(this) { acc, (key, value) ->
        acc.replace("{{${key}}}", value)
    }

private fun SinglePassport.toPlaceholderMap(meta: PassportMeta): Map<String, String> = mapOf(
    Placeholder.PASSPORT_CONTENT to contentMap.getValue(meta.markdownFilename).renderHtml(),
    Placeholder.VERSION to version,
    Placeholder.YEAR to Year.now().toString(),
    Placeholder.LANG to meta.languageCode,
    Placeholder.HEADER_TITLE to meta.headerTitle(),
    Placeholder.FONT_FAMILY to meta.font.familyName,
    Placeholder.DIRECTION to meta.direction()
)

private fun CombinedPassport.toPlaceholderMap(): Map<String, String> = mapOf(
    Placeholder.PASSPORT_INDEX_ITEMS to renderIndexItems(
        configs = passportConfigs, indexTemplate = indexTemplate
    ),
    Placeholder.PASSPORT_ARTICLE_ITEMS to renderArticleSections(
        configs = passportConfigs, contentMap = contentMap, articleTemplate = articleTemplate
    ),
    Placeholder.LANGUAGE_FONT_STYLES to renderFontStyles(passportConfigs),
    Placeholder.VERSION to version,
    Placeholder.YEAR to Year.now().toString(),
    Placeholder.HEADER_TITLE to "OIFE Passport combined",
    Placeholder.LANG to "en"
)

fun SinglePassport.renderHtml(meta: PassportMeta): String = htmlTemplate.replacePlaceholders(
    toPlaceholderMap(meta)
)

fun CombinedPassport.renderHtml(): String = htmlTemplate.replacePlaceholders(
    toPlaceholderMap()
)

fun PassportMeta.hiddenClass(): String =
    if (isLocalizedTitleSame()) "hidden" else ""

fun PassportMeta.toCssStyleBlock(): String = """
    .lang-${font.toCssClass(languageCode)} {
        font-family: "${font.familyName}", sans-serif;
        direction: ${font.direction};
        text-align: ${font.toTextAlign()};
    }
""".trimIndent()

fun PassportMeta.toArticleHtml(
    articleTemplate: String,
    content: String,
    isLast: Boolean
): String = articleTemplate.replacePlaceholders(
    mapOf(
        Placeholder.LANGUAGE_CODE to languageCode,
        Placeholder.LOCALIZED_TITLE to localizedTitle,
        Placeholder.TITLE to title,
        Placeholder.FONT_TYPE to font.toCssClass(languageCode),
        Placeholder.BODY to content.renderHtml(),
        Placeholder.PAGE_BREAK_AFTER to if (isLast) "" else Placeholder.PAGE_BREAK_AFTER,
        Placeholder.DIRECTION to direction(),
        Placeholder.HIDDEN to hiddenClass()
    )
)

fun renderFontStyles(passportConfigs: List<PassportMeta>): String = buildString {
    passportConfigs
        .distinctBy { it.font }
        .forEach { appendLine(it.toCssStyleBlock()) }
}

fun renderArticleSections(
    configs: List<PassportMeta>,
    contentMap: Map<String, String>,
    articleTemplate: String,
): String = buildString {
    configs.sortedBy { it.languageCode }
        .forEachIndexed { index, config ->
            val isLast = index == configs.lastIndex
            val content = contentMap.getValue(config.markdownFilename)
            appendLine(config.toArticleHtml(articleTemplate, content, isLast))
        }
}

fun renderIndexItems(configs: List<PassportMeta>, indexTemplate: String): String = buildString {
    configs.sortedBy { it.languageCode }.forEach { config ->
        appendLine(config.toIndexPlaceholders(indexTemplate))
    }
}

private fun PassportMeta.toIndexPlaceholders(indexTemplate: String): String = indexTemplate.replacePlaceholders(
    mapOf(
        Placeholder.LANGUAGE_CODE to languageCode,
        Placeholder.FONT_TYPE to font.toCssClass(languageCode),
        Placeholder.LOCALIZED_TITLE to localizedTitle,
        Placeholder.TITLE to title,
        Placeholder.HIDDEN to hiddenClass()
    )
)
