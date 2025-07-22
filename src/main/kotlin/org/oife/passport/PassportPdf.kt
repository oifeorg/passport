package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

const val OUTPUT_DIR_NAME = "generated"
val outputDir: Path = Paths.get(OUTPUT_DIR_NAME).also { Files.createDirectories(it) }
private val logger = LoggerFactory.getLogger("PassportPdfGenerator")

data class PdfDocumentInput(
    val filledHtml: String,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val pdfFileName: String,
)

fun SinglePassport.toPdfInput(meta: PassportMeta) = PdfDocumentInput(
    filledHtml = renderHtml(meta), fontMap = fontMap, pdfFileName = meta.pdfFileName()
)

fun CombinedPassport.toPdfInput() = PdfDocumentInput(
    filledHtml = renderHtml(), fontMap = fontMap, pdfFileName = Pdf.TEMP_COMBINED
)


suspend fun PdfDocumentInput.renderToPdf(
    outputPath: Path = outputDir.resolve(pdfFileName),
): Path = withContext(Dispatchers.IO) {
    Files.newOutputStream(outputPath).use { out ->
        PdfRendererBuilder().apply {
            fontMap.forEach { (familyName, font) ->
                useFont(font, familyName)
            }
            withHtmlContent(filledHtml, null)
            toStream(out)
        }.run()
    }
    outputPath
}

suspend fun CombinedPassport.generate(): Path {
    val combined = toPdfInput().renderToPdf().also { logger.info(Messages.CombinedPdfGenerated(it.pathString)) }
    val merged = mergePdfFilesToFile(
        parts = listOf(
            loadResourceTempFile("/covers/${Pdf.TITLE_COVER}"),
            combined,
            loadResourceTempFile("/covers/${Pdf.TITLE_BACK}")
        ), outputPath = outputDir.resolve(Pdf.ALL_PASSPORT_COMBINED)
    ).also { logger.info(Messages.PdfGenerated(it.pathString)) }
    Files.delete(combined)
    logger.info(Messages.PdfDeleted(Pdf.TEMP_COMBINED))

    return merged
}

suspend fun SinglePassport.generateAll() {
    passportConfigs.forEach { meta ->
        toPdfInput(meta).renderToPdf().also { logger.info(Messages.PdfGenerated(it.pathString)) }
    }
}

suspend fun mergePdfFilesToFile(
    parts: List<Path>,
    outputPath: Path,
): Path = withContext(Dispatchers.IO) {
    PDFMergerUtility().apply {
        destinationFileName = outputPath.toString()
        parts.forEach { addSource(it.toFile()) }
    }.mergeDocuments(null)
    outputPath
}