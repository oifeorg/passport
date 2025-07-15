package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

private val logger = LoggerFactory.getLogger("ResourceLoader")

const val OUTPUT_DIR_NAME = "generated"
val outputDir = File(OUTPUT_DIR_NAME).apply { mkdirs() }


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

val fontSupplierMap = buildFontSupplierMap()