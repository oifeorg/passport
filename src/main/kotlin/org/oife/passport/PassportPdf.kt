package org.oife.passport

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

const val OUTPUT_DIR_NAME = "generated"
val outputDir: Path = Paths.get(OUTPUT_DIR_NAME).also { Files.createDirectories(it) }
private val logger = LoggerFactory.getLogger("PassportPdfGenerator")

suspend fun renderToPdf(
    document: RenderableData,
    outputPath: Path = outputDir.resolve(document.pdfFileName),
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

suspend fun generateCombinedPassport(combinedDocumentResource: CombinedDocumentResource): Path {
    val tempCombinedPath =
        renderToPdf(combinedDocumentResource.toRenderable()).also { logger.info(Messages.CombinedPdfGenerated(it.pathString)) }
    val merged = mergePdfFilesToFile(
        parts = listOf(
            loadResourceTempFile("/covers/${Pdf.TITLE_COVER}"),
            tempCombinedPath,
            loadResourceTempFile("/covers/${Pdf.TITLE_BACK}"),
        ),
        outputPath = outputDir.resolve(Pdf.ALL_PASSPORT_COMBINED)
    ).also { logger.info(Messages.PdfGenerated(it.pathString)) }
    Files.delete(tempCombinedPath).also { logger.info(Messages.PdfDeleted(Pdf.TEMP_COMBINED)) }
    return merged
}

suspend fun generateSinglePassports(
    documentResource: DocumentResource
) {
    documentResource.passportConfigs.forEach {
        renderToPdf(documentResource.toRenderable(it)).also { path ->
            logger.info(
                Messages.PdfGenerated(path.pathString)
            )
        }
    }
}

suspend fun mergePdfFilesToFile(
    parts: List<Path>,
    outputPath: Path,
): Path = withContext(Dispatchers.IO) {
    val merger = PDFMergerUtility().apply {
        destinationFileName = outputPath.toString()
        parts.forEach { addSource(it.toFile()) }
    }

    merger.mergeDocuments(null)
    outputPath
}