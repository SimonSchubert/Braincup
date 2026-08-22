package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object MeasurementContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "measurement-units",
            topic = MathTopic.MEASUREMENT,
            title = "Units & Conversions",
            summary = "Metric prefixes, and which way to multiply when you switch units.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Metric units are built from one base unit plus a prefix that scales " +
                        "it by a power of ten. Learn the prefixes once and every unit follows.",
                    formula = "kilo = ×1000   centi = ÷100   milli = ÷1000",
                    visual = LearnVisual.RULER,
                ),
                LessonStep.Concept(
                    body = "Converting to a smaller unit gives a bigger number, because it takes " +
                        "more of them to cover the same amount. Converting to a bigger unit gives " +
                        "a smaller number. Check the direction before you divide.",
                    formula = "1 km = 1000 m = 100 000 cm = 1 000 000 mm",
                ),
                LessonStep.Choice(
                    question = "How many centimetres are in 2.5 m?",
                    options = listOf("25", "250", "2500", "0.025"),
                    correctIndex = 1,
                    explanation = "Centimetres are smaller than metres, so multiply: 2.5 × 100 = 250 cm.",
                ),
                LessonStep.Worked(
                    problem = "Convert 4500 g to kilograms.",
                    lines = listOf(
                        "1 kg = 1000 g",
                        "Kilograms are the bigger unit, so divide",
                        "4500 ÷ 1000",
                    ),
                    result = "4.5 kg",
                ),
                LessonStep.Numeric(
                    question = "How many millilitres are in 1.2 litres?",
                    answer = "1200",
                    explanation = "1 L = 1000 mL, and millilitres are smaller, so 1.2 × 1000 = 1200 mL.",
                ),
                LessonStep.Choice(
                    question = "Which of these masses is the heaviest?",
                    options = listOf("900 g", "1.2 kg", "0.85 kg", "1100 g"),
                    correctIndex = 1,
                    explanation = "Convert everything to grams: 900 g, 1200 g, 850 g, 1100 g. " +
                        "1.2 kg = 1200 g wins.",
                ),
            ),
        ),
        LearnLesson(
            id = "measurement-area-volume",
            topic = MathTopic.MEASUREMENT,
            title = "Perimeter, Area & Volume",
            summary = "Measuring the edge, the surface and the space inside a shape.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Perimeter is the distance all the way around a shape, measured in " +
                        "length units. Area is the surface it covers, in square units. Volume is " +
                        "the space it fills, in cubic units.",
                    formula = "cm   |   cm²   |   cm³",
                    visual = LearnVisual.AREA_RECTANGLE,
                ),
                LessonStep.Concept(
                    body = "The everyday formulas all come from counting rows of unit squares or " +
                        "layers of unit cubes.",
                    formula = "P = 2(l + w)    A = l × w    V = l × w × h",
                ),
                LessonStep.Worked(
                    problem = "A rectangle measures 8 cm by 3 cm. Find its perimeter and area.",
                    lines = listOf(
                        "P = 2(l + w) = 2(8 + 3) = 2 × 11",
                        "A = l × w = 8 × 3",
                    ),
                    result = "P = 22 cm,  A = 24 cm²",
                ),
                LessonStep.Numeric(
                    question = "A room is 5 m long and 4 m wide. What is its area, in square metres?",
                    answer = "20",
                    explanation = "A = 5 × 4 = 20 m².",
                ),
                LessonStep.Choice(
                    question = "A triangle has base 10 cm and height 6 cm. What is its area?",
                    options = listOf("60 cm²", "30 cm²", "16 cm²", "32 cm²"),
                    correctIndex = 1,
                    explanation = "A triangle is half of the rectangle around it: A = ½ × 10 × 6 = 30 cm².",
                ),
                LessonStep.Choice(
                    question = "What is the volume of a cube with side 3 cm?",
                    options = listOf("9 cm³", "27 cm³", "18 cm³", "12 cm³"),
                    correctIndex = 1,
                    explanation = "V = 3 × 3 × 3 = 27 cm³. (9 cm² would be the area of one face.)",
                ),
            ),
        ),
        LearnLesson(
            id = "measurement-rates",
            topic = MathTopic.MEASUREMENT,
            title = "Time, Speed & Rates",
            summary = "Comparing two different units — and the triangle that links them.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A rate compares two quantities with different units: kilometres per " +
                        "hour, euros per kilogram, words per minute. The word \"per\" is a division sign.",
                    formula = "speed = distance ÷ time",
                ),
                LessonStep.Concept(
                    body = "One relationship, rearranged three ways. Cover the quantity you want " +
                        "and the other two show you what to do.",
                    formula = "v = d / t    d = v × t    t = d / v",
                ),
                LessonStep.Worked(
                    problem = "A train covers 180 km in 2 hours. What is its average speed?",
                    lines = listOf(
                        "v = d / t",
                        "180 ÷ 2",
                    ),
                    result = "90 km/h",
                ),
                LessonStep.Numeric(
                    question = "You cycle at 15 km/h for 3 hours. How many kilometres do you cover?",
                    answer = "45",
                    explanation = "d = v × t = 15 × 3 = 45 km.",
                ),
                LessonStep.Choice(
                    question = "45 minutes written as a decimal number of hours is…",
                    options = listOf("0.45 h", "0.75 h", "4.5 h", "0.65 h"),
                    correctIndex = 1,
                    explanation = "45 ÷ 60 = 0.75. Time is not decimal — 45 minutes is three quarters of an hour.",
                ),
                LessonStep.Choice(
                    question = "3 kg of apples cost 7.50 €. What is the price per kilogram?",
                    options = listOf("2.50 €", "2.25 €", "22.50 €", "3.75 €"),
                    correctIndex = 0,
                    explanation = "\"Per kilogram\" means divide by the kilograms: 7.50 ÷ 3 = 2.50 €.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.MEASUREMENT,
        questions = listOf(
            QuizQuestion(
                prompt = "How many metres are in 3.5 km?",
                options = listOf("35", "350", "3500", "35 000"),
                correctIndex = 2,
                explanation = "1 km = 1000 m, so 3.5 × 1000 = 3500 m.",
            ),
            QuizQuestion(
                prompt = "A rectangle is 12 m by 7 m. What is its perimeter?",
                options = listOf("19 m", "38 m", "84 m", "42 m"),
                correctIndex = 1,
                explanation = "P = 2(12 + 7) = 2 × 19 = 38 m. (84 m² would be the area.)",
            ),
            QuizQuestion(
                prompt = "A tank measures 2 m × 3 m × 1.5 m. What is its volume?",
                options = listOf("6.5 m³", "9 m³", "6 m³", "11 m³"),
                correctIndex = 1,
                explanation = "V = 2 × 3 × 1.5 = 9 m³.",
            ),
            QuizQuestion(
                prompt = "A car travels 240 km in 3 hours. What is its average speed?",
                options = listOf("60 km/h", "80 km/h", "720 km/h", "90 km/h"),
                correctIndex = 1,
                explanation = "v = 240 ÷ 3 = 80 km/h.",
            ),
            QuizQuestion(
                prompt = "How long does 150 km take at 50 km/h?",
                options = listOf("2 h", "2.5 h", "3 h", "7500 h"),
                correctIndex = 2,
                explanation = "t = d / v = 150 ÷ 50 = 3 hours.",
            ),
            QuizQuestion(
                prompt = "A triangle has base 14 cm and height 5 cm. What is its area?",
                options = listOf("70 cm²", "35 cm²", "19 cm²", "28 cm²"),
                correctIndex = 1,
                explanation = "A = ½ × 14 × 5 = 35 cm².",
            ),
            QuizQuestion(
                prompt = "Which is the same as 2500 mL?",
                options = listOf("0.25 L", "2.5 L", "25 L", "250 L"),
                correctIndex = 1,
                explanation = "1000 mL = 1 L, so 2500 ÷ 1000 = 2.5 L.",
            ),
            QuizQuestion(
                prompt = "Which container holds the most?",
                options = listOf("750 mL", "0.8 L", "0.65 L", "700 mL"),
                correctIndex = 1,
                explanation = "In millilitres: 750, 800, 650, 700. 0.8 L = 800 mL is the largest.",
            ),
        ),
    )
}
