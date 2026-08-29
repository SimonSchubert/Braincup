package com.inspiredandroid.braincup.learn

import org.jetbrains.compose.resources.StringResource

/**
 * A run of guide entries that belong together, with the line that says what they have in common.
 *
 * Generic over the entry because the shape guide and the rules guide list different things but
 * group them the same way, and their screens then render the heading from the same composable.
 */
data class GuideSection<E>(
    val id: String,
    val title: StringResource,
    val blurb: StringResource,
    val entries: List<E>,
)
