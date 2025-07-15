package org.oife.passport

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun markdownToHtml(markdown: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val tree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
    return HtmlGenerator(markdown, tree, flavour).generateHtml()
}

fun fillHtmlTemplate(
    template: String,
    metadata: PassportMetaData,
    bodyHtml: String
): String = template
    .replace("{{lang}}", metadata.languageCode)
    .replace("{{title}}", metadata.documentTitle)
    .replace("{{font-family}}", metadata.font.familyName)
    .replace("{{body}}", bodyHtml)

fun renderPdfToFile(
    html: String,
    fontSupplier: FSSupplier<InputStream>,
    outputFile: File,
    fontFamily: String
): Result<File> = runCatching {
    FileOutputStream(outputFile).use { out ->
        PdfRendererBuilder()
            .useFont(fontSupplier, fontFamily)
            .withHtmlContent(html, null)
            .toStream(out)
            .run()
    }
    outputFile
}