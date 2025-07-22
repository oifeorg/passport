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
import java.nio.file.Files
import java.nio.file.Path

private val logger = LoggerFactory.getLogger("ResourceLoader")

data class SinglePassport(
    val htmlTemplate: String,
    val passportConfigs: List<PassportMeta>,
    val contentMap: Map<String, String>,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val version: String
)

data class CombinedPassport(
    val indexTemplate: String,
    val articleTemplate: String,
    val htmlTemplate: String,
    val passportConfigs: List<PassportMeta>,
    val contentMap: Map<String, String>,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val version: String
)

fun SinglePassport.toCombined(
    indexTemplate: String,
    articleTemplate: String,
    htmlTemplate: String,
): CombinedPassport = CombinedPassport(
    indexTemplate = indexTemplate,
    articleTemplate = articleTemplate,
    htmlTemplate = htmlTemplate,
    passportConfigs = this.passportConfigs,
    contentMap = this.contentMap,
    fontMap = this.fontMap,
    version = this.version
)

suspend fun fontMap(passports: List<PassportMeta>): Map<String, FSSupplier<InputStream>> = coroutineScope {
    passports
        .map { it.font }
        .distinctBy { it.familyName }
        .map { font ->
            async {
                val path = "/fonts/${font.fileName}"
                val bytes = loadResourceBytes(path)
                font.familyName to FSSupplier<InputStream> { bytes.inputStream() }
            }
        }
        .awaitAll()
        .toMap()
}

private val jsonFormat = Json { ignoreUnknownKeys = true }
suspend fun loadPassportConfigs(): List<PassportMeta> {
    val json = loadResourceContent("/passport-config.json")
    return jsonFormat
        .decodeFromString(json)
}

suspend fun passportContentMap(passports: List<PassportMeta>): Map<String, String> = coroutineScope {
    passports.map { metadata ->
        async {
            val path = "/data/${metadata.markdownFilename}"
            metadata.markdownFilename to loadResourceContent(path)
        }
    }.awaitAll().toMap()
}

private suspend fun <T> loadResource(path: String, reader: (InputStream) -> T): T =
    withContext(Dispatchers.IO) {
        val stream = object {}.javaClass.getResourceAsStream(path)
            ?: throw IllegalStateException(Messages.ResourceNotFound(path))

        logger.info(Messages.ResourceLoaded(path))

        reader(stream)
    }

suspend fun loadResourceContent(path: String): String =
    loadResource(path) { it.bufferedReader().use { reader -> reader.readText() } }

suspend fun loadResourceBytes(path: String): ByteArray =
    loadResource(path) { it.readBytes() }

suspend fun loadResourceTempFile(path: String): Path =
    loadResource(path) { inputStream ->
        val fileName = Path.of(path).fileName.toString()
        val suffix = if (fileName.contains('.')) fileName.substringAfterLast('.') else "tmp"
        val tempFile = Files.createTempFile("resource-", ".$suffix")
        tempFile.toFile().deleteOnExit()
        inputStream.use { it.copyTo(tempFile.toFile().outputStream()) }
        tempFile
    }

suspend fun loadSinglePassport(htmlTemplatePath: String, version: String): SinglePassport = coroutineScope {
    val htmlTemplateDeferred = async { loadResourceContent(htmlTemplatePath) }
    val passportConfigs = loadPassportConfigs()
    val fontMapDeferred = async { fontMap(passportConfigs) }
    val contentMapDeferred = async { passportContentMap(passportConfigs) }
    SinglePassport(
        version = version,
        htmlTemplate = htmlTemplateDeferred.await(),
        passportConfigs = passportConfigs,
        contentMap = contentMapDeferred.await(),
        fontMap = fontMapDeferred.await()
    )
}

suspend fun loadCombinedPassport(
    singleDocumentResource: SinglePassport
): CombinedPassport = coroutineScope {
    val indexDeferred = async { loadResourceContent(Template.PASSPORT_INDEX_ITEM) }
    val articleDeferred = async { loadResourceContent(Template.PASSPORT_ARTICLE_ITEM) }
    val htmlDeferred = async { loadResourceContent(Template.PASSPORT_COMBINED) }

    singleDocumentResource.toCombined(
        indexTemplate = indexDeferred.await(),
        articleTemplate = articleDeferred.await(),
        htmlTemplate = htmlDeferred.await()
    )
}
