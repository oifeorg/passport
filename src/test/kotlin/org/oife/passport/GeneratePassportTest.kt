package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.file.shouldExist
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlin.io.path.createTempFile

class GeneratePassportTest : StringSpec({

    val fakeOutputPath = createTempFile("rendered", ".pdf").apply {
        toFile().writeBytes(dummyPdfContent())
        toFile().deleteOnExit()
    }

    beforeTest {
        mockkStatic(::renderToPdf)
        coEvery { renderToPdf(any(), any()) } returns fakeOutputPath
    }

    afterTest {
        unmockkAll()
    }

    "should load covers from resources and produce final combined file" {
        val combinedResource = CombinedDocumentResource(
            passportConfigs = emptyList(),
            articleTemplate = "<hello></hello>",
            contentMap = emptyMap(),
            fontMap = emptyMap(),
            indexTemplate = "<hello></hello>",
            htmlTemplate = "<hello></hello>",
            version = ""
        )

        generateCombinedPassport(combinedResource).toFile().apply {
            shouldExist()
            length() shouldBeGreaterThan 500 // small real PDFs can be very small
        }
    }

    "should generate a PDF for each passport config" {
        val documentResource = DocumentResource(
            htmlTemplate = "<hello></hello>",
            passportConfigs = listOf(mockk<SinglePassportMeta>(relaxed = true)),
            contentMap = emptyMap(),
            fontMap = emptyMap(),
            version = ""
        )

        generateSinglePassports(documentResource)
    }
})
