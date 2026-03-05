package org.oife.passport

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class PassportMetaTest :
    ShouldSpec({

        val meta =
            PassportMeta(
                markdownFilename = TEST_MARKDOWN_FILE,
                languageCode = "en",
                title = "OIFE Passport",
                localizedTitle = "My test",
            )

        should("return correct pdf file name") {
            meta.fileName shouldBe "test.pdf"
        }

        should("return rtl direction for rtl font") {
            PassportMeta(
                "a.md",
                "ar",
                "Arabic",
                font = FontMeta("SomethingElse", "Noto Something", direction = "rtl"),
            ).direction shouldBe
                "rtl"
        }

        should("return ltr direction by default") {
            PassportMeta("b.md", "en", "English").direction shouldBe "ltr"
        }
    })
