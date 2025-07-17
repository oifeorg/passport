package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import kotlin.io.path.exists

class GeneratePassportsTest : StringSpec({

    "should generate PDF files for given passports" {
        val version = "test-version"
        val htmlTemplate = "<html><body>{{body}}</body></html>"

        val metaConfigs = listOf(
            SinglePassportMeta("test.md", "en", "English", defaultFont),
        )

        runBlocking {
            generatePassports(version, htmlTemplate, metaConfigs)
        }

        val expectedFiles = metaConfigs.map { Paths.get(OUTPUT_DIR_NAME, it.pdfFileName) }
        expectedFiles.forEach {
            it.exists()
        }
    }
})