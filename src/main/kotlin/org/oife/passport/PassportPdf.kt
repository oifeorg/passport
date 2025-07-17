package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

const val OUTPUT_DIR_NAME = "generated"
val outputDir = File(OUTPUT_DIR_NAME).apply { mkdirs() }

private val logger = LoggerFactory.getLogger("PassportPdfGenerator")


suspend fun renderToPdf(
    document: PdfDocument,
    outputFile: File = File(outputDir, document.metaInfo.pdfFileName)
): File = withContext(Dispatchers.IO) {
    outputFile.outputStream().use { out ->
        PdfRendererBuilder().apply {
            useFont(document.font, document.metaInfo.font.familyName)
            withHtmlContent(
                document.filledHtml,
                null
            )
            toStream(out)
        }.run()
    }

    outputFile
}

suspend fun generatePassports(
    version: String,
    htmlTemplate: String,
    metaConfigs: List<SinglePassportMeta>
) = coroutineScope {
    val fontMapDeferred = async { fontMap(metaConfigs) }
    val contentMapDeferred = async { passportContentMap(metaConfigs) }

    val fontMap = fontMapDeferred.await()
    val contentMap = contentMapDeferred.await()

    metaConfigs
        .map { meta ->
            PdfDocument(
                version = version,
                contentMarkdown = contentMap.getValue(meta.markdownFilename),
                metaInfo = meta,
                htmlTemplate = htmlTemplate,
                font = fontMap.getValue(meta.font.fileName)
            )
        }
        .onEach { doc ->
            renderToPdf(doc).also {
                logger.info("✅ PDF generated: ${it.absolutePath}")
            }
        }
}

