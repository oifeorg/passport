package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.string.shouldContain


class PassportContentMapTest : StringSpec({

    "should load markdown content into map" {
        val passports = listOf(
            PassportMeta("en-english.md", "en", "English"),
            PassportMeta("da-danish.md", "da", "Danish")
        )

        with(loadPassportContents(passports)) {
            keys shouldContainExactly setOf("en-english.md", "da-danish.md")
            getValue("en-english.md").length shouldBeGreaterThan 10
        }
    }

    "should fail if markdown file is missing" {
        val passports = listOf(
            PassportMeta("missing-file.md", "xx", "Missing")
        )

        shouldThrow<IllegalStateException> {
            loadPassportContents(passports)
        }.message shouldContain "missing-file.md"
    }
})