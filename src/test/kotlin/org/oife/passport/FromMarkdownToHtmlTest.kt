package org.oife.passport

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class FromMarkdownToHtmlTest :
    ShouldSpec({

        should("convert markdown header to h1") {
            "# Hello"
                .renderHtml()
                .trim() shouldBe "<h1>Hello</h1>"
        }

        should("convert bold markdown to strong tag") {
            "**bold**"
                .renderHtml()
                .trim() shouldBe "<p><strong>bold</strong></p>"
        }

        should("unordered list to HTML list") {
            with("- One\n- Two".renderHtml()) {
                shouldContain("<ul>")
                shouldContain("<li>One</li>")
                shouldContain("<li>Two</li>")
            }
        }
    })
