package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ToFilledHtmlTest : StringSpec({

    "replaces a single placeholder" {
        "<p>{{name}}</p>".replacePlaceholders(mapOf("name" to "Andre"))
            .shouldBe("<p>Andre</p>")
    }

    "replaces multiple placeholders" {
        "<p>{{a}}, {{b}}</p>".replacePlaceholders(mapOf("a" to "1", "b" to "2"))
            .shouldBe("<p>1, 2</p>")
    }

    "leaves unknown placeholders untouched" {
        "<p>{{greet}}, {{name}}</p>".replacePlaceholders(mapOf("greet" to "Hi"))
            .shouldBe("<p>Hi, {{name}}</p>")
    }

    "does nothing if no placeholders match" {
        "<p>Hello</p>".replacePlaceholders(mapOf("unused" to "value"))
            .shouldBe("<p>Hello</p>")
    }
})