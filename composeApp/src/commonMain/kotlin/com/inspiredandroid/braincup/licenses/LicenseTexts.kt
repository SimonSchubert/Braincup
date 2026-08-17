package com.inspiredandroid.braincup.licenses

import braincup.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

/** Every entry sharing a license reads the same file, and Apache-2.0 alone is ~11 KB. */
object LicenseTexts {
    private val cache = mutableMapOf<SoftwareLicense, String>()

    fun cached(license: SoftwareLicense): String? = cache[license]

    @OptIn(ExperimentalResourceApi::class)
    suspend fun load(license: SoftwareLicense): String? {
        val path = license.textPath ?: return null
        cache[license]?.let { return it }
        return try {
            Res.readBytes(path).decodeToString().also { cache[license] = it }
        } catch (_: Exception) {
            null
        }
    }
}
