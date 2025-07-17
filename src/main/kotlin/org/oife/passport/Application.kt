package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")
suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        val htmlTemplate = loadResourceContent("/templates/passport-single.html")
        val metaConfigs = singlePassportConfigs()
        generatePassports(version, htmlTemplate, metaConfigs)
    }.onFailure {
        logger.error("❌ Unexpected error", it)
    }
}