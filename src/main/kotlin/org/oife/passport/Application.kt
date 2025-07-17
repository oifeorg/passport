package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGenerator")

suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"
    runCatching {

        val htmlTemplate = loadResourceContent("/templates/passport-single.html")
        val passportMetaConfigs = passportMetaConfigs()
        val fontMap = fontMap(passportMetaConfigs)
        val passportContentMap = passportContentMap(passportMetaConfigs)
    }.onFailure {
        logger.error("❌ Unexpected error", it)
    }
    // Load markdown files


    // Load font map


    // generateAllSinglePassports()
}
