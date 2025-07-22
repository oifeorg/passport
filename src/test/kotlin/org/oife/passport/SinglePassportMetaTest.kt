package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SinglePassportMetaTest : StringSpec({

    val meta = PassportMeta(
        markdownFilename = testMarkdownFile,
        languageCode = "en",
        title = "OIFE Passport",
        localizedTitle = "My test"
    )

    "returns correct pdf file name" {
        meta.pdfFileName() shouldBe "test.pdf"
    }

    "uses correct direction based on rtl flag" {
        PassportMeta("a.md", "ar", "Arabic", font = FontMeta("SomethingElse", "Noto Something", direction = "rtl")).direction() shouldBe "rtl"
        PassportMeta("b.md", "en", "English").direction() shouldBe "ltr"
    }

    "generates correct html replacements" {
        meta.toReplacements().apply {
            this[Placeholder.LANG] shouldBe "en"
            this[Placeholder.HEADER_TITLE] shouldBe "My test - OIFE Passport"
            this[Placeholder.FONT_FAMILY] shouldBe "Noto Sans"
            this[Placeholder.DIRECTION] shouldBe "ltr"
        }
    }
})