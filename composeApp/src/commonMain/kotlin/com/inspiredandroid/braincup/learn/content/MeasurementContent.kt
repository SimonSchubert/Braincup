package com.inspiredandroid.braincup.learn.content

import braincup.composeapp.generated.resources.*
import com.inspiredandroid.braincup.learn.GaugeKind
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.Clock
import com.inspiredandroid.braincup.learn.LearnVisual.Coins
import com.inspiredandroid.braincup.learn.LearnVisual.Gauge
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.RatioBar
import com.inspiredandroid.braincup.learn.LearnVisual.Ruler
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.counted
import com.inspiredandroid.braincup.learn.filled
import com.inspiredandroid.braincup.learn.learnUnit
import com.inspiredandroid.braincup.learn.math
import com.inspiredandroid.braincup.learn.mathOptions
import com.inspiredandroid.braincup.learn.wordOptions
import com.inspiredandroid.braincup.learn.words

/**
 * Measurement: reading the instruments a learner meets first - a ruler, a clock, a handful of
 * coins, a scale and a jug - then the metric ladder that ties their units together, and finally
 * the one compound measure that ladder is needed for, speed.
 *
 * Perimeter and area are deliberately **not** here. They moved into Geometry when the section was
 * restructured, because what they teach is a property of a shape rather than the act of reading a
 * quantity off an instrument; see `docs/learn-release-status.md`.
 *
 * The `Gauge` figure was added for this topic. Mass and capacity had no drawing at all, and the
 * skill they are taught with is reading a scale whose marks are not worth one unit each - which is
 * a graduation, not a bar of a length, so `Ruler` could not be borrowed for it.
 *
 * Two figure habits are worth knowing before editing a step here:
 *
 * * **A `Steps` ladder prints every term it holds**, so a question that asks for the next rung
 *   must stop the ladder short of it. Three hops of 90 km beside "how far in four hours" is the
 *   pattern with the answer left off; a fourth term would have answered it.
 * * **A rate question has no ladder that can pose it honestly**, because a hop label *is* the
 *   rate. Those draw the journey as **one** `RatioBar` run cut into as many cells as there are
 *   hours - `parts = listOf(4)`, not `listOf(1, 1, 1, 1)` - with `reveal = false`. A run per hour
 *   comes out in a different colour each, which reads as four different things being compared,
 *   and captions each one "1", which reads as a ratio.
 */
internal object MeasurementContent {

    // --- Length -------------------------------------------------------------------------------

