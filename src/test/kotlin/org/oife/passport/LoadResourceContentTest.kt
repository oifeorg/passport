package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain

class LoadResourceContentTest :
    ShouldSpec({

        should("load markdown resource as string") {
            val content = loadResourceContent("/data/$TEST_MARKDOWN_FILE")
            content shouldContain "# Hello test"
        }

        should("throw if resource does not exist") {
            shouldThrow<IllegalStateException> {
                loadResourceContent("/data/nonexistent.md")
            }
        }
    })
