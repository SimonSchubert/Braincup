package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.licenses.Attribution
import com.inspiredandroid.braincup.licenses.AttributionCategory
import com.inspiredandroid.braincup.licenses.AttributionSection
import com.inspiredandroid.braincup.licenses.SoftwareLicense

/**
 * Play Services ships only in this flavor (see the `playStoreImplementation` dependencies in
 * androidApp's build script), so only this build credits it.
 */
val storeAttributions: AttributionSection? = AttributionSection(
    AttributionCategory.LIBRARIES,
    listOf(
        Attribution(
            name = "Play Games Services v2",
            url = "https://developers.google.com/games/services",
            holder = "Google LLC",
            license = SoftwareLicense.GOOGLE_PLAY_SERVICES,
        ),
        Attribution(
            name = "Play In-App Review",
            url = "https://developer.android.com/guide/playcore/in-app-review",
            holder = "Google LLC",
            license = SoftwareLicense.GOOGLE_PLAY_SERVICES,
        ),
    ),
)
