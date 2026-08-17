package com.inspiredandroid.braincup.licenses

/**
 * iOS links neither Coil nor Ktor, and Game Center comes from the system GameKit framework rather
 * than a bundled dependency, so everything the iOS app ships is already in the common list.
 */
actual val platformAttributionSections: List<AttributionSection> = emptyList()
