package org.oife.passport

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PassportGeneratorApplication")

suspend fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1.0.0"

    runCatching {
        ensureOutputDirectoryExists()
        loadSinglePassport(version)
            .also { it.generateAll() }
            .toCombinedPassport()
            .also {
                it.generate()
                it.generateDownloadableList()
            }
    }.onFailure {
        logger.error(Messages.UnexpectedError.toString(), it)
    }
}
