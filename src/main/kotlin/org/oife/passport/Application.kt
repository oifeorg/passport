package org.oife.passport

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("PassportGenerator")

fun main() {

    passports.forEach {
        it.renderToPdf(singleHtmlTemplateFile)
            .onSuccess { file -> logger.info("✅ PDF generated at: ${file.absolutePath}") }
            .onFailure { logger.error("❌ PDF generation failed", it) }
    }
}