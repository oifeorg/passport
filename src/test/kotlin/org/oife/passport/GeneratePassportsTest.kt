package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.longs.shouldBeGreaterThan
import kotlinx.coroutines.runBlocking
import java.io.File

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

        val expectedFiles = metaConfigs.map { File(outputDir, it.pdfFileName) }
        expectedFiles.forEach {
            it.shouldExist()
            it.length() shouldBeGreaterThan 100L
        }
    }
})