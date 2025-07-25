package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import kotlin.io.path.createTempFile
import kotlin.io.path.extension
import kotlin.io.path.fileSize

class GeneratePassportTest : StringSpec({

    "should load covers from resources and produce final combined file" {
        val combinedPassport = CombinedPassport(
            passportConfigs = emptyList(),
            articleTemplate = "<hello></hello>",
            contentMap = emptyMap(),
            fontMap = emptyMap(),
            indexTemplate = "<hello></hello>",
            htmlTemplate = "<hello></hello>",
            version = ""
        )

        combinedPassport.generate().toFile().apply {
            shouldExist()
            length() shouldBeGreaterThan 500 // small real PDFs can be very small
        }
    }

    "should render a valid PDF file for a document" {
        val defaultFont = FontMeta()
        val meta = PassportMeta(
            markdownFilename = "test.md",
            languageCode = "en",
            title = "Test Document"
        )
        val singlePassport = SinglePassport(
            "<html><body>{{passport-content}}</body></html>", emptyList(),
            contentMap = mapOf("test.md" to "# Hello"),
            fontMap = mapOf(defaultFont.familyName to loadTestFont(defaultFont)),
            version = "v1.0.0",
        )

        val outputPath = createTempFile("test-passport-", ".pdf").apply { toFile().deleteOnExit() }

        with(singlePassport.toPdfInput(meta).renderToPdf(outputPath)) {
            shouldExist()
            fileSize() shouldBeGreaterThan 100L
            extension shouldBe "pdf"
        }
    }
})
