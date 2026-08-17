package com.inspiredandroid.braincup.licenses

/**
 * Android, desktop and wasm pull Coil and Ktor in through the `nonIosMain` source set for avatar
 * loading and leaderboard requests.
 */
actual val platformAttributionSections: List<AttributionSection> = listOf(
    AttributionSection(
        AttributionCategory.LIBRARIES,
        listOf(
            Attribution(
                name = "Ktor",
                url = "https://ktor.io",
                holder = "JetBrains s.r.o.",
                license = SoftwareLicense.APACHE_2_0,
            ),
            Attribution(
                name = "Coil",
                url = "https://github.com/coil-kt/coil",
                holder = "Coil Contributors",
                license = SoftwareLicense.APACHE_2_0,
            ),
        ),
    ),
)
