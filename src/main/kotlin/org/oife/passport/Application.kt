package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

fun main() {

    val lang = "en"
    val title = "OIFE Passport"
    val header = "Welcome"
    val body = "This PDF uses the Noto Sans font."

    val htmlTemplate = object {}.javaClass.getResourceAsStream("/templates/passport-content.html")
        ?.bufferedReader()?.use { it.readText() }
        ?: error("HTML template not found")

    val html = htmlTemplate
        .replace("{{lang}}", lang)
        .replace("{{title}}", title)
        .replace("{{header}}", header)
        .replace("{{body}}", body)

    val fontTempFile = Files.createTempFile("font-", ".ttf").toFile()
    object {}.javaClass.getResourceAsStream("/fonts/NotoSans-Regular.ttf")
        ?.use { input ->
            fontTempFile.outputStream().use { output -> input.copyTo(output) }
        }
        ?: error("Font file not found")
    val outputFile = File("output.pdf")

    val out = FileOutputStream(outputFile)
    try {
        PdfRendererBuilder().useFont(fontTempFile, "NotoSans")
            .withHtmlContent(html, null)
            .toStream(out)
            .run()
    } finally {
        out.close()
    }

    println("✅ PDF generated at: ${outputFile.absolutePath}")
}