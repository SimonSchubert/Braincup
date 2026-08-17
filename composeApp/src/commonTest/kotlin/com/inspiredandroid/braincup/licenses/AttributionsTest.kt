package com.inspiredandroid.braincup.licenses

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttributionsTest {

    private fun section(category: AttributionCategory, vararg names: String) = AttributionSection(
        category,
        names.map { Attribution(name = it, url = "https://example.invalid/$it") },
    )

    @Test
    fun mergeAttributions_foldsCategoriesIntoOneSection() {
        val merged = mergeAttributions(
            listOf(
                section(AttributionCategory.LIBRARIES, "Kotlin"),
                section(AttributionCategory.LIBRARIES, "Ktor", "Coil"),
            ),
        )

        assertEquals(1, merged.size)
        assertEquals(listOf("Kotlin", "Ktor", "Coil"), merged.single().entries.map { it.name })
    }

    @Test
    fun mergeAttributions_dropsDuplicateNames() {
        val merged = mergeAttributions(
            listOf(
                section(AttributionCategory.LIBRARIES, "Ktor"),
                section(AttributionCategory.LIBRARIES, "Ktor"),
            ),
        )

        assertEquals(listOf("Ktor"), merged.single().entries.map { it.name })
    }

    @Test
    fun mergeAttributions_ordersByCategoryDeclarationOrder() {
        val merged = mergeAttributions(
            listOf(
                section(AttributionCategory.WORD_LISTS, "Wordle word lists"),
                section(AttributionCategory.LIBRARIES, "Kotlin"),
                section(AttributionCategory.FONTS, "Rubik"),
            ),
        )

        assertEquals(
            listOf(
                AttributionCategory.LIBRARIES,
                AttributionCategory.FONTS,
                AttributionCategory.WORD_LISTS,
            ),
            merged.map { it.category },
        )
    }

    @Test
    fun attributionSections_creditNoStoreLibrariesUntilTheHostRegistersThem() {
        // Desktop, wasm and the FOSS Android flavor never register store attributions, so nothing
        // should claim Play Services on their behalf.
        val names = attributionSections.flatMap { it.entries }.map { it.name }

        assertTrue(names.none { it.startsWith("Play ") }, "unexpected store attribution in $names")
    }

    @Test
    fun attributionSections_everyBundledLicenseTextIsReachable() {
        val paths = attributionSections
            .flatMap { it.entries }
            .mapNotNull { it.license?.textPath }
            .distinct()

        assertEquals(
            setOf(
                "files/licenses/apache-2.0.txt",
                "files/licenses/mit-flag-icons.txt",
                "files/licenses/ofl-1.1.txt",
                "files/licenses/cc0-1.0.txt",
            ),
            paths.toSet(),
        )
    }
}
