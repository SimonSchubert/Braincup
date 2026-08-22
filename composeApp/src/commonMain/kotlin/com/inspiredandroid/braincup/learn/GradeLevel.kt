package com.inspiredandroid.braincup.learn

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_level_g1112
import braincup.composeapp.generated.resources.learn_level_g1112_subtitle
import braincup.composeapp.generated.resources.learn_level_g12
import braincup.composeapp.generated.resources.learn_level_g12_subtitle
import braincup.composeapp.generated.resources.learn_level_g35
import braincup.composeapp.generated.resources.learn_level_g35_subtitle
import braincup.composeapp.generated.resources.learn_level_g68
import braincup.composeapp.generated.resources.learn_level_g68_subtitle
import braincup.composeapp.generated.resources.learn_level_g910
import braincup.composeapp.generated.resources.learn_level_g910_subtitle
import org.jetbrains.compose.resources.StringResource

/**
 * A school-grade band in the Learn section. The band, not the subject, is the first choice a
 * learner makes: the same topic comes back at several bands with harder material each time, so
 * picking "Geometry" alone would drop a nine-year-old into circle theorems.
 *
 * [id] is persisted (unit ids, certificates) and [urlSlug] appears in the web build's address bar,
 * so neither may be renamed once shipped.
 */
enum class GradeLevel(
    val id: String,
    val urlSlug: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    /** Tile accent, running from warm (youngest) to cool (oldest). */
    val accentColor: Long,
) {
    GRADES_1_2(
        id = "g12",
        urlSlug = "grade-1-2",
        titleRes = Res.string.learn_level_g12,
        subtitleRes = Res.string.learn_level_g12_subtitle,
        accentColor = 0xFFFEF3C7,
    ),
    GRADES_3_5(
        id = "g35",
        urlSlug = "grade-3-5",
        titleRes = Res.string.learn_level_g35,
        subtitleRes = Res.string.learn_level_g35_subtitle,
        accentColor = 0xFFD1FAE5,
    ),
    GRADES_6_8(
        id = "g68",
        urlSlug = "grade-6-8",
        titleRes = Res.string.learn_level_g68,
        subtitleRes = Res.string.learn_level_g68_subtitle,
        accentColor = 0xFFDBEAFE,
    ),
    GRADES_9_10(
        id = "g910",
        urlSlug = "grade-9-10",
        titleRes = Res.string.learn_level_g910,
        subtitleRes = Res.string.learn_level_g910_subtitle,
        accentColor = 0xFFEDE9FE,
    ),
    GRADES_11_12(
        id = "g1112",
        urlSlug = "grade-11-12",
        titleRes = Res.string.learn_level_g1112,
        subtitleRes = Res.string.learn_level_g1112_subtitle,
        accentColor = 0xFFFCE7F3,
    ),
    ;

    /** The units taught at this band, in the order they should be worked through. */
    val units: List<LearnUnit> get() = LearnCatalog.units(this)

    companion object {
        fun byId(id: String): GradeLevel? = entries.firstOrNull { it.id == id }

        fun bySlug(slug: String): GradeLevel? = entries.firstOrNull { it.urlSlug == slug }
    }
}
