package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SinglePassportMetaTest : StringSpec({

    val meta = SinglePassportMeta(
        markdownFilename = testMarkdownFile,
        languageCode = "en",
        title = "OIFE Passport"
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
            this["lang"] shouldBe "en"
            this["title"] shouldBe "OIFE Passport"
            this["font-family"] shouldBe "Noto Sans"
            this["rtl"] shouldBe "ltr"
        }
    }
})