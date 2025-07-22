package org.oife.passport

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.time.Year


fun PassportMeta.toReplacements(): Map<String, String> = mapOf(
    Placeholder.LANG to languageCode,
    Placeholder.HEADER_TITLE to if (isLocalizedTitleSame()) title else "$localizedTitle - $title",
    Placeholder.FONT_FAMILY to font.familyName,
    Placeholder.DIRECTION to direction()
)

fun FontMeta.toCssClass(languageCode: String): String = if (this == FontMeta()) "default" else languageCode

fun FontMeta.toTextAlign(): String = if (this.direction == "ltr") "left" else "right"


fun String.toHtml(): String {
    val flavour = flavor()
    val tree = MarkdownParser(flavour).parse(IElementType("ROOT"), this)
    return HtmlGenerator(this, tree, flavour).generateHtml()
}

fun String.replacePlaceholders(replacements: Map<String, String>): String =
    replacements.entries.fold(this) { acc, (key, value) ->
        acc.replace("{{${key}}}", value)
    }

private fun flavor() = GFMFlavourDescriptor()

private fun SinglePassport.toPlaceholderMap(meta: PassportMeta): Map<String, String> = mapOf(
    Placeholder.PASSPORT_CONTENT to contentMap.getValue(meta.markdownFilename).toHtml(),
    Placeholder.VERSION to version,
    Placeholder.YEAR to Year.now().toString()
) + meta.toReplacements()

private fun CombinedPassport.toPlaceholderMap(): Map<String, String> = mapOf(
    Placeholder.PASSPORT_INDEX_ITEMS to renderIndexItems(
        configs = passportConfigs,
        indexTemplate = indexTemplate
    ),
    Placeholder.PASSPORT_ARTICLE_ITEMS to renderArticleSections(
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

fun SinglePassport.toHtml(meta: PassportMeta): String = htmlTemplate.replacePlaceholders(
    toPlaceholderMap(meta)
)

fun CombinedPassport.toHtml(): String = htmlTemplate.replacePlaceholders(
    toPlaceholderMap()
)

fun renderFontStyles(passportConfigs: List<PassportMeta>): String = buildString {
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

fun renderArticleSections(
    configs: List<PassportMeta>,
    contentMap: Map<String, String>,
    articleTemplate: String,
): String = buildString {
    val sorted = configs.sortedBy { it.languageCode }
    sorted.forEachIndexed { index, config ->
        appendLine(
            articleTemplate.replacePlaceholders(
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

fun renderIndexItems(configs: List<PassportMeta>, indexTemplate: String): String = buildString {
    configs.sortedBy { it.languageCode }.forEach { config ->
        appendLine(
            indexTemplate.replacePlaceholders(
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

