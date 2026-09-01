package com.inspiredandroid.braincup.learn

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_topic_algebra
import braincup.composeapp.generated.resources.learn_topic_algebra_subtitle
import braincup.composeapp.generated.resources.learn_topic_arithmetic
import braincup.composeapp.generated.resources.learn_topic_arithmetic_subtitle
import braincup.composeapp.generated.resources.learn_topic_geometry
import braincup.composeapp.generated.resources.learn_topic_geometry_subtitle
import org.jetbrains.compose.resources.StringResource

/**
 * A subject area in the Learn section, and the first choice a learner makes. A topic carries no
 * lessons of its own: it holds a ladder of [LearnUnit] sub-topics, ordered easiest first, and the
 * lessons live on those.
 *
 * [id] is persisted (as part of unit ids and certificates) and [urlSlug] appears in the web build's
 * address bar, so neither may be renamed once shipped.
 *
 * Arithmetic, Geometry and Algebra ship. The remaining five topics were written, then cut to the
 * frozen `learn-parked` branch; see `docs/learn-release-status.md` to restore one.
 */
enum class MathTopic(
    val id: String,
    val urlSlug: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    /** Tile accent, drawn from the same pastel family as the mini-game category colors. */
    val accentColor: Long,
) {
    ARITHMETIC(
        id = "arithmetic",
        urlSlug = "arithmetic",
        titleRes = Res.string.learn_topic_arithmetic,
        subtitleRes = Res.string.learn_topic_arithmetic_subtitle,
        accentColor = 0xFFDBEAFE,
    ),
    GEOMETRY(
        id = "geometry",
        urlSlug = "geometry",
        titleRes = Res.string.learn_topic_geometry,
        subtitleRes = Res.string.learn_topic_geometry_subtitle,
        accentColor = 0xFFFFEDD5,
    ),
    ALGEBRA(
        id = "algebra",
        urlSlug = "algebra",
        titleRes = Res.string.learn_topic_algebra,
        subtitleRes = Res.string.learn_topic_algebra_subtitle,
        accentColor = 0xFFFCE7F3,
    ),
    ;

    /** The sub-topics of this topic, easiest first. */
    val units: List<LearnUnit> get() = LearnCatalog.units(this)

    companion object {
        fun byId(id: String): MathTopic? = entries.firstOrNull { it.id == id }

        fun bySlug(slug: String): MathTopic? = entries.firstOrNull { it.urlSlug == slug }
    }
}
