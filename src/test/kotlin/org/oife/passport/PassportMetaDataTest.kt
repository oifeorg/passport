package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class PassportMetaDataTest : StringSpec({

    "returns correct pdf file name" {
        val meta = PassportMetaData(
            markdownFilename = "passport-en.md",
            languageCode = "en",
            documentTitle = "OIFE Passport"
        )
        meta.pdfFileName shouldBe "passport-en.pdf"
    }

    "uses correct direction based on rtl flag" {
        PassportMetaData("a.md", "ar", "Arabic", font = FontMeta(rtl = true)).direction shouldBe "rtl"
        PassportMetaData("b.md", "en", "English", font = FontMeta(rtl = false)).direction shouldBe "ltr"
    }

    "generates correct html replacements" {
        val fakeMeta = PassportMetaData(
            markdownFilename = "test.md",
            languageCode = "en",
            documentTitle = "Test Title",
            font = FontMeta(familyName = "FakeFont", rtl = false)
        )

        val replacements = fakeMeta.toHtmlReplacements()

        replacements["lang"] shouldBe "en"
        replacements["title"] shouldBe "Test Title"
        replacements["font-family"] shouldBe "FakeFont"
        replacements["rtl"] shouldBe "ltr"
        replacements["body"] shouldBe fakeMeta.markdownContent.fromMarkdownToHtml()
    }

    "renderToPdf delegates correctly" {
        val fakeTemplate = "<html><body>{{body}}</body></html>"
        val fakeMeta = PassportMetaData(
            markdownFilename = "test.md",
            languageCode = "en",
            documentTitle = "Test",
        )
        val expectedFile = File("$OUTPUT_DIR_NAME/${fakeMeta.pdfFileName}")
        val result = fakeMeta.renderToPdf(fakeTemplate)
        result.getOrThrow() shouldBe expectedFile
    }
})