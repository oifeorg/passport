package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")
suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        loadSinglePassport(Template.PASSPORT_SINGLE, version).apply {
            generateAll()
            toCombinedPassport().generate()
        }
    }.onFailure {
        logger.error(Messages.UnexpectedError.toString(), it)
    }
}
