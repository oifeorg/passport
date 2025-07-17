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

val singleHtmlTemplateFile = loadResourceText("/templates/passport-single.html")
val fontSupplierMap = buildFontSupplierMap()

private fun getResourceStream(path: String): InputStream? =
    object {}.javaClass.getResourceAsStream(path)?.also {
        logger.info("✅ Loaded resource: $path")
    } ?: run {
        logger.error("❌ Resource not found: $path")
        null
    }

fun loadResourceText(path: String): String {
    return getResourceStream(path)?.bufferedReader()?.use { it.readText() } ?: run {
        logger.error("❌ Failed to load text resource: $path")
        error("Resource not found or unreadable: $path")
    }
}

//suspend fun loadResourceContent(path: String): String = withContext(Dispatchers.IO) {
//    val stream = object {}.javaClass.getResourceAsStream(path)
//        ?: throw IllegalStateException("Resource not found: $path")
//
//    logger.info("📄 Loaded resource: $path")
//
//    stream.bufferedReader().use { it.readText() }
//}

fun buildFontSupplierMap(): Map<String, FSSupplier<InputStream>> =
    passports
        .map { it.font }
        .distinctBy { it.fileName }
        .associate { font ->
            val bytes = getResourceStream("/fonts/${font.fileName}")
                ?.readBytes()
                ?: error("❌ Font not found: ${font.fileName}")
            font.fileName to FSSupplier { bytes.inputStream() }
        }

suspend fun fontMap(passports: List<PassportMetaData>): Map<String, FSSupplier<InputStream>> = coroutineScope {
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

suspend fun passportContentMap(passports: List<PassportMetaData>): Map<String, String> = coroutineScope {
    passports.map { metadata ->
        async {
            val path = "/data/${metadata.markdownFilename}"
            metadata.markdownFilename to loadResourceContent(path)
        }
    }.awaitAll().toMap()
}

//private suspend fun loadResourceBytes(path: String): ByteArray = withContext(Dispatchers.IO) {
//    val stream = object {}.javaClass.getResourceAsStream(path)
//        ?: throw IllegalStateException("Resource not found: $path")
//
//    logger.info("📦 Loaded binary resource: $path")
//
//    stream.readBytes()
//}

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
