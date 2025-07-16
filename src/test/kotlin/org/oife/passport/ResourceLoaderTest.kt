package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.InputStream

class ResourceLoaderTest : StringSpec({

    "loadResourceText returns file content from test resources" {
        val text = loadResourceText("/data/$testMarkdownFile")
        text.trim().startsWith("#") shouldBe true // assuming it's a markdown heading
    }

    "loadResourceText throws error on missing file" {
        val exception = kotlin.runCatching {
            loadResourceText("/nonexistent/file.txt")
        }.exceptionOrNull()

        exception shouldNotBe null
        exception?.message shouldBe "Resource not found or unreadable: /nonexistent/file.txt"
    }

    "buildFontSupplierMap includes test font file" {
        val map = buildFontSupplierMap()
        val keys = map.keys

        // Adjust this to match your actual test font file name
        val expectedFontFile = defaultFont.fileName

        keys.contains(expectedFontFile) shouldBe true

        val supplier = map[expectedFontFile]
        supplier shouldNotBe null

        val inputStream: InputStream? = supplier?.supply()
        inputStream shouldNotBe null
        inputStream?.readBytes()?.isNotEmpty() shouldBe true
    }
})