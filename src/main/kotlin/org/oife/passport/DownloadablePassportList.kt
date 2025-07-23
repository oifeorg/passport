package org.oife.passport

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.pathString

private val logger = LoggerFactory.getLogger("DownloadablePassportListGenerator")


private val jsonOutputFormat = Json { prettyPrint = true }

private fun CombinedPassport.loadDownloadablePassports(): JsonArray = buildJsonArray {
    add(Pdf.ALL_PASSPORT_COMBINED)
    this@loadDownloadablePassports.passportConfigs.sortedBy { it.languageCode }.forEach {
        add(it.pdfFileName())
    }
}

fun CombinedPassport.generateDownloadableList(): Path? =
    Files.writeString(
        outputDir.resolve("passport-list.json"),
        jsonOutputFormat.encodeToString(JsonArray.serializer(), loadDownloadablePassports()),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE
    ).also { logger.info(Messages.PassportListGenerated(it.pathString)) }
