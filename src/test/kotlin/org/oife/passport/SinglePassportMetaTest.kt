package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SinglePassportMetaTest : StringSpec({

    val meta = SinglePassportMeta(
        markdownFilename = testMarkdownFile,
        languageCode = "en",
        title = "OIFE Passport",
        localizedTitle = "My test"
    )

    "returns correct pdf file name" {
        meta.pdfFileName shouldBe "test.pdf"
    }

    "uses correct direction based on rtl flag" {
        SinglePassportMeta("a.md", "ar", "Arabic", font = FontType.AR).direction shouldBe "rtl"
        SinglePassportMeta("b.md", "en", "English", font = FontType.DEFAULT).direction shouldBe "ltr"
    }

    "generates correct html replacements" {
        meta.toHtmlReplacements().apply {
            this[Placeholder.LANG] shouldBe "en"
            this[Placeholder.HEADER_TITLE] shouldBe "My test - OIFE Passport"
            this[Placeholder.FONT_FAMILY] shouldBe "Noto Sans"
            this[Placeholder.DIRECTION] shouldBe "ltr"
        }
    }
})