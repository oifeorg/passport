package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.shouldContain

class FontMapTest : StringSpec({

    "should load fonts into supplier map" {
        val fonts = listOf(
            SinglePassportMeta("xx.md", "xx", "Dummy", FontMeta("NotoSans-Regular.ttf", "NotoSans")),
            SinglePassportMeta("yy.md", "yy", "Dummy", FontMeta("NotoSans-Regular.ttf", "NotoSans"))
        )

        with(fontMap(fonts)) {
            keys shouldContainExactly setOf("NotoSans-Regular.ttf")
        }
    }

    "should fail if font file does not exist" {
        val fonts = listOf(
            SinglePassportMeta("xx.md", "xx", "Missing", FontMeta("missing-font.ttf", "UnknownFont"))
        )

        shouldThrow<IllegalStateException> {
            fontMap(fonts)
        }.message shouldContain "missing-font.ttf"
    }
})