package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly

class FontMapTest : StringSpec({

    "should load fonts into supplier map" {
        val fonts = listOf(
            SinglePassportMeta("xx.md", "xx", "Dummy"),
            SinglePassportMeta("yy.md", "yy", "Dummy")
        )

        with(fontMap(fonts)) {
            keys shouldContainExactly setOf("NotoSans-Regular.ttf")
        }
    }
})