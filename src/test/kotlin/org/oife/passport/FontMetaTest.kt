package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class FontMetaTest : StringSpec({

    val defaultFont = FontMeta()

    "should return 'default' for default FontMeta in toCssClass" {
        defaultFont.toCssClass("ar") shouldBe "default"
    }

    "should return language code in toCssClass for non-default FontMeta" {
        with(FontMeta(fileName = "NotoNaskhArabic-Regular.ttf", familyName = "Noto Naskh Arabic", direction = "rtl")) {
            toCssClass("ar") shouldBe "ar"
        }
    }

    "should return 'left' when direction is ltr in toTextAlign" {
        defaultFont.toTextAlign() shouldBe "left"
    }

    "should return 'right' when direction is rtl in toTextAlign" {
        FontMeta(direction = "rtl").toTextAlign() shouldBe "right"
    }

    "should serialize and deserialize FontMeta" {
        val font = FontMeta(
            familyName = "Noto Sans", fileName = "NotoSans-Regular.ttf", direction = "ltr"
        )

        with(Json.encodeToString(font)) {
            Json.decodeFromString<FontMeta>(this).shouldBe(font)
        }
    }
})