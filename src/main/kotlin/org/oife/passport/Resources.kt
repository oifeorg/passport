package org.oife.passport

import com.openhtmltopdf.extend.FSSupplier
import org.slf4j.LoggerFactory
import java.io.InputStream

private val logger = LoggerFactory.getLogger("ResourceLoader")

fun getResourceStream(path: String): InputStream? =
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

fun fontSupplier(path: String): FSSupplier<InputStream> = FSSupplier {
    getResourceStream(path)
        ?: error("Font not found in resources: $path")
}