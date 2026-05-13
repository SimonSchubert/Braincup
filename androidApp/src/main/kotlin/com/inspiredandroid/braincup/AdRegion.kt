package com.inspiredandroid.braincup

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

// EU 27 + EEA (IS, LI, NO) + UK + Switzerland. AdMob's "European regulations" region.
private val CONSENT_REQUIRED_COUNTRIES = setOf(
    "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
    "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL",
    "PL", "PT", "RO", "SK", "SI", "ES", "SE",
    "IS", "LI", "NO",
    "GB", "CH",
)

// Best-effort EEA detection. Any signal hitting the list suppresses ads — false
// positives just skip ads for a non-EU user; false negatives are the unsafe direction.
fun isConsentRequiredRegion(context: Context): Boolean {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val candidates = listOfNotNull(
        tm?.networkCountryIso,
        tm?.simCountryIso,
        Locale.getDefault().country,
    ).map { it.uppercase(Locale.ROOT) }
    return candidates.any { it in CONSENT_REQUIRED_COUNTRIES }
}
