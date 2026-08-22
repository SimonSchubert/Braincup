package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AngleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.DecimalGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.Pictogram
import com.inspiredandroid.braincup.learn.LearnVisual.Polygon
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Ruler
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.Symmetry
import com.inspiredandroid.braincup.learn.LearnVisual.Tally
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Grades 3-5: times tables, fractions and decimals, metric units, angles and first statistics. */
internal object Grade3To5Content {

    private val level = GradeLevel.GRADES_3_5

    private val arithmetic = learnUnit(
        level = level,
        topic = MathTopic.ARITHMETIC,
        summary = "Multiplication and division, equivalent fractions and decimals.",
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-multiplication",
                title = "Multiplication as an array",
                summary = "Rows and columns make the times tables make sense.",
                steps = listOf(
                    Concept(
                        body = "Multiplication is equal rows.",
                        formula = "4 x 6 = 24",
                        visual = ArrayDots(rows = 4, cols = 6),
                    ),
                    Concept(
                        body = "Turn the array on its side and the total is unchanged, so half the facts come free.",
                        formula = "4 x 6 = 6 x 4",
                        visual = ArrayDots(rows = 6, cols = 4),
                    ),
                    Choice(
                        question = "How many dots are here?",
                        options = listOf("18", "21", "24", "27"),
                        correctIndex = 1,
                        explanation = "Seven rows of three.",
                        visual = ArrayDots(rows = 7, cols = 3, reveal = false),
                    ),
                    Worked(
                        problem = "6 x 8 = ?",
                        lines = listOf(
                            "Break it into facts you already know.",
                            "5 x 8 = 40.",
                            "1 x 8 = 8.",
                            "40 + 8 = 48.",
                        ),
                        result = "6 x 8 = 48",
                        visual = ArrayDots(rows = 6, cols = 8),
                    ),
                    Numeric(
                        question = "A tray holds 4 rows of 7 cakes. How many cakes?",
                        answer = "28",
                        explanation = "4 x 7.",
                        visual = ArrayDots(rows = 4, cols = 7, reveal = false),
                    ),
                    Concept(
                        body = "Division splits the array back into rows.",
                        formula = "28 / 7 = 4",
                        visual = ArrayDots(rows = 4, cols = 7),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-arithmetic-fractions",
                title = "Fractions that match",
                summary = "Equivalent fractions, seen side by side.",
                steps = listOf(
                    Concept(
                        body = "The bottom number cuts the bar; the top number takes the pieces.",
                        visual = Fraction(numerator = 3, denominator = 4),
                    ),
                    Concept(
                        body = "Cut every piece in half and you get twice as many, each half the size. Same shading.",
                        formula = "1/2 = 4/8",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 4 to 8),
                    ),
                    Choice(
                        question = "Which fraction shades the same amount as this bar?",
                        options = listOf("3/4", "4/6", "2/6", "6/3"),
                        correctIndex = 1,
                        explanation = "Double both numbers of 2/3.",
                        visual = Fraction(numerator = 2, denominator = 3),
                    ),
                    Concept(
                        body = "Same bottom number? Then just compare the tops.",
                        visual = Fraction(numerator = 3, denominator = 5, compare = 2 to 5),
                    ),
                    Numeric(
                        question = "A pizza has 8 slices and you eat 3. How many are left?",
                        answer = "5",
                        explanation = "8 take away 3.",
                        visual = Fraction(numerator = 3, denominator = 8),
                    ),
                    Choice(
                        question = "Which bar is shaded more?",
                        options = listOf("2/3", "3/4", "They are equal", "You cannot compare them"),
                        correctIndex = 1,
                        explanation = "In twelfths that is 8/12 against 9/12.",
                        visual = Fraction(numerator = 2, denominator = 3, compare = 3 to 4),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-arithmetic-decimals",
                title = "Decimals",
                summary = "Tenths and hundredths on a hundred-square.",
                steps = listOf(
                    Concept(
                        body = "Place value carries on past the point: tenths, then hundredths.",
                        formula = "0.35 = 3 tenths + 5 hundredths",
                        visual = DecimalGrid(value = 0.35),
                    ),
                    Concept(
                        body = "A decimal and a fraction can be the same amount, written differently.",
                        formula = "1/4 = 0.25",
                        visual = DecimalGrid(value = 0.25),
                    ),
                    Choice(
                        question = "Which grid is shaded more?",
                        options = listOf("0.4", "0.35", "They are equal", "You cannot tell"),
                        correctIndex = 0,
                        explanation = "Four tenths beats three tenths. Extra digits do not make a number bigger.",
                        visual = DecimalGrid(value = 0.4, compare = 0.35),
                    ),
                    Worked(
                        problem = "2.4 + 1.35 = ?",
                        lines = listOf(
                            "Line up the points, not the last digits.",
                            "Write 2.4 as 2.40.",
                            "Hundredths 0 + 5, tenths 4 + 3.",
                            "Wholes 2 + 1.",
                        ),
                        result = "2.4 + 1.35 = 3.75",
                        visual = DecimalGrid(value = 0.75),
                    ),
                    Numeric(
                        question = "Write the shaded amount as a decimal.",
                        answer = "0.75",
                        explanation = "75 of the 100 squares.",
                        visual = DecimalGrid(value = 0.75, reveal = false),
                    ),
                    Choice(
                        question = "What does 3.05 euro mean?",
                        options = listOf("3 euro 50 cents", "3 euro 5 cents", "30 euro 5 cents", "3 euro 500 cents"),
                        correctIndex = 1,
                        explanation = "The 0 holds the tenths place, so the 5 is hundredths.",
                        visual = DecimalGrid(value = 0.05),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many dots are here?",
                options = listOf("54", "56", "48", "64"),
                correctIndex = 1,
                explanation = "8 rows of 7.",
                visual = ArrayDots(rows = 8, cols = 7, reveal = false),
            ),
            QuizQuestion(
                prompt = "72 divided by 9 = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "Nine rows of eight.",
                visual = ArrayDots(rows = 9, cols = 8, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which fraction shades the same as the bar?",
                options = listOf("3/5", "4/8", "2/5", "5/12"),
                correctIndex = 1,
                explanation = "4 is half of 8.",
                visual = Fraction(numerator = 1, denominator = 2),
            ),
            QuizQuestion(
                prompt = "Which grid is shaded more?",
                options = listOf("0.5", "0.45", "They match", "You cannot tell"),
                correctIndex = 0,
                explanation = "Five tenths beats four tenths and five hundredths.",
                visual = DecimalGrid(value = 0.5, compare = 0.45),
            ),
            QuizQuestion(
                prompt = "3/5 + 1/5 = ?",
                options = listOf("4/10", "4/5", "3/10", "4/25"),
                correctIndex = 1,
                explanation = "The pieces are the same size, so add the tops only.",
                visual = Fraction(numerator = 3, denominator = 5),
            ),
            QuizQuestion(
                prompt = "Write this shaded amount as a fraction.",
                options = listOf("1/2", "1/4", "2/5", "1/5"),
                correctIndex = 1,
                explanation = "25 hundredths is one quarter.",
                visual = DecimalGrid(value = 0.25, reveal = false),
            ),
        ),
    )

    private val measurement = learnUnit(
        level = level,
        topic = MathTopic.MEASUREMENT,
        summary = "Converting metric units, perimeter and area.",
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

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Angles and turns, families of quadrilaterals, and symmetry.",
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-angles",
                title = "Angles and turns",
                summary = "Degrees measure turn, not length.",
                steps = listOf(
                    Concept(
                        body = "An angle measures turn. Longer arms do not make a bigger angle.",
                        visual = AngleFigure(degrees = 50),
                    ),
                    Concept(
                        body = "A quarter turn is 90 degrees: a right angle.",
                        formula = "full turn = 360 degrees",
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = "What kind of angle is this?",
                        options = listOf("obtuse", "acute", "reflex", "straight"),
                        correctIndex = 1,
                        explanation = "Acute angles are under 90 degrees.",
                        visual = AngleFigure(degrees = 40, reveal = false),
                    ),
                    Numeric(
                        question = "Angles on a straight line. How many degrees is the other one?",
                        answer = "50",
                        explanation = "180 - 130.",
                        visual = AngleFigure(degrees = 130, supplement = true, reveal = false),
                    ),
                    Concept(
                        body = "The three angles inside any triangle always add to 180.",
                        formula = "a + b + c = 180",
                        visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
                    ),
                    Choice(
                        question = "A triangle has angles of 40 and 75 degrees. What is the third?",
                        options = listOf("55", "65", "75", "105"),
                        correctIndex = 1,
                        explanation = "180 - 40 - 75.",
                        visual = RightTriangle(a = 5, b = 3, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-quadrilaterals",
                title = "Sorting quadrilaterals",
                summary = "Parallel and equal sides decide the name.",
                steps = listOf(
                    Concept(
                        body = "Any four straight sides makes a quadrilateral.",
                        visual = Polygon(sides = 4),
                    ),
                    Concept(
                        body = "A parallelogram has two pairs of parallel sides; a trapezium has one.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = "Which is always true of a rhombus?",
                        options = listOf(
                            "It has four right angles",
                            "All four sides are equal",
                            "It has one pair of parallel sides",
                            "It has three sides",
                        ),
                        correctIndex = 1,
                        explanation = "A rhombus is a pushed-over square: equal sides, any angles.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Concept(
                        body = "Every square is also a rectangle and also a rhombus.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Numeric(
                        question = "The angles inside a quadrilateral add to how many degrees?",
                        answer = "360",
                        explanation = "Split it into two triangles: 180 + 180.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Choice(
                        question = "Three angles are 90, 90 and 100. What is the fourth?",
                        options = listOf("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = "360 - 280.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-symmetry",
                title = "Symmetry",
                summary = "Fold lines that leave a shape unchanged.",
                steps = listOf(
                    Concept(
                        body = "Fold along a line of symmetry and the two halves land on each other.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Concept(
                        body = "A square has four fold lines. A circle has more than you could count.",
                        visual = Symmetry(sides = 4, lines = 4),
                    ),
                    Numeric(
                        question = "How many lines of symmetry does this triangle have?",
                        answer = "3",
                        explanation = "One from each corner to the opposite side.",
                        visual = Symmetry(sides = 3, lines = 3, reveal = false),
                    ),
                    Choice(
                        question = "How many lines of symmetry does this rectangle have?",
                        options = listOf("0", "1", "2", "4"),
                        correctIndex = 2,
                        explanation = "One horizontal, one vertical. The diagonals do not match up.",
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
                    ),
                    Concept(
                        body = "Rotational symmetry means it looks the same after part of a turn.",
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Choice(
                        question = "Which capital letter has a vertical line of symmetry?",
                        options = listOf("F", "A", "R", "P"),
                        correctIndex = 1,
                        explanation = "Fold A down the middle and the halves match.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many degrees is this angle?",
                options = listOf("45", "90", "180", "360"),
                correctIndex = 1,
                explanation = "A quarter of a full turn.",
                visual = AngleFigure(degrees = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = "A triangle has angles of 90 and 35. What is the third?",
                options = listOf("45", "55", "65", "125"),
                correctIndex = 1,
                explanation = "180 - 125.",
                visual = RightTriangle(a = 5, b = 3, labels = false),
            ),
            QuizQuestion(
                prompt = "Which shape has two pairs of parallel sides and four equal sides?",
                options = listOf("Trapezium", "Rhombus", "Kite", "Triangle"),
                correctIndex = 1,
                explanation = "That is the definition of a rhombus.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "The angles of a quadrilateral add up to...",
                options = listOf("180", "270", "360", "540"),
                correctIndex = 2,
                explanation = "Two triangles' worth.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry does this square have?",
                options = listOf("1", "2", "4", "8"),
                correctIndex = 2,
                explanation = "Two through the sides and two along the diagonals.",
                visual = Symmetry(sides = 4, lines = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "What kind of angle is this?",
                options = listOf("acute", "right", "obtuse", "reflex"),
                correctIndex = 2,
                explanation = "It sits between 90 and 180 degrees.",
                visual = AngleFigure(degrees = 120, reveal = false),
            ),
        ),
    )

    private val data = learnUnit(
        level = level,
        topic = MathTopic.DATA,
        summary = "Reading graphs, collecting data and finding the mean.",
        lessons = listOf(
            LessonSpec(
                id = "g35-data-bar-graphs",
                title = "Reading bar graphs",
                summary = "Check the scale before the bars.",
                steps = listOf(
                    Concept(
                        body = "Bars turn counts into heights you can compare at a glance.",
                        visual = BarChart(
                            values = listOf(12, 8, 15, 5),
                            labels = listOf("Ana", "Ben", "Cy", "Dee"),
                        ),
                    ),
                    Concept(
                        body = "Read the scale first: each gridline here is worth five, not one.",
                        visual = BarChart(values = listOf(10, 15, 5), gridStep = 5),
                    ),
                    Choice(
                        question = "Each gridline is 5. What does the tallest bar show?",
                        options = listOf("3", "5", "10", "15"),
                        correctIndex = 3,
                        explanation = "Three gridlines at five each.",
                        visual = BarChart(values = listOf(5, 10, 15), gridStep = 5, reveal = false),
                    ),
                    Numeric(
                        question = "How many books were read altogether?",
                        answer = "40",
                        explanation = "12 + 8 + 15 + 5.",
                        visual = BarChart(values = listOf(12, 8, 15, 5)),
                    ),
                    Concept(
                        body = "The gap between two bars answers 'how many more'.",
                        visual = BarChart(values = listOf(15, 9, 5), highlight = setOf(0, 2)),
                    ),
                    Choice(
                        question = "How many more does the tallest bar show than the shortest?",
                        options = listOf("3", "5", "10", "20"),
                        correctIndex = 2,
                        explanation = "15 - 5.",
                        visual = BarChart(values = listOf(15, 9, 5), highlight = setOf(0, 2)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-data-tally-line-plot",
                title = "Tally charts and pictograms",
                summary = "Recording data while it happens.",
                steps = listOf(
                    Concept(
                        body = "Tally marks come in gates of five, so a long list stays countable.",
                        visual = Tally(count = 17),
                    ),
                    Concept(
                        body = "A pictogram gives every symbol a fixed value.",
                        visual = Pictogram(rows = listOf(4f, 2.5f, 3f), unitValue = 10),
                    ),
                    Numeric(
                        question = "How many does this tally show?",
                        answer = "17",
                        explanation = "Three full gates and two more.",
                        visual = Tally(count = 17, reveal = false),
                    ),
                    Choice(
                        question = "One symbol is 10 apples. How many is this row?",
                        options = listOf("12", "20", "25", "50"),
                        correctIndex = 2,
                        explanation = "Two whole symbols and a half.",
                        visual = Pictogram(rows = listOf(2.5f), unitValue = 10, reveal = false),
                    ),
                    Concept(
                        body = "A line plot stacks a dot per value, so the shape of the data shows.",
                        visual = BarChart(values = listOf(1, 3, 5, 2, 1)),
                    ),
                    Choice(
                        question = "Which is best for counting cars as they pass?",
                        options = listOf("Bar graph", "Tally chart", "Pie chart", "The mean"),
                        correctIndex = 1,
                        explanation = "One mark at a time, no redrawing.",
                        visual = Tally(count = 13),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-data-mean",
                title = "The mean",
                summary = "The fair-share value.",
                steps = listOf(
                    Concept(
                        body = "The mean levels the bars off: pour the total out evenly.",
                        formula = "mean = total / how many",
                        visual = BarChart(values = listOf(4, 7, 6, 3), showMean = true),
                    ),
                    Worked(
                        problem = "Find the mean of 4, 7, 6 and 3.",
                        lines = listOf(
                            "Add them: 20.",
                            "Count them: 4.",
                            "20 / 4.",
                        ),
                        result = "Mean = 5",
                        visual = BarChart(values = listOf(4, 7, 6, 3), showMean = true),
                    ),
                    Numeric(
                        question = "What is the mean of these three values?",
                        answer = "12",
                        explanation = "36 / 3.",
                        visual = BarChart(values = listOf(10, 12, 14)),
                    ),
                    Choice(
                        question = "Five children have a mean of 6 sweets. How many sweets altogether?",
                        options = listOf("11", "24", "30", "36"),
                        correctIndex = 2,
                        explanation = "Total = mean x how many.",
                        visual = ArrayDots(rows = 5, cols = 6, reveal = false),
                    ),
                    Concept(
                        body = "One extreme value drags the mean above almost all the data.",
                        visual = BarChart(values = listOf(2, 3, 40), showMean = true),
                    ),
                    Choice(
                        question = "Which statement about the mean is true?",
                        options = listOf(
                            "It is always one of the values",
                            "It can be a decimal",
                            "It is always the largest value",
                            "It only works for even counts",
                        ),
                        correctIndex = 1,
                        explanation = "3 and 4 have a mean of 3.5, which is not in the data.",
                        visual = BarChart(values = listOf(3, 4), showMean = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Each gridline is 2. What does the tallest bar show?",
                options = listOf("5", "7", "10", "12"),
                correctIndex = 2,
                explanation = "Five gridlines at two each.",
                visual = BarChart(values = listOf(4, 6, 10), gridStep = 2, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many does this tally show?",
                options = listOf("10", "12", "13", "15"),
                correctIndex = 2,
                explanation = "Two full gates and three more.",
                visual = Tally(count = 13, reveal = false),
            ),
            QuizQuestion(
                prompt = "One symbol is 6 cars. How many cars is this row?",
                options = listOf("10", "18", "24", "46"),
                correctIndex = 2,
                explanation = "Four symbols at six each.",
                visual = Pictogram(rows = listOf(4f), unitValue = 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the mean of these values?",
                options = listOf("7", "8", "9", "24"),
                correctIndex = 1,
                explanation = "24 / 3.",
                visual = BarChart(values = listOf(5, 9, 10)),
            ),
            QuizQuestion(
                prompt = "Four scores have a mean of 7. What is their total?",
                options = listOf("11", "21", "28", "74"),
                correctIndex = 2,
                explanation = "7 x 4.",
                visual = ArrayDots(rows = 4, cols = 7, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which is best for recording data as it happens?",
                options = listOf("Pie chart", "Tally chart", "The mean", "Line graph"),
                correctIndex = 1,
                explanation = "You add one mark at a time.",
                visual = Tally(count = 9),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, measurement, geometry, data)
}
