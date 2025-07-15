package org.oife.passport

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


fun String.fromMarkdownToHtml(): String {
    val flavour = flavor()
    val tree = MarkdownParser(flavour).buildMarkdownTreeFromString(this)
    return HtmlGenerator(this, tree, flavour).generateHtml()
}
private fun flavor(): CommonMarkFlavourDescriptor = CommonMarkFlavourDescriptor()

fun String.toFilledHtml(placeholders: Map<String, String>): String =
    placeholders.entries.fold(this) { acc, (key, value) -> acc.replace(key, value) }
