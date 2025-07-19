package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FontMappingTest : StringSpec({

    "should return correct FontMeta for each FontType" {
        FontType.DEFAULT.toFontMeta() shouldBe FontMeta()

        FontType.AR.toFontMeta() shouldBe FontMeta(
            fileName = "NotoNaskhArabic-Regular.ttf",
            familyName = "Noto Naskh Arabic",
            rtl = true
        )

        FontType.GU.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansGujarati-Regular.ttf",
            familyName = "Noto Sans Gujarati"
        )

        FontType.KA.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansGeorgian-Regular.ttf",
            familyName = "Noto Sans Georgian"
        )

        FontType.JA.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansJP-Regular.ttf",
            familyName = "Noto Sans JP"
        )

        FontType.ZH.toFontMeta() shouldBe FontMeta(
            fileName = "NotoSansSC-Regular.ttf",
            familyName = "Noto Sans SC"
        )
    }

    "should mark only ARABIC as RTL" {
        FontType.AR.toFontMeta().rtl shouldBe true

        listOf(
            FontType.DEFAULT,
            FontType.GU,
            FontType.KA,
            FontType.JA,
            FontType.ZH
        ).forEach {
            it.toFontMeta().rtl shouldBe false
        }
    }
})