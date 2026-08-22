package com.inspiredandroid.braincup.learn

import com.inspiredandroid.braincup.learn.content.Grade11To12Content
import com.inspiredandroid.braincup.learn.content.Grade1To2Content
import com.inspiredandroid.braincup.learn.content.Grade3To5Content
import com.inspiredandroid.braincup.learn.content.Grade6To8Content
import com.inspiredandroid.braincup.learn.content.Grade9To10Content

/**
 * Every unit in the Learn section, keyed by grade band.
 *
 * Unit ids and lesson ids are persisted as progress, so they may be appended to but never renamed
 * or reused for different content.
 */
object LearnCatalog {

    private val byLevel: Map<GradeLevel, List<LearnUnit>> = mapOf(
        GradeLevel.GRADES_1_2 to Grade1To2Content.units,
        GradeLevel.GRADES_3_5 to Grade3To5Content.units,
        GradeLevel.GRADES_6_8 to Grade6To8Content.units,
        GradeLevel.GRADES_9_10 to Grade9To10Content.units,
        GradeLevel.GRADES_11_12 to Grade11To12Content.units,
    )

    /** Every unit, youngest band first. */
    val allUnits: List<LearnUnit> = GradeLevel.entries.flatMap { byLevel.getValue(it) }

    /** Every lesson across every unit, in curriculum order. */
    val allLessons: List<LearnLesson> = allUnits.flatMap { it.lessons }

    val totalUnitCount: Int = allUnits.size

    val totalLessonCount: Int = allLessons.size

    fun units(level: GradeLevel): List<LearnUnit> = byLevel.getValue(level)

    /** The bands that teach [topic], youngest first. */
    fun unitsOf(topic: MathTopic): List<LearnUnit> = allUnits.filter { it.topic == topic }

    fun unitById(id: String): LearnUnit? = allUnits.firstOrNull { it.id == id }

    fun unitOf(level: GradeLevel, topic: MathTopic): LearnUnit? = unitById(LearnUnit.idFor(level, topic))

    fun lessonById(id: String): LearnLesson? = allLessons.firstOrNull { it.id == id }

    /** The unit a lesson belongs to. Every lesson in the catalog has one. */
    fun unitOfLesson(lesson: LearnLesson): LearnUnit? = unitById(lesson.unitId)

    /**
     * Compare a typed answer with the expected one. The number pad only produces digits and
     * operators, so this tolerates the incidental differences a learner can still create:
     * surrounding space, thousands separators, and a trailing ".0".
     */
    fun matchesNumericAnswer(input: String, expected: String): Boolean {
        val normalizedInput = normalizeNumeric(input)
        return normalizedInput.isNotEmpty() && normalizedInput == normalizeNumeric(expected)
    }

    private fun normalizeNumeric(value: String): String {
        val trimmed = value.trim().replace(" ", "").replace(",", "").replace("−", "-")
        // "6700." and "6700.0" should both count as 6700, but "0.75" must keep its digits.
        return if (trimmed.contains('.')) {
            trimmed.trimEnd('0').trimEnd('.')
        } else {
            trimmed
        }
    }
}
