package org.oife.passport

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun String.fromMarkdownToHtml(): String {
    val flavour = flavor()
    val tree = MarkdownParser(flavour).buildMarkdownTreeFromString(this)
    return HtmlGenerator(this, tree, flavour).generateHtml()
}

private fun flavor(): CommonMarkFlavourDescriptor = CommonMarkFlavourDescriptor()

fun String.toFilledHtml(placeholders: Map<String, String>): String =
    placeholders.entries.fold(this) { acc, (key, value) -> acc.replace(key, value) }

fun renderPdfToFile(
    filledHtml: String,
    fontSupplier: FSSupplier<InputStream>,
    outputFile: File,
    fontFamily: String
): Result<File> = runCatching {
    FileOutputStream(outputFile).use { out ->
        PdfRendererBuilder()
            .useFont(fontSupplier, fontFamily)
            .withHtmlContent(filledHtml, null)
            .toStream(out)
            .run()
    }
    outputFile
}