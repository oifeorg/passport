package org.oife.passport

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ToFilledHtmlTest :
    ShouldSpec({

        should("replace a single placeholder") {
            "<p>{{name}}</p>"
                .replacePlaceholders(mapOf("name" to "Andre"))
                .shouldBe("<p>Andre</p>")
        }

        should("replace multiple placeholders") {
            "<p>{{a}}, {{b}}</p>"
                .replacePlaceholders(mapOf("a" to "1", "b" to "2"))
                .shouldBe("<p>1, 2</p>")
        }

        should("leave unknown placeholders untouched") {
            "<p>{{greet}}, {{name}}</p>"
                .replacePlaceholders(mapOf("greet" to "Hi"))
                .shouldBe("<p>Hi, {{name}}</p>")
        }

        should("do nothing if no placeholders match") {
            "<p>Hello</p>"
                .replacePlaceholders(mapOf("unused" to "value"))
                .shouldBe("<p>Hello</p>")
        }
    })
