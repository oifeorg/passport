package org.oife.passport

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGenerator")

suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        val metaConfigs = singlePassportConfigs()

        coroutineScope {
            val htmlTemplateDeferred = async { loadResourceContent("/templates/passport-single.html") }
            val fontMapDeferred = async { fontMap(metaConfigs) }
            val contentMapDeferred = async { passportContentMap(metaConfigs) }

            val htmlTemplate = htmlTemplateDeferred.await()
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
    }.onFailure {
        logger.error("❌ Unexpected error", it)
    }
}