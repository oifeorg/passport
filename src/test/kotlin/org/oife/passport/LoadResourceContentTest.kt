package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain

class LoadResourceContentTest : StringSpec({

    "should load markdown resource as string" {
        val content = loadResourceContent("/data/$testMarkdownFile")
        content shouldContain "# Hello test"
    }

    "should throw if resource does not exist" {
        shouldThrow<IllegalStateException> {
            loadResourceContent("/data/nonexistent.md")
        }
    }
})