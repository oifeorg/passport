package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ResourceLoaderTest : StringSpec({

    "loadResourceText returns file content from test resources" {
        loadResourceText("/data/$testMarkdownFile")
            .trim()
            .startsWith("# Hello") shouldBe true // assuming markdown heading
    }

    "loadResourceText throws error on missing file" {
        runCatching {
            loadResourceText("/nonexistent/file.txt")
        }.exceptionOrNull().let {
            it shouldNotBe null
            it?.message shouldBe "Resource not found or unreadable: /nonexistent/file.txt"
        }
    }

//    "buildFontSupplierMap includes test font file" {
//        fontMap().apply {
//            containsKey(defaultFont.fileName) shouldBe true
//
//            get(defaultFont.fileName).also { supplier ->
//                supplier shouldNotBe null
//                supplier?.supply().also { stream ->
//                    stream shouldNotBe null
//                    stream?.readBytes()?.isNotEmpty() shouldBe true
//                }
//            }
//        }
//    }
})