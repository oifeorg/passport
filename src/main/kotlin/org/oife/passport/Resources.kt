package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.InputStream

private val logger = LoggerFactory.getLogger("ResourceLoader")

data class DocumentResource(
    val htmlTemplate: String,
    val passportConfigs: List<SinglePassportMeta>,
    val contentMap: Map<String, String>,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val version: String
)

suspend fun fontMap(passports: List<SinglePassportMeta>): Map<String, FSSupplier<InputStream>> = coroutineScope {
    passports
        .map { it.font }
        .distinctBy { it.toFontMeta().familyName }
        .map { fontType ->
            val fontMeta = fontType.toFontMeta()
            async {
                val path = "/fonts/${fontMeta.fileName}"
                val bytes = loadResourceBytes(path)
                fontMeta.familyName to FSSupplier<InputStream> { bytes.inputStream() }
            }
        }
        .awaitAll()
        .toMap()
}

private val jsonFormat = Json { ignoreUnknownKeys = true }
suspend fun loadPassportConfigs(): List<SinglePassportMeta> {
    val json = loadResourceContent("/passport-config.json")
    return jsonFormat
        .decodeFromString(json)
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

        logger.debug("$description Loaded resource: $path")

        reader(stream)
    }

suspend fun loadResourceContent(path: String): String =
    loadResource(path, "📄") { it.bufferedReader().use { reader -> reader.readText() } }

suspend fun loadResourceBytes(path: String): ByteArray =
    loadResource(path, "📦") { it.readBytes() }

suspend fun getDocumentResource(htmlTemplatePath: String, version: String): DocumentResource = coroutineScope {
    val htmlTemplateDeferred = async { loadResourceContent(htmlTemplatePath) }
    val passportConfigs = loadPassportConfigs()
    val fontMapDeferred = async { fontMap(passportConfigs) }
    val contentMapDeferred = async { passportContentMap(passportConfigs) }

    val fontMap = fontMapDeferred.await()
    val contentMap = contentMapDeferred.await()
    val htmlTemplate = htmlTemplateDeferred.await()
    DocumentResource(
        version = version,
        htmlTemplate = htmlTemplate,
        passportConfigs = passportConfigs,
        contentMap = contentMap,
        fontMap = fontMap
    )
}
