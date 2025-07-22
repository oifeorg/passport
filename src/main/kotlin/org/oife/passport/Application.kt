package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")
suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        val singleDocumentResource = buildDocumentResource(Template.PASSPORT_SINGLE, version)
        generateSinglePassports(singleDocumentResource)

        generateCombinedPassport(getCombinedDocumentResource(singleDocumentResource))
    }.onFailure {
        logger.error(Messages.UnexpectedError.toString(), it)
    }
}
