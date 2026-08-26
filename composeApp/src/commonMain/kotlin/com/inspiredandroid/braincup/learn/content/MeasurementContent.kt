package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Clock
import com.inspiredandroid.braincup.learn.LearnVisual.Coins
import com.inspiredandroid.braincup.learn.LearnVisual.Counters
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.Ruler
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Measurement: reading a ruler and a clock, then metric units, perimeter and area. */
internal object MeasurementContent {

    private val lengthTimeMoney = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "length-time-money",
        title = "Length, time and money",
        summary = "Length with a ruler, time on a clock, and counting coins.",
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-measurement-length",
                title = "Measuring length",
                summary = "Count equal units from zero.",
                steps = listOf(
                    Concept(
                        body = "Measuring counts equal units laid end to end.",
                        visual = Ruler(length = 6, span = 10),
                    ),
                    Concept(
                        body = "Start at 0 and read the number at the far end.",
                        visual = Ruler(length = 9, span = 10),
                    ),
                    Choice(
                        question = "How long is this pencil?",
                        options = listOf("8 cm", "9 cm", "10 cm", "You cannot tell"),
                        correctIndex = 1,
                        explanation = "Nine units fit along it.",
                        visual = Ruler(length = 9, span = 10, reveal = false),
                    ),
                    Numeric(
                        question = "A worm is 6 cm. A stick is 4 cm longer. How long is the stick?",
                        answer = "10",
                        explanation = "6 and 4 more.",
                        visual = Counters(groups = listOf(6, 4), reveal = false),
                    ),
                    Concept(
                        body = "Long things need big units, so we switch to metres.",
                        visual = Ruler(length = 8, span = 10, unit = "m"),
                    ),
                    Choice(
                        question = "Which unit fits a classroom?",
                        options = listOf("Millimetres", "Centimetres", "Metres", "Kilometres"),
                        correctIndex = 2,
                        explanation = "A room is a handful of metres across.",
                        visual = Ruler(length = 6, span = 10, unit = "m", reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-measurement-time",
                title = "Telling the time",
                summary = "Hours, half past and quarter past.",
                steps = listOf(
                    Concept(
                        body = "Short hand shows the hour, long hand counts the minutes.",
                        visual = Clock(hour = 3, minute = 0),
                    ),
                    Concept(
                        body = "Half a turn of the long hand is 30 minutes: half past.",
                        visual = Clock(hour = 3, minute = 30),
                    ),
                    Choice(
                        question = "What time is this?",
                        options = listOf("3 o'clock", "Half past 3", "Quarter past 3", "Half past 6"),
                        correctIndex = 1,
                        explanation = "The long hand points straight down.",
                        visual = Clock(hour = 3, minute = 30),
                    ),
                    Numeric(
                        question = "How many minutes are in a quarter of an hour?",
                        answer = "15",
                        explanation = "60 split into four equal parts.",
                        visual = Clock(hour = 12, minute = 15),
                    ),
                    Worked(
                        problem = "It is 2 o'clock. What time in 3 hours?",
                        lines = listOf(
                            "The hour hand moves one number each hour.",
                            "2, then 3, 4, 5.",
                        ),
                        result = "5 o'clock",
                        visual = Clock(hour = 5, minute = 0),
                    ),
                    Choice(
                        question = "What time is this?",
                        options = listOf("Quarter past 9", "Quarter to 9", "Half past 9", "9 o'clock"),
                        correctIndex = 0,
                        explanation = "A quarter turn past the hour.",
                        visual = Clock(hour = 9, minute = 15),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-measurement-money",
                title = "Counting coins",
                summary = "Count value, not coins.",
                steps = listOf(
                    Concept(
                        body = "Coins are worth different amounts, so count value.",
                        visual = Coins(values = listOf(50, 20, 10)),
                    ),
                    Concept(
                        body = "Start with the biggest coin and count on.",
                        visual = Coins(values = listOf(50, 20, 20, 5)),
                    ),
                    Numeric(
                        question = "How many cents is this?",
                        answer = "80",
                        explanation = "50, then 70, then 80.",
                        visual = Coins(values = listOf(50, 20, 10), reveal = false),
                    ),
                    Choice(
                        question = "Which set is worth the most?",
                        options = listOf("Three 10c", "Two 20c", "One 50c", "Four 5c"),
                        correctIndex = 2,
                        explanation = "30c, 40c, 50c and 20c.",
                        visual = Coins(values = listOf(10, 10, 10), reveal = false),
                    ),
                    Worked(
                        problem = "A sticker costs 35c and you pay 50c.",
                        lines = listOf(
                            "Count up from 35 to 40: that is 5c.",
                            "40 up to 50: 10c more.",
                            "5 + 10 = 15.",
                        ),
                        result = "15c change",
                        visual = NumberLine(from = 30, to = 55, tickStep = 5, start = 35, jump = 15, hops = 3),
                    ),
                    Choice(
                        question = "You have 40c and the toy costs 65c. How much more do you need?",
                        options = listOf("15c", "20c", "25c", "30c"),
                        correctIndex = 2,
                        explanation = "From 40 up to 65.",
                        visual = NumberLine(from = 35, to = 70, tickStep = 5, start = 40),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How long is this pen?",
                options = listOf("10 cm", "11 cm", "12 cm", "13 cm"),
                correctIndex = 2,
                explanation = "Read the far end against the ruler.",
                visual = Ruler(length = 12, span = 14, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many minutes have passed since the hour?",
                options = listOf("15", "30", "45", "60"),
                correctIndex = 1,
                explanation = "Half a turn of the long hand.",
                visual = Clock(hour = 12, minute = 30),
            ),
            QuizQuestion(
                prompt = "What time is this?",
                options = listOf("Half past 8", "8 o'clock", "Quarter past 8", "12 o'clock"),
                correctIndex = 1,
                explanation = "The long hand has not moved off 12.",
                visual = Clock(hour = 8, minute = 0),
            ),
            QuizQuestion(
                prompt = "How much is this altogether?",
                options = listOf("40c", "45c", "50c", "25c"),
                correctIndex = 1,
                explanation = "20, 40, then 45.",
                visual = Coins(values = listOf(20, 20, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = "Which unit fits the distance between two towns?",
                options = listOf("Centimetres", "Metres", "Kilometres", "Millimetres"),
                correctIndex = 2,
                explanation = "Thousands of metres at a time.",
                visual = Ruler(length = 7, span = 10, unit = "km", reveal = false),
            ),
            QuizQuestion(
                prompt = "You pay 50c for a 30c pencil. What change do you get?",
                options = listOf("10c", "15c", "20c", "25c"),
                correctIndex = 2,
                explanation = "From 30 up to 50.",
                visual = NumberLine(from = 25, to = 55, tickStep = 5, start = 30),
            ),
        ),
    )

    private val unitsAndArea = learnUnit(
        topic = MathTopic.MEASUREMENT,
        urlSlug = "units-and-area",
        title = "Metric units, perimeter and area",
        summary = "Converting metric units, perimeter and area.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-measurement-units",
                title = "Metric units",
                summary = "Every conversion is a jump of ten.",
                steps = listOf(
                    Concept(
                        body = "Metric units step by ten, so converting is only ever x10 or /10.",
                        formula = "10 mm = 1 cm",
                        visual = Ruler(length = 10, span = 10),
                    ),
                    Concept(
                        body = "A smaller unit means more of them, so multiply.",
                        formula = "2.5 m = 250 cm",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Choice(
                        question = "How many millimetres are in 7 cm?",
                        options = listOf("0.7", "7", "70", "700"),
                        correctIndex = 2,
                        explanation = "Ten millimetres inside every centimetre.",
                        visual = Ruler(length = 7, span = 10, reveal = false),
                    ),
                    Numeric(
                        question = "A ribbon is 1.2 m. How many centimetres is that?",
                        answer = "120",
                        explanation = "1 m is 100 cm.",
                        visual = Steps(terms = listOf(1, 10, 100), multiply = true),
                    ),
                    Concept(
                        body = "Mass and capacity follow the same pattern.",
                        formula = "1000 g = 1 kg",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Choice(
                        question = "A bottle holds 1.5 litres. How many millilitres?",
                        options = listOf("15", "150", "1500", "15000"),
                        correctIndex = 2,
                        explanation = "1000 ml in a litre.",
                        visual = Steps(terms = listOf(1, 1000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-perimeter",
                title = "Perimeter",
                summary = "The walk all the way around.",
                steps = listOf(
                    Concept(
                        body = "Perimeter is the distance around the outside.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false, showPerimeter = true),
                    ),
                    Concept(
                        body = "Opposite sides match, so add one of each and double it.",
                        formula = "P = 2 x (length + width)",
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Worked(
                        problem = "A rectangle is 8 cm by 5 cm.",
                        lines = listOf(
                            "8 + 5 = 13.",
                            "Every side has an equal partner.",
                            "13 x 2 = 26.",
                        ),
                        result = "Perimeter = 26 cm",
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Numeric(
                        question = "What is the perimeter of this square, in cm?",
                        answer = "36",
                        explanation = "Four sides of 9.",
                        visual = AreaGrid(cols = 9, rows = 9, showArea = false, showPerimeter = true, reveal = false),
                    ),
                    Choice(
                        question = "Which shape has a perimeter of 20 cm?",
                        options = listOf("A 6 by 4 rectangle", "A 5 by 5 square", "Both of them", "Neither"),
                        correctIndex = 2,
                        explanation = "6 + 4 doubled is 20, and 4 x 5 is 20 as well.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false, reveal = false),
                    ),
                    Concept(
                        body = "Perimeter is a length, so it is never measured in square units.",
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, showPerimeter = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-area",
                title = "Area of rectangles",
                summary = "Count the squares, or multiply instead.",
                steps = listOf(
                    Concept(
                        body = "Area counts the unit squares that cover a shape.",
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Concept(
                        body = "The squares sit in equal rows, so multiply instead of counting.",
                        formula = "A = length x width",
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Numeric(
                        question = "How many squares cover this rug?",
                        answer = "18",
                        explanation = "6 rows of 3.",
                        visual = AreaGrid(cols = 6, rows = 3, reveal = false),
                    ),
                    Choice(
                        question = "Which unit belongs to an area?",
                        options = listOf("cm", "square cm", "cubic cm", "ml"),
                        correctIndex = 1,
                        explanation = "Area counts squares, so the unit is squared.",
                        visual = AreaGrid(cols = 4, rows = 4),
                    ),
                    Worked(
                        problem = "An L-shape: a 5 by 2 rectangle joined to a 3 by 2 one.",
                        lines = listOf(
                            "Split it into rectangles you know.",
                            "5 x 2 = 10.",
                            "3 x 2 = 6.",
                            "10 + 6.",
                        ),
                        result = "Area = 16 square cm",
                        visual = AreaGrid(cols = 5, rows = 2),
                    ),
                    Choice(
                        question = "The area is 24 square cm and one side is 4 cm. How long is the other?",
                        options = listOf("4 cm", "6 cm", "8 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = "24 divided by 4.",
                        visual = AreaGrid(cols = 6, rows = 4, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many centimetres are in 3 metres?",
                options = listOf("30", "300", "3000", "0.03"),
                correctIndex = 1,
                explanation = "1 m = 100 cm.",
                visual = Steps(terms = listOf(1, 10, 100), multiply = true),
            ),
            QuizQuestion(
                prompt = "How many grams are in 2.5 kg?",
                options = listOf("250", "2500", "25", "25000"),
                correctIndex = 1,
                explanation = "1 kg = 1000 g.",
                visual = Steps(terms = listOf(1, 1000), multiply = true),
            ),
            QuizQuestion(
                prompt = "What is the perimeter of this rectangle?",
                options = listOf("10 cm", "20 cm", "21 cm", "14 cm"),
                correctIndex = 1,
                explanation = "(7 + 3) doubled.",
                visual = AreaGrid(cols = 7, rows = 3, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the area of this rectangle?",
                options = listOf("10 square cm", "20 square cm", "21 square cm", "14 square cm"),
                correctIndex = 2,
                explanation = "7 rows of 3.",
                visual = AreaGrid(cols = 7, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "A square has a perimeter of 32 cm. How long is one side?",
                options = listOf("4 cm", "6 cm", "8 cm", "16 cm"),
                correctIndex = 2,
                explanation = "32 divided by 4.",
                visual = AreaGrid(cols = 8, rows = 8, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which measurement is an area?",
                options = listOf("12 m", "12 square m", "12 ml", "12 kg"),
                correctIndex = 1,
                explanation = "Only the squared unit measures a surface.",
                visual = AreaGrid(cols = 4, rows = 3),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(lengthTimeMoney, unitsAndArea)
}
