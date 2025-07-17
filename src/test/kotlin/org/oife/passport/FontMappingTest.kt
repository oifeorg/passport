package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FontMappingTest : StringSpec({

    "should return correct FontMeta for each FontType" {
        FontType.DEFAULT.toFontMeta() shouldBe FontMeta()

        FontType.ARABIC.toFontMeta() shouldBe FontMeta(
            fileName = "NotoNaskhArabic-Regular.ttf",
            familyName = "Noto Naskh Arabic",
            rtl = true
        )

        FontType.INDIAN.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansGujarati-Regular.ttf",
            familyName = "Noto Sans Gujarati"
        )

        FontType.GEORGIAN.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansGeorgian-Regular.ttf",
            familyName = "Noto Sans Georgian"
        )

        FontType.JAPANESE.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansJP-Regular.ttf",
            familyName = "Noto Sans JP"
        )

        FontType.CHINESE.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansSC-Regular.ttf",
            familyName = "Noto Sans SC"
        )
    }

    "should mark only ARABIC as RTL" {
        FontType.ARABIC.toFontMeta().rtl shouldBe true

        listOf(
            FontType.DEFAULT,
            FontType.INDIAN,
            FontType.GEORGIAN,
            FontType.JAPANESE,
            FontType.CHINESE
        ).forEach {
            it.toFontMeta().rtl shouldBe false
        }
    }
})