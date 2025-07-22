package org.oife.passport

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.file.shouldExist
import kotlin.io.path.createTempFile

class MergePdfFilesTest : StringSpec({

    "should merge given PDF files into a single output path" {
        val part1 = createTempFile().apply {
            toFile().writeBytes(dummyPdfContent())
            toFile().deleteOnExit()
        }
        val part2 = createTempFile().apply {
            toFile().writeBytes(dummyPdfContent())
            toFile().deleteOnExit()
        }
        val output = createTempFile().apply { toFile().deleteOnExit() }

        mergePdfFilesToFile(listOf(part1, part2), output).toFile().apply {
            shouldExist()
            length() shouldBeGreaterThan 580
        }
    }
})
