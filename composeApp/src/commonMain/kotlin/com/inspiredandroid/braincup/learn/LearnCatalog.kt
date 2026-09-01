package com.inspiredandroid.braincup.learn

import com.inspiredandroid.braincup.learn.content.AlgebraContent
import com.inspiredandroid.braincup.learn.content.ArithmeticContent
import com.inspiredandroid.braincup.learn.content.GeometryContent

/**
 * Every sub-topic in the Learn section, keyed by topic.
 *
 * Each topic holds a ladder of sub-topics ordered easiest first. Unit ids and lesson ids are
 * persisted as progress, so they may be appended to but never renamed or reused for different
 * content.
 */
object LearnCatalog {

    private val byTopic: Map<MathTopic, List<LearnUnit>> = mapOf(
        MathTopic.ARITHMETIC to ArithmeticContent.units,
        MathTopic.GEOMETRY to GeometryContent.units,
        MathTopic.ALGEBRA to AlgebraContent.units,
    )

    /** Every sub-topic, in menu order: topic by topic, easiest first inside each. */
    val allUnits: List<LearnUnit> = MathTopic.entries.flatMap { byTopic.getValue(it) }

    /** Every lesson across every sub-topic, in curriculum order. */
    val allLessons: List<LearnLesson> = allUnits.flatMap { it.lessons }

    val totalUnitCount: Int = allUnits.size

    val totalLessonCount: Int = allLessons.size

    /** The sub-topics of [topic], easiest first. */
    fun units(topic: MathTopic): List<LearnUnit> = byTopic.getValue(topic)

    fun unitById(id: String): LearnUnit? = allUnits.firstOrNull { it.id == id }

    fun unitBySlug(topic: MathTopic, slug: String): LearnUnit? = byTopic.getValue(topic).firstOrNull { it.urlSlug == slug }

    fun lessonById(id: String): LearnLesson? = allLessons.firstOrNull { it.id == id }

    /** The sub-topic a lesson belongs to. Every lesson in the catalog has one. */
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