    private val length = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "length",
        title = Res.string.learn_unit_measurement_length_title,
        summary = Res.string.learn_unit_measurement_length_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "measurement-length-ruler",
                title = Res.string.learn_measurement_length_ruler_title,
                summary = Res.string.learn_measurement_length_ruler_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_length_ruler_s1_body),
                        visual = Ruler(length = 6, span = 10),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_length_ruler_s2_body),
                        visual = Ruler(length = 7, span = 10),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_length_ruler_s3_question),
                        options = mathOptions("8 cm", "9 cm", "10 cm", "11 cm"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_length_ruler_s3_explanation),
                        visual = Ruler(length = 9, span = 10, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_length_ruler_s4_question),
                        formula = math("{a:12} - {b:4} = ?"),
                        answer = "8",
                        explanation = words(Res.string.learn_measurement_length_ruler_s4_explanation),
                        visual = Ruler(length = 12, span = 14, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_length_ruler_s5_body),
                        formula = math("10 mm = 1 cm"),
                        // A rule ten marks long, covered end to end: one centimetre, drawn as the
                        // ten millimetres it is made of rather than as a mark on a longer rule.
                        visual = Ruler(length = 10, span = 10, unit = "mm"),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_length_ruler_s6_question),
                        formula = math("{a:3} * {b:10} = ?"),
                        options = mathOptions("13", "30", "300", "0.3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_length_ruler_s6_explanation),
                        visual = Ruler(length = 3, span = 10, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-length-units",
                title = Res.string.learn_measurement_length_units_title,
                summary = Res.string.learn_measurement_length_units_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_length_units_s1_body),
                        formula = math("100 cm = 1 m"),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_length_units_s2_body),
                        formula = math("2 m = 200 cm"),
                        visual = Steps(terms = listOf(1, 10, 100), multiply = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_length_units_s3_question),
                        formula = math("{a:3} * {b:100} = ?"),
                        answer = "300",
                        explanation = words(Res.string.learn_measurement_length_units_s3_explanation),
                        visual = Ruler(length = 3, span = 5, unit = "m", reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_length_units_s4_body),
                        formula = math("1000 m = 1 km"),
                        visual = Steps(terms = listOf(1000, 100, 10, 1), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_length_units_s5_question),
                        formula = math("{a:2} * {b:1000} = ?"),
                        options = mathOptions("20", "200", "2000", "20000"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_length_units_s5_explanation),
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_length_units_s6_question),
                        options = wordOptions(
                            Res.string.learn_opt_unit_millimetres,
                            Res.string.learn_opt_unit_centimetres,
                            Res.string.learn_opt_unit_metres,
                            Res.string.learn_opt_unit_kilometres,
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_measurement_length_units_s6_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-length-comparing",
                title = Res.string.learn_measurement_length_comparing_title,
                summary = Res.string.learn_measurement_length_comparing_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_length_comparing_s1_body),
                        visual = NumberLine(from = 0, to = 120, tickStep = 20, compare = listOf(90, 100)),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_which_is_longer, "90", "1"),
                        options = listOf(
                            math("90 cm"),
                            math("1 m"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_length_comparing_s2_explanation),
                        visual = NumberLine(
                            from = 0,
                            to = 120,
                            tickStep = 20,
                            compare = listOf(90, 100),
                            reveal = false,
                        ),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_length_comparing_s3_question),
                        formula = math("{a:45} - {b:30} = ?"),
                        answer = "15",
                        explanation = words(Res.string.learn_measurement_length_comparing_s3_explanation),
                        visual = NumberLine(from = 0, to = 50, tickStep = 10, start = 45, jump = -30, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_length_comparing_s4_body),
                        formula = math("{a:20} + {b:35} = 55"),
                        visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 20, jump = 35),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_length_comparing_s5_question),
                        formula = math("{a:30} + {b:25} = ?"),
                        options = mathOptions("50 cm", "55 cm", "60 cm", "5 cm"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_length_comparing_s5_explanation),
                        visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 30, jump = 25, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_length_comparing_s6_question),
                        formula = math("{a:100} - {b:35} = ?"),
                        answer = "65",
                        explanation = words(Res.string.learn_measurement_length_comparing_s6_explanation),
                        visual = NumberLine(from = 0, to = 100, tickStep = 20, start = 100, jump = -35, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_length_q1_prompt),
                options = mathOptions("10 cm", "11 cm", "12 cm", "13 cm"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_length_q1_explanation),
                visual = Ruler(length = 12, span = 14, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_length_q2_prompt),
                options = mathOptions("15", "50", "500", "0.5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_length_q2_explanation),
                visual = Ruler(length = 5, span = 10, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_length_q3_prompt),
                options = wordOptions(
                    Res.string.learn_opt_unit_millimetres,
                    Res.string.learn_opt_unit_centimetres,
                    Res.string.learn_opt_unit_metres,
                    Res.string.learn_opt_unit_kilometres,
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_length_q3_explanation),
                visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_length_q4_prompt),
                options = mathOptions("2.5 m", "25 m", "0.25 m", "2500 m"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_length_q4_explanation),
                visual = Steps(terms = listOf(1, 100), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_which_is_longer, "80", "1"),
                options = listOf(
                    math("80 cm"),
                    math("1 m"),
                    words(Res.string.learn_shared_they_equal),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_length_q5_explanation),
                visual = NumberLine(from = 0, to = 120, tickStep = 20, compare = listOf(80, 100), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_longest),
                options = mathOptions("9 mm", "9 cm", "9 m", "9 km"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_measurement_length_q6_explanation),
                visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
            ),
        ),
    )

    // --- Time ---------------------------------------------------------------------------------

    private val time = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "time",
        title = Res.string.learn_unit_measurement_time_title,
        summary = Res.string.learn_unit_measurement_time_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "measurement-time-clock",
                title = Res.string.learn_measurement_time_clock_title,
                summary = Res.string.learn_measurement_time_clock_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_time_clock_s1_body),
                        visual = Clock(hour = 3, minute = 0),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_clock_s2_body),
                        // Twenty past, not o'clock: the body is about the short hand creeping on
                        // between the numbers, which a clock standing exactly on one cannot show.
                        visual = Clock(hour = 8, minute = 20),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_time_is_this),
                        options = listOf(
                            filled(Res.string.learn_opt_oclock, "5"),
                            filled(Res.string.learn_opt_oclock, "6"),
                            filled(Res.string.learn_opt_half_past, "5"),
                            filled(Res.string.learn_opt_quarter_past, "5"),
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_time_clock_s3_explanation),
                        visual = Clock(hour = 5, minute = 0),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_clock_s4_body),
                        visual = Clock(hour = 3, minute = 30),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_time_is_this),
                        options = listOf(
                            filled(Res.string.learn_opt_oclock, "9"),
                            filled(Res.string.learn_opt_half_past, "9"),
                            filled(Res.string.learn_opt_half_past, "6"),
                            filled(Res.string.learn_opt_quarter_past, "9"),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_time_clock_s5_explanation),
                        visual = Clock(hour = 9, minute = 30),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_time_clock_s6_question),
                        answer = "60",
                        explanation = words(Res.string.learn_measurement_time_clock_s6_explanation),
                        visual = Clock(hour = 12, minute = 0),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-time-quarters",
                title = Res.string.learn_measurement_time_quarters_title,
                summary = Res.string.learn_measurement_time_quarters_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_time_quarters_s1_body),
                        visual = Clock(hour = 2, minute = 15),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_quarters_s2_body),
                        visual = Clock(hour = 2, minute = 45),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_time_is_this),
                        options = listOf(
                            filled(Res.string.learn_opt_quarter_past, "7"),
                            filled(Res.string.learn_opt_quarter_to, "7"),
                            filled(Res.string.learn_opt_half_past, "7"),
                            filled(Res.string.learn_opt_oclock, "7"),
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_time_quarters_s3_explanation),
                        visual = Clock(hour = 7, minute = 15),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_quarters_s4_body),
                        formula = math("{a:15} * {b:4} = 60"),
                        visual = Steps(terms = listOf(15, 30, 45, 60)),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_time_is_this),
                        options = listOf(
                            filled(Res.string.learn_opt_quarter_past, "11"),
                            filled(Res.string.learn_opt_quarter_to, "12"),
                            filled(Res.string.learn_opt_quarter_to, "11"),
                            filled(Res.string.learn_opt_half_past, "11"),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_time_quarters_s5_explanation),
                        visual = Clock(hour = 11, minute = 45),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_time_quarters_s6_question),
                        answer = "15",
                        explanation = words(Res.string.learn_measurement_time_quarters_s6_explanation),
                        visual = Clock(hour = 12, minute = 15),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-time-how-long",
                title = Res.string.learn_measurement_time_how_long_title,
                summary = Res.string.learn_measurement_time_how_long_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_time_how_long_s1_body),
                        formula = math("60 s = 1 min"),
                        visual = Steps(terms = listOf(1, 60, 3600), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_how_long_s2_body),
                        formula = math("{a:10} + {b:35} = 45"),
                        visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 10, jump = 35),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_time_how_long_s3_question),
                        // Written as a gap to be filled rather than as a subtraction, because that
                        // is what the hop on the line beside it is doing.
                        formula = math("{a:20} + ? = 50"),
                        answer = "30",
                        explanation = words(Res.string.learn_measurement_time_how_long_s3_explanation),
                        // Both ends marked and no hop between them. A hop arc carries its own
                        // label whatever `reveal` says, and on a question about the gap that
                        // label is the answer.
                        visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 20, compare = listOf(50), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_time_how_long_s4_body),
                        formula = math("{a:2} * {b:60} = 120"),
                        visual = Steps(terms = listOf(1, 60), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_minutes_in_hours, "3"),
                        formula = math("{a:3} * {b:60} = ?"),
                        options = mathOptions("30", "63", "180", "360"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_time_how_long_s5_explanation),
                        visual = Steps(terms = listOf(1, 60), multiply = true),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_hours_in_days, "2"),
                        formula = math("{a:2} * {b:24} = ?"),
                        answer = "48",
                        explanation = words(Res.string.learn_measurement_time_how_long_s6_explanation),
                        visual = Steps(terms = listOf(1, 24), multiply = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_what_time_is_this),
                options = listOf(
                    filled(Res.string.learn_opt_oclock, "8"),
                    filled(Res.string.learn_opt_half_past, "8"),
                    filled(Res.string.learn_opt_quarter_past, "8"),
                    filled(Res.string.learn_opt_oclock, "12"),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_time_q1_explanation),
                visual = Clock(hour = 8, minute = 0),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_what_time_is_this),
                options = listOf(
                    filled(Res.string.learn_opt_quarter_to, "11"),
                    filled(Res.string.learn_opt_quarter_to, "10"),
                    filled(Res.string.learn_opt_quarter_past, "10"),
                    filled(Res.string.learn_opt_half_past, "10"),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_time_q2_explanation),
                visual = Clock(hour = 10, minute = 45),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_time_q3_prompt),
                options = mathOptions("15", "30", "45", "60"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_time_q3_explanation),
                visual = Steps(terms = listOf(1, 60), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_minutes_in_hours, "4"),
                options = mathOptions("40", "64", "240", "400"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_time_q4_explanation),
                visual = Steps(terms = listOf(1, 60), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_time_q5_prompt),
                options = mathOptions("30", "35", "40", "70"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_time_q5_explanation),
                visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 15, compare = listOf(55), reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_hours_in_days, "3"),
                options = mathOptions("27", "36", "72", "24"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_time_q6_explanation),
                visual = Steps(terms = listOf(1, 24), multiply = true),
            ),
        ),
    )

    // --- Money --------------------------------------------------------------------------------

    private val money = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "money",
        title = Res.string.learn_unit_measurement_money_title,
        summary = Res.string.learn_unit_measurement_money_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "measurement-money-coins",
                title = Res.string.learn_measurement_money_coins_title,
                summary = Res.string.learn_measurement_money_coins_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_money_coins_s1_body),
                        visual = Coins(values = listOf(50, 20, 10)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_money_coins_s2_body),
                        visual = Coins(values = listOf(50, 20, 20, 5)),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_money_coins_s3_question),
                        answer = "80",
                        explanation = words(Res.string.learn_measurement_money_coins_s3_explanation),
                        visual = Coins(values = listOf(50, 20, 10), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_money_coins_s4_body),
                        visual = Coins(values = listOf(10, 10, 10, 5)),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_how_much_altogether),
                        options = mathOptions("45c", "50c", "55c", "60c"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_money_coins_s5_explanation),
                        visual = Coins(values = listOf(20, 20, 10, 5), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_money_coins_s6_question),
                        options = mathOptions("10c", "20c", "50c", "100c"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_money_coins_s6_explanation),
                        visual = Coins(values = listOf(20, 20, 10), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-money-euros",
                title = Res.string.learn_measurement_money_euros_title,
                summary = Res.string.learn_measurement_money_euros_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_money_euros_s1_body),
                        formula = math("100c = 1 euro"),
                        visual = Coins(values = listOf(50, 20, 20, 10)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_money_euros_s2_body),
                        visual = Coins(values = listOf(50, 50)),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_cents_in_euro, "2"),
                        formula = math("{a:2} * {b:100} = ?"),
                        answer = "200",
                        explanation = words(Res.string.learn_measurement_money_euros_s3_explanation),
                        visual = Steps(terms = listOf(1, 100), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_money_euros_s4_body),
                        formula = math("250c = 2 euro 50c"),
                        visual = Coins(values = listOf(50, 50, 50, 50, 50)),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_money_euros_s5_question),
                        options = listOf(
                            filled(Res.string.learn_opt_euro_cents, "1", "75"),
                            filled(Res.string.learn_opt_euro_cents, "17", "5"),
                            filled(Res.string.learn_opt_euro_cents, "1", "7"),
                            filled(Res.string.learn_opt_euro_cents, "7", "15"),
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_money_euros_s5_explanation),
                        visual = Coins(values = listOf(50, 50, 50, 20, 5), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_money_euros_s6_question),
                        options = mathOptions("30c", "300c", "3000c", "103c"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_money_euros_s6_explanation),
                        visual = Steps(terms = listOf(1, 100), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-money-change",
                title = Res.string.learn_measurement_money_change_title,
                summary = Res.string.learn_measurement_money_change_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_money_change_s1_body),
                        visual = NumberLine(from = 30, to = 55, tickStep = 5, start = 35, hopSteps = listOf(5, 10)),
                    ),
                    Worked(
                        problem = math("{a:35} + ? = 50"),
                        lines = listOf(
                            words(Res.string.learn_measurement_money_change_s2_l1),
                            words(Res.string.learn_measurement_money_change_s2_l2),
                            words(Res.string.learn_measurement_money_change_s2_l3),
                        ),
                        result = math("15c"),
                        visual = NumberLine(from = 30, to = 55, tickStep = 5, start = 35, hopSteps = listOf(5, 10)),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_money_change_s3_question),
                        options = mathOptions("15c", "20c", "25c", "30c"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_money_change_s3_explanation),
                        visual = NumberLine(from = 35, to = 70, tickStep = 5, start = 40, compare = listOf(65), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_money_change_s4_body),
                        formula = math("{a:65} + {b:35} = 100"),
                        visual = NumberLine(from = 60, to = 100, tickStep = 10, start = 65, jump = 35),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_money_change_s5_question),
                        formula = math("{a:45} + ? = 100"),
                        answer = "55",
                        explanation = words(Res.string.learn_measurement_money_change_s5_explanation),
                        visual = NumberLine(from = 40, to = 100, tickStep = 10, start = 45, compare = listOf(100), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_money_change_s6_question),
                        options = mathOptions("5c", "10c", "15c", "20c"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_money_change_s6_explanation),
                        // What the two stickers come to and what was handed over: the first half
                        // of the question is the multiplying, and the line picks it up from there.
                        visual = NumberLine(from = 0, to = 60, tickStep = 10, start = 40, compare = listOf(50), reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_much_altogether),
                options = mathOptions("40c", "45c", "50c", "55c"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_money_q1_explanation),
                visual = Coins(values = listOf(20, 20, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_cents_in_euro, "4"),
                options = mathOptions("40", "400", "4000", "104"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_money_q2_explanation),
                visual = Steps(terms = listOf(1, 100), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_money_q3_prompt),
                options = mathOptions("10c", "15c", "20c", "25c"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_money_q3_explanation),
                visual = NumberLine(from = 25, to = 55, tickStep = 5, start = 30, compare = listOf(50), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_money_q4_prompt),
                options = listOf(
                    filled(Res.string.learn_opt_euro_cents, "2", "50"),
                    filled(Res.string.learn_opt_euro_cents, "2", "5"),
                    counted(Res.plurals.learn_opt_euro, 25),
                    filled(Res.string.learn_opt_euro_cents, "1", "50"),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_money_q4_explanation),
                visual = Coins(values = listOf(50, 50, 50, 50, 50), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_money_q5_prompt),
                options = mathOptions("5c", "10c", "20c", "50c"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_money_q5_explanation),
                visual = Coins(values = listOf(10, 10), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_money_q6_prompt),
                options = mathOptions("15c", "20c", "25c", "35c"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_money_q6_explanation),
                visual = NumberLine(from = 55, to = 90, tickStep = 5, start = 60, compare = listOf(85), reveal = false),
            ),
        ),
    )

    // --- Mass and capacity --------------------------------------------------------------------

    private val massAndCapacity = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "mass-and-capacity",
        title = Res.string.learn_unit_measurement_mass_title,
        summary = Res.string.learn_unit_measurement_mass_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "measurement-mass-scales",
                title = Res.string.learn_measurement_mass_scales_title,
                summary = Res.string.learn_measurement_mass_scales_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_mass_scales_s1_body),
                        // Numbers every 500 g with a tick at each quarter turn, so "work out what
                        // one step is worth" is something the figure actually asks of the reader.
                        visual = Gauge(GaugeKind.DIAL, value = 750, max = 1000, step = 500, unit = "g", minorStep = 250),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_mass_scales_s2_body),
                        formula = math("1000 / {b:4} = 250"),
                        visual = Gauge(GaugeKind.DIAL, value = 500, max = 1000, step = 500, unit = "g", minorStep = 250),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_does_scale_read),
                        options = mathOptions("400 g", "450 g", "500 g", "600 g"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_mass_scales_s3_explanation),
                        visual = Gauge(GaugeKind.DIAL, value = 500, max = 1000, step = 200, unit = "g", minorStep = 100, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_mass_scales_s4_body),
                        formula = math("1000 g = 1 kg"),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_mass_scales_s5_question),
                        answer = "700",
                        explanation = words(Res.string.learn_measurement_mass_scales_s5_explanation),
                        visual = Gauge(GaugeKind.DIAL, value = 700, max = 1000, step = 200, unit = "g", minorStep = 100, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_mass_scales_s6_question),
                        formula = math("{a:2} * {b:1000} = ?"),
                        options = mathOptions("20", "200", "2000", "20000"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_mass_scales_s6_explanation),
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-capacity-jugs",
                title = Res.string.learn_measurement_capacity_jugs_title,
                summary = Res.string.learn_measurement_capacity_jugs_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_capacity_jugs_s1_body),
                        visual = Gauge(GaugeKind.JUG, value = 600, max = 1000, step = 200, unit = "ml", minorStep = 100),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_capacity_jugs_s2_body),
                        visual = Gauge(GaugeKind.JUG, value = 400, max = 1000, step = 200, unit = "ml", minorStep = 100),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_how_much_in_jug),
                        options = mathOptions("300 ml", "500 ml", "600 ml", "800 ml"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_capacity_jugs_s3_explanation),
                        visual = Gauge(GaugeKind.JUG, value = 500, max = 1000, step = 200, unit = "ml", minorStep = 100, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_capacity_jugs_s4_body),
                        formula = math("1000 ml = 1 l"),
                        visual = Gauge(GaugeKind.JUG, value = 1000, max = 1000, step = 250, unit = "ml"),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_capacity_jugs_s5_question),
                        answer = "900",
                        explanation = words(Res.string.learn_measurement_capacity_jugs_s5_explanation),
                        visual = Gauge(GaugeKind.JUG, value = 900, max = 1000, step = 200, unit = "ml", minorStep = 100, reveal = false),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_millilitres_in_litres, "1.5"),
                        options = mathOptions("15", "150", "1500", "15000"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_capacity_jugs_s6_explanation),
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-mass-capacity-compare",
                title = Res.string.learn_measurement_mass_compare_title,
                summary = Res.string.learn_measurement_mass_compare_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_mass_compare_s1_body),
                        visual = NumberLine(from = 0, to = 1200, tickStep = 200, compare = listOf(800, 1000)),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_which_is_heavier, "800", "1"),
                        options = listOf(
                            math("800 g"),
                            math("1 kg"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_mass_compare_s2_explanation),
                        visual = NumberLine(
                            from = 0,
                            to = 1200,
                            tickStep = 200,
                            compare = listOf(800, 1000),
                            reveal = false,
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_mass_compare_s3_body),
                        formula = math("1000 / {b:2} = 500"),
                        visual = Gauge(GaugeKind.DIAL, value = 500, max = 1000, step = 250, unit = "g"),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_mass_compare_s4_question),
                        formula = math("{a:400} * {b:2} = ?"),
                        answer = "800",
                        explanation = words(Res.string.learn_measurement_mass_compare_s4_explanation),
                        // One tin on the scale. The question states there are two of them, so the
                        // figure's job is the reading, not the doubling.
                        visual = Gauge(GaugeKind.DIAL, value = 400, max = 1000, step = 200, unit = "g", reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_mass_compare_s5_question),
                        formula = math("{a:300} + {b:450} = ?"),
                        options = mathOptions("150 ml", "700 ml", "750 ml", "850 ml"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_mass_compare_s5_explanation),
                        visual = Gauge(GaugeKind.JUG, value = 300, max = 1000, step = 200, unit = "ml", minorStep = 100, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_largest),
                        options = mathOptions("900 ml", "1 l", "0.5 l", "90 ml"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_mass_compare_s6_explanation),
                        visual = NumberLine(
                            from = 0,
                            to = 1000,
                            tickStep = 200,
                            compare = listOf(90, 500, 900, 1000),
                            reveal = false,
                        ),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_what_does_scale_read),
                options = mathOptions("200 g", "300 g", "400 g", "600 g"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_mass_q1_explanation),
                visual = Gauge(GaugeKind.DIAL, value = 300, max = 1000, step = 200, unit = "g", minorStep = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_much_in_jug),
                options = mathOptions("200 ml", "300 ml", "400 ml", "500 ml"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_mass_q2_explanation),
                visual = Gauge(GaugeKind.JUG, value = 300, max = 1000, step = 200, unit = "ml", minorStep = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_grams_in_kilograms, "3"),
                options = mathOptions("30", "300", "3000", "30000"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_mass_q3_explanation),
                visual = Steps(terms = listOf(1, 1000), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_mass_q4_prompt),
                options = wordOptions(
                    Res.string.learn_opt_unit_grams,
                    Res.string.learn_opt_unit_kilograms,
                    Res.string.learn_opt_unit_millilitres,
                    Res.string.learn_opt_unit_litres,
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_mass_q4_explanation),
                visual = Gauge(GaugeKind.DIAL, value = 150, max = 1000, step = 200, unit = "g", reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_which_is_heavier, "1200", "1"),
                options = listOf(
                    math("1200 g"),
                    math("1 kg"),
                    words(Res.string.learn_shared_they_equal),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_mass_q5_explanation),
                visual = NumberLine(
                    from = 0,
                    to = 1400,
                    tickStep = 200,
                    compare = listOf(1000, 1200),
                    reveal = false,
                ),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_millilitres_in_litres, "2"),
                options = mathOptions("20", "200", "2000", "20000"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_mass_q6_explanation),
                visual = Steps(terms = listOf(1, 1000), multiply = true),
            ),
        ),
    )

    // --- Metric units -------------------------------------------------------------------------

    private val metricUnits = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "metric-units",
        title = Res.string.learn_unit_measurement_metric_title,
        summary = Res.string.learn_unit_measurement_metric_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "measurement-metric-ladder",
                title = Res.string.learn_measurement_metric_ladder_title,
                summary = Res.string.learn_measurement_metric_ladder_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_metric_ladder_s1_body),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_metric_ladder_s2_body),
                        formula = math("1 km = 1000 m"),
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_smallest),
                        options = mathOptions("1 m", "1 cm", "1 mm", "1 km"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_metric_ladder_s3_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_metric_ladder_s4_body),
                        formula = math("1000 ml = 1 l"),
                        visual = Gauge(GaugeKind.JUG, value = 1000, max = 1000, step = 250, unit = "ml"),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_centimetres_in_metres, "7"),
                        formula = math("{a:7} * {b:100} = ?"),
                        answer = "700",
                        explanation = words(Res.string.learn_measurement_metric_ladder_s5_explanation),
                        visual = Steps(terms = listOf(1, 100), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_metric_ladder_s6_question),
                        options = mathOptions("1.5 kg", "15 kg", "0.15 kg", "150 kg"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_metric_ladder_s6_explanation),
                        visual = Gauge(GaugeKind.DIAL, value = 1500, max = 2000, step = 500, unit = "g", reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-metric-converting",
                title = Res.string.learn_measurement_metric_converting_title,
                summary = Res.string.learn_measurement_metric_converting_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_metric_converting_s1_body),
                        visual = Steps(terms = listOf(1000, 100, 10, 1), multiply = true),
                    ),
                    Worked(
                        problem = math("4500 m = ? km"),
                        lines = listOf(
                            words(Res.string.learn_measurement_metric_converting_s2_l1),
                            words(Res.string.learn_measurement_metric_converting_s2_l2),
                            math("4500 / {b:1000} = 4.5"),
                        ),
                        result = math("4.5 km"),
                        visual = Steps(terms = listOf(1000, 1), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_kilograms_in_grams, "3200"),
                        options = mathOptions("3.2 kg", "32 kg", "0.32 kg", "320 kg"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_metric_converting_s3_explanation),
                        visual = Gauge(GaugeKind.DIAL, value = 3200, max = 4000, step = 1000, unit = "g", reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_metric_converting_s4_body),
                        formula = math("1000 mm = 1 m"),
                        visual = Steps(terms = listOf(1, 10, 1000), multiply = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_metric_converting_s5_question),
                        formula = math("{a:2} * {b:1000} = ?"),
                        answer = "2000",
                        explanation = words(Res.string.learn_measurement_metric_converting_s5_explanation),
                        visual = Steps(terms = listOf(1, 10, 1000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_metric_converting_s6_question),
                        options = wordOptions(
                            Res.string.learn_opt_m_to_cm,
                            Res.string.learn_opt_g_to_kg,
                            Res.string.learn_opt_l_to_ml,
                            Res.string.learn_opt_km_to_m,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_metric_converting_s6_explanation),
                        visual = Steps(terms = listOf(1000, 100, 10, 1), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-metric-decimals",
                title = Res.string.learn_measurement_metric_decimals_title,
                summary = Res.string.learn_measurement_metric_decimals_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_metric_decimals_s1_body),
                        formula = math("2.5 m = 250 cm"),
                        visual = Steps(terms = listOf(2.5, 25, 250), multiply = true),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_grams_in_kilograms, "1.4"),
                        options = mathOptions("14", "140", "1400", "14000"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_metric_decimals_s2_explanation),
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_metric_decimals_s3_body),
                        formula = math("750 g = 0.75 kg"),
                        visual = Gauge(GaugeKind.DIAL, value = 750, max = 1000, step = 250, unit = "g"),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_grams_in_kilograms, "0.4"),
                        formula = math("{a:0.4} * {b:1000} = ?"),
                        answer = "400",
                        explanation = words(Res.string.learn_measurement_metric_decimals_s4_explanation),
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_metric_decimals_s5_question),
                        options = mathOptions("25 ml", "250 ml", "2500 ml", "2.5 ml"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_metric_decimals_s5_explanation),
                        visual = Gauge(GaugeKind.JUG, value = 250, max = 1000, step = 500, unit = "ml", minorStep = 250, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_largest),
                        options = mathOptions("0.9 kg", "950 g", "1.1 kg", "90 g"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_measurement_metric_decimals_s6_explanation),
                        visual = NumberLine(
                            from = 0,
                            to = 1200,
                            tickStep = 200,
                            compare = listOf(90, 900, 950, 1100),
                            reveal = false,
                        ),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = filled(Res.string.learn_t_centimetres_in_metres, "6"),
                options = mathOptions("60", "600", "6000", "0.06"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_metric_q1_explanation),
                visual = Steps(terms = listOf(1, 100), multiply = true),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_kilograms_in_grams, "2500"),
                options = mathOptions("2.5 kg", "25 kg", "0.25 kg", "250 kg"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_metric_q2_explanation),
                visual = Gauge(GaugeKind.DIAL, value = 2500, max = 4000, step = 1000, unit = "g", reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_metric_q3_prompt),
                options = mathOptions("75", "750", "7500", "7.5"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_metric_q3_explanation),
                visual = Gauge(GaugeKind.JUG, value = 750, max = 1000, step = 500, unit = "ml", minorStep = 250, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_smallest),
                options = mathOptions("500 ml", "0.4 l", "1 l", "750 ml"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_metric_q4_explanation),
                visual = NumberLine(
                    from = 0,
                    to = 1000,
                    tickStep = 200,
                    compare = listOf(400, 500, 750, 1000),
                    reveal = false,
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_metric_q5_prompt),
                options = wordOptions(
                    Res.string.learn_opt_m_to_cm,
                    Res.string.learn_opt_g_to_kg,
                    Res.string.learn_opt_ml_to_l,
                    Res.string.learn_opt_m_to_km,
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_metric_q5_explanation),
                visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_metric_q6_prompt),
                options = mathOptions("12", "120", "1200", "12000"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_metric_q6_explanation),
                visual = Steps(terms = listOf(1, 1000), multiply = true),
            ),
        ),
    )

    // --- Speed --------------------------------------------------------------------------------

    private val speed = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "speed",
        title = Res.string.learn_unit_measurement_speed_title,
        summary = Res.string.learn_unit_measurement_speed_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "measurement-speed-rate",
                title = Res.string.learn_measurement_speed_rate_title,
                summary = Res.string.learn_measurement_speed_rate_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_speed_rate_s1_body),
                        visual = Steps(terms = listOf(0, 60, 120, 180)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_speed_rate_s2_body),
                        visual = Steps(terms = listOf(0, 40, 80, 120)),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_speed_rate_s3_question),
                        formula = math("{a:15} * {b:4} = ?"),
                        answer = "60",
                        explanation = words(Res.string.learn_measurement_speed_rate_s3_explanation),
                        // Three hours of the pattern, so the fourth is the learner's to add.
                        visual = Steps(terms = listOf(0, 15, 30, 45)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_speed_rate_s4_body),
                        formula = math("150 / {b:3} = 50"),
                        // A teaching step may show the working, and here the working is the three
                        // equal hops the body describes.
                        visual = Steps(terms = listOf(0, 50, 100, 150)),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_speed_rate_s5_question),
                        formula = math("{a:90} * {b:4} = ?"),
                        options = mathOptions("94 km", "180 km", "270 km", "360 km"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_measurement_speed_rate_s5_explanation),
                        visual = Steps(terms = listOf(0, 90, 180, 270)),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_speed_rate_s6_question),
                        formula = math("240 / {b:80} = ?"),
                        answer = "3",
                        explanation = words(Res.string.learn_measurement_speed_rate_s6_explanation),
                        visual = Steps(terms = listOf(0, 80, 160)),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-speed-formula",
                title = Res.string.learn_measurement_speed_formula_title,
                summary = Res.string.learn_measurement_speed_formula_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_speed_formula_s1_body),
                        // Letters rather than the words, so the card reads the same in every
                        // language; the body says which letter stands for what.
                        formula = math("s = d / t"),
                        visual = Steps(terms = listOf(0, 70, 140, 210)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_speed_formula_s2_body),
                        formula = math("d = s * t"),
                        visual = Steps(terms = listOf(0, 30, 60, 90)),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_speed_formula_s3_question),
                        options = mathOptions("d * s", "d / s", "s / d", "s * t"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_speed_formula_s3_explanation),
                        visual = Steps(terms = listOf(0, 30, 60, 90)),
                    ),
                    Worked(
                        problem = math("s = 60, t = 3, d = ?"),
                        lines = listOf(
                            words(Res.string.learn_measurement_speed_formula_s4_l1),
                            math("d = 60 * 3"),
                            words(Res.string.learn_measurement_speed_formula_s4_l3),
                        ),
                        result = math("180 km"),
                        visual = Steps(terms = listOf(0, 60, 120, 180)),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_speed_formula_s5_question),
                        formula = math("12 / {b:2} = ?"),
                        answer = "6",
                        explanation = words(Res.string.learn_measurement_speed_formula_s5_explanation),
                        visual = RatioBar(parts = listOf(2), reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_speed_formula_s6_question),
                        formula = math("240 / {b:60} = ?"),
                        options = mathOptions("3 h", "4 h", "5 h", "6 h"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_measurement_speed_formula_s6_explanation),
                        visual = Steps(terms = listOf(0, 60, 120, 180)),
                    ),
                ),
            ),
            LessonSpec(
                id = "measurement-speed-units",
                title = Res.string.learn_measurement_speed_units_title,
                summary = Res.string.learn_measurement_speed_units_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_measurement_speed_units_s1_body),
                        formula = math("60 km/h"),
                        visual = Steps(terms = listOf(0, 60, 120, 180)),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_speed_units_s2_body),
                        visual = Steps(terms = listOf(1, 60, 3600), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_measurement_speed_units_s3_question),
                        formula = math("100 / {b:5} = ?"),
                        options = mathOptions("20 m/s", "25 m/s", "50 m/s", "500 m/s"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_speed_units_s3_explanation),
                        visual = RatioBar(parts = listOf(5), reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_measurement_speed_units_s4_body),
                        formula = math("36 km/h = 10 m/s"),
                        visual = Steps(terms = listOf(1, 60, 3600), multiply = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_faster),
                        options = listOf(
                            math("10 m/s"),
                            math("30 km/h"),
                            words(Res.string.learn_shared_they_equal),
                            words(Res.string.learn_shared_you_cannot_tell),
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_measurement_speed_units_s5_explanation),
                        // Only the speed already in km/h. Marking the converted 36 as well is
                        // the whole of the working, and it left nothing to do but read it off.
                        visual = NumberLine(from = 0, to = 40, tickStep = 10, compare = listOf(30), reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_measurement_speed_units_s6_question),
                        formula = math("{a:5} * {b:3} = ?"),
                        answer = "15",
                        explanation = words(Res.string.learn_measurement_speed_units_s6_explanation),
                        visual = Steps(terms = listOf(0, 5, 10)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_speed_q1_prompt),
                options = mathOptions("2 h", "3 h", "4 h", "40 h"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_speed_q1_explanation),
                visual = Steps(terms = listOf(0, 20, 40)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_speed_q2_prompt),
                options = mathOptions("s * t", "s / t", "t / s", "d * t"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_speed_q2_explanation),
                visual = Steps(terms = listOf(0, 40, 80, 120)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_speed_q3_prompt),
                options = mathOptions("70 km/h", "75 km/h", "80 km/h", "1200 km/h"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_speed_q3_explanation),
                visual = RatioBar(parts = listOf(4), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_speed_q4_prompt),
                options = mathOptions("60 km", "60 h", "60 km/h", "60 m"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_measurement_speed_q4_explanation),
                visual = Steps(terms = listOf(0, 60, 120, 180)),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_faster),
                options = listOf(
                    math("20 m/s"),
                    math("60 km/h"),
                    words(Res.string.learn_shared_they_equal),
                    words(Res.string.learn_shared_you_cannot_tell),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_measurement_speed_q5_explanation),
                visual = NumberLine(from = 0, to = 80, tickStep = 20, compare = listOf(60), reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_measurement_speed_q6_prompt),
                options = mathOptions("3 km/h", "4 km/h", "5 km/h", "6 km/h"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_measurement_speed_q6_explanation),
                // The conversion the question turns on: ninety minutes is not 1.9 hours.
                visual = Steps(terms = listOf(1, 60), multiply = true),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(length, time, money, massAndCapacity, metricUnits, speed)
}
