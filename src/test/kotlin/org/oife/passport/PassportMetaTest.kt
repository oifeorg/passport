package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PassportMetaTest :
    StringSpec({

        val meta =
            PassportMeta(
                markdownFilename = TEST_MARKDOWN_FILE,
                languageCode = "en",
                title = "OIFE Passport",
                localizedTitle = "My test",
            )

        "returns correct pdf file name" {
            meta.fileName shouldBe "test.pdf"
        }

        "returns rtl direction for rtl font" {
            PassportMeta(
                "a.md",
                "ar",
                "Arabic",
                font = FontMeta("SomethingElse", "Noto Something", direction = "rtl"),
            ).direction shouldBe
                "rtl"
        }

        "returns ltr direction by default" {
            PassportMeta("b.md", "en", "English").direction shouldBe "ltr"
        }
    })
