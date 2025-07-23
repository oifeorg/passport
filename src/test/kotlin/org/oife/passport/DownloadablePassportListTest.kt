package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.JsonArray
import kotlin.io.path.createTempFile
import kotlin.io.path.fileSize

class DownloadablePassportListTest : StringSpec({

    "should create json array with all pdf filenames including combined" {

        val meta1 = mockk<PassportMeta> {
            every { languageCode } returns "de"
            every { markdownFilename } returns "de-german.md"
        }

        val meta2 = mockk<PassportMeta> {
            every { languageCode } returns "en"
            every { markdownFilename } returns "en-english.md"
        }

        val passport = mockk<CombinedPassport> {
            every { passportConfigs } returns listOf(meta1, meta2)
        }

        passport.loadDownloadablePassports() shouldBe JsonArray(
            listOf(
                Pdf.ALL_PASSPORT_COMBINED,
                meta1.pdfFileName(),
                meta2.pdfFileName()
            ).map { kotlinx.serialization.json.JsonPrimitive(it) }
        )
    }

    "should write passport-list.json to output dir" {
        val outputPath = createTempFile("passport-list", ".json").apply { toFile().deleteOnExit() }
        val meta = mockk<PassportMeta> {
            every { languageCode } returns "en"
            every { markdownFilename } returns "en-english.md"
        }
        val passport = mockk<CombinedPassport> {
            every { passportConfigs } returns listOf(meta)
        }

        passport.generateDownloadableList(outputPath).apply {
            shouldExist()
            fileSize() shouldBeGreaterThan 50L
            fileName.toString() shouldStartWith  "passport-list"
            fileName.toString() shouldEndWith "json"
        }
    }
})