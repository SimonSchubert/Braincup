package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AngleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Balance
import com.inspiredandroid.braincup.learn.LearnVisual.CircleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Polygon
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.UnitCircleFigure
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.learnUnit

/** Grades 9-10: quadratics and systems, similarity and circle theorems, right-triangle trig. */
internal object Grade9To10Content {

    private val level = GradeLevel.GRADES_9_10

    private val algebra = learnUnit(
        level = level,
        topic = MathTopic.ALGEBRA,
        summary = "Quadratics, simultaneous equations and the laws of indices.",
        lessons = listOf(
            LessonSpec(
                id = "g910-algebra-quadratics",
                title = "Quadratics",
                summary = "Roots, vertex and factorising.",
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
                    Concept(
                        body = "Factorising finds the roots without a graph: two numbers that multiply to c and add to b.",
                        formula = "x² + 7x + 12 = (x + 3)(x + 4)",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Numeric(
                        question = "Read the larger root off this graph.",
                        answer = "3",
                        explanation = "The curve is 0.5(x - 2)(x - 3), so it crosses at 2 and 3.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -2.5f, c = 3f)),
                    ),
                    Choice(
                        question = "What has changed to make this parabola open downwards?",
                        options = listOf("b is negative", "a is negative", "c is negative", "nothing"),
                        correctIndex = 1,
                        explanation = "A negative x² coefficient flips the whole curve.",
                        visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-algebra-systems",
                title = "Simultaneous equations",
                summary = "Two lines, one crossing point.",
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
                    Choice(
                        question = "If y = 2x, what does 3x + y = 15 become?",
                        options = listOf("3x + 2x = 15", "3x + 2 = 15", "5y = 15", "3x = 15"),
                        correctIndex = 0,
                        explanation = "Swap y for 2x, giving 5x = 15.",
                        visual = Plot(curve = Curve.Linear(m = 2f)),
                    ),
                    Numeric(
                        question = "Solve 2x + 3 = 11. What is x?",
                        answer = "4",
                        explanation = "Take 3 from both sides, then halve.",
                        visual = Balance(leftX = 2, leftOnes = 3, rightOnes = 11, remove = 3),
                    ),
                    Choice(
                        question = "These two lines are parallel. How many solutions are there?",
                        options = listOf("one", "none", "infinitely many", "two"),
                        correctIndex = 1,
                        explanation = "Parallel lines never meet.",
                        visual = Plot(curve = Curve.Linear(m = 1f, c = 1f), second = Curve.Linear(m = 1f, c = -1.5f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-algebra-indices",
                title = "Powers and roots",
                summary = "Index laws, zero powers and roots.",
                steps = listOf(
                    Concept(
                        body = "An index counts how many times a number multiplies itself.",
                        formula = "2^5 = 32",
                        visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
                    ),
                    Concept(
                        body = "Multiplying powers of the same base adds the indices.",
                        formula = "a^m x a^n = a^(m+n)",
                        visual = Steps(terms = listOf(1, 2, 4, 8, 16), multiply = true),
                    ),
                    Choice(
                        question = "Simplify x^5 x x^3",
                        options = listOf("x^8", "x^15", "x^2", "2x^8"),
                        correctIndex = 0,
                        explanation = "Add the indices.",
                        visual = Steps(terms = listOf(2, 4, 8, 16), multiply = true),
                    ),
                    Concept(
                        body = "Keep halving past 1 and you get the negative indices.",
                        formula = "a^0 = 1 and a^-n = 1 / a^n",
                        visual = Steps(terms = listOf(8, 4, 2, 1), multiply = true),
                    ),
                    Numeric(
                        question = "What is 5^0 + 2^3?",
                        answer = "9",
                        explanation = "1 + 8.",
                        visual = Steps(terms = listOf(1, 2, 4, 8), multiply = true),
                    ),
                    Choice(
                        question = "x^(1/2) means...",
                        options = listOf("half of x", "the square root of x", "x divided by 2", "x squared"),
                        correctIndex = 1,
                        explanation = "Half an index plus half an index makes one whole x.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
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
                prompt = "Where do these two lines cross?",
                options = listOf("(1, 2)", "(2, 1)", "(3, 0)", "(0, 3)"),
                correctIndex = 1,
                explanation = "Read the crossing point straight off the grid.",
                visual = Plot(curve = Curve.Linear(m = -1f, c = 3f), second = Curve.Linear(m = 1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "Simplify y^7 / y^4",
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
                prompt = "Which point is the vertex of this parabola?",
                options = listOf("(0, -2)", "(-2, 0)", "(2, 0)", "(0, 2)"),
                correctIndex = 0,
                explanation = "The lowest point sits on the axis of symmetry.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f)),
            ),
        ),
    )

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Similarity and scale factors, transformations and circle theorems.",
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-similarity",
                title = "Similar figures",
                summary = "Same shape, and what that does to area.",
                steps = listOf(
                    Concept(
                        body = "Similar shapes share every angle; the lengths all scale by one factor.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "Every length doubles, and the shape is unmistakably the same.",
                        formula = "new = k x old",
                        visual = RightTriangle(a = 8, b = 6),
                    ),
                    Choice(
                        question = "Two matching sides are 4 cm and 10 cm. What is the scale factor?",
                        options = listOf("0.4", "2.5", "6", "14"),
                        correctIndex = 1,
                        explanation = "10 / 4.",
                        visual = RightTriangle(a = 10, b = 6, labels = false),
                    ),
                    Numeric(
                        question = "This 3-4-5 triangle is enlarged by 3. How long is the longest side?",
                        answer = "15",
                        explanation = "5 x 3.",
                        visual = RightTriangle(a = 4, b = 3, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = "Area does not scale like length: double the sides and you get four times the squares.",
                        formula = "length x k, area x k², volume x k³",
                        visual = AreaGrid(cols = 4, rows = 4),
                    ),
                    Choice(
                        question = "A shape is enlarged by scale factor 4. Its area is multiplied by...",
                        options = listOf("4", "8", "12", "16"),
                        correctIndex = 3,
                        explanation = "Area scales by the square of the factor.",
                        visual = AreaGrid(cols = 4, rows = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-transformations",
                title = "Transformations",
                summary = "Slide, flip and turn on the grid.",
                steps = listOf(
                    Concept(
                        body = "Translations slide, reflections flip, rotations turn, enlargements resize.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = "A translation vector reads across, then up.",
                        formula = "(3, -2)",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(
                                PlotPoint(x = -1f, y = 1f, label = "A"),
                                PlotPoint(x = 2f, y = -1f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = "Translate this point by (1, -2). Where does it land?",
                        options = listOf("(3, -1)", "(3, 3)", "(1, -1)", "(2, -2)"),
                        correctIndex = 0,
                        explanation = "Add the vector to the coordinates.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)")),
                        ),
                    ),
                    Concept(
                        body = "Reflecting in the y-axis flips the sign of x only.",
                        formula = "(x, y) becomes (-x, y)",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(
                                PlotPoint(x = 2f, y = 2f, label = "A"),
                                PlotPoint(x = -2f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Numeric(
                        question = "Reflect this point in the x-axis. What is the new y-coordinate?",
                        answer = "-2",
                        explanation = "Reflecting in the x-axis negates y.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "A")),
                        ),
                    ),
                    Choice(
                        question = "Which transformation does NOT keep the size?",
                        options = listOf("translation", "reflection", "rotation", "enlargement"),
                        correctIndex = 3,
                        explanation = "The other three are congruence transformations.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-circle-theorems",
                title = "Circle theorems",
                summary = "Angles at the centre and on the edge.",
                steps = listOf(
                    Concept(
                        body = "The angle at the centre is exactly twice the one at the circumference on the same arc.",
                        visual = CircleFigure(centreAngle = 80),
                    ),
                    Concept(
                        body = "Stretch the centre angle to a straight 180 and the edge angle becomes a right angle.",
                        visual = CircleFigure(centreAngle = 180),
                    ),
                    Choice(
                        question = "The centre angle is 80. What is the angle at the circumference?",
                        options = listOf("20", "40", "80", "160"),
                        correctIndex = 1,
                        explanation = "Half the centre angle.",
                        visual = CircleFigure(centreAngle = 80, reveal = false),
                    ),
                    Concept(
                        body = "Opposite angles in a cyclic quadrilateral add to 180.",
                        formula = "a + c = 180",
                        visual = Polygon(sides = 4, countCorners = false),
                    ),
                    Numeric(
                        question = "One angle of a cyclic quadrilateral is 110. How many degrees is the opposite one?",
                        answer = "70",
                        explanation = "180 - 110.",
                        visual = AngleFigure(degrees = 110, supplement = true, reveal = false),
                    ),
                    Choice(
                        question = "A tangent meets the radius at an angle of...",
                        options = listOf("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = "A tangent is always perpendicular to the radius it touches.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Two similar shapes have scale factor 3. Their areas differ by a factor of...",
                options = listOf("3", "6", "9", "27"),
                correctIndex = 2,
                explanation = "Area scales by the square.",
                visual = AreaGrid(cols = 3, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Translate this point by (2, -3). Where does it land?",
                options = listOf("(3, -1)", "(3, 5)", "(-1, -1)", "(2, 6)"),
                correctIndex = 0,
                explanation = "(1 + 2, 2 - 3).",
                visual = Plot(
                    curve = Curve.Linear(m = 0.5f),
                    points = listOf(PlotPoint(x = 1f, y = 2f, label = "(1, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "Reflect this point in the y-axis.",
                options = listOf("(-2, 2)", "(2, -2)", "(-2, -2)", "(2, 2)"),
                correctIndex = 0,
                explanation = "Only the sign of x flips.",
                visual = Plot(
                    curve = Curve.Linear(m = 0.5f),
                    points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "The centre angle is 120. What is the angle at the circumference?",
                options = listOf("30", "60", "120", "240"),
                correctIndex = 1,
                explanation = "Half of the centre angle.",
                visual = CircleFigure(centreAngle = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = "One angle of a cyclic quadrilateral is 95. Its opposite angle is...",
                options = listOf("85", "95", "105", "185"),
                correctIndex = 0,
                explanation = "Opposite angles sum to 180.",
                visual = AngleFigure(degrees = 95, supplement = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "An angle in a semicircle is always...",
                options = listOf("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = "Half of the straight angle at the centre.",
                visual = CircleFigure(centreAngle = 180, reveal = false),
            ),
        ),
    )

    private val trigonometry = learnUnit(
        level = level,
        topic = MathTopic.TRIGONOMETRY,
        summary = "Sine, cosine and tangent in right triangles, and where they are used.",
        lessons = listOf(
            LessonSpec(
                id = "g910-trigonometry-ratios",
                title = "Sine, cosine and tangent",
                summary = "Three ratios fixed by the angle alone.",
                steps = listOf(
                    Concept(
                        body = "Name the sides from the angle: opposite faces it, adjacent touches it, hypotenuse faces the right angle.",
                        visual = RightTriangle(a = 4, b = 3, angle = 37),
                    ),
                    Concept(
                        body = "Similar triangles share these ratios, so they depend on the angle and nothing else.",
                        formula = "sin = opp/hyp, cos = adj/hyp, tan = opp/adj",
                        visual = RightTriangle(a = 8, b = 6, angle = 37),
                    ),
                    Choice(
                        question = "Which ratio pairs the opposite with the adjacent side?",
                        options = listOf("sine", "cosine", "tangent", "none of them"),
                        correctIndex = 2,
                        explanation = "tan = opposite / adjacent.",
                        visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
                    ),
                    Numeric(
                        question = "Opposite 3, hypotenuse 5. What is the sine, as a decimal?",
                        answer = "0.6",
                        explanation = "3 / 5.",
                        visual = RightTriangle(a = 4, b = 3, angle = 37),
                    ),
                    Concept(
                        body = "Worth knowing by heart: sin 30 = 0.5 and tan 45 = 1.",
                        visual = UnitCircleFigure(degrees = 30),
                    ),
                    Choice(
                        question = "As the angle grows from 0 to 90, the sine...",
                        options = listOf("falls from 1 to 0", "grows from 0 to 1", "stays at 1", "grows without limit"),
                        correctIndex = 1,
                        explanation = "It is the height on the unit circle.",
                        visual = UnitCircleFigure(degrees = 70, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-trigonometry-missing-sides",
                title = "Finding sides and angles",
                summary = "Label first, then pick the ratio.",
                steps = listOf(
                    Concept(
                        body = "Label all three sides, then pick the ratio linking what you know to what you want.",
                        visual = RightTriangle(a = 4, b = 3, angle = 37),
                    ),
                    Worked(
                        problem = "Angle 30, hypotenuse 10. How long is the opposite side?",
                        lines = listOf(
                            "Known hypotenuse, wanted opposite: that is sine.",
                            "sin 30 = opposite / 10.",
                            "opposite = 10 x sin 30.",
                            "sin 30 = 0.5.",
                        ),
                        result = "opposite = 5",
                        visual = RightTriangle(a = 9, b = 5, angle = 30, unknown = Side.B),
                    ),
                    Choice(
                        question = "You know the adjacent side and want the hypotenuse. Which ratio?",
                        options = listOf("sine", "cosine", "tangent", "Pythagoras only"),
                        correctIndex = 1,
                        explanation = "cos = adjacent / hypotenuse.",
                        visual = RightTriangle(a = 5, b = 3, angle = 31, labels = false),
                    ),
                    Concept(
                        body = "To go from a ratio back to an angle, use the inverse functions.",
                        formula = "tan x = 1 gives x = 45",
                        visual = RightTriangle(a = 4, b = 4, angle = 45),
                    ),
                    Numeric(
                        question = "Opposite and adjacent are both 5. What is the angle, in degrees?",
                        answer = "45",
                        explanation = "tan is 1, and that happens at 45.",
                        visual = RightTriangle(a = 5, b = 5, labels = false),
                    ),
                    Choice(
                        question = "Two sides known, no angle anywhere. Use...",
                        options = listOf("sine", "cosine", "Pythagoras", "tangent"),
                        correctIndex = 2,
                        explanation = "Trig ratios always involve an angle.",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-trigonometry-applications",
                title = "Elevation and bearings",
                summary = "Turning a real situation into a triangle.",
                steps = listOf(
                    Concept(
                        body = "An angle of elevation is measured up from the horizontal.",
                        visual = RightTriangle(a = 6, b = 4, angle = 34),
                    ),
                    Worked(
                        problem = "From 50 m away the elevation to a tower top is 40 degrees.",
                        lines = listOf(
                            "Adjacent is 50, opposite is the height.",
                            "That pair is tangent.",
                            "tan 40 is about 0.839.",
                            "50 x 0.839.",
                        ),
                        result = "About 42 m",
                        visual = RightTriangle(a = 6, b = 5, angle = 40, unknown = Side.B),
                    ),
                    Choice(
                        question = "Which ratio connects a horizontal distance with a height?",
                        options = listOf("sine", "cosine", "tangent", "none"),
                        correctIndex = 2,
                        explanation = "Horizontal is adjacent, height is opposite.",
                        visual = RightTriangle(a = 6, b = 4, angle = 34, labels = false),
                    ),
                    Numeric(
                        question = "A ramp rises 3 m over 4 m. How long is the ramp surface, in metres?",
                        answer = "5",
                        explanation = "9 + 16 = 25.",
                        visual = RightTriangle(a = 4, b = 3, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = "Bearings are measured clockwise from north and always written with three digits.",
                        visual = UnitCircleFigure(degrees = 45, showSin = false, showCos = false, label = "045"),
                    ),
                    Choice(
                        question = "A 30 m kite string at 60 degrees elevation. Which gives the height?",
                        options = listOf("30 x sin 60", "30 x cos 60", "30 / tan 60", "30 + 60"),
                        correctIndex = 0,
                        explanation = "String is the hypotenuse, height is opposite.",
                        visual = RightTriangle(a = 3, b = 5, angle = 60, labels = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which fraction is the sine of the marked angle?",
                options = listOf("opposite / adjacent", "opposite / hypotenuse", "adjacent / hypotenuse", "hypotenuse / opposite"),
                correctIndex = 1,
                explanation = "Sine pairs opposite with hypotenuse.",
                visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
            ),
            QuizQuestion(
                prompt = "Opposite 6, adjacent 8. What is the tangent?",
                options = listOf("0.6", "0.75", "0.8", "1.33"),
                correctIndex = 1,
                explanation = "6 / 8.",
                visual = RightTriangle(a = 8, b = 6, angle = 37),
            ),
            QuizQuestion(
                prompt = "Hypotenuse 20, angle 30. How long is the opposite side?",
                options = listOf("10", "14.1", "17.3", "40"),
                correctIndex = 0,
                explanation = "20 x sin 30.",
                visual = RightTriangle(a = 9, b = 5, angle = 30, unknown = Side.B),
            ),
            QuizQuestion(
                prompt = "What is tan 45?",
                options = listOf("0", "0.5", "1", "undefined"),
                correctIndex = 2,
                explanation = "The two legs are equal.",
                visual = RightTriangle(a = 4, b = 4, angle = 45, labels = false),
            ),
            QuizQuestion(
                prompt = "You know two sides and want the angle. You use...",
                options = listOf("Pythagoras", "an inverse trig function", "the mean", "a bearing"),
                correctIndex = 1,
                explanation = "Inverse sine, cosine or tangent turns a ratio into an angle.",
                visual = RightTriangle(a = 4, b = 3, labels = false),
            ),
            QuizQuestion(
                prompt = "Elevation 60, horizontal distance 10 m. The height is...",
                options = listOf("10 x sin 60", "10 x cos 60", "10 x tan 60", "10 / tan 60"),
                correctIndex = 2,
                explanation = "Opposite over adjacent is tangent.",
                visual = RightTriangle(a = 4, b = 6, angle = 60, unknown = Side.B),
            ),
        ),
    )

    private val functions = learnUnit(
        level = level,
        topic = MathTopic.FUNCTIONS,
        summary = "Function notation, linear and quadratic graphs, and transformations.",
        lessons = listOf(
            LessonSpec(
                id = "g910-functions-notation",
                title = "Function notation",
                summary = "One input, exactly one output.",
                steps = listOf(
                    Concept(
                        body = "A function is a machine: put a number in, one number comes out.",
                        formula = "f(x) = x + 1",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "f(1) = 2")),
                        ),
                    ),
                    Concept(
                        body = "f(2) does not mean f times 2. It is the output at input 2.",
                        visual = Plot(
                            curve = Curve.Linear(m = 1f, c = 1f),
                            points = listOf(PlotPoint(x = 2f, y = 3f, label = "f(2) = 3")),
                        ),
                    ),
                    Numeric(
                        question = "If f(x) = x² - 1, what is f(4)?",
                        answer = "15",
                        explanation = "16 - 1.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1f)),
                    ),
                    Concept(
                        body = "The domain is the inputs allowed; the range is the outputs produced.",
                        visual = Plot(curve = Curve.Linear(m = 1f)),
                    ),
                    Choice(
                        question = "Which of these is NOT a function?",
                        options = listOf("y = x²", "y = 3x + 1", "x = y²", "y = 1/x"),
                        correctIndex = 2,
                        explanation = "x = y² gives two y values for most x.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f)),
                    ),
                    Choice(
                        question = "For f(x) = 5 - x, what is f(-2)?",
                        options = listOf("3", "7", "-7", "-3"),
                        correctIndex = 1,
                        explanation = "5 - (-2).",
                        visual = Plot(curve = Curve.Linear(m = -1f, c = 2f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-linear-quadratic",
                title = "Linear and quadratic graphs",
                summary = "Straight lines, parabolas, vertices and roots.",
                steps = listOf(
                    Concept(
                        body = "A line rises at a constant rate. A parabola's rate keeps changing.",
                        visual = Plot(curve = Curve.Linear(m = 1f), second = Curve.Quadratic(a = 0.5f, c = -2f)),
                    ),
                    Concept(
                        body = "The vertex sits on the line of symmetry.",
                        formula = "x = -b / 2a",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f), markVertex = true),
                    ),
                    Choice(
                        question = "Which way does this parabola open?",
                        options = listOf("upwards", "downwards", "sideways", "it is a straight line"),
                        correctIndex = 1,
                        explanation = "A negative x² coefficient flips it.",
                        visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
                    ),
                    Numeric(
                        question = "Find the x-coordinate of this vertex.",
                        answer = "1",
                        explanation = "-b / 2a with a = 0.5 and b = -1.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f)),
                    ),
                    Concept(
                        body = "Where a graph crosses the x-axis, y is zero. Those are the roots.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -2f), markRoots = true),
                    ),
                    Choice(
                        question = "How many times does this curve cross the x-axis?",
                        options = listOf("never", "once", "twice", "infinitely often"),
                        correctIndex = 0,
                        explanation = "It sits entirely above the axis.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-transformations",
                title = "Transforming graphs",
                summary = "Four moves for every graph.",
                steps = listOf(
                    Concept(
                        body = "Change the formula and the graph moves in a predictable way.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f)),
                    ),
                    Concept(
                        body = "Adding outside lifts the whole curve.",
                        formula = "f(x) + 1.5",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f), second = Curve.Quadratic(a = 0.5f, c = 1.5f)),
                    ),
                    Choice(
                        question = "The green curve is y = (x - 2)². How has it moved?",
                        options = listOf("2 to the left", "2 to the right", "2 up", "2 down"),
                        correctIndex = 1,
                        explanation = "Inside changes move the opposite way to their sign.",
                        visual = Plot(
                            curve = Curve.Quadratic(a = 0.5f),
                            second = Curve.Quadratic(a = 0.5f, b = -2f, c = 2f),
                        ),
                    ),
                    Concept(
                        body = "A minus outside flips the curve in the x-axis.",
                        formula = "-f(x)",
                        visual = Plot(
                            curve = Curve.Quadratic(a = 0.5f, c = -1f),
                            second = Curve.Quadratic(a = -0.5f, c = 1f),
                        ),
                    ),
                    Numeric(
                        question = "y = x² is shifted 1.5 up. What is y when x = 0?",
                        answer = "1.5",
                        explanation = "The vertex rises with the curve.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1.5f)),
                    ),
                    Choice(
                        question = "Compared with the orange curve, the green one is...",
                        options = listOf("shifted up", "three times taller", "shifted right", "three times wider"),
                        correctIndex = 1,
                        explanation = "Multiplying outside stretches every output.",
                        visual = Plot(curve = Curve.Quadratic(a = 0.2f), second = Curve.Quadratic(a = 0.6f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "If f(x) = 3x - 4, what is f(5)?",
                options = listOf("11", "15", "19", "-1"),
                correctIndex = 0,
                explanation = "15 - 4.",
                visual = Plot(curve = Curve.Linear(m = 1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "If f(x) = x² + 2, what is f(-3)?",
                options = listOf("-7", "7", "11", "-11"),
                correctIndex = 2,
                explanation = "9 + 2.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = 1f)),
            ),
            QuizQuestion(
                prompt = "What is the x-coordinate of this vertex?",
                options = listOf("-4", "-2", "1", "4"),
                correctIndex = 2,
                explanation = "-b / 2a.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, b = -1f, c = -1f)),
            ),
            QuizQuestion(
                prompt = "Which way does this parabola open?",
                options = listOf("upwards", "downwards", "sideways", "as a line"),
                correctIndex = 1,
                explanation = "The x² coefficient is negative.",
                visual = Plot(curve = Curve.Quadratic(a = -0.5f, c = 2f)),
            ),
            QuizQuestion(
                prompt = "The green curve is the orange one shifted how?",
                options = listOf("up", "down", "left", "right"),
                correctIndex = 0,
                explanation = "Adding outside the function lifts it.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f, c = -1.5f), second = Curve.Quadratic(a = 0.5f, c = 0.5f)),
            ),
            QuizQuestion(
                prompt = "Compared with y = f(x), the graph of y = f(x - 3) is...",
                options = listOf("3 left", "3 right", "3 up", "3 down"),
                correctIndex = 1,
                explanation = "Inside changes move the opposite way to their sign.",
                visual = Plot(curve = Curve.Quadratic(a = 0.5f), second = Curve.Quadratic(a = 0.5f, b = -2f, c = 2f)),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(algebra, geometry, trigonometry, functions)
}
