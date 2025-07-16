package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FromMarkdownToHtmlTest : StringSpec({

    "converts markdown header to h1" {
        "# Hello".fromMarkdownToHtml()
            .trim() shouldBe "<h1>Hello</h1>"
    }

    "converts bold markdown to strong tag" {
        "**bold**".fromMarkdownToHtml()
            .trim() shouldBe "<p><strong>bold</strong></p>"
    }

    "converts unordered list to HTML list" {
        with("- One\n- Two".fromMarkdownToHtml()) {
            contains("<ul>") shouldBe true
            contains("<li>One</li>") shouldBe true
            contains("<li>Two</li>") shouldBe true
        }
    }
})