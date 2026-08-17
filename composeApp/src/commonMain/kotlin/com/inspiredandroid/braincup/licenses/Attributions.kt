package com.inspiredandroid.braincup.licenses

/**
 * Open source license texts ship with the app instead of being linked: Apache-2.0 section 4,
 * OFL-1.1 condition 2 and the MIT permission notice all require the full license to travel with
 * the binary, and the desktop and offline mobile builds cannot fall back to fetching it.
 * Proprietary terms are linked instead via [infoUrl], since they are hosted terms that change
 * without a version we could pin.
 */
enum class SoftwareLicense(
    val label: String,
    val textPath: String? = null,
    val infoUrl: String? = null,
) {
    APACHE_2_0("Apache-2.0", textPath = "files/licenses/apache-2.0.txt"),

    // MIT carries its copyright line inside the notice, so the text is per-project rather than
    // shared the way the Apache and CC0 files are.
    MIT_FLAG_ICONS("MIT", textPath = "files/licenses/mit-flag-icons.txt"),
    OFL_1_1("OFL-1.1", textPath = "files/licenses/ofl-1.1.txt"),
    CC0_1_0("CC0-1.0", textPath = "files/licenses/cc0-1.0.txt"),
    GOOGLE_PLAY_SERVICES("Google APIs Terms of Service", infoUrl = "https://developers.google.com/terms"),
}

enum class AttributionCategory {
    LIBRARIES,
    FONTS,
    SOUND,
    ARTWORK,
    WORD_LISTS,
}

data class Attribution(
    val name: String,
    val url: String,
    val holder: String? = null,
    val license: SoftwareLicense? = null,
)

data class AttributionSection(
    val category: AttributionCategory,
    val entries: List<Attribution>,
)

const val BRAINCUP_SOURCE_URL = "https://github.com/SimonSchubert/Braincup"

/**
 * Everything every build ships, regardless of target or distribution channel. Target-specific
 * libraries live in [platformAttributionSections] and store-specific ones in [AttributionRegistry],
 * so a build never credits itself for code it does not actually contain.
 */
private val commonAttributionSections: List<AttributionSection> = listOf(
    AttributionSection(
        AttributionCategory.LIBRARIES,
        listOf(
            Attribution(
                name = "Kotlin",
                url = "https://kotlinlang.org",
                holder = "JetBrains s.r.o. and Kotlin Programming Language contributors",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "kotlinx.coroutines",
                url = "https://github.com/Kotlin/kotlinx.coroutines",
                holder = "JetBrains s.r.o.",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "kotlinx.serialization",
                url = "https://github.com/Kotlin/kotlinx.serialization",
                holder = "JetBrains s.r.o.",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "kotlinx-datetime",
                url = "https://github.com/Kotlin/kotlinx-datetime",
                holder = "JetBrains s.r.o.",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "kotlinx.collections.immutable",
                url = "https://github.com/Kotlin/kotlinx.collections.immutable",
                holder = "JetBrains s.r.o.",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "Compose Multiplatform",
                url = "https://github.com/JetBrains/compose-multiplatform",
                holder = "JetBrains s.r.o. and respective authors",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "Jetpack Compose and AndroidX",
                url = "https://developer.android.com/jetpack/androidx",
                holder = "The Android Open Source Project",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "Multiplatform Settings",
                url = "https://github.com/russhwolf/multiplatform-settings",
                holder = "Russell Wolf",
                license = SoftwareLicense.APACHE_2_0,
            ),
        ),
    ),
    AttributionSection(
        AttributionCategory.FONTS,
        listOf(
            Attribution(
                name = "Rubik",
                url = "https://github.com/googlefonts/rubik",
                holder = "Copyright 2015 The Rubik Project Authors",
                license = SoftwareLicense.OFL_1_1,
            ),
            Attribution(
                name = "Bungee",
                url = "https://github.com/djrrb/Bungee",
                holder = "Copyright 2023 The Bungee Project Authors",
                license = SoftwareLicense.OFL_1_1,
            ),
            Attribution(
                name = "Tektur",
                url = "https://github.com/hyvyys/Tektur",
                holder = "Copyright 2023 The Tektur Project Authors",
                license = SoftwareLicense.OFL_1_1,
            ),
        ),
    ),
    AttributionSection(
        AttributionCategory.SOUND,
        listOf(
            Attribution(
                name = "Drum and Piano Loop",
                url = "https://freesound.org/people/EEE3333E/sounds/841825/",
                holder = "EEE3333E",
                license = SoftwareLicense.CC0_1_0,
            ),
            Attribution(
                name = "Parallel Universe",
                url = "https://freesound.org/people/andrewkn/sounds/404458/",
                holder = "Andrewkn",
                license = SoftwareLicense.CC0_1_0,
            ),
        ),
    ),
    AttributionSection(
        AttributionCategory.ARTWORK,
        listOf(
            Attribution(
                name = "Sea Life icons",
                url = "https://www.svgrepo.com/collection/sea-life-2/",
                holder = "SVG Repo",
                license = SoftwareLicense.CC0_1_0,
            ),
            // The flags are redrawn from the 4x3 set: transforms and clips are baked into the
            // path data so they load as Android vector drawables on every target. Geometry and
            // colours are upstream's, so this row is a real MIT obligation, not just provenance.
            Attribution(
                name = "Country flags",
                url = "https://github.com/lipis/flag-icons",
                holder = "Copyright (c) 2013 Panayiotis Lipiridis",
                license = SoftwareLicense.MIT_FLAG_ICONS,
            ),
            // The brain mascot illustrations are in-house, so they need no row of their own: the
            // Apache-2.0 notice at the foot of the screen already covers everything we drew.
        ),
    ),
    AttributionSection(
        AttributionCategory.WORD_LISTS,
        listOf(
            // The four Wordle languages each mix several upstream dictionaries and frequency
            // lists under different terms, so the per-language provenance lives in NOTICE.md
            // rather than being flattened into one license here.
            Attribution(
                name = "Wordle word lists",
                url = "$BRAINCUP_SOURCE_URL/blob/master/composeApp/src/commonMain/composeResources/files/wordle/NOTICE.md",
            ),
        ),
    ),
)

/**
 * Libraries only some targets link against, so each build credits exactly what it bundles. iOS
 * ships neither Coil nor Ktor (see the `nonIosMain` dependencies in composeApp's build script).
 */
expect val platformAttributionSections: List<AttributionSection>

/**
 * Attributions contributed by the host application for dependencies this module cannot see.
 *
 * Android product flavors are invisible to Kotlin Multiplatform source sets, which is why
 * [platformAttributionSections] alone cannot express them: `playStore` bundles Play Services while
 * `foss` does not, and both compile against the same `androidMain`. The flavor registers its own
 * entries here, keeping the F-Droid, Flatpak and AUR builds from claiming libraries they never ship.
 *
 * Register before the licenses screen is first shown. Registering the same category twice replaces
 * the previous entry rather than appending, so an activity recreation cannot duplicate rows.
 */
object AttributionRegistry {
    private val sections = mutableMapOf<AttributionCategory, AttributionSection>()

    val registered: List<AttributionSection>
        get() = sections.values.toList()

    fun register(section: AttributionSection) {
        sections[section.category] = section
    }
}

/**
 * Every attribution this build actually ships, ordered by [AttributionCategory] declaration order
 * so the screen reads the same however the contributing pieces were assembled.
 */
val attributionSections: List<AttributionSection>
    get() = mergeAttributions(
        commonAttributionSections + platformAttributionSections + AttributionRegistry.registered,
    )

/**
 * Folds contributions from every source into one section per category. Entries are deduplicated by
 * name so a library credited from two sources appears once, which also keeps the screen's list keys
 * unique.
 */
internal fun mergeAttributions(sections: List<AttributionSection>): List<AttributionSection> = sections
    .groupBy { it.category }
    .map { (category, group) ->
        AttributionSection(category, group.flatMap { it.entries }.distinctBy { it.name })
    }
    .sortedBy { it.category.ordinal }
