package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

const val OUTPUT_DIR_NAME = "generated"
val outputDir: Path = Paths.get(OUTPUT_DIR_NAME).also { Files.createDirectories(it) }
private val logger = LoggerFactory.getLogger("PassportPdfGenerator")



suspend fun renderToPdf(
    document: PdfDocument,
    outputPath: Path = outputDir.resolve(document.metaInfo.pdfFileName)
): Path = withContext(Dispatchers.IO) {
    Files.newOutputStream(outputPath).use { out ->
        PdfRendererBuilder().apply {
            useFont(document.font, document.metaInfo.font.toFontMeta().familyName)
            withHtmlContent(document.filledHtml, null)
            toStream(out)
        }.run()
    }

    outputPath
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
                font = fontMap.getValue(meta.font.toFontMeta().fileName)
            )
        }
        .onEach { doc ->
            renderToPdf(doc).also {
                logger.info("✅ PDF generated: ${it.pathString}")
            }
        }
}

