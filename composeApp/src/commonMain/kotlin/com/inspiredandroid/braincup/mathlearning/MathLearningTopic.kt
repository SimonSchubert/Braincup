package com.inspiredandroid.braincup.mathlearning

import androidx.compose.runtime.Immutable
import braincup.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

@Immutable
data class PracticeQuestion(
    val questionRes: StringResource,
    val options: List<String>,
    val correctIndex: Int,
    val explanationRes: StringResource,
)

@Immutable
data class LessonStep(
    val titleRes: StringResource,
    val explanationRes: StringResource,
    val formula: String? = null,
    val practiceQuestion: PracticeQuestion? = null,
)

@Immutable
data class TestQuestion(
    val questionRes: StringResource,
    val options: List<String>,
    val correctIndex: Int,
)

enum class MathLearningTopic(
    val id: String,
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val accentColor: Long,
    val lessons: List<LessonStep>,
    val testQuestions: List<TestQuestion>,
) {
    ARITHMETIC(
        id = "arithmetic",
        titleRes = Res.string.math_topic_arithmetic,
        descriptionRes = Res.string.math_topic_arithmetic_desc,
        accentColor = 0xFF4CAF50, // Green
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_arithmetic_l1_title,
                explanationRes = Res.string.math_arithmetic_l1_desc,
                formula = "a * (b + c) = a * b + a * c",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_arithmetic_q1_question,
                    options = listOf("14", "20", "26", "30"),
                    correctIndex = 1, // 4 * (2 + 3) = 20
                    explanationRes = Res.string.math_arithmetic_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_arithmetic_l2_title,
                explanationRes = Res.string.math_arithmetic_l2_desc,
                formula = "PEMDAS / BODMAS",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_arithmetic_q2_question,
                    options = listOf("16", "14", "20", "10"),
                    correctIndex = 0, // 2 + 3 * 4 + 2 = 2 + 12 + 2 = 16
                    explanationRes = Res.string.math_arithmetic_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_arithmetic_t1,
                options = listOf("15", "21", "27", "33"),
                correctIndex = 1, // 3 * (4 + 3) = 21
            ),
            TestQuestion(
                questionRes = Res.string.math_arithmetic_t2,
                options = listOf("10", "14", "18", "22"),
                correctIndex = 1, // 8 + 12 / 2 = 14
            ),
            TestQuestion(
                questionRes = Res.string.math_arithmetic_t3,
                options = listOf("25", "35", "45", "50"),
                correctIndex = 2, // 5 * (6 + 3) = 45
            ),
        ),
    ),
    MEASUREMENT(
        id = "measurement",
        titleRes = Res.string.math_topic_measurement,
        descriptionRes = Res.string.math_topic_measurement_desc,
        accentColor = 0xFFFF9800, // Orange
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_measurement_l1_title,
                explanationRes = Res.string.math_measurement_l1_desc,
                formula = "1 km = 1000 m, 1 m = 100 cm",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_measurement_q1_question,
                    options = listOf("30 cm", "300 cm", "3000 cm", "30000 cm"),
                    correctIndex = 1, // 3 meters = 300 cm
                    explanationRes = Res.string.math_measurement_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_measurement_l2_title,
                explanationRes = Res.string.math_measurement_l2_desc,
                formula = "Area = width * height",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_measurement_q2_question,
                    options = listOf("12 m²", "18 m²", "24 m²", "36 m²"),
                    correctIndex = 2, // 6 * 4 = 24
                    explanationRes = Res.string.math_measurement_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_measurement_t1,
                options = listOf("250 cm", "2500 cm", "25000 cm", "25 cm"),
                correctIndex = 0, // 2.5 m = 250 cm
            ),
            TestQuestion(
                questionRes = Res.string.math_measurement_t2,
                options = listOf("20 cm²", "35 cm²", "40 cm²", "70 cm²"),
                correctIndex = 1, // 5 * 7 = 35
            ),
            TestQuestion(
                questionRes = Res.string.math_measurement_t3,
                options = listOf("30 m", "60 m", "120 m", "200 m"),
                correctIndex = 1, // Perimeter of 10x20 rectangle = 2*(10+20) = 60
            ),
        ),
    ),
    GEOMETRY(
        id = "geometry",
        titleRes = Res.string.math_topic_geometry,
        descriptionRes = Res.string.math_topic_geometry_desc,
        accentColor = 0xFF2196F3, // Blue
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_geometry_l1_title,
                explanationRes = Res.string.math_geometry_l1_desc,
                formula = "a² + b² = c²",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_geometry_q1_question,
                    options = listOf("4", "5", "6", "7"),
                    correctIndex = 1, // sqrt(3^2 + 4^2) = 5
                    explanationRes = Res.string.math_geometry_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_geometry_l2_title,
                explanationRes = Res.string.math_geometry_l2_desc,
                formula = "Area = (base * height) / 2",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_geometry_q2_question,
                    options = listOf("16 cm²", "24 cm²", "32 cm²", "48 cm²"),
                    correctIndex = 1, // (8 * 6) / 2 = 24
                    explanationRes = Res.string.math_geometry_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_geometry_t1,
                options = listOf("10", "12", "13", "15"),
                correctIndex = 2, // sqrt(5^2 + 12^2) = 13
            ),
            TestQuestion(
                questionRes = Res.string.math_geometry_t2,
                options = listOf("180°", "360°", "540°", "720°"),
                correctIndex = 0, // Sum of angles in triangle = 180°
            ),
            TestQuestion(
                questionRes = Res.string.math_geometry_t3,
                options = listOf("15 cm²", "30 cm²", "60 cm²", "120 cm²"),
                correctIndex = 1, // (10 * 6) / 2 = 30
            ),
        ),
    ),
    DATA_ANALYSIS(
        id = "data_analysis",
        titleRes = Res.string.math_topic_data_analysis,
        descriptionRes = Res.string.math_topic_data_analysis_desc,
        accentColor = 0xFF9C27B0, // Purple
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_data_l1_title,
                explanationRes = Res.string.math_data_l1_desc,
                formula = "Mean = Sum / Count",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_data_q1_question,
                    options = listOf("4", "6", "8", "10"),
                    correctIndex = 1, // (2 + 4 + 6 + 8 + 10) / 5 = 6
                    explanationRes = Res.string.math_data_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_data_l2_title,
                explanationRes = Res.string.math_data_l2_desc,
                formula = "P(A) = Favorable / Total",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_data_q2_question,
                    options = listOf("1/6", "1/3", "1/2", "2/3"),
                    correctIndex = 2, // 3/6 = 1/2
                    explanationRes = Res.string.math_data_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_data_t1,
                options = listOf("10", "12", "15", "18"),
                correctIndex = 1, // (5 + 10 + 15 + 18) / 4 = 12
            ),
            TestQuestion(
                questionRes = Res.string.math_data_t2,
                options = listOf("1/4", "1/2", "1/3", "1/6"),
                correctIndex = 0, // Two coin flips, both heads = 1/4
            ),
            TestQuestion(
                questionRes = Res.string.math_data_t3,
                options = listOf("Median", "Mean", "Mode", "Range"),
                correctIndex = 2, // Most frequent value = Mode
            ),
        ),
    ),
    ALGEBRA(
        id = "algebra",
        titleRes = Res.string.math_topic_algebra,
        descriptionRes = Res.string.math_topic_algebra_desc,
        accentColor = 0xFFE91E63, // Pink
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_algebra_l1_title,
                explanationRes = Res.string.math_algebra_l1_desc,
                formula = "ax + b = c => x = (c - b) / a",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_algebra_q1_question,
                    options = listOf("3", "4", "5", "6"),
                    correctIndex = 1, // 2x + 5 = 13 => 2x = 8 => x = 4
                    explanationRes = Res.string.math_algebra_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_algebra_l2_title,
                explanationRes = Res.string.math_algebra_l2_desc,
                formula = "(a + b)² = a² + 2ab + b²",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_algebra_q2_question,
                    options = listOf("x² + 6", "x² + 6x + 9", "x² + 9", "x² + 3x + 9"),
                    correctIndex = 1, // (x + 3)^2 = x^2 + 6x + 9
                    explanationRes = Res.string.math_algebra_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_algebra_t1,
                options = listOf("3", "5", "7", "9"),
                correctIndex = 1, // 3x - 4 = 11 => 3x = 15 => x = 5
            ),
            TestQuestion(
                questionRes = Res.string.math_algebra_t2,
                options = listOf("x² + 16", "x² + 8x + 16", "x² + 4x + 16", "2x + 8"),
                correctIndex = 1, // (x + 4)^2 = x^2 + 8x + 16
            ),
            TestQuestion(
                questionRes = Res.string.math_algebra_t3,
                options = listOf("(x - 2)(x + 2)", "(x - 4)(x + 4)", "(x - 2)²", "x(x - 4)"),
                correctIndex = 0, // x^2 - 4 = (x - 2)(x + 2)
            ),
        ),
    ),
    TRIGONOMETRY(
        id = "trigonometry",
        titleRes = Res.string.math_topic_trigonometry,
        descriptionRes = Res.string.math_topic_trigonometry_desc,
        accentColor = 0xFF00BCD4, // Cyan
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_trig_l1_title,
                explanationRes = Res.string.math_trig_l1_desc,
                formula = "sin = Opp/Hyp, cos = Adj/Hyp, tan = Opp/Adj",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_trig_q1_question,
                    options = listOf("3/5", "4/5", "3/4", "5/3"),
                    correctIndex = 0, // sin = 3/5
                    explanationRes = Res.string.math_trig_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_trig_l2_title,
                explanationRes = Res.string.math_trig_l2_desc,
                formula = "sin²(θ) + cos²(θ) = 1",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_trig_q2_question,
                    options = listOf("0", "1/2", "1", "2"),
                    correctIndex = 2, // sin^2 + cos^2 = 1
                    explanationRes = Res.string.math_trig_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_trig_t1,
                options = listOf("0", "1/2", "1", "undefined"),
                correctIndex = 1, // sin(30°) = 1/2
            ),
            TestQuestion(
                questionRes = Res.string.math_trig_t2,
                options = listOf("0", "1/2", "1", "infinity"),
                correctIndex = 2, // tan(45°) = 1
            ),
            TestQuestion(
                questionRes = Res.string.math_trig_t3,
                options = listOf("sin(θ)", "cos(θ)", "tan(θ)", "1"),
                correctIndex = 2, // sin(θ) / cos(θ) = tan(θ)
            ),
        ),
    ),
    PRE_CALCULUS(
        id = "pre_calculus",
        titleRes = Res.string.math_topic_pre_calculus,
        descriptionRes = Res.string.math_topic_pre_calculus_desc,
        accentColor = 0xFF673AB7, // Deep Purple
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_precalc_l1_title,
                explanationRes = Res.string.math_precalc_l1_desc,
                formula = "f(x) = y",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_precalc_q1_question,
                    options = listOf("7", "9", "11", "13"),
                    correctIndex = 2, // f(x) = 2x + 5 => f(3) = 11
                    explanationRes = Res.string.math_precalc_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_precalc_l2_title,
                explanationRes = Res.string.math_precalc_l2_desc,
                formula = "log_b(x) = y <=> b^y = x",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_precalc_q2_question,
                    options = listOf("2", "3", "4", "5"),
                    correctIndex = 1, // log_2(8) = 3
                    explanationRes = Res.string.math_precalc_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_precalc_t1,
                options = listOf("10", "13", "16", "19"),
                correctIndex = 1, // f(x) = 3x + 4, f(3) = 13
            ),
            TestQuestion(
                questionRes = Res.string.math_precalc_t2,
                options = listOf("1", "2", "3", "100"),
                correctIndex = 1, // log_10(100) = 2
            ),
            TestQuestion(
                questionRes = Res.string.math_precalc_t3,
                options = listOf("x = 0", "All real numbers", "x > 0", "x < 0"),
                correctIndex = 1, // Domain of f(x) = x^2 + 1 is All real numbers
            ),
        ),
    ),
    CALCULUS(
        id = "calculus",
        titleRes = Res.string.math_topic_calculus,
        descriptionRes = Res.string.math_topic_calculus_desc,
        accentColor = 0xFF3F51B5, // Indigo
        lessons = listOf(
            LessonStep(
                titleRes = Res.string.math_calc_l1_title,
                explanationRes = Res.string.math_calc_l1_desc,
                formula = "d/dx(xⁿ) = n * xⁿ⁻¹",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_calc_q1_question,
                    options = listOf("2x", "3x²", "x³", "6x"),
                    correctIndex = 1, // d/dx(x^3) = 3x^2
                    explanationRes = Res.string.math_calc_q1_exp,
                ),
            ),
            LessonStep(
                titleRes = Res.string.math_calc_l2_title,
                explanationRes = Res.string.math_calc_l2_desc,
                formula = "∫ xⁿ dx = (xⁿ⁺¹ / (n + 1)) + C",
                practiceQuestion = PracticeQuestion(
                    questionRes = Res.string.math_calc_q2_question,
                    options = listOf("x² + C", "x³ + C", "(x³ / 3) + C", "2x + C"),
                    correctIndex = 0, // ∫ 2x dx = x^2 + C
                    explanationRes = Res.string.math_calc_q2_exp,
                ),
            ),
        ),
        testQuestions = listOf(
            TestQuestion(
                questionRes = Res.string.math_calc_t1,
                options = listOf("4x³", "x⁴", "12x²", "4x"),
                correctIndex = 0, // d/dx(x^4) = 4x^3
            ),
            TestQuestion(
                questionRes = Res.string.math_calc_t2,
                options = listOf("x³ + C", "(x³ / 3) + C", "3x² + C", "x⁴ + C"),
                correctIndex = 0, // ∫ 3x^2 dx = x^3 + C
            ),
            TestQuestion(
                questionRes = Res.string.math_calc_t3,
                options = listOf("The rate of change", "The total area under a curve", "The maximum value", "The slope"),
                correctIndex = 1, // A definite integral represents the area under a curve
            ),
        ),
    );

    companion object {
        fun getById(id: String): MathLearningTopic? = entries.firstOrNull { it.id == id }
    }
}
