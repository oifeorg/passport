package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.InputStream

private val logger = LoggerFactory.getLogger("ResourceLoader")

suspend fun fontMap(passports: List<SinglePassportMeta>): Map<String, FSSupplier<InputStream>> = coroutineScope {
    passports
        .map { it.font }
        .distinctBy { it.fileName }
        .map { font ->
            async {
                val path = "/fonts/${font.fileName}"
                val bytes = loadResourceBytes(path)
                font.fileName to FSSupplier<InputStream> { bytes.inputStream() }
            }
        }
        .awaitAll()
        .toMap()
}

suspend fun passportContentMap(passports: List<SinglePassportMeta>): Map<String, String> = coroutineScope {
    passports.map { metadata ->
        async {
            val path = "/data/${metadata.markdownFilename}"
            metadata.markdownFilename to loadResourceContent(path)
        }
    }.awaitAll().toMap()
}

private suspend fun <T> loadResource(path: String, description: String, reader: (InputStream) -> T): T =
    withContext(Dispatchers.IO) {
        val stream = object {}.javaClass.getResourceAsStream(path)
            ?: throw IllegalStateException("Resource not found: $path")

        logger.info("$description Loaded resource: $path")

        reader(stream)
    }

suspend fun loadResourceContent(path: String): String =
    loadResource(path, "📄") { it.bufferedReader().use { reader -> reader.readText() } }

suspend fun loadResourceBytes(path: String): ByteArray =
    loadResource(path, "📦") { it.readBytes() }
