package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.Balance
import com.inspiredandroid.braincup.learn.LearnVisual.Counters
import com.inspiredandroid.braincup.learn.LearnVisual.Inequality
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/**
 * Algebra, as a ladder of sub-topics rather than a pair of school-year slices.
 *
 * Each rung is small enough to finish in a sitting and earns its own certificate, and each one
 * leans on the rung below: expressions are what equations are made of, equations are what a line's
 * graph draws, inequalities are equations with a range of answers, and so on up to quadratics.
 * [GradeLevel] rides along only as the age hint on each rung.
 */
internal object AlgebraContent {

    private val expressions = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "expressions",
        title = "Expressions and variables",
        summary = "Letters that stand for numbers, and how to tidy them up.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-expressions-letters",
                title = "Letters for numbers",
                summary = "What a variable actually is.",
                steps = listOf(
                    Concept(
                        body = "A letter stands for a number you do not know yet.",
                        formula = "n + 3 = 8",
                        visual = Balance(leftX = 1, leftOnes = 3, rightOnes = 8),
                    ),
                    Concept(
                        body = "3n is short for n + n + n. The number in front counts the copies.",
                        formula = "3n = n + n + n",
                        visual = ArrayDots(rows = 3, cols = 4),
                    ),
                    Choice(
                        question = "Which expression means 'five more than n'?",
                        options = listOf("5n", "n + 5", "n - 5", "n to the power 5"),
                        correctIndex = 1,
                        explanation = "More than means adding. 5n would be five copies of n.",
                        visual = NumberLine(from = 0, to = 12, start = 4, jump = 5, reveal = false),
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
                        question = "If a = 6, what is a squared minus a?",
                        options = listOf("30", "0", "36", "12"),
                        correctIndex = 0,
                        explanation = "36 - 6 = 30.",
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-expressions-collecting",
                title = "Collecting like terms",
                summary = "Only matching letters can be added.",
                steps = listOf(
                    Concept(
                        body = "Same letter? Collect them. Different letters stay apart.",
                        formula = "3a + 5a = 8a",
                        visual = Counters(groups = listOf(3, 5)),
                    ),
                    Concept(
                        body = "a and b are different things, so 3a + 2b cannot be shortened.",
                        formula = "3a + 2b stays as it is",
                        visual = Counters(groups = listOf(3, 2), merge = false),
                    ),
                    Choice(
                        question = "Simplify 4x + 3 + 2x - 1",
                        options = listOf("6x + 2", "6x + 4", "9x", "5x + 2"),
                        correctIndex = 0,
                        explanation = "4x + 2x = 6x, and 3 - 1 = 2.",
                        visual = Counters(groups = listOf(4, 2), reveal = false),
                    ),
                    Worked(
                        problem = "Simplify 7m - 2n + m + 5n",
                        lines = listOf(
                            "Sort the m terms: 7m + m = 8m.",
                            "Sort the n terms: -2n + 5n = 3n.",
                            "Write them side by side.",
                        ),
                        result = "8m + 3n",
                        visual = Counters(groups = listOf(7, 1)),
                    ),
                    Numeric(
                        question = "Simplify 9k - 4k. How many k are left?",
                        answer = "5",
                        explanation = "Nine of something take away four of it.",
                        visual = Counters(groups = listOf(9, 4), reveal = false),
                    ),
                    Choice(
                        question = "Why can 2x and 2x squared not be collected?",
                        options = listOf(
                            "they use different letters",
                            "one is a length, one is an area",
                            "the numbers differ",
                            "they can be collected",
                        ),
                        correctIndex = 1,
                        explanation = "x and x squared grow at different rates, so they are unlike terms.",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-expressions-brackets",
                title = "Expanding and factorising",
                summary = "Brackets open out, and fold back in.",
                steps = listOf(
                    Concept(
                        body = "A bracket multiplies everything inside it. The area of the whole rectangle is the sum of its parts.",
                        formula = "3(x + 4) = 3x + 12",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Choice(
                        question = "Expand 3(x + 4)",
                        options = listOf("3x + 4", "x + 12", "3x + 12", "3x + 7"),
                        correctIndex = 2,
                        explanation = "Both terms inside the bracket are multiplied by 3.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Concept(
                        body = "A minus outside the bracket flips every sign inside it.",
                        formula = "-2(x - 5) = -2x + 10",
                        visual = AreaGrid(cols = 5, rows = 2, showArea = false),
                    ),
                    Worked(
                        problem = "Expand and simplify 2(x + 3) + 4(x - 1)",
                        lines = listOf(
                            "First bracket: 2x + 6.",
                            "Second bracket: 4x - 4.",
                            "Collect: 2x + 4x = 6x, and 6 - 4 = 2.",
                        ),
                        result = "6x + 2",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false),
                    ),
                    Concept(
                        body = "Factorising runs it backwards: take out what every term shares.",
                        formula = "6x + 15 = 3(2x + 5)",
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false),
                    ),
                    Choice(
                        question = "Factorise 8x + 12",
                        options = listOf("4(2x + 3)", "2(4x + 12)", "8(x + 12)", "4(2x + 12)"),
                        correctIndex = 0,
                        explanation = "4 is the biggest number that divides both 8 and 12.",
                        visual = AreaGrid(cols = 3, rows = 4, showArea = false),
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
                prompt = "Which expression means 'four less than k'?",
                options = listOf("4 - k", "k - 4", "4k", "k + 4"),
                correctIndex = 1,
                explanation = "Start at k and go down four.",
                visual = NumberLine(from = 0, to = 12, start = 9, jump = -4, reveal = false),
            ),
            QuizQuestion(
                prompt = "Expand 4(x - 2)",
                options = listOf("4x - 2", "4x - 8", "x - 8", "4x + 8"),
                correctIndex = 1,
                explanation = "Both terms multiply by 4.",
                visual = AreaGrid(cols = 2, rows = 4, showArea = false),
            ),
            QuizQuestion(
                prompt = "If x = 3, what is 4x - 5?",
                options = listOf("2", "7", "12", "17"),
                correctIndex = 1,
                explanation = "12 - 5.",
                visual = ArrayDots(rows = 4, cols = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Simplify 6p + 2q - p + 3q",
                options = listOf("5p + 5q", "10pq", "7p + 5q", "5p + q"),
                correctIndex = 0,
                explanation = "6p - p = 5p, and 2q + 3q = 5q.",
                visual = Counters(groups = listOf(6, 1), reveal = false),
            ),
            QuizQuestion(
                prompt = "Factorise 10x + 25",
                options = listOf("5(2x + 5)", "5(2x + 25)", "10(x + 25)", "2(5x + 5)"),
                correctIndex = 0,
                explanation = "5 divides both 10 and 25.",
                visual = AreaGrid(cols = 5, rows = 5, showArea = false),
            ),
        ),
    )

    private val linearEquations = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "linear-equations",
        title = "Linear equations",
        summary = "Undo the operations until the letter stands alone.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-equations-balance",
                title = "Keeping the scale level",
                summary = "The one rule behind every solution.",
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
                    Numeric(
                        question = "Solve x + 9 = 15. What is x?",
                        answer = "6",
                        explanation = "Take 9 off both sides.",
                        visual = Balance(leftX = 1, leftOnes = 9, rightOnes = 15),
                    ),
                    Concept(
                        body = "Dividing both sides works the same way: share each pan into equal groups.",
                        formula = "4x = 20 gives x = 5",
                        visual = Balance(leftX = 4, leftOnes = 0, rightOnes = 20),
                    ),
                    Choice(
                        question = "Solve 6x = 42",
                        options = listOf("x = 6", "x = 7", "x = 36", "x = 48"),
                        correctIndex = 1,
                        explanation = "Divide both sides by 6.",
                        visual = Balance(leftX = 6, leftOnes = 0, rightOnes = 42),
                    ),
                    Concept(
                        body = "Always check by putting your answer back in.",
                        formula = "6 * 7 = 42",
                        visual = Balance(leftX = 6, leftOnes = 0, rightOnes = 42),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-equations-twostep",
                title = "Two-step equations",
                summary = "Undo the adding first, then the multiplying.",
                steps = listOf(
                    Concept(
                        body = "When two things were done to x, undo them in reverse order: the last one first.",
                        formula = "3x + 4 = 19",
                        visual = Balance(leftX = 3, leftOnes = 4, rightOnes = 19),
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
                    Concept(
                        body = "A bracket can be undone by dividing first, which is often the quicker route.",
                        formula = "2(x - 3) = 10 gives x - 3 = 5",
                        visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 10),
                    ),
                    Choice(
                        question = "Solve 2(x - 3) = 10",
                        options = listOf("x = 2", "x = 5", "x = 8", "x = 13"),
                        correctIndex = 2,
                        explanation = "Halve both sides: x - 3 = 5.",
                        visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 10),
                    ),
                    Numeric(
                        question = "Solve 4(x + 1) = 24. What is x?",
                        answer = "5",
                        explanation = "Divide by 4 to get x + 1 = 6.",
                        visual = Balance(leftX = 4, leftOnes = 4, rightOnes = 24),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-equations-bothsides",
                title = "Letters on both sides",
                summary = "Gather the letters on one pan first.",
                steps = listOf(
                    Concept(
                        body = "When x appears on both pans, take the smaller pile off both sides first.",
                        formula = "5x + 2 = 3x + 10",
                        visual = Balance(leftX = 5, leftOnes = 2, rightOnes = 10),
                    ),
                    Worked(
                        problem = "Solve 5x + 2 = 3x + 10",
                        lines = listOf(
                            "Take 3x off both sides: 2x + 2 = 10.",
                            "Take 2 off both sides: 2x = 8.",
                            "Halve both sides.",
                        ),
                        result = "x = 4",
                        visual = Balance(leftX = 2, leftOnes = 2, rightOnes = 10, remove = 2),
                    ),
                    Numeric(
                        question = "Solve 7x - 4 = 4x + 8. What is x?",
                        answer = "4",
                        explanation = "3x = 12 once the 4x and the 4 have moved.",
                        visual = Balance(leftX = 3, leftOnes = 0, rightOnes = 12),
                    ),
                    Choice(
                        question = "Solve 2x + 9 = 5x",
                        options = listOf("x = 3", "x = 9", "x = 1", "x = 7"),
                        correctIndex = 0,
                        explanation = "Take 2x off both sides: 9 = 3x.",
                        visual = Balance(leftX = 3, leftOnes = 0, rightOnes = 9),
                    ),
                    Concept(
                        body = "Some equations have no solution at all: if the letters vanish and the numbers disagree, nothing works.",
                        formula = "x + 1 = x + 2 is never true",
                        visual = Balance(leftX = 1, leftOnes = 1, rightOnes = 2),
                    ),
                    Choice(
                        question = "How many solutions does 3x + 6 = 3(x + 2) have?",
                        options = listOf("none", "one", "two", "every number"),
                        correctIndex = 3,
                        explanation = "Both sides are the same expression, so any x balances it.",
                        visual = AreaGrid(cols = 2, rows = 3, showArea = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Solve 2x + 5 = 17",
                options = listOf("x = 5", "x = 6", "x = 11", "x = 22"),
                correctIndex = 1,
                explanation = "2x = 12.",
                visual = Balance(leftX = 2, leftOnes = 5, rightOnes = 17, remove = 5),
            ),
            QuizQuestion(
                prompt = "Solve x - 8 = 3",
                options = listOf("x = 5", "x = 11", "x = -5", "x = 24"),
                correctIndex = 1,
                explanation = "Add 8 to both sides.",
                visual = Balance(leftX = 1, leftOnes = 0, rightOnes = 11),
            ),
            QuizQuestion(
                prompt = "Solve 7x = 56",
                options = listOf("x = 7", "x = 8", "x = 49", "x = 63"),
                correctIndex = 1,
                explanation = "Divide both sides by 7.",
                visual = Balance(leftX = 7, leftOnes = 0, rightOnes = 56),
            ),
            QuizQuestion(
                prompt = "Solve 3(x + 2) = 18",
                options = listOf("x = 4", "x = 6", "x = 8", "x = 12"),
                correctIndex = 0,
                explanation = "Divide by 3, then take 2 off.",
                visual = Balance(leftX = 3, leftOnes = 6, rightOnes = 18),
            ),
            QuizQuestion(
                prompt = "Solve 6x - 1 = 2x + 15",
                options = listOf("x = 2", "x = 4", "x = 8", "x = 16"),
                correctIndex = 1,
                explanation = "4x = 16.",
                visual = Balance(leftX = 4, leftOnes = 0, rightOnes = 16),
            ),
            QuizQuestion(
                prompt = "What is the very first step in solving 5x - 2 = 13?",
                options = listOf("divide by 5", "add 2 to both sides", "subtract 13", "multiply by 5"),
                correctIndex = 1,
                explanation = "Undo the subtraction before the multiplication.",
                visual = Balance(leftX = 5, leftOnes = 2, rightOnes = 13, remove = 2),
            ),
        ),
    )

    private val straightLineGraphs = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "straight-line-graphs",
        title = "Straight-line graphs",
        summary = "Gradient and intercept, seen on the grid.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-graphs-coordinates",
                title = "Points and lines",
                summary = "An equation drawn as a picture.",
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
                    Numeric(
                        question = "On the line y = 2x + 1, what is y when x = 3?",
                        answer = "7",
                        explanation = "Two threes, then one more.",
                        visual = Plot(curve = Curve.Linear(m = 2f, c = 1f)),
                    ),
                    Concept(
                        body = "Every point on the line makes the equation true. Every point off it does not.",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "on")),
                        ),
                    ),
                    Choice(
                        question = "Does the point (2, 5) lie on y = 2x + 1?",
                        options = listOf("yes", "no", "only if x changes", "cannot tell"),
                        correctIndex = 0,
                        explanation = "2 times 2 plus 1 is 5, so it fits.",
                        visual = Plot(
                            curve = Curve.Linear(m = 2f, c = 1f),
                            points = listOf(PlotPoint(x = 2f, y = 5f)),
                        ),
                    ),
                    Choice(
                        question = "What does a horizontal line mean?",
                        options = listOf("y never changes", "x never changes", "both change", "the gradient is 1"),
                        correctIndex = 0,
                        explanation = "Move across as far as you like and y stays put.",
                        visual = Plot(curve = Curve.Linear(m = 0f, c = 2f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-graphs-gradient",
                title = "Gradient and intercept",
                summary = "The two numbers that fix a line.",
                steps = listOf(
                    Concept(
                        body = "m is the gradient, c is where the line crosses the y-axis.",
                        formula = "y = mx + c",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 0f, y = 1f, label = "c = 1")),
                        ),
                    ),
                    Concept(
                        body = "Gradient is how far up you go for one step across.",
                        formula = "gradient = up divided by across",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Numeric(
                        question = "This line has gradient 2. If x rises by 3, how much does y rise?",
                        answer = "6",
                        explanation = "Two up for every one across.",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Choice(
                        question = "Where does this line cross the y-axis?",
                        options = listOf("(0, 3)", "(0, -2)", "(-2, 0)", "(3, 0)"),
                        correctIndex = 1,
                        explanation = "At x = 0 the 3x term vanishes.",
                        visual = Plot(curve = Curve.Linear(m = 3f, c = -2f)),
                    ),
                    Concept(
                        body = "A negative gradient falls as you go right.",
                        formula = "y = -2x + 3",
                        visual = Plot(curve = Curve.Linear(m = -2f, c = 3f)),
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
            LessonSpec(
                id = "algebra-graphs-writing",
                title = "Writing the equation",
                summary = "From a drawn line back to its formula.",
                steps = listOf(
                    Concept(
                        body = "Read c off the y-axis, count the gradient off the grid, and you have the equation.",
                        formula = "y = 2x + 1",
                        visual = Plot(
                            curve = Curve.Linear(m = 2f, c = 1f),
                            points = listOf(PlotPoint(x = 0f, y = 1f, label = "c")),
                        ),
                    ),
                    Worked(
                        problem = "Write the equation of a line through (0, -1) with gradient 3",
                        lines = listOf(
                            "It crosses the y-axis at -1, so c = -1.",
                            "The gradient is 3, so m = 3.",
                            "Put both into y = mx + c.",
                        ),
                        result = "y = 3x - 1",
                        visual = Plot(curve = Curve.Linear(m = 3f, c = -1f)),
                    ),
                    Choice(
                        question = "What is the equation of this line?",
                        options = listOf("y = x + 2", "y = 2x", "y = 2x + 2", "y = x"),
                        correctIndex = 0,
                        explanation = "It rises one for one across and cuts the axis at 2.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 2f)),
                    ),
                    Concept(
                        body = "Lines with the same gradient never meet: they are parallel.",
                        formula = "y = 2x + 1 and y = 2x - 2",
                        visual = Plot(curve = Curve.Linear(m = 2f, c = 1f), second = Curve.Linear(m = 2f, c = -2f)),
                    ),
                    Choice(
                        question = "Which line is parallel to y = 4x + 1?",
                        options = listOf("y = x + 4", "y = 4x - 7", "y = -4x + 1", "y = 4 - x"),
                        correctIndex = 1,
                        explanation = "Same gradient, different intercept.",
                        visual = Plot(curve = Curve.Linear(m = 4f, c = 1f), second = Curve.Linear(m = 4f, c = -2f)),
                    ),
                    Numeric(
                        question = "A line has gradient 5 and passes through (0, 2). What is y when x = 2?",
                        answer = "12",
                        explanation = "y = 5x + 2, so 10 + 2.",
                        visual = Plot(curve = Curve.Linear(m = 5f, c = 2f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
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
            QuizQuestion(
                prompt = "In y = mx + c, what does c tell you?",
                options = listOf("the steepness", "where it cuts the y-axis", "where it cuts the x-axis", "the length"),
                correctIndex = 1,
                explanation = "c is the value of y when x is zero.",
                visual = Plot(
                    curve = Curve.Linear(m = 1f, c = 2f),
                    points = listOf(PlotPoint(x = 0f, y = 2f)),
                ),
            ),
            QuizQuestion(
                prompt = "Which line is parallel to y = 3x - 5?",
                options = listOf("y = 3x + 2", "y = -3x - 5", "y = x - 5", "y = 5x - 3"),
                correctIndex = 0,
                explanation = "Parallel means the same gradient.",
                visual = Plot(curve = Curve.Linear(m = 3f, c = -1f), second = Curve.Linear(m = 3f, c = 2f)),
            ),
            QuizQuestion(
                prompt = "On y = 4x - 3, what is y when x = 2?",
                options = listOf("5", "8", "11", "-3"),
                correctIndex = 0,
                explanation = "8 - 3.",
                visual = Plot(curve = Curve.Linear(m = 4f, c = -3f)),
            ),
            QuizQuestion(
                prompt = "A line is flat. What is its gradient?",
                options = listOf("0", "1", "-1", "it has none"),
                correctIndex = 0,
                explanation = "No rise for any amount of run.",
                visual = Plot(curve = Curve.Linear(m = 0f, c = 1f)),
            ),
        ),
    )

    private val inequalities = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "inequalities",
        title = "Inequalities",
        summary = "Equations with a whole range of answers, not just one.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "algebra-inequalities-reading",
                title = "Reading an inequality",
                summary = "A range of answers, drawn on the line.",
                steps = listOf(
                    Concept(
                        body = "An equation has one answer. An inequality has a whole stretch of them.",
                        formula = "x > 3",
                        visual = Inequality(from = 0, to = 8, value = 3, greater = true),
                    ),
                    Concept(
                        body = "The hollow ring means 3 itself is not included. Fill it in and it is.",
                        formula = "x >= 3",
                        visual = Inequality(from = 0, to = 8, value = 3, greater = true, orEqual = true),
                    ),
                    Choice(
                        question = "Which numbers satisfy x < 5?",
                        options = listOf("5 and above", "everything below 5", "only 4", "5 exactly"),
                        correctIndex = 1,
                        explanation = "The arrow points down the line, away from 5.",
                        visual = Inequality(from = 0, to = 8, value = 5, greater = false, reveal = false),
                    ),
                    Concept(
                        body = "The open end of the sign always faces the bigger side.",
                        formula = "2 < 7 and 7 > 2",
                        visual = NumberLine(from = 0, to = 8, start = 2, jump = 5),
                    ),
                    Choice(
                        question = "Is 4 a solution of x <= 4?",
                        options = listOf("yes", "no", "only if x is a whole number", "cannot tell"),
                        correctIndex = 0,
                        explanation = "The 'or equal' half of the sign lets the boundary in.",
                        visual = Inequality(from = 0, to = 8, value = 4, greater = false, orEqual = true, reveal = false),
                    ),
                    Numeric(
                        question = "What is the smallest whole number that satisfies x > 6?",
                        answer = "7",
                        explanation = "6 itself is excluded, so the next whole number up.",
                        visual = Inequality(from = 3, to = 10, value = 6, greater = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-inequalities-solving",
                title = "Solving inequalities",
                summary = "The same moves as an equation.",
                steps = listOf(
                    Concept(
                        body = "Adding or subtracting on both sides works exactly as it does for equations.",
                        formula = "x + 3 > 8 gives x > 5",
                        visual = Inequality(from = 0, to = 10, value = 5, greater = true),
                    ),
                    Worked(
                        problem = "Solve 2x + 1 < 11",
                        lines = listOf(
                            "Take 1 from both sides: 2x < 10.",
                            "Halve both sides.",
                            "The sign does not move.",
                        ),
                        result = "x < 5",
                        visual = Inequality(from = 0, to = 10, value = 5, greater = false),
                    ),
                    Numeric(
                        question = "Solve x - 4 >= 2. What is the smallest x that works?",
                        answer = "6",
                        explanation = "x >= 6, and 6 is allowed in.",
                        visual = Inequality(from = 2, to = 10, value = 6, greater = true, orEqual = true, reveal = false),
                    ),
                    Choice(
                        question = "Solve 3x <= 12",
                        options = listOf("x <= 4", "x >= 4", "x <= 36", "x < 4"),
                        correctIndex = 0,
                        explanation = "Divide by 3 and keep the sign as it was.",
                        visual = Inequality(from = 0, to = 8, value = 4, greater = false, orEqual = true, reveal = false),
                    ),
                    Concept(
                        body = "Multiplying or dividing by a positive number leaves the sign alone.",
                        formula = "4x > 20 gives x > 5",
                        visual = Inequality(from = 0, to = 10, value = 5, greater = true),
                    ),
                    Choice(
                        question = "Solve 5x - 2 > 13",
                        options = listOf("x > 3", "x > 5", "x < 3", "x > 15"),
                        correctIndex = 0,
                        explanation = "5x > 15.",
                        visual = Inequality(from = 0, to = 8, value = 3, greater = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-inequalities-flipping",
                title = "Flipping the sign",
                summary = "The one move that reverses everything.",
                steps = listOf(
                    Concept(
                        body = "2 is less than 5. Multiply both by -1 and -2 is now greater than -5: the order turns over.",
                        formula = "2 < 5 but -2 > -5",
                        visual = NumberLine(from = -6, to = 6, start = 2, jump = -4),
                    ),
                    Concept(
                        body = "So multiplying or dividing an inequality by a negative flips the sign.",
                        formula = "-2x < 6 gives x > -3",
                        visual = Inequality(from = -6, to = 4, value = -3, greater = true),
                    ),
                    Worked(
                        problem = "Solve -3x >= 9",
                        lines = listOf(
                            "Divide both sides by -3.",
                            "Dividing by a negative flips the sign.",
                            "Greater-or-equal becomes less-or-equal.",
                        ),
                        result = "x <= -3",
                        visual = Inequality(from = -6, to = 4, value = -3, greater = false, orEqual = true),
                    ),
                    Choice(
                        question = "Solve -x > 4",
                        options = listOf("x > -4", "x < -4", "x > 4", "x < 4"),
                        correctIndex = 1,
                        explanation = "Multiply both sides by -1 and turn the sign over.",
                        visual = Inequality(from = -8, to = 2, value = -4, greater = false, reveal = false),
                    ),
                    Concept(
                        body = "Adding a negative is not the same as multiplying by one: only multiplying and dividing flip the sign.",
                        formula = "x - 5 > 1 still gives x > 6",
                        visual = Inequality(from = 0, to = 10, value = 6, greater = true),
                    ),
                    Choice(
                        question = "Which step forces the sign to flip?",
                        options = listOf(
                            "subtracting 7 from both sides",
                            "dividing both sides by -2",
                            "adding 3 to both sides",
                            "multiplying both sides by 5",
                        ),
                        correctIndex = 1,
                        explanation = "Only a negative multiplier or divisor reverses the order.",
                        visual = Inequality(from = -4, to = 6, value = 1, greater = true, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What does this number line show?",
                options = listOf("x > 2", "x >= 2", "x < 2", "x <= 2"),
                correctIndex = 0,
                explanation = "The ring is hollow, so 2 is not included, and the arrow runs right.",
                visual = Inequality(from = 0, to = 8, value = 2, greater = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "Solve x + 6 < 10",
                options = listOf("x < 4", "x > 4", "x < 16", "x > 16"),
                correctIndex = 0,
                explanation = "Take 6 off both sides.",
                visual = Inequality(from = 0, to = 8, value = 4, greater = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "Solve 4x >= 20",
                options = listOf("x >= 5", "x <= 5", "x >= 80", "x > 5"),
                correctIndex = 0,
                explanation = "Divide by 4; a positive divisor keeps the sign.",
                visual = Inequality(from = 0, to = 10, value = 5, greater = true, orEqual = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "Solve -2x > 8",
                options = listOf("x > -4", "x < -4", "x > 4", "x < 4"),
                correctIndex = 1,
                explanation = "Dividing by -2 flips the sign.",
                visual = Inequality(from = -8, to = 2, value = -4, greater = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the largest whole number satisfying x < 7?",
                options = listOf("6", "7", "8", "there is none"),
                correctIndex = 0,
                explanation = "7 is excluded, so the whole number below it.",
                visual = Inequality(from = 2, to = 10, value = 7, greater = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these does NOT change the direction of the sign?",
                options = listOf("dividing by -5", "multiplying by -1", "subtracting 9", "multiplying by -3"),
                correctIndex = 2,
                explanation = "Adding and subtracting never flip an inequality.",
                visual = Inequality(from = 0, to = 8, value = 3, greater = true, reveal = false),
            ),
        ),
    )

    private val simultaneousEquations = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "simultaneous-equations",
        title = "Simultaneous equations",
        summary = "Two equations, two unknowns, one crossing point.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-systems-crossing",
                title = "Two lines, one crossing",
                summary = "What a pair of equations means.",
                steps = listOf(
                    Concept(
                        body = "Two equations in two unknowns meet at exactly one point.",
                        visual = Plot(
                            curve = Curve.Linear(m = -1f, c = 3f),
                            second = Curve.Linear(m = 1f, c = -1f),
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)")),
                        ),
                    ),
                    Concept(
                        body = "That crossing point is the only pair of values that satisfies both equations at once.",
                        formula = "x = 2 and y = 1",
                        visual = Plot(
                            curve = Curve.Linear(m = -1f, c = 3f),
                            second = Curve.Linear(m = 1f, c = -1f),
                            points = listOf(PlotPoint(x = 2f, y = 1f)),
                        ),
                    ),
                    Choice(
                        question = "Where do these two lines cross?",
                        options = listOf("(1, 2)", "(2, 1)", "(3, 0)", "(0, 3)"),
                        correctIndex = 1,
                        explanation = "Read the crossing point straight off the grid.",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 3f), second = Curve.Linear(m = 1f, c = -1f)),
                    ),
                    Concept(
                        body = "Parallel lines never cross, so that pair of equations has no solution.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = 1f, c = -1.5f)),
                    ),
                    Choice(
                        question = "These two lines are parallel. How many solutions are there?",
                        options = listOf("one", "none", "infinitely many", "two"),
                        correctIndex = 1,
                        explanation = "Parallel lines never meet.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = 1f, c = -1.5f)),
                    ),
                    Choice(
                        question = "Reading a solution off a graph can be inexact. Why?",
                        options = listOf(
                            "the lines might be wrong",
                            "the crossing may fall between grid lines",
                            "graphs cannot show negatives",
                            "it is always exact",
                        ),
                        correctIndex = 1,
                        explanation = "Which is why the algebra methods that follow are worth learning.",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 2f), second = Curve.Linear(m = 2f, c = -1f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-systems-elimination",
                title = "Elimination",
                summary = "Add or subtract until one letter disappears.",
                steps = listOf(
                    Concept(
                        body = "Elimination adds the equations so that one letter cancels.",
                        formula = "(x + y = 3) + (x - y = 1) gives 2x = 4",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 3f), second = Curve.Linear(m = 1f, c = -1f)),
                    ),
                    Worked(
                        problem = "Solve x + y = 3 and x - y = 1",
                        lines = listOf(
                            "Add them - the y terms cancel.",
                            "2x = 4, so x = 2.",
                            "Put x = 2 back into x + y = 3.",
                        ),
                        result = "x = 2, y = 1",
                        visual = Plot(
                            curve = Curve.Linear(m = -1f, c = 3f),
                            second = Curve.Linear(m = 1f, c = -1f),
                            points = listOf(PlotPoint(x = 2f, y = 1f)),
                        ),
                    ),
                    Concept(
                        body = "When the matching terms have the same sign, subtract instead of adding.",
                        formula = "(3x + y = 10) - (x + y = 6) gives 2x = 4",
                        visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 4),
                    ),
                    Numeric(
                        question = "From 3x + y = 10 and x + y = 6, subtracting gives 2x = 4. What is x?",
                        answer = "2",
                        explanation = "Halve both sides.",
                        visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 4),
                    ),
                    Concept(
                        body = "If nothing cancels yet, scale one equation up until something does.",
                        formula = "2 * (x + y = 5) gives 2x + 2y = 10",
                        visual = ArrayDots(rows = 2, cols = 5),
                    ),
                    Choice(
                        question = "To eliminate y from 3x + 2y = 12 and x + y = 5, what should you do?",
                        options = listOf(
                            "double the second equation",
                            "add them as they are",
                            "triple the first",
                            "halve the first",
                        ),
                        correctIndex = 0,
                        explanation = "Doubling gives 2x + 2y = 10, and the 2y terms then cancel.",
                        visual = ArrayDots(rows = 2, cols = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-systems-substitution",
                title = "Substitution",
                summary = "Swap one letter out for what it equals.",
                steps = listOf(
                    Concept(
                        body = "If one equation already tells you what a letter equals, put that straight into the other.",
                        formula = "y = 2x turns 3x + y = 15 into 5x = 15",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Choice(
                        question = "If y = 2x, what does 3x + y = 15 become?",
                        options = listOf("3x + 2x = 15", "3x + 2 = 15", "5y = 15", "3x = 15"),
                        correctIndex = 0,
                        explanation = "Swap y for 2x, giving 5x = 15.",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Worked(
                        problem = "Solve y = x + 1 and 2x + y = 7",
                        lines = listOf(
                            "Replace y with x + 1 in the second equation.",
                            "2x + x + 1 = 7, so 3x = 6.",
                            "x = 2, then y = 2 + 1.",
                        ),
                        result = "x = 2, y = 3",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            second = Curve.Linear(m = -2f, c = 7f),
                            points = listOf(PlotPoint(x = 2f, y = 3f)),
                        ),
                    ),
                    Numeric(
                        question = "Solve 2x + 3 = 11. What is x?",
                        answer = "4",
                        explanation = "Take 3 from both sides, then halve.",
                        visual = Balance(leftX = 2, leftOnes = 3, rightOnes = 11, remove = 3),
                    ),
                    Concept(
                        body = "Substitution suits a system where one letter is already alone; elimination suits one where nothing is.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = -2f, c = 7f)),
                    ),
                    Choice(
                        question = "Which method fits y = 4 - x and 3x + 2y = 9 best?",
                        options = listOf("substitution", "elimination", "neither works", "graphing only"),
                        correctIndex = 0,
                        explanation = "y is already on its own, so drop it straight into the other equation.",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 4f), second = Curve.Linear(m = -1.5f, c = 4.5f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Where do these two lines cross?",
                options = listOf("(1, 2)", "(2, 1)", "(3, 0)", "(0, 3)"),
                correctIndex = 1,
                explanation = "Read the crossing point straight off the grid.",
                visual = Plot(curve = Curve.Linear(m = -1f, c = 3f), second = Curve.Linear(m = 1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "Adding x + y = 7 and x - y = 1 gives what?",
                options = listOf("2x = 8", "2y = 8", "2x = 6", "x = 8"),
                correctIndex = 0,
                explanation = "The y terms cancel and the numbers add.",
                visual = Balance(leftX = 2, leftOnes = 0, rightOnes = 8),
            ),
            QuizQuestion(
                prompt = "From 2x = 8 and x + y = 7, what is y?",
                options = listOf("3", "4", "7", "11"),
                correctIndex = 0,
                explanation = "x = 4, so y = 3.",
                visual = Balance(leftX = 1, leftOnes = 4, rightOnes = 7),
            ),
            QuizQuestion(
                prompt = "How many solutions do two parallel lines give?",
                options = listOf("none", "one", "two", "infinitely many"),
                correctIndex = 0,
                explanation = "They never cross.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = 1f, c = -1.5f)),
            ),
            QuizQuestion(
                prompt = "If y = 3x, what does 2x + y = 20 become?",
                options = listOf("5x = 20", "2x + 3 = 20", "6x = 20", "5y = 20"),
                correctIndex = 0,
                explanation = "2x + 3x = 5x.",
                visual = Plot(curve = Curve.Linear(m = 3f)),
            ),
            QuizQuestion(
                prompt = "Two identical lines are drawn on top of each other. How many solutions?",
                options = listOf("none", "one", "two", "infinitely many"),
                correctIndex = 3,
                explanation = "Every point on the line satisfies both equations.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = 1f, c = 1f)),
            ),
        ),
    )

    private val quadratics = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "quadratics",
        title = "Quadratics",
        summary = "Curves with two roots, and three ways to find them.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-quadratics-parabola",
                title = "The parabola",
                summary = "Roots, vertex and the shape itself.",
                steps = listOf(
                    Concept(
                        body = "A quadratic graphs as a parabola: symmetric, with one lowest point.",
                        formula = "y = ax² + bx + c",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markVertex = true),
                    ),
                    Concept(
                        body = "Where the curve meets the x-axis, y is zero. Those crossings are the roots.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = "How many times does this parabola cross the x-axis?",
                        options = listOf("never", "once", "twice", "three times"),
                        correctIndex = 2,
                        explanation = "It dips below the axis and comes back, so two roots.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1.5f)),
                    ),
                    Choice(
                        question = "What has changed to make this parabola open downwards?",
                        options = listOf("b is negative", "a is negative", "c is negative", "nothing"),
                        correctIndex = 1,
                        explanation = "A negative x² coefficient flips the whole curve.",
                        visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
                    ),
                    Numeric(
                        question = "Read the larger root off this graph.",
                        answer = "3",
                        explanation = "The curve is 0.5(x - 2)(x - 3), so it crosses at 2 and 3.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -2.5f, c = 3f)),
                    ),
                    Choice(
                        question = "This curve stays above the axis. How many real roots does it have?",
                        options = listOf("none", "one", "two", "infinitely many"),
                        correctIndex = 0,
                        explanation = "No crossing means no real solution.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-quadratics-factorising",
                title = "Factorising quadratics",
                summary = "Two brackets that multiply back.",
                steps = listOf(
                    Concept(
                        body = "Factorising finds the roots without a graph: two numbers that multiply to c and add to b.",
                        formula = "x² + 7x + 12 = (x + 3)(x + 4)",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Worked(
                        problem = "Factorise x² + 5x + 6",
                        lines = listOf(
                            "Which pairs multiply to 6? 1 and 6, or 2 and 3.",
                            "Which of those adds to 5? 2 and 3.",
                            "Write the brackets.",
                        ),
                        result = "(x + 2)(x + 3)",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = "Factorise x² + 9x + 20",
                        options = listOf("(x + 4)(x + 5)", "(x + 2)(x + 10)", "(x + 1)(x + 20)", "(x + 9)(x + 20)"),
                        correctIndex = 0,
                        explanation = "4 times 5 is 20, and 4 plus 5 is 9.",
                        visual = AreaGrid(cols = 5, rows = 4, showArea = false),
                    ),
                    Concept(
                        body = "If two things multiply to zero, one of them must be zero. That turns the brackets into the roots.",
                        formula = "(x - 4)(x + 1) = 0 gives x = 4 or x = -1",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1.5f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = "Solve (x - 4)(x + 1) = 0",
                        options = listOf("x = 4 or x = -1", "x = -4 or x = 1", "x = 4 or x = 1", "x = 3 only"),
                        correctIndex = 0,
                        explanation = "Set each factor to zero.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1.5f, c = -2f)),
                    ),
                    Numeric(
                        question = "Solve x² - 9 = 0. What is the positive root?",
                        answer = "3",
                        explanation = "The difference of two squares: (x - 3)(x + 3).",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markRoots = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-quadratics-formula",
                title = "The quadratic formula",
                summary = "The method that never fails.",
                steps = listOf(
                    Concept(
                        body = "Not every quadratic factorises neatly. The formula solves all of them.",
                        formula = "x = (-b ± the root of (b² - 4ac)) over 2a",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1.5f), markRoots = true),
                    ),
                    Concept(
                        body = "The part under the root sign decides how many roots there are.",
                        formula = "b² - 4ac",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = "If b² - 4ac is negative, how many real roots are there?",
                        options = listOf("none", "one", "two", "three"),
                        correctIndex = 0,
                        explanation = "A negative under the root sign has no real value, so the curve misses the axis.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
                    ),
                    Choice(
                        question = "If b² - 4ac is exactly zero, what does the graph do?",
                        options = listOf("misses the axis", "touches it once", "crosses twice", "is a straight line"),
                        correctIndex = 1,
                        explanation = "The two roots have merged into one, right at the vertex.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), markVertex = true),
                    ),
                    Worked(
                        problem = "Solve x² - 5x + 6 = 0 with the formula",
                        lines = listOf(
                            "a = 1, b = -5, c = 6.",
                            "b² - 4ac = 25 - 24 = 1.",
                            "x = (5 ± 1) over 2.",
                        ),
                        result = "x = 3 or x = 2",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -2.5f, c = 3f), markRoots = true),
                    ),
                    Numeric(
                        question = "For x² - 6x + 9 = 0, what is b² - 4ac?",
                        answer = "0",
                        explanation = "36 - 36, so the curve just touches the axis.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), markVertex = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Where does this parabola cross the x-axis?",
                options = listOf("at 1 and 3", "at -2 and 2", "only at 0", "nowhere"),
                correctIndex = 1,
                explanation = "0.5x² - 2 is zero when x² = 4.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f)),
            ),
            QuizQuestion(
                prompt = "Solve (x - 4)(x + 1) = 0",
                options = listOf("x = 4 or x = -1", "x = -4 or x = 1", "x = 4 or x = 1", "x = 3 only"),
                correctIndex = 0,
                explanation = "Set each factor to zero.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1.5f, c = -2f)),
            ),
            QuizQuestion(
                prompt = "Factorise x² + 8x + 15",
                options = listOf("(x + 3)(x + 5)", "(x + 1)(x + 15)", "(x + 4)(x + 4)", "(x + 8)(x + 15)"),
                correctIndex = 0,
                explanation = "3 times 5 is 15, and they add to 8.",
                visual = AreaGrid(cols = 5, rows = 3, showArea = false),
            ),
            QuizQuestion(
                prompt = "Which point is the vertex of this parabola?",
                options = listOf("(0, -2)", "(-2, 0)", "(2, 0)", "(0, 2)"),
                correctIndex = 0,
                explanation = "The lowest point sits on the axis of symmetry.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markVertex = true),
            ),
            QuizQuestion(
                prompt = "A quadratic has b² - 4ac = 0. How many real roots?",
                options = listOf("none", "one", "two", "cannot tell"),
                correctIndex = 1,
                explanation = "The curve touches the axis exactly once.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f), markVertex = true),
            ),
            QuizQuestion(
                prompt = "What makes a parabola open downwards?",
                options = listOf("a is negative", "b is negative", "c is negative", "a is zero"),
                correctIndex = 0,
                explanation = "The sign of the x² coefficient sets which way it opens.",
                visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
            ),
        ),
    )

    private val indices = learnUnit(
        topic = MathTopic.ALGEBRA,
        urlSlug = "powers-and-roots",
        title = "Powers and roots",
        summary = "Index laws, zero powers, and what a fractional index means.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "algebra-indices-powers",
                title = "What an index counts",
                summary = "Repeated multiplying, written short.",
                steps = listOf(
                    Concept(
                        body = "An index counts how many times a number multiplies itself.",
                        formula = "2^5 = 32",
                        visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
                    ),
                    Concept(
                        body = "Multiplying powers of the same base adds the indices.",
                        formula = "a^m * a^n = a^(m+n)",
                        visual = Steps(terms = listOf(1, 2, 4, 8, 16), multiply = true),
                    ),
                    Choice(
                        question = "Simplify x^5 * x^3",
                        options = listOf("x^8", "x^15", "x^2", "2x^8"),
                        correctIndex = 0,
                        explanation = "Add the indices.",
                        visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
                    ),
                    Concept(
                        body = "A power of a power multiplies the indices instead.",
                        formula = "(a^m)^n = a^(m * n)",
                        visual = Steps(terms = listOf(2, 4, 16), multiply = true),
                    ),
                    Numeric(
                        question = "What is 3^2 * 3^2?",
                        answer = "81",
                        explanation = "3^4, which is 81.",
                        visual = Steps(terms = listOf(3, 9, 27, 81), multiply = true),
                    ),
                    Choice(
                        question = "Why is 2^3 * 3^2 not a single power?",
                        options = listOf("the indices differ", "the bases differ", "it is one", "3^2 is not a power"),
                        correctIndex = 1,
                        explanation = "The index laws only apply when the base is the same.",
                        visual = Steps(terms = listOf(2, 4, 8), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-indices-dividing",
                title = "Dividing and zero powers",
                summary = "Where a^0 = 1 comes from.",
                steps = listOf(
                    Concept(
                        body = "Dividing powers of the same base subtracts the indices.",
                        formula = "a^m ÷ a^n = a^(m-n)",
                        visual = Steps(terms = listOf(16, 8, 4, 2), multiply = true),
                    ),
                    Choice(
                        question = "Simplify y^7 ÷ y^4",
                        options = listOf("y^3", "y^11", "y^2", "3y"),
                        correctIndex = 0,
                        explanation = "Dividing subtracts the indices.",
                        visual = Steps(terms = listOf(16, 8, 4, 2), multiply = true),
                    ),
                    Concept(
                        body = "Divide something by itself and you get 1. The index rule says you get a^0, so a^0 must be 1.",
                        formula = "a^3 ÷ a^3 = a^0 = 1",
                        visual = Steps(terms = listOf(8, 4, 2, 1), multiply = true),
                    ),
                    Numeric(
                        question = "What is 5^0 + 2^3?",
                        answer = "9",
                        explanation = "1 + 8.",
                        visual = Steps(terms = listOf(1, 2, 4, 8), multiply = true),
                    ),
                    Concept(
                        body = "Keep halving past 1 and you get the negative indices.",
                        formula = "a^-n = 1 ÷ a^n",
                        visual = Steps(terms = listOf(8, 4, 2, 1), multiply = true),
                    ),
                    Choice(
                        question = "What is 2^-3?",
                        options = listOf("-8", "one eighth", "-6", "8"),
                        correctIndex = 1,
                        explanation = "A negative index means one over the positive power.",
                        visual = Steps(terms = listOf(8, 4, 2, 1), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "algebra-indices-roots",
                title = "Fractional indices",
                summary = "Half an index is a square root.",
                steps = listOf(
                    Concept(
                        body = "Half an index plus half an index makes one whole x, so a half index must be the square root.",
                        formula = "x^(1÷2) * x^(1÷2) = x",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = "x^(1÷2) means...",
                        options = listOf("half of x", "the square root of x", "x divided by 2", "x squared"),
                        correctIndex = 1,
                        explanation = "Half an index plus half an index makes one whole x.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Concept(
                        body = "A third of an index is the cube root, and so on down.",
                        formula = "8^(1÷3) = 2",
                        visual = Steps(terms = listOf(2, 4, 8), multiply = true),
                    ),
                    Numeric(
                        question = "What is 25^(1÷2)?",
                        answer = "5",
                        explanation = "The square root of 25.",
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false),
                    ),
                    Concept(
                        body = "The top of the fraction is still a power. Take the root first, then raise it.",
                        formula = "9^(3÷2) = 3^3 = 27",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false),
                    ),
                    Choice(
                        question = "What is 16^(3÷4)?",
                        options = listOf("8", "12", "4", "64"),
                        correctIndex = 0,
                        explanation = "The fourth root of 16 is 2, and 2 cubed is 8.",
                        visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Simplify y^7 ÷ y^4",
                options = listOf("y^3", "y^11", "y^2", "3y"),
                correctIndex = 0,
                explanation = "Dividing subtracts the indices.",
                visual = Steps(terms = listOf(16, 8, 4, 2), multiply = true),
            ),
            QuizQuestion(
                prompt = "What is 4^0?",
                options = listOf("0", "1", "4", "undefined"),
                correctIndex = 1,
                explanation = "Halving down to the zero index lands on 1.",
                visual = Steps(terms = listOf(16, 4, 1), multiply = true),
            ),
            QuizQuestion(
                prompt = "Simplify a^4 * a^6",
                options = listOf("a^10", "a^24", "a^2", "2a^10"),
                correctIndex = 0,
                explanation = "Multiplying adds the indices.",
                visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
            ),
            QuizQuestion(
                prompt = "What is 3^-2?",
                options = listOf("-9", "one ninth", "-6", "9"),
                correctIndex = 1,
                explanation = "A negative index flips it into a fraction.",
                visual = Steps(terms = listOf(9, 3, 1), multiply = true),
            ),
            QuizQuestion(
                prompt = "What is 49^(1÷2)?",
                options = listOf("7", "24.5", "98", "2401"),
                correctIndex = 0,
                explanation = "A half index is the square root.",
                visual = AreaGrid(cols = 7, rows = 7, showArea = false),
            ),
            QuizQuestion(
                prompt = "Simplify (x^3)^2",
                options = listOf("x^6", "x^5", "x^9", "2x^3"),
                correctIndex = 0,
                explanation = "A power of a power multiplies the indices.",
                visual = Steps(terms = listOf(2, 4, 16), multiply = true),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        expressions,
        linearEquations,
        straightLineGraphs,
        inequalities,
        simultaneousEquations,
        quadratics,
        indices,
    )
}
