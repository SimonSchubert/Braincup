package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.CircleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.Clock
import com.inspiredandroid.braincup.learn.LearnVisual.Coins
import com.inspiredandroid.braincup.learn.LearnVisual.Counters
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.PlaceValue
import com.inspiredandroid.braincup.learn.LearnVisual.Polygon
import com.inspiredandroid.braincup.learn.LearnVisual.Ruler
import com.inspiredandroid.braincup.learn.LearnVisual.Solid
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.TenFrame
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.learnUnit

/** Grades 1-2: counting, first sums, everyday measures and the shapes around us. */
internal object Grade1To2Content {

    private val level = GradeLevel.GRADES_1_2

    private val arithmetic = learnUnit(
        level = level,
        topic = MathTopic.ARITHMETIC,
        summary = "Counting on, sums to 20, and tens and ones.",
        lessons = listOf(
            LessonSpec(
                id = "g12-arithmetic-counting",
                title = "Counting to 100",
                summary = "Find any number on the number line.",
                steps = listOf(
                    Concept(
                        body = "One step right is one more. One step left is one less.",
                        visual = NumberLine(from = 0, to = 10, start = 6, jump = 1),
                    ),
                    Concept(
                        body = "Counting on is adding. Watch three hops land on ten.",
                        formula = "7 + 3 = 10",
                        visual = NumberLine(from = 0, to = 12, start = 7, jump = 3, hops = 3),
                    ),
                    Choice(
                        question = "Start at 12 and hop on 4. Where do you land?",
                        options = listOf("14", "15", "16", "17"),
                        correctIndex = 2,
                        explanation = "13, 14, 15, 16 - four hops to the right.",
                        visual = NumberLine(from = 10, to = 20, start = 12, jump = 4, hops = 4, reveal = false),
                    ),
                    Concept(
                        body = "Skip counting jumps by the same amount every time.",
                        visual = Steps(terms = listOf(10, 20, 30, 40, 50)),
                    ),
                    Numeric(
                        question = "30, 40, 50, ... what comes next?",
                        answer = "60",
                        explanation = "Every jump adds ten.",
                        visual = Steps(terms = listOf(30, 40, 50)),
                    ),
                    Choice(
                        question = "Which number comes just before 40?",
                        options = listOf("30", "39", "41", "44"),
                        correctIndex = 1,
                        explanation = "One step to the left of 40.",
                        visual = NumberLine(from = 35, to = 45, start = 40),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-addsub",
                title = "Adding and taking away",
                summary = "Make ten, and count back to subtract.",
                steps = listOf(
                    Concept(
                        body = "Adding pushes two groups into one.",
                        formula = "4 + 3 = 7",
                        visual = Counters(groups = listOf(4, 3)),
                    ),
                    Worked(
                        problem = "6 + 7 = ?",
                        lines = listOf(
                            "6 needs 4 more to fill the ten-frame.",
                            "Split the 7 into 4 and 3.",
                            "6 + 4 = 10.",
                            "10 + 3 = 13.",
                        ),
                        result = "6 + 7 = 13",
                        visual = TenFrame(filled = 6, added = 7),
                    ),
                    Choice(
                        question = "For 8 + 5, how do you split the 5 to make ten?",
                        options = listOf("1 and 4", "2 and 3", "3 and 2", "4 and 1"),
                        correctIndex = 1,
                        explanation = "8 needs 2 to reach ten, leaving 3.",
                        visual = TenFrame(filled = 8, added = 5, reveal = false),
                    ),
                    Concept(
                        body = "Subtracting hops back down the line.",
                        formula = "15 - 8 = 7",
                        visual = NumberLine(from = 0, to = 16, start = 15, jump = -8, hops = 8),
                    ),
                    Numeric(
                        question = "15 - 8 = ?",
                        answer = "7",
                        explanation = "From 8 up to 10 is 2, then 10 up to 15 is 5.",
                        visual = NumberLine(from = 0, to = 16, start = 15),
                    ),
                    Concept(
                        body = "4, 9 and 13 belong together: learn one fact and you get four.",
                        formula = "4 + 9 = 13 and 13 - 9 = 4",
                        visual = Counters(groups = listOf(4, 9)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-tens",
                title = "Tens and ones",
                summary = "Build and compare two-digit numbers.",
                steps = listOf(
                    Concept(
                        body = "A two-digit number is rods of ten plus loose ones.",
                        formula = "37 = 30 + 7",
                        visual = PlaceValue(tens = 3, ones = 7),
                    ),
                    Choice(
                        question = "How many tens are here?",
                        options = listOf("6", "8", "60", "68"),
                        correctIndex = 0,
                        explanation = "Six full rods, and eight cubes left over.",
                        visual = PlaceValue(tens = 6, ones = 8, reveal = false),
                    ),
                    Worked(
                        problem = "25 + 13 = ?",
                        lines = listOf(
                            "Tens: 20 + 10 = 30.",
                            "Ones: 5 + 3 = 8.",
                            "Put them together.",
                        ),
                        result = "25 + 13 = 38",
                        visual = PlaceValue(tens = 3, ones = 8),
                    ),
                    Numeric(
                        question = "What number is built here?",
                        answer = "42",
                        explanation = "4 rods is 40, plus 2 cubes.",
                        visual = PlaceValue(tens = 4, ones = 2, reveal = false),
                    ),
                    Concept(
                        body = "Compare the rods first: 74 has seven, 47 has only four.",
                        visual = PlaceValue(tens = 7, ones = 4),
                    ),
                    Choice(
                        question = "Which number is the largest?",
                        options = listOf("58", "85", "61", "49"),
                        correctIndex = 1,
                        explanation = "8 rods beat 6, 5 and 4.",
                        visual = PlaceValue(tens = 8, ones = 5, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What comes next?",
                options = listOf("41", "45", "50", "60"),
                correctIndex = 2,
                explanation = "The jumps are tens.",
                visual = Steps(terms = listOf(20, 30, 40)),
            ),
            QuizQuestion(
                prompt = "9 + 6 = ?",
                options = listOf("14", "15", "16", "13"),
                correctIndex = 1,
                explanation = "9 needs 1 to make ten, leaving 5.",
                visual = TenFrame(filled = 9, added = 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "14 - 6 = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "Hop back six from 14.",
                visual = NumberLine(from = 0, to = 16, start = 14),
            ),
            QuizQuestion(
                prompt = "How many loose ones are here?",
                options = listOf("5", "3", "50", "53"),
                correctIndex = 1,
                explanation = "Five rods and three cubes make 53.",
                visual = PlaceValue(tens = 5, ones = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which number is smaller, 62 or 26?",
                options = listOf("62", "26", "They are equal", "You cannot tell"),
                correctIndex = 1,
                explanation = "26 has only two rods.",
                visual = PlaceValue(tens = 2, ones = 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "What comes after 25?",
                options = listOf("26", "30", "35", "40"),
                correctIndex = 1,
                explanation = "Each jump adds five.",
                visual = Steps(terms = listOf(15, 20, 25)),
            ),
        ),
    )

    private val measurement = learnUnit(
        level = level,
        topic = MathTopic.MEASUREMENT,
        summary = "Length with a ruler, time on a clock, and counting coins.",
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

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Flat shapes, solid shapes, and splitting things into equal parts.",
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-flat-shapes",
                title = "Flat shapes",
                summary = "Count the sides to name the shape.",
                steps = listOf(
                    Concept(
                        body = "Shapes are named by how many straight sides they have.",
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = "Every side meets another at a corner, so the counts always match.",
                        visual = Polygon(sides = 5),
                    ),
                    Choice(
                        question = "How many sides does this shape have?",
                        options = listOf("4", "5", "6", "8"),
                        correctIndex = 1,
                        explanation = "Count the corners as they light up: five.",
                        visual = Polygon(sides = 5, reveal = false),
                    ),
                    Numeric(
                        question = "How many corners does this shape have?",
                        answer = "6",
                        explanation = "Six sides means six corners.",
                        visual = Polygon(sides = 6, reveal = false),
                    ),
                    Concept(
                        body = "A circle curves the whole way round: no sides, no corners.",
                        visual = CircleFigure(showRadius = false),
                    ),
                    Choice(
                        question = "Which is NOT true of every rectangle?",
                        options = listOf(
                            "It has four sides",
                            "It has four square corners",
                            "All four sides are equal",
                            "Opposite sides are equal",
                        ),
                        correctIndex = 2,
                        explanation = "Only squares have all four sides the same.",
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-solid-shapes",
                title = "Solid shapes",
                summary = "Faces, edges and corners you can hold.",
                steps = listOf(
                    Concept(
                        body = "Solid shapes take up space.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "Faces are the flat parts, edges are where two faces meet.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Choice(
                        question = "Which solid rolls but still has two flat circles?",
                        options = listOf("Sphere", "Cube", "Cylinder", "Cone"),
                        correctIndex = 2,
                        explanation = "It rolls on its curved side and stands on either circle.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Numeric(
                        question = "How many faces does this solid have?",
                        answer = "6",
                        explanation = "Top, bottom and four sides.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = "A cone rises from one circle to a single point.",
                        visual = Solid(kind = SolidKind.CONE, counts = true),
                    ),
                    Choice(
                        question = "A tin of soup is closest to which solid?",
                        options = listOf("Cube", "Cone", "Sphere", "Cylinder"),
                        correctIndex = 3,
                        explanation = "Two circular ends and a curved side.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-equal-parts",
                title = "Halves and quarters",
                summary = "Fair shares are equal shares.",
                steps = listOf(
                    Concept(
                        body = "A fraction splits something into equal parts.",
                        visual = Fraction(numerator = 1, denominator = 2),
                    ),
                    Concept(
                        body = "The more parts you cut, the smaller each one gets.",
                        formula = "1 half = 2 quarters",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 2 to 4),
                    ),
                    Choice(
                        question = "You eat 2 of the 4 equal pieces. How much is that?",
                        options = listOf("A quarter", "A half", "Three quarters", "All of it"),
                        correctIndex = 1,
                        explanation = "Two of four fills exactly half the bar.",
                        visual = Fraction(numerator = 2, denominator = 4),
                    ),
                    Numeric(
                        question = "A pizza is cut into 4 and you eat 1. How many slices are left?",
                        answer = "3",
                        explanation = "4 take away 1.",
                        visual = Fraction(numerator = 1, denominator = 4),
                    ),
                    Concept(
                        body = "Half of a half is a quarter.",
                        visual = Fraction(numerator = 1, denominator = 4),
                    ),
                    Choice(
                        question = "Which is the bigger share of the same cake?",
                        options = listOf("A quarter", "A half", "They are the same", "It depends"),
                        correctIndex = 1,
                        explanation = "One of two beats one of four.",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 1 to 4),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many sides does this shape have?",
                options = listOf("4", "5", "6", "8"),
                correctIndex = 1,
                explanation = "A pentagon has five.",
                visual = Polygon(sides = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which shape has no corners at all?",
                options = listOf("Triangle", "Square", "Circle", "Hexagon"),
                correctIndex = 2,
                explanation = "One smooth curve, so no two sides ever meet.",
                visual = CircleFigure(showRadius = false),
            ),
            QuizQuestion(
                prompt = "How many edges does this solid have?",
                options = listOf("6", "8", "10", "12"),
                correctIndex = 3,
                explanation = "Four on top, four underneath and four uprights.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is this solid called?",
                options = listOf("Cylinder", "Cone", "Sphere", "Cube"),
                correctIndex = 1,
                explanation = "One circle rising to a point.",
                visual = Solid(kind = SolidKind.CONE, reveal = false),
            ),
            QuizQuestion(
                prompt = "How much of the bar is shaded?",
                options = listOf("A quarter", "A half", "A third", "The whole bar"),
                correctIndex = 1,
                explanation = "One of two equal parts.",
                visual = Fraction(numerator = 1, denominator = 2),
            ),
            QuizQuestion(
                prompt = "Which of these is NOT true of a square?",
                options = listOf("4 equal sides", "4 square corners", "4 corners", "Curved edges"),
                correctIndex = 3,
                explanation = "Every side of a square is straight.",
                visual = Polygon(sides = 4, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, measurement, geometry)
}
