package com.inspiredandroid.braincup.learn

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_topic_algebra
import braincup.composeapp.generated.resources.learn_topic_algebra_subtitle
import braincup.composeapp.generated.resources.learn_topic_arithmetic
import braincup.composeapp.generated.resources.learn_topic_arithmetic_subtitle
import braincup.composeapp.generated.resources.learn_topic_calculus
import braincup.composeapp.generated.resources.learn_topic_calculus_subtitle
import braincup.composeapp.generated.resources.learn_topic_data
import braincup.composeapp.generated.resources.learn_topic_data_subtitle
import braincup.composeapp.generated.resources.learn_topic_functions
import braincup.composeapp.generated.resources.learn_topic_functions_subtitle
import braincup.composeapp.generated.resources.learn_topic_geometry
import braincup.composeapp.generated.resources.learn_topic_geometry_subtitle
import braincup.composeapp.generated.resources.learn_topic_measurement
import braincup.composeapp.generated.resources.learn_topic_measurement_subtitle
import braincup.composeapp.generated.resources.learn_topic_trigonometry
import braincup.composeapp.generated.resources.learn_topic_trigonometry_subtitle
import org.jetbrains.compose.resources.StringResource

/**
 * A subject area in the Learn section. A topic spans several grade bands — see [GradeLevel] — so it
 * carries no lessons of its own; the lessons live on the [LearnUnit] for one band of one topic.
 *
 * [id] is persisted (as part of unit ids and certificates) and [urlSlug] appears in the web build's
 * address bar, so neither may be renamed once shipped.
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
    MEASUREMENT(
        id = "measurement",
        urlSlug = "measurement",
        titleRes = Res.string.learn_topic_measurement,
        subtitleRes = Res.string.learn_topic_measurement_subtitle,
        accentColor = 0xFFD1FAE5,
    ),
    GEOMETRY(
        id = "geometry",
        urlSlug = "geometry",
        titleRes = Res.string.learn_topic_geometry,
        subtitleRes = Res.string.learn_topic_geometry_subtitle,
        accentColor = 0xFFFFEDD5,
    ),
    DATA(
        id = "data",
        urlSlug = "data",
        titleRes = Res.string.learn_topic_data,
        subtitleRes = Res.string.learn_topic_data_subtitle,
        accentColor = 0xFFEDE9FE,
    ),
    ALGEBRA(
        id = "algebra",
        urlSlug = "algebra",
        titleRes = Res.string.learn_topic_algebra,
        subtitleRes = Res.string.learn_topic_algebra_subtitle,
        accentColor = 0xFFFCE7F3,
    ),
    TRIGONOMETRY(
        id = "trigonometry",
        urlSlug = "trigonometry",
        titleRes = Res.string.learn_topic_trigonometry,
        subtitleRes = Res.string.learn_topic_trigonometry_subtitle,
        accentColor = 0xFFFEF3C7,
    ),
    FUNCTIONS(
        id = "functions",
        urlSlug = "functions",
        titleRes = Res.string.learn_topic_functions,
        subtitleRes = Res.string.learn_topic_functions_subtitle,
        accentColor = 0xFFCFFAFE,
    ),
    CALCULUS(
        id = "calculus",
        urlSlug = "calculus",
        titleRes = Res.string.learn_topic_calculus,
        subtitleRes = Res.string.learn_topic_calculus_subtitle,
        accentColor = 0xFFE0E7FF,
    ),
    ;

    /** Every band that teaches this topic, youngest first. */
    val units: List<LearnUnit> get() = LearnCatalog.unitsOf(this)

    companion object {
        fun byId(id: String): MathTopic? = entries.firstOrNull { it.id == id }

        fun bySlug(slug: String): MathTopic? = entries.firstOrNull { it.urlSlug == slug }
    }
}
