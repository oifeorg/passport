package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class PassportMetaDataTest : StringSpec({

    val meta = PassportMetaData(
        markdownFilename = testMarkdownFile,
        languageCode = "en",
        documentTitle = "OIFE Passport"
    )

    "returns correct pdf file name" {
        meta.pdfFileName shouldBe "test.pdf"
    }

    "uses correct direction based on rtl flag" {
        PassportMetaData("a.md", "ar", "Arabic", font = FontMeta(rtl = true)).direction shouldBe "rtl"
        PassportMetaData("b.md", "en", "English", font = FontMeta(rtl = false)).direction shouldBe "ltr"
    }

    "generates correct html replacements" {
        meta.toHtmlReplacements().apply {
            this["lang"] shouldBe "en"
            this["title"] shouldBe "OIFE Passport"
            this["font-family"] shouldBe "NotoSans"
            this["rtl"] shouldBe "ltr"
            this["body"] shouldBe meta.markdownContent.fromMarkdownToHtml()
        }
    }

    "renderToPdf delegates correctly" {
        val fakeTemplate = "<html><body>{{body}}</body></html>"
        val expectedFile = File("$OUTPUT_DIR_NAME/${meta.pdfFileName}")
        val result = meta.renderToPdf(fakeTemplate)
        result.getOrThrow() shouldBe expectedFile
    }
})