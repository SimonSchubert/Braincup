package com.inspiredandroid.braincup.learn

/**
 * Where a sub-topic sits in school.
 *
 * This is a hint shown on the sub-topic ("Ages 11-14") and the key its topic's ladder is ordered
 * by, not a navigation layer: learners pick a topic and then a sub-topic, never a school year. [id]
 * is persisted in saved state, so it may not be renamed once shipped.
 */
enum class GradeLevel(
    val id: String,
    val urlSlug: String,
    /** The ages this band is usually taught at, shown beside a sub-topic's name. */
    val ageRange: String,
) {
    GRADES_1_2(
        id = "g12",
        urlSlug = "grade-1-2",
        ageRange = "6–8",
    ),
    GRADES_3_5(
        id = "g35",
        urlSlug = "grade-3-5",
        ageRange = "8–11",
    ),
    GRADES_6_8(
        id = "g68",
        urlSlug = "grade-6-8",
        ageRange = "11–14",
    ),
    GRADES_9_10(
        id = "g910",
        urlSlug = "grade-9-10",
        ageRange = "14–16",
    ),
    GRADES_11_12(
        id = "g1112",
        urlSlug = "grade-11-12",
        ageRange = "16–18",
    ),
    ;

    companion object {
        fun byId(id: String): GradeLevel? = entries.firstOrNull { it.id == id }

        fun bySlug(slug: String): GradeLevel? = entries.firstOrNull { it.urlSlug == slug }
    }
}
