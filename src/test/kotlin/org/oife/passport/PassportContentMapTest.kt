package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.string.shouldContain


class PassportContentMapTest : StringSpec({

    "should load markdown content into map" {
        val passports = listOf(
            SinglePassportMeta("en-english.md", "en", "English", defaultFont),
            SinglePassportMeta("da-danish.md", "da", "Danish", defaultFont)
        )

        with(passportContentMap(passports)) {
            keys shouldContainExactly setOf("en-english.md", "da-danish.md")
            getValue("en-english.md").length shouldBeGreaterThan 10
        }
    }

    "should fail if markdown file is missing" {
        val passports = listOf(
            SinglePassportMeta("missing-file.md", "xx", "Missing", defaultFont)
        )

        shouldThrow<IllegalStateException> {
            passportContentMap(passports)
        }.message shouldContain "missing-file.md"
    }
})