package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")
suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        val singlePassport = loadSinglePassport(Template.PASSPORT_SINGLE, version)
        singlePassport.generateAll()

        val combinedPassport = loadCombinedPassport(singlePassport)
        combinedPassport.generate()
    }.onFailure {
        logger.error(Messages.UnexpectedError.toString(), it)
    }
}
