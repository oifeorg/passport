package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
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
    document: RenderableDocument,
    outputPath: Path = outputDir.resolve(document.pdfFileName)
): Path = withContext(Dispatchers.IO) {
    Files.newOutputStream(outputPath).use { out ->
        PdfRendererBuilder().apply {
            document.fontMap.forEach { (familyName, font) ->
                useFont(font, familyName)
            }
            withHtmlContent(document.filledHtml, null)
            toStream(out)
        }.run()
    }

    outputPath
}

suspend fun generateSinglePassports(
    documentResource: DocumentResource
) = coroutineScope {

    documentResource.passportConfigs
        .map { meta ->
            SinglePdfDocument(
                metaInfo = meta,
                documentResource = documentResource
            )
        }
        .onEach { doc ->
            renderToPdf(doc).also {
                logger.info("✅ PDF generated: ${it.pathString}")
            }
        }
}

