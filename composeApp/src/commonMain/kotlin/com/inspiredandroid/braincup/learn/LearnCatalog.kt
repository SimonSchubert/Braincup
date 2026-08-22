package com.inspiredandroid.braincup.learn

import com.inspiredandroid.braincup.learn.content.AlgebraContent
import com.inspiredandroid.braincup.learn.content.ArithmeticContent
import com.inspiredandroid.braincup.learn.content.CalculusContent
import com.inspiredandroid.braincup.learn.content.DataContent
import com.inspiredandroid.braincup.learn.content.FunctionsContent
import com.inspiredandroid.braincup.learn.content.GeometryContent
import com.inspiredandroid.braincup.learn.content.MeasurementContent
import com.inspiredandroid.braincup.learn.content.TrigonometryContent

/**
 * Every lesson and test in the Learn section, keyed by topic.
 *
 * Lesson ids are persisted as completion progress, so they may be appended to but never renamed
 * or reused for different content.
 */
object LearnCatalog {

    private val byTopic: Map<MathTopic, Pair<List<LearnLesson>, LearnQuiz>> = mapOf(
        MathTopic.ARITHMETIC to (ArithmeticContent.lessons to ArithmeticContent.quiz),
        MathTopic.MEASUREMENT to (MeasurementContent.lessons to MeasurementContent.quiz),
        MathTopic.GEOMETRY to (GeometryContent.lessons to GeometryContent.quiz),
        MathTopic.DATA to (DataContent.lessons to DataContent.quiz),
        MathTopic.ALGEBRA to (AlgebraContent.lessons to AlgebraContent.quiz),
        MathTopic.TRIGONOMETRY to (TrigonometryContent.lessons to TrigonometryContent.quiz),
        MathTopic.FUNCTIONS to (FunctionsContent.lessons to FunctionsContent.quiz),
        MathTopic.CALCULUS to (CalculusContent.lessons to CalculusContent.quiz),
    )

    /** Every lesson across every topic, in curriculum order. */
    val allLessons: List<LearnLesson> = MathTopic.entries.flatMap { byTopic.getValue(it).first }

    val totalLessonCount: Int = allLessons.size

    fun lessons(topic: MathTopic): List<LearnLesson> = byTopic.getValue(topic).first

    fun quiz(topic: MathTopic): LearnQuiz = byTopic.getValue(topic).second

    fun lessonById(id: String): LearnLesson? = allLessons.firstOrNull { it.id == id }

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
