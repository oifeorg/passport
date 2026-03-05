package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class FromMarkdownToHtmlTest :
    StringSpec({

        "converts markdown header to h1" {
            "# Hello"
                .renderHtml()
                .trim() shouldBe "<h1>Hello</h1>"
        }

        "converts bold markdown to strong tag" {
            "**bold**"
                .renderHtml()
                .trim() shouldBe "<p><strong>bold</strong></p>"
        }

        "converts unordered list to HTML list" {
            with("- One\n- Two".renderHtml()) {
                shouldContain("<ul>")
                shouldContain("<li>One</li>")
                shouldContain("<li>Two</li>")
            }
        }
    })
