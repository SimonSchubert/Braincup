package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.Balance
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.CircleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.Counters
import com.inspiredandroid.braincup.learn.LearnVisual.DecimalGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.PieChart
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Solid
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.learnUnit

/** Grades 6-8: negatives, ratio and percent, first algebra, Pythagoras, and statistics. */
internal object Grade6To8Content {

    private val level = GradeLevel.GRADES_6_8

    private val arithmetic = learnUnit(
        level = level,
        topic = MathTopic.ARITHMETIC,
        summary = "Negative numbers, ratio and proportion, and percentages.",
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-negatives",
                title = "Negative numbers",
                summary = "Left of zero, and what signs do when you multiply.",
                steps = listOf(
                    Concept(
                        body = "The line carries on to the left of zero.",
                        visual = NumberLine(from = -10, to = 10, tickStep = 2),
                    ),
                    Concept(
                        body = "Adding moves right. Subtracting a negative also moves right, because losing a debt helps.",
                        formula = "5 - (-3) = 8",
                        visual = NumberLine(from = -6, to = 10, tickStep = 2, start = 5, jump = 3, hops = 3),
                    ),
                    Choice(
                        question = "The temperature is -4 and drops 6 more. What is it now?",
                        options = listOf("2", "-2", "-10", "10"),
                        correctIndex = 2,
                        explanation = "Six steps to the left of -4.",
                        visual = NumberLine(from = -12, to = 6, tickStep = 2, start = -4),
                    ),
                    Concept(
                        body = "Same signs give a positive, different signs a negative.",
                        formula = "(-3) x (-4) = 12",
                        visual = ArrayDots(rows = 3, cols = 4),
                    ),
                    Numeric(
                        question = "-7 + 12 = ?",
                        answer = "5",
                        explanation = "Twelve steps right from -7 passes zero after seven.",
                        visual = NumberLine(from = -10, to = 8, tickStep = 2, start = -7),
                    ),
                    Choice(
                        question = "Which of these is the largest?",
                        options = listOf("-10", "-1", "0", "-100"),
                        correctIndex = 2,
                        explanation = "Further left on the line means smaller.",
                        visual = NumberLine(from = -10, to = 2, tickStep = 2),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-arithmetic-ratio",
                title = "Ratio and proportion",
                summary = "Comparing by division, and scaling recipes.",
                steps = listOf(
                    Concept(
                        body = "A ratio of 1 : 4 means one part in every five.",
                        visual = Fraction(numerator = 1, denominator = 5),
                    ),
                    Concept(
                        body = "Scale both parts by the same number and the mix is unchanged.",
                        formula = "3 : 5 = 12 : 20",
                        visual = Fraction(numerator = 3, denominator = 8, compare = 12 to 32),
                    ),
                    Choice(
                        question = "Rice for 4 people is 300 g. How much for 6?",
                        options = listOf("350 g", "400 g", "450 g", "600 g"),
                        correctIndex = 2,
                        explanation = "One person needs 75 g.",
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Worked(
                        problem = "Share 60 euro in the ratio 2 : 3.",
                        lines = listOf(
                            "2 + 3 = 5 parts.",
                            "60 / 5 = 12 per part.",
                            "2 x 12 and 3 x 12.",
                        ),
                        result = "24 euro and 36 euro",
                        visual = Fraction(numerator = 2, denominator = 5),
                    ),
                    Numeric(
                        question = "Map scale 1 : 50000. A 4 cm road is how many cm in reality?",
                        answer = "200000",
                        explanation = "4 x 50000.",
                        visual = Steps(terms = listOf(1, 50000), multiply = true),
                    ),
                    Choice(
                        question = "Which ratio matches the shaded bar?",
                        options = listOf("2 : 3", "3 : 2", "6 : 3", "1 : 2"),
                        correctIndex = 0,
                        explanation = "6 : 9 divides by 3 to give 2 : 3.",
                        visual = Fraction(numerator = 6, denominator = 15, compare = 2 to 5),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-arithmetic-percent",
                title = "Percentages",
                summary = "Parts of a hundred, up and down.",
                steps = listOf(
                    Concept(
                        body = "Per cent means out of a hundred.",
                        formula = "35% = 0.35",
                        visual = DecimalGrid(value = 0.35),
                    ),
                    Concept(
                        body = "To take a percentage, turn it into a decimal and multiply.",
                        formula = "20% of 80 = 16",
                        visual = DecimalGrid(value = 0.2),
                    ),
                    Numeric(
                        question = "What is 15% of 200?",
                        answer = "30",
                        explanation = "10% is 20 and 5% is half of that.",
                        visual = DecimalGrid(value = 0.15),
                    ),
                    Choice(
                        question = "A 40 euro jacket is 25% off. What do you pay?",
                        options = listOf("10 euro", "15 euro", "30 euro", "35 euro"),
                        correctIndex = 2,
                        explanation = "A quarter of 40 is 10, so 40 - 10.",
                        visual = DecimalGrid(value = 0.25),
                    ),
                    Worked(
                        problem = "A price rises from 50 to 65.",
                        lines = listOf(
                            "The change is 15.",
                            "Compare with the original: 15/50.",
                            "= 0.3.",
                            "x 100.",
                        ),
                        result = "A 30% increase",
                        visual = BarChart(values = listOf(50, 65), labels = listOf("before", "after")),
                    ),
                    Choice(
                        question = "Up 10%, then down 10%. Where do you end up?",
                        options = listOf("back at the start", "slightly better off", "slightly worse off", "cannot tell"),
                        correctIndex = 2,
                        explanation = "100 becomes 110, and 10% of 110 is 11.",
                        visual = BarChart(values = listOf(100, 110, 99), labels = listOf("start", "+10%", "-10%")),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "-6 + 9 = ?",
                options = listOf("-15", "-3", "3", "15"),
                correctIndex = 2,
                explanation = "Nine steps right of -6.",
                visual = NumberLine(from = -8, to = 8, tickStep = 2, start = -6),
            ),
            QuizQuestion(
                prompt = "(-4) x (-5) = ?",
                options = listOf("-20", "-9", "9", "20"),
                correctIndex = 3,
                explanation = "Two negatives make a positive.",
                visual = ArrayDots(rows = 4, cols = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which ratio matches the shaded bar?",
                options = listOf("1 : 2", "3 : 5", "5 : 3", "15 : 5"),
                correctIndex = 1,
                explanation = "15 : 25 divides by 5.",
                visual = Fraction(numerator = 3, denominator = 8),
            ),
            QuizQuestion(
                prompt = "Share 80 euro in the ratio 3 : 1. What is the larger share?",
                options = listOf("20 euro", "40 euro", "60 euro", "75 euro"),
                correctIndex = 2,
                explanation = "Four parts of 20 euro; the larger share takes three.",
                visual = Fraction(numerator = 3, denominator = 4),
            ),
            QuizQuestion(
                prompt = "What is 12% of 150?",
                options = listOf("12", "15", "18", "24"),
                correctIndex = 2,
                explanation = "0.12 x 150.",
                visual = DecimalGrid(value = 0.12),
            ),
            QuizQuestion(
                prompt = "A price falls from 80 to 60. What is the percentage decrease?",
                options = listOf("20%", "25%", "30%", "33%"),
                correctIndex = 1,
                explanation = "The change is 20, and 20/80 = 0.25.",
                visual = BarChart(values = listOf(80, 60), labels = listOf("before", "after")),
            ),
        ),
    )

    private val algebra = learnUnit(
        level = level,
        topic = MathTopic.ALGEBRA,
        summary = "Expressions, solving linear equations, and straight-line graphs.",
        lessons = listOf(
            LessonSpec(
                id = "g68-algebra-expressions",
                title = "Letters for numbers",
                summary = "Collect, substitute and expand.",
                steps = listOf(
                    Concept(
                        body = "A letter stands for a number you do not know yet.",
                        formula = "n + 3 = 8",
                        visual = Balance(leftX = 1, leftOnes = 3, rightOnes = 8),
                    ),
                    Concept(
                        body = "Same letter? Collect them. Different letters stay apart.",
                        formula = "3a + 5a = 8a",
                        visual = Counters(groups = listOf(3, 5)),
                    ),
                    Choice(
                        question = "Simplify 4x + 3 + 2x - 1",
                        options = listOf("6x + 2", "6x + 4", "9x", "5x + 2"),
                        correctIndex = 0,
                        explanation = "4x + 2x = 6x, and 3 - 1 = 2.",
                        visual = Counters(groups = listOf(4, 2), reveal = false),
                    ),
                    Concept(
                        body = "Substituting puts a number back in place of the letter.",
                        formula = "x = 4 gives 3x + 2 = 14",
                        visual = ArrayDots(rows = 3, cols = 4),
                    ),
                    Numeric(
                        question = "If y = 5, what is 2y + 7?",
                        answer = "17",
                        explanation = "2 fives, then 7 more.",
                        visual = ArrayDots(rows = 2, cols = 5, reveal = false),
                    ),
                    Choice(
                        question = "Expand 3(x + 4)",
                        options = listOf("3x + 4", "x + 12", "3x + 12", "3x + 7"),
                        correctIndex = 2,
                        explanation = "Both terms inside the bracket are multiplied by 3.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-algebra-equations",
                title = "Solving equations",
                summary = "Keep the scale level.",
                steps = listOf(
                    Concept(
                        body = "An equation balances. Whatever you do to one pan, do to the other.",
                        formula = "x + 7 = 12",
                        visual = Balance(leftX = 1, leftOnes = 7, rightOnes = 12),
                    ),
                    Concept(
                        body = "Lift the same blocks off both pans and it still balances.",
                        formula = "x = 5",
                        visual = Balance(leftX = 1, leftOnes = 7, rightOnes = 12, remove = 7),
                    ),
                    Worked(
                        problem = "Solve 3x + 4 = 19",
                        lines = listOf(
                            "Undo the adding first.",
                            "Take 4 from both sides: 3x = 15.",
                            "Divide both sides by 3.",
                        ),
                        result = "x = 5",
                        visual = Balance(leftX = 3, leftOnes = 4, rightOnes = 19, remove = 4),
                    ),
                    Numeric(
                        question = "Solve 5x - 3 = 22. What is x?",
                        answer = "5",
                        explanation = "Add 3 for 5x = 25, then divide by 5.",
                        visual = Balance(leftX = 5, leftOnes = 0, rightOnes = 25),
                    ),
                    Choice(
                        question = "Solve 2(x - 3) = 10",
                        options = listOf("x = 2", "x = 5", "x = 8", "x = 13"),
                        correctIndex = 2,
                        explanation = "Halve both sides: x - 3 = 5.",
                        visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 10),
                    ),
                    Concept(
                        body = "Always check by putting your answer back in.",
                        formula = "3 x 5 + 4 = 19",
                        visual = Balance(leftX = 3, leftOnes = 4, rightOnes = 19),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-algebra-slope",
                title = "Straight-line graphs",
                summary = "Gradient and intercept, seen on the grid.",
                steps = listOf(
                    Concept(
                        body = "A pair of coordinates fixes one point: across, then up.",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f),
                            points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                        ),
                    ),
                    Concept(
                        body = "An equation gives a y for every x, and the points line up straight.",
                        formula = "y = 2x + 1",
                        visual = Plot(curve = Curve.Linear(m = 2f, c = 1f)),
                    ),
                    Concept(
                        body = "m is the gradient, c is where the line crosses the y-axis.",
                        formula = "y = mx + c",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 0f, y = 1f, label = "c = 1")),
                        ),
                    ),
                    Choice(
                        question = "Where does this line cross the y-axis?",
                        options = listOf("(0, 3)", "(0, -2)", "(-2, 0)", "(3, 0)"),
                        correctIndex = 1,
                        explanation = "At x = 0 the 3x term vanishes.",
                        visual = Plot(curve = Curve.Linear(m = 3f, c = -2f)),
                    ),
                    Numeric(
                        question = "This line has gradient 2. If x rises by 3, how much does y rise?",
                        answer = "6",
                        explanation = "Two up for every one across.",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Choice(
                        question = "Which line is steeper?",
                        options = listOf("the orange one", "the green one", "they match", "the higher one"),
                        correctIndex = 1,
                        explanation = "Gradient alone decides steepness: 5 beats 2.",
                        visual = Plot(curve = Curve.Linear(m = 2f, c = 1f), second = Curve.Linear(m = 5f, c = -1f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Simplify 5a + 2 - 3a",
                options = listOf("2a + 2", "8a", "2a - 2", "5a"),
                correctIndex = 0,
                explanation = "5a - 3a = 2a.",
                visual = Counters(groups = listOf(5, 3), reveal = false),
            ),
            QuizQuestion(
                prompt = "Expand 4(x - 2)",
                options = listOf("4x - 2", "4x - 8", "x - 8", "4x + 8"),
                correctIndex = 1,
                explanation = "Both terms multiply by 4.",
                visual = AreaGrid(cols = 2, rows = 4, showArea = false),
            ),
            QuizQuestion(
                prompt = "Solve 2x + 5 = 17",
                options = listOf("x = 5", "x = 6", "x = 11", "x = 22"),
                correctIndex = 1,
                explanation = "2x = 12.",
                visual = Balance(leftX = 2, leftOnes = 5, rightOnes = 17, remove = 5),
            ),
            QuizQuestion(
                prompt = "If x = 3, what is 4x - 5?",
                options = listOf("2", "7", "12", "17"),
                correctIndex = 1,
                explanation = "12 - 5.",
                visual = ArrayDots(rows = 4, cols = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the gradient of this line?",
                options = listOf("1", "2", "-3", "-7"),
                correctIndex = 2,
                explanation = "It falls three for every one across.",
                visual = Plot(curve = Curve.Linear(m = -3f, c = 1f)),
            ),
            QuizQuestion(
                prompt = "Where does this line cross the y-axis?",
                options = listOf("(0, 2)", "(0, 1)", "(1, 0)", "(2, 0)"),
                correctIndex = 1,
                explanation = "Set x = 0.",
                visual = Plot(curve = Curve.Linear(m = 2f, c = 1f)),
            ),
        ),
    )

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Pythagoras, circle measurements and the volume of prisms.",
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-pythagoras",
                title = "Pythagoras' theorem",
                summary = "Two squares that fill a third.",
                steps = listOf(
                    Concept(
                        body = "The longest side always faces the right angle.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "The squares on the two short sides hold exactly as much as the square on the long one.",
                        formula = "a² + b² = c²",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Worked(
                        problem = "Short sides 3 and 4.",
                        lines = listOf(
                            "3² = 9 and 4² = 16.",
                            "9 + 16 = 25.",
                            "Take the square root.",
                        ),
                        result = "c = 5",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = "How long is the hypotenuse?",
                        answer = "10",
                        explanation = "36 + 64 = 100.",
                        visual = RightTriangle(a = 8, b = 6, unknown = Side.HYPOTENUSE),
                    ),
                    Choice(
                        question = "A 13 m ladder stands 5 m from the wall. How high does it reach?",
                        options = listOf("8 m", "10 m", "12 m", "14 m"),
                        correctIndex = 2,
                        explanation = "169 - 25 = 144.",
                        visual = RightTriangle(a = 5, b = 12, unknown = Side.B),
                    ),
                    Concept(
                        body = "It works only for right-angled triangles - but also backwards.",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-circles",
                title = "Circles",
                summary = "Where pi comes from.",
                steps = listOf(
                    Concept(
                        body = "The diameter crosses the middle, so it is twice the radius.",
                        formula = "d = 2r",
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Concept(
                        body = "Roll a circle once and it covers pi diameters.",
                        formula = "C = pi x d",
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Numeric(
                        question = "The radius is 5 cm. What is the diameter?",
                        answer = "10",
                        explanation = "Twice the radius.",
                        visual = CircleFigure(radius = 5),
                    ),
                    Choice(
                        question = "A wheel of diameter 70 cm. How far does one turn take it?",
                        options = listOf("110 cm", "220 cm", "350 cm", "440 cm"),
                        correctIndex = 1,
                        explanation = "3.14 x 70.",
                        visual = CircleFigure(sweepCircumference = true),
                    ),
                    Concept(
                        body = "Area squares the radius, so doubling r gives four times the area.",
                        formula = "A = pi x r²",
                        visual = CircleFigure(radius = 10, fillArea = true),
                    ),
                    Choice(
                        question = "Radius 10 cm, pi as 3.14. What is the area?",
                        options = listOf("31.4", "62.8", "314", "628"),
                        correctIndex = 2,
                        explanation = "3.14 x 100.",
                        visual = CircleFigure(fillArea = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-volume",
                title = "Volume of prisms",
                summary = "Cross-section times length.",
                steps = listOf(
                    Concept(
                        body = "Volume counts unit cubes, so its units are cubed.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "A prism keeps the same cross-section the whole way along.",
                        formula = "V = base area x length",
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = "A box is 4 by 3 by 2 cm. What is its volume in cubic cm?",
                        answer = "24",
                        explanation = "4 x 3 x 2.",
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
                    ),
                    Choice(
                        question = "A cylinder has base radius r and height h. Which is its volume?",
                        options = listOf("2 x pi x r x h", "pi x r² x h", "pi x d x h", "r² + h"),
                        correctIndex = 1,
                        explanation = "The circle of area pi r², repeated up the height.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Worked(
                        problem = "A triangular prism, cross-section 6 cm², length 9 cm.",
                        lines = listOf(
                            "Same triangle all the way along.",
                            "Volume is cross-section x length.",
                            "6 x 9.",
                        ),
                        result = "V = 54 cm³",
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Choice(
                        question = "Double every side of a cube. The volume is multiplied by...",
                        options = listOf("2", "4", "6", "8"),
                        correctIndex = 3,
                        explanation = "All three dimensions double.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How long is the hypotenuse?",
                options = listOf("13", "14", "15", "17"),
                correctIndex = 0,
                explanation = "25 + 144 = 169.",
                visual = RightTriangle(a = 12, b = 5, unknown = Side.HYPOTENUSE),
            ),
            QuizQuestion(
                prompt = "The diameter is 18 cm. What is the radius?",
                options = listOf("6 cm", "9 cm", "18 cm", "36 cm"),
                correctIndex = 1,
                explanation = "Half the diameter.",
                visual = CircleFigure(showDiameter = true),
            ),
            QuizQuestion(
                prompt = "Circumference of a circle of diameter 10 cm, pi as 3.14?",
                options = listOf("15.7 cm", "31.4 cm", "78.5 cm", "314 cm"),
                correctIndex = 1,
                explanation = "pi x d.",
                visual = CircleFigure(sweepCircumference = true),
            ),
            QuizQuestion(
                prompt = "Area of a circle of radius 4 cm, pi as 3.14?",
                options = listOf("12.56", "25.12", "50.24", "100.48"),
                correctIndex = 2,
                explanation = "3.14 x 16.",
                visual = CircleFigure(fillArea = true),
            ),
            QuizQuestion(
                prompt = "What is the volume of a 5 by 4 by 3 cm box?",
                options = listOf("12", "20", "47", "60"),
                correctIndex = 3,
                explanation = "5 x 4 x 3.",
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which gives the volume of any prism?",
                options = listOf("perimeter x height", "cross-section x length", "pi x r²", "2 x pi x r"),
                correctIndex = 1,
                explanation = "The cross-section repeats unchanged.",
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
        ),
    )

    private val data = learnUnit(
        level = level,
        topic = MathTopic.DATA,
        summary = "Mean, median and range, first probability, and honest charts.",
        lessons = listOf(
            LessonSpec(
                id = "g68-data-averages",
                title = "Averages and range",
                summary = "Three middles and one spread.",
                steps = listOf(
                    Concept(
                        body = "The mean levels the bars off; the median is the middle one; the mode is the tallest pile.",
                        visual = BarChart(values = listOf(3, 7, 9, 4, 6), showMean = true),
                    ),
                    Worked(
                        problem = "Median of 7, 3, 9, 4, 6",
                        lines = listOf(
                            "Sort them: 3, 4, 6, 7, 9.",
                            "Five values, so take the third.",
                        ),
                        result = "Median = 6",
                        visual = BarChart(values = listOf(3, 4, 6, 7, 9), highlight = setOf(2)),
                    ),
                    Numeric(
                        question = "What is the median of these four values?",
                        answer = "6.5",
                        explanation = "Mean of the two middle ones: (5 + 8) / 2.",
                        visual = BarChart(values = listOf(2, 5, 8, 11), highlight = setOf(1, 2)),
                    ),
                    Choice(
                        question = "One salary dwarfs the rest. Which average describes a typical one?",
                        options = listOf("The mean", "The median", "The mode", "The range"),
                        correctIndex = 1,
                        explanation = "The outlier drags the mean above every ordinary salary.",
                        visual = BarChart(values = listOf(20, 22, 21, 23, 200), showMean = true),
                    ),
                    Concept(
                        body = "Range measures spread: largest minus smallest.",
                        visual = BarChart(values = listOf(4, 9, 2, 15), highlight = setOf(2, 3)),
                    ),
                    Choice(
                        question = "What is the range shown here?",
                        options = listOf("4", "9", "13", "15"),
                        correctIndex = 2,
                        explanation = "15 - 2.",
                        visual = BarChart(values = listOf(4, 9, 2, 15), highlight = setOf(2, 3)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-data-probability",
                title = "Probability",
                summary = "Counting outcomes from 0 to 1.",
                steps = listOf(
                    Concept(
                        body = "Probability runs from impossible to certain.",
                        visual = PieChart(shares = listOf(1, 3), labels = listOf("happens", "does not")),
                    ),
                    Concept(
                        body = "When outcomes are equally likely, probability is just counting.",
                        formula = "P = wanted / all",
                        visual = PieChart(shares = listOf(3, 3), labels = listOf("even", "odd")),
                    ),
                    Choice(
                        question = "What is the chance of an even number on a fair six-sided die?",
                        options = listOf("1/6", "1/3", "1/2", "2/3"),
                        correctIndex = 2,
                        explanation = "Three faces out of six.",
                        visual = PieChart(shares = listOf(3, 3), reveal = false),
                    ),
                    Numeric(
                        question = "3 red and 7 blue balls. What is P(red) as a decimal?",
                        answer = "0.3",
                        explanation = "3 out of 10.",
                        visual = PieChart(shares = listOf(3, 7), labels = listOf("red", "blue"), reveal = false),
                    ),
                    Concept(
                        body = "All outcomes together make 1.",
                        formula = "P(not A) = 1 - P(A)",
                        visual = PieChart(shares = listOf(35, 65), labels = listOf("rain", "dry")),
                    ),
                    Choice(
                        question = "The chance of rain is 0.35. What is the chance of no rain?",
                        options = listOf("0.35", "0.55", "0.65", "0.75"),
                        correctIndex = 2,
                        explanation = "1 - 0.35.",
                        visual = PieChart(shares = listOf(35, 65), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-data-charts",
                title = "Choosing a chart",
                summary = "Match the chart, and spot the tricks.",
                steps = listOf(
                    Concept(
                        body = "Bars compare separate categories; pies show shares of one whole.",
                        visual = PieChart(shares = listOf(40, 35, 25)),
                    ),
                    Concept(
                        body = "Every slice is a fraction of a full 360 degree turn.",
                        formula = "angle = fraction x 360",
                        visual = PieChart(shares = listOf(25, 75), labels = listOf("a quarter", "the rest")),
                    ),
                    Numeric(
                        question = "This slice covers a quarter of the data. How many degrees is it?",
                        answer = "90",
                        explanation = "0.25 x 360.",
                        visual = PieChart(shares = listOf(25, 75), reveal = false),
                    ),
                    Choice(
                        question = "Which chart best shows a town's population year by year?",
                        options = listOf("Pie chart", "Line graph", "Tally chart", "Pictogram"),
                        correctIndex = 1,
                        explanation = "A trend across time needs time on the axis.",
                        visual = BarChart(values = listOf(3, 4, 5, 7, 9, 12)),
                    ),
                    Concept(
                        body = "An axis that does not start at zero makes tiny gaps look enormous.",
                        visual = BarChart(values = listOf(96, 98, 100), gridStep = 50),
                    ),
                    Choice(
                        question = "A bar chart's axis starts at 50 instead of 0. What happens?",
                        options = listOf("Nothing changes", "Differences look smaller", "Differences look bigger", "Bars reorder"),
                        correctIndex = 2,
                        explanation = "Cutting off the bottom exaggerates every gap.",
                        visual = BarChart(values = listOf(52, 55, 60), gridStep = 25),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What is the median of these values?",
                options = listOf("5", "6", "6.4", "8"),
                correctIndex = 1,
                explanation = "Sorted, the middle value is 6.",
                visual = BarChart(values = listOf(3, 5, 6, 8, 10), highlight = setOf(2)),
            ),
            QuizQuestion(
                prompt = "What is the range shown here?",
                options = listOf("8", "11", "16", "20"),
                correctIndex = 2,
                explanation = "20 - 4.",
                visual = BarChart(values = listOf(12, 4, 9, 20), highlight = setOf(1, 3)),
            ),
            QuizQuestion(
                prompt = "What is the chance of rolling a 5 or a 6 on a fair die?",
                options = listOf("1/6", "1/3", "1/2", "2/3"),
                correctIndex = 1,
                explanation = "Two faces out of six.",
                visual = PieChart(shares = listOf(2, 4), reveal = false),
            ),
            QuizQuestion(
                prompt = "If P(A) = 0.2, what is P(not A)?",
                options = listOf("0.2", "0.5", "0.8", "1.2"),
                correctIndex = 2,
                explanation = "The whole pie is 1.",
                visual = PieChart(shares = listOf(20, 80), reveal = false),
            ),
            QuizQuestion(
                prompt = "This slice is 90 degrees. What share of the data is it?",
                options = listOf("10%", "25%", "50%", "90%"),
                correctIndex = 1,
                explanation = "90 out of 360.",
                visual = PieChart(shares = listOf(25, 75), reveal = false),
            ),
            QuizQuestion(
                prompt = "Which chart shows change over time best?",
                options = listOf("Pie chart", "Line graph", "Bar chart of categories", "Tally chart"),
                correctIndex = 1,
                explanation = "A line makes a trend visible.",
                visual = BarChart(values = listOf(2, 4, 5, 8, 11)),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, algebra, geometry, data)
}
