package org.oife.passport

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser


fun String.fromMarkdownToHtml(): String {
    val flavour = flavor()
    val tree = MarkdownParser(flavour).parse(IElementType("ROOT"), this)
    return HtmlGenerator(this, tree, flavour).generateHtml()
}

fun String.toFilledHtml(replacements: Map<String, String>): String =
    replacements.entries.fold(this) { acc, (key, value) ->
        acc.replace("{{${key}}}", value)
    }

private fun flavor(): CommonMarkFlavourDescriptor = GFMFlavourDescriptor()
