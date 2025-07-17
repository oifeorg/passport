package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan

class LoadResourceBytesTest : StringSpec({

    "should load font resource as bytes" {
        val bytes = loadResourceBytes("/fonts/NotoSans-Regular.ttf")
        bytes.size shouldBeGreaterThan 1000
    }

    "should throw if font does not exist" {
        shouldThrow<IllegalStateException> {
            loadResourceBytes("/fonts/does-not-exist.ttf")
        }
    }
})