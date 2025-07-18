package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")
suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        val singleDocumentResource = getDocumentResource("/templates/passport-single.html", version)
        generateSinglePassports(singleDocumentResource)
    }.onFailure {
        logger.error("❌ Unexpected error", it)
    }
}
