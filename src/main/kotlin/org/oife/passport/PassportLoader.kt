package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

private val logger = LoggerFactory.getLogger("PassportLoader")

data class SinglePassport(
    val htmlTemplate: String,
    val passportConfigs: List<PassportMeta>,
    val contentMap: Map<String, String>,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val version: String,
)

data class CombinedPassport(
    val indexTemplate: String,
    val articleTemplate: String,
    val htmlTemplate: String,
    val passportConfigs: List<PassportMeta>,
    val contentMap: Map<String, String>,
    val fontMap: Map<String, FSSupplier<InputStream>>,
    val version: String,
)

suspend fun loadFontSuppliers(passports: List<PassportMeta>): Map<String, FSSupplier<InputStream>> = coroutineScope {
    passports.map { it.font }.distinctBy { it.familyName }.map { font ->
        async {
            val path = "/fonts/${font.fileName}"
            val bytes = loadResourceBytes(path)
            font.familyName to FSSupplier<InputStream> { bytes.inputStream() }
        }
    }.awaitAll().toMap()
}

private val jsonInputFormat = Json { ignoreUnknownKeys = true }
suspend fun loadPassportConfigs(): List<PassportMeta> =
    jsonInputFormat.decodeFromString(loadResourceContent("/passport-config.json"))

suspend fun loadPassportContents(passports: List<PassportMeta>): Map<String, String> = coroutineScope {
    passports.map { metadata ->
        async { metadata.markdownFilename to loadResourceContent("/data/${metadata.markdownFilename}") }
    }.awaitAll().toMap()
}

private suspend fun <T> loadResource(path: String, reader: (InputStream) -> T): T = withContext(Dispatchers.IO) {
    object {}.javaClass.getResourceAsStream(path)?.use { stream ->
        logger.info(Messages.ResourceLoaded(path))
        reader(stream)
    } ?: throw IllegalStateException(Messages.ResourceNotFound(path))
}

suspend fun loadResourceContent(path: String): String =
    loadResource(path) { it.bufferedReader().use { reader -> reader.readText() } }

suspend fun loadResourceBytes(path: String): ByteArray = loadResource(path) { it.readBytes() }

suspend fun loadResourceTempFile(path: String): Path = loadResource(path) { inputStream ->
    val fileName = Path.of(path).fileName.toString()
    val suffix = if (fileName.contains('.')) fileName.substringAfterLast('.') else "tmp"
    val tempFile = Files.createTempFile("resource-", ".$suffix")
    tempFile.toFile().deleteOnExit()
    inputStream.use { it.copyTo(tempFile.toFile().outputStream()) }
    tempFile
}

suspend fun loadSinglePassport(version: String, htmlTemplatePath: String = Template.PASSPORT_SINGLE): SinglePassport =
    coroutineScope {
        val htmlTemplateDeferred = async { loadResourceContent(htmlTemplatePath) }
        val passportConfigs = loadPassportConfigs()
        val fontMapDeferred = async { loadFontSuppliers(passportConfigs) }
        val contentMapDeferred = async { loadPassportContents(passportConfigs) }
        SinglePassport(
            version = version,
            htmlTemplate = htmlTemplateDeferred.await(),
            passportConfigs = passportConfigs,
            contentMap = contentMapDeferred.await(),
            fontMap = fontMapDeferred.await()
        )
    }

suspend fun SinglePassport.toCombinedPassport(): CombinedPassport = coroutineScope {
    val indexDeferred = async { loadResourceContent(Template.PASSPORT_INDEX_ITEM) }
    val articleDeferred = async { loadResourceContent(Template.PASSPORT_ARTICLE_ITEM) }
    val htmlDeferred = async { loadResourceContent(Template.PASSPORT_COMBINED) }

    CombinedPassport(
        indexTemplate = indexDeferred.await(),
        articleTemplate = articleDeferred.await(),
        htmlTemplate = htmlDeferred.await(),
        passportConfigs = passportConfigs,
        contentMap = contentMap,
        fontMap = fontMap,
        version = version
    )
}
