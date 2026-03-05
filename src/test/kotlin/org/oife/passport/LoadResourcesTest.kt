package org.oife.passport

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain

class LoadResourcesTest :
    ShouldSpec({

        should("load resources for single passports without failing") {
            loadSinglePassport("v1.0.0")
        }

        should("throw exception because of unknown template") {
            shouldThrow<IllegalStateException> {
                loadSinglePassport("v1.0.0", "unknown")
            }.message shouldContain "unknown"
        }

        should("load resources for combined passports without failing") {
            loadSinglePassport("v.1.0.0").toCombinedPassport()
        }
    })
