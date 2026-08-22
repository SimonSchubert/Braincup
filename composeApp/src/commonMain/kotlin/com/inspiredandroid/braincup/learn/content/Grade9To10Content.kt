package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
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
                summary = "Factorise, solve, and know when to use the formula.",
                steps = listOf(
                    Concept(
                        body = "A quadratic has x² as its highest power. Its graph is a parabola - a symmetric U, or an upside-down U when the x² term is negative.",
                        formula = "y = ax² + bx + c",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Concept(
                        body = "Factorising reverses expanding. For x² + bx + c, look for two numbers that multiply to c and add to b.",
                        formula = "x² + 7x + 12 = (x + 3)(x + 4)",
                    ),
                    Worked(
                        problem = "Solve x² + 7x + 12 = 0",
                        lines = listOf(
                            "Two numbers multiplying to 12 and adding to 7 are 3 and 4.",
                            "So the equation is (x + 3)(x + 4) = 0.",
                            "A product is zero only when one of its factors is zero.",
                            "Either x + 3 = 0 or x + 4 = 0.",
                        ),
                        result = "x = -3 or x = -4",
                    ),
                    Choice(
                        question = "Factorise x² - 9",
                        options = listOf("(x - 3)²", "(x - 3)(x + 3)", "(x - 9)(x + 1)", "x(x - 9)"),
                        correctIndex = 1,
                        explanation = "Difference of two squares: a² - b² = (a - b)(a + b).",
                    ),
                    Concept(
                        body = "When no neat factors exist, the quadratic formula always works. The part under the root, b² - 4ac, tells you how many real solutions there are.",
                        formula = "x = (-b ± root(b² - 4ac)) / 2a",
                    ),
                    Numeric(
                        question = "Solve x² - 5x + 6 = 0. What is the larger root?",
                        answer = "3",
                        explanation = "It factorises as (x - 2)(x - 3), so the roots are 2 and 3.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-algebra-systems",
                title = "Simultaneous equations",
                summary = "Two equations, two unknowns, one crossing point.",
                steps = listOf(
                    Concept(
                        body = "Two equations with two unknowns pin down a single point: the place where the two lines cross.",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Concept(
                        body = "Elimination adds or subtracts the two equations so that one letter cancels out completely.",
                        formula = "(x + y = 10) - (x - y = 2) gives 2y = 8",
                    ),
                    Worked(
                        problem = "Solve x + y = 10 and x - y = 2",
                        lines = listOf(
                            "Add the two equations - the y terms cancel.",
                            "2x = 12, so x = 6.",
                            "Put x = 6 back into x + y = 10.",
                        ),
                        result = "x = 6, y = 4",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Choice(
                        question = "Substitution replaces one letter using the other equation. If y = 2x, what does 3x + y = 15 become?",
                        options = listOf("3x + 2x = 15", "3x + 2 = 15", "5y = 15", "3x = 15"),
                        correctIndex = 0,
                        explanation = "Swap y for 2x, giving 5x = 15 and so x = 3.",
                    ),
                    Numeric(
                        question = "Solve 2x + y = 11 given that y = 3. What is x?",
                        answer = "4",
                        explanation = "2x + 3 = 11, so 2x = 8.",
                    ),
                    Choice(
                        question = "If the two lines are parallel, the system has...",
                        options = listOf("one solution", "no solution", "infinitely many solutions", "two solutions"),
                        correctIndex = 1,
                        explanation = "Parallel lines never meet, so no pair (x, y) can satisfy both equations.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-algebra-indices",
                title = "Powers and roots",
                summary = "Index laws, zero powers and fractional indices.",
                steps = listOf(
                    Concept(
                        body = "An index says how many times a number multiplies itself, so 2 to the power 5 means 2 x 2 x 2 x 2 x 2 = 32.",
                    ),
                    Concept(
                        body = "Multiplying powers of the same base adds the indices; dividing subtracts them. This is why the laws feel like counting.",
                        formula = "a^m x a^n = a^(m+n)",
                    ),
                    Choice(
                        question = "Simplify x^5 x x^3",
                        options = listOf("x^8", "x^15", "x^2", "2x^8"),
                        correctIndex = 0,
                        explanation = "Add the indices: 5 + 3 = 8.",
                    ),
                    Concept(
                        body = "A zero index always gives 1, and a negative index means one over the power. Both follow from the subtraction rule.",
                        formula = "a^0 = 1 and a^-n = 1 / a^n",
                    ),
                    Numeric(
                        question = "What is 5^0 + 2^3?",
                        answer = "9",
                        explanation = "5^0 = 1 and 2^3 = 8, so the total is 9.",
                    ),
                    Choice(
                        question = "A fractional index is a root, so x^(1/2) means...",
                        options = listOf("half of x", "the square root of x", "x divided by 2", "x squared"),
                        correctIndex = 1,
                        explanation = "Adding the indices 1/2 + 1/2 gives 1, and the square root times itself gives x.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Factorise x² + 5x + 6",
                options = listOf("(x + 1)(x + 6)", "(x + 2)(x + 3)", "(x - 2)(x - 3)", "(x + 5)(x + 1)"),
                correctIndex = 1,
                explanation = "2 and 3 multiply to 6 and add to 5.",
            ),
            QuizQuestion(
                prompt = "Solve (x - 4)(x + 1) = 0",
                options = listOf("x = 4 or x = -1", "x = -4 or x = 1", "x = 4 or x = 1", "x = 3 only"),
                correctIndex = 0,
                explanation = "Set each factor to zero in turn.",
            ),
            QuizQuestion(
                prompt = "Solve x + y = 7 and x - y = 1",
                options = listOf("x = 3, y = 4", "x = 4, y = 3", "x = 5, y = 2", "x = 6, y = 1"),
                correctIndex = 1,
                explanation = "Adding gives 2x = 8, so x = 4 and then y = 3.",
            ),
            QuizQuestion(
                prompt = "Simplify y^7 / y^4",
                options = listOf("y^3", "y^11", "y^2", "3y"),
                correctIndex = 0,
                explanation = "Dividing subtracts the indices.",
            ),
            QuizQuestion(
                prompt = "What is 4^0?",
                options = listOf("0", "1", "4", "undefined"),
                correctIndex = 1,
                explanation = "Any non-zero number to the power zero is 1.",
            ),
            QuizQuestion(
                prompt = "In the quadratic formula, what sits under the square root?",
                options = listOf("b² - 4ac", "b² + 4ac", "2a", "-b"),
                correctIndex = 0,
                explanation = "That expression is called the discriminant.",
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
                summary = "Same shape, different size - and what that does to area.",
                steps = listOf(
                    Concept(
                        body = "Congruent figures are identical in shape and size. Similar figures share the shape but not the size: every angle matches and every pair of sides is in the same ratio.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Concept(
                        body = "The scale factor says how many times bigger the second figure is. Multiply every length by it.",
                        formula = "new length = scale factor x old length",
                    ),
                    Choice(
                        question = "Two similar triangles have corresponding sides of 4 cm and 10 cm. What is the scale factor?",
                        options = listOf("0.4", "2.5", "6", "14"),
                        correctIndex = 1,
                        explanation = "10 / 4 = 2.5.",
                    ),
                    Numeric(
                        question = "A 3-4-5 triangle is enlarged by scale factor 3. How long is its longest side?",
                        answer = "15",
                        explanation = "5 x 3 = 15.",
                    ),
                    Concept(
                        body = "Area and volume do not scale like length. Enlarging lengths by k multiplies areas by k squared and volumes by k cubed.",
                        formula = "length x k, area x k², volume x k³",
                    ),
                    Choice(
                        question = "A shape is enlarged by scale factor 4. Its area is multiplied by...",
                        options = listOf("4", "8", "12", "16"),
                        correctIndex = 3,
                        explanation = "Area scales by the square of the factor: 4² = 16.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-transformations",
                title = "Transformations",
                summary = "Slide, flip, turn and resize on the coordinate grid.",
                steps = listOf(
                    Concept(
                        body = "A transformation moves a shape around the grid. Translations slide it, reflections flip it, rotations turn it and enlargements resize it.",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Concept(
                        body = "A translation is described by a vector: how far across, then how far up. Negative numbers mean left or down.",
                        formula = "(3, -2) means 3 right and 2 down",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Choice(
                        question = "The point (2, 5) is translated by (3, -2). Where does it land?",
                        options = listOf("(5, 3)", "(5, 7)", "(-1, 7)", "(6, -10)"),
                        correctIndex = 0,
                        explanation = "Add the vector to the coordinates: (2 + 3, 5 - 2).",
                    ),
                    Concept(
                        body = "Reflecting in the y-axis flips the sign of x; reflecting in the x-axis flips the sign of y.",
                        formula = "(x, y) becomes (-x, y) in the y-axis",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Numeric(
                        question = "Reflect the point (4, 3) in the x-axis. What is the new y-coordinate?",
                        answer = "-3",
                        explanation = "Reflecting in the x-axis negates the y-coordinate.",
                    ),
                    Choice(
                        question = "Translations, reflections and rotations all preserve...",
                        options = listOf("the size and the shape", "only the size", "only the angles", "nothing at all"),
                        correctIndex = 0,
                        explanation = "They are congruence transformations. Only enlargement changes the size.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-circle-theorems",
                title = "Circle theorems",
                summary = "Angles at the centre, in a semicircle and in cyclic quadrilaterals.",
                steps = listOf(
                    Concept(
                        body = "Angles inside a circle follow strict rules. The angle at the centre is exactly twice the angle at the circumference standing on the same arc.",
                        formula = "centre angle = 2 x circumference angle",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Concept(
                        body = "An angle in a semicircle is always a right angle - it is just the special case where the angle at the centre is a straight 180 degrees.",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Choice(
                        question = "The angle at the centre standing on an arc is 80 degrees. What is the angle at the circumference on the same arc?",
                        options = listOf("20 degrees", "40 degrees", "80 degrees", "160 degrees"),
                        correctIndex = 1,
                        explanation = "It is half the angle at the centre.",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Concept(
                        body = "In a cyclic quadrilateral - one whose four corners all sit on the circle - opposite angles add up to 180 degrees.",
                        formula = "a + c = 180 degrees",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Numeric(
                        question = "A cyclic quadrilateral has one angle of 110 degrees. How many degrees is the opposite angle?",
                        answer = "70",
                        explanation = "180 - 110 = 70.",
                    ),
                    Choice(
                        question = "A tangent meets the radius at the point of contact at an angle of...",
                        options = listOf("45 degrees", "60 degrees", "90 degrees", "180 degrees"),
                        correctIndex = 2,
                        explanation = "A tangent is always perpendicular to the radius it touches.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Two similar shapes have scale factor 3. Their areas differ by a factor of...",
                options = listOf("3", "6", "9", "27"),
                correctIndex = 2,
                explanation = "Area scales by the square of the scale factor.",
            ),
            QuizQuestion(
                prompt = "The point (1, 4) is translated by (2, -3). Where does it land?",
                options = listOf("(3, 1)", "(3, 7)", "(-1, 1)", "(2, 12)"),
                correctIndex = 0,
                explanation = "(1 + 2, 4 - 3).",
            ),
            QuizQuestion(
                prompt = "Reflect (5, -2) in the y-axis.",
                options = listOf("(-5, -2)", "(5, 2)", "(-5, 2)", "(-2, 5)"),
                correctIndex = 0,
                explanation = "Reflecting in the y-axis flips the sign of x only.",
            ),
            QuizQuestion(
                prompt = "The angle at the centre is 120 degrees. The angle at the circumference on the same arc is...",
                options = listOf("30 degrees", "60 degrees", "120 degrees", "240 degrees"),
                correctIndex = 1,
                explanation = "Half of the centre angle.",
            ),
            QuizQuestion(
                prompt = "One angle of a cyclic quadrilateral is 95 degrees. Its opposite angle is...",
                options = listOf("85 degrees", "95 degrees", "105 degrees", "185 degrees"),
                correctIndex = 0,
                explanation = "Opposite angles sum to 180.",
            ),
            QuizQuestion(
                prompt = "An angle in a semicircle is always...",
                options = listOf("45 degrees", "60 degrees", "90 degrees", "180 degrees"),
                correctIndex = 2,
                explanation = "It is half of the straight angle at the centre.",
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
                summary = "Three ratios that depend only on the angle.",
                steps = listOf(
                    Concept(
                        body = "In a right triangle, name the sides from the angle you care about: the opposite side faces it, the adjacent side touches it, and the hypotenuse faces the right angle.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Concept(
                        body = "Similar triangles have equal side ratios, so these ratios depend on the angle and nothing else. That is exactly what sine, cosine and tangent are.",
                        formula = "sin = opp/hyp, cos = adj/hyp, tan = opp/adj",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Choice(
                        question = "Which ratio uses the opposite and the adjacent sides?",
                        options = listOf("sine", "cosine", "tangent", "none of them"),
                        correctIndex = 2,
                        explanation = "tan = opposite / adjacent.",
                    ),
                    Numeric(
                        question = "A right triangle has opposite 3 and hypotenuse 5. What is the sine of that angle, as a decimal?",
                        answer = "0.6",
                        explanation = "3 / 5 = 0.6.",
                    ),
                    Concept(
                        body = "A few values are worth knowing by heart: sin 30 = 0.5, cos 60 = 0.5 and tan 45 = 1.",
                    ),
                    Choice(
                        question = "As the angle grows from 0 to 90 degrees, the sine...",
                        options = listOf("falls from 1 to 0", "grows from 0 to 1", "stays at 1", "grows without limit"),
                        correctIndex = 1,
                        explanation = "sin 0 = 0 and sin 90 = 1, and it rises steadily in between.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-trigonometry-missing-sides",
                title = "Finding sides and angles",
                summary = "Label the triangle, then pick the ratio that fits.",
                steps = listOf(
                    Concept(
                        body = "Pick the ratio that connects the side you know with the side you want. Label all three sides first, then choose - never the other way around.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Worked(
                        problem = "An angle is 30 degrees and the hypotenuse is 10. How long is the opposite side?",
                        lines = listOf(
                            "Known: hypotenuse. Wanted: opposite. That pair is sine.",
                            "sin 30 = opposite / 10.",
                            "So opposite = 10 x sin 30.",
                            "sin 30 = 0.5.",
                        ),
                        result = "opposite = 5",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Choice(
                        question = "You know the adjacent side and want the hypotenuse. Which ratio?",
                        options = listOf("sine", "cosine", "tangent", "Pythagoras only"),
                        correctIndex = 1,
                        explanation = "cos = adjacent / hypotenuse links exactly those two sides.",
                    ),
                    Concept(
                        body = "To find an angle rather than a side, use the inverse functions written sin-1, cos-1 and tan-1 on a calculator.",
                        formula = "tan x = 1 gives x = 45 degrees",
                    ),
                    Numeric(
                        question = "A right triangle has opposite 5 and adjacent 5. What is the angle, in degrees?",
                        answer = "45",
                        explanation = "tan of the angle is 5/5 = 1, and that happens at 45 degrees.",
                    ),
                    Choice(
                        question = "You know two sides and want the third, with no angle involved. Use...",
                        options = listOf("sine", "cosine", "Pythagoras", "tangent"),
                        correctIndex = 2,
                        explanation = "Trig ratios always involve an angle; Pythagoras does not.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-trigonometry-applications",
                title = "Elevation and bearings",
                summary = "Turning a real situation into a right triangle.",
                steps = listOf(
                    Concept(
                        body = "An angle of elevation is measured up from the horizontal and an angle of depression down from it. For the same line of sight the two are equal.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Worked(
                        problem = "From 50 m away the angle of elevation to the top of a tower is 40 degrees. How tall is the tower?",
                        lines = listOf(
                            "Sketch the right triangle: adjacent = 50, opposite = the height.",
                            "Opposite with adjacent means tangent.",
                            "tan 40 is about 0.839.",
                            "height = 50 x 0.839.",
                        ),
                        result = "About 42 m",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Choice(
                        question = "Which ratio connects a horizontal distance with a height?",
                        options = listOf("sine", "cosine", "tangent", "none of them"),
                        correctIndex = 2,
                        explanation = "Horizontal is adjacent and height is opposite, which is tangent.",
                    ),
                    Numeric(
                        question = "A ramp rises 3 m over a horizontal distance of 4 m. How long is the ramp surface, in metres?",
                        answer = "5",
                        explanation = "Pythagoras: 9 + 16 = 25, and the root of 25 is 5.",
                    ),
                    Concept(
                        body = "Bearings are angles measured clockwise from north and are always written with three digits, so 45 degrees east of north is written 045.",
                    ),
                    Choice(
                        question = "A kite string 30 m long sits at an elevation of 60 degrees. Which calculation gives the kite's height?",
                        options = listOf("30 x sin 60", "30 x cos 60", "30 / tan 60", "30 + 60"),
                        correctIndex = 0,
                        explanation = "The string is the hypotenuse and the height is opposite the angle, so use sine.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which fraction is the sine of an angle?",
                options = listOf("opposite / adjacent", "opposite / hypotenuse", "adjacent / hypotenuse", "hypotenuse / opposite"),
                correctIndex = 1,
                explanation = "Sine pairs the opposite side with the hypotenuse.",
            ),
            QuizQuestion(
                prompt = "Opposite is 6 and adjacent is 8. What is the tangent of the angle?",
                options = listOf("0.6", "0.75", "0.8", "1.33"),
                correctIndex = 1,
                explanation = "6 / 8 = 0.75.",
            ),
            QuizQuestion(
                prompt = "The hypotenuse is 20 and the angle is 30 degrees. How long is the opposite side?",
                options = listOf("10", "14.1", "17.3", "40"),
                correctIndex = 0,
                explanation = "20 x sin 30 = 20 x 0.5.",
            ),
            QuizQuestion(
                prompt = "What is tan 45 degrees?",
                options = listOf("0", "0.5", "1", "undefined"),
                correctIndex = 2,
                explanation = "At 45 degrees the opposite and adjacent sides are equal.",
            ),
            QuizQuestion(
                prompt = "You know two sides and want the angle. You use...",
                options = listOf("Pythagoras", "an inverse trig function", "the mean", "a bearing"),
                correctIndex = 1,
                explanation = "Inverse sine, cosine or tangent turns a ratio back into an angle.",
            ),
            QuizQuestion(
                prompt = "The angle of elevation is 60 degrees and the horizontal distance is 10 m. The height is...",
                options = listOf("10 x sin 60", "10 x cos 60", "10 x tan 60", "10 / tan 60"),
                correctIndex = 2,
                explanation = "Height is opposite and distance is adjacent, so height = distance x tan.",
            ),
        ),
    )

    private val functions = learnUnit(
        level = level,
        topic = MathTopic.FUNCTIONS,
        summary = "Function notation, linear and quadratic graphs, and graph transformations.",
        lessons = listOf(
            LessonSpec(
                id = "g910-functions-notation",
                title = "Function notation",
                summary = "One input, exactly one output.",
                steps = listOf(
                    Concept(
                        body = "A function is a rule that turns each input into exactly one output. Think of f(x) = 2x + 1 as a machine: put 3 in and 7 comes out.",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Concept(
                        body = "f(3) does not mean f times 3. It means 'the output when the input is 3'.",
                        formula = "f(x) = 2x + 1, so f(3) = 7",
                    ),
                    Numeric(
                        question = "If f(x) = x² - 1, what is f(4)?",
                        answer = "15",
                        explanation = "16 - 1 = 15.",
                    ),
                    Concept(
                        body = "The domain is the set of inputs allowed and the range is the set of outputs produced. f(x) = 1/x produces nothing at x = 0, so 0 is not in its domain.",
                    ),
                    Choice(
                        question = "Which of these is NOT a function?",
                        options = listOf("y = x²", "y = 3x + 1", "x = y²", "y = 1/x"),
                        correctIndex = 2,
                        explanation = "x = y² gives two y values for most x, so one input has more than one output.",
                    ),
                    Choice(
                        question = "For f(x) = 5 - x, what is f(-2)?",
                        options = listOf("3", "7", "-7", "-3"),
                        correctIndex = 1,
                        explanation = "5 - (-2) = 5 + 2 = 7.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-linear-quadratic",
                title = "Linear and quadratic graphs",
                summary = "Straight lines, parabolas, vertices and roots.",
                steps = listOf(
                    Concept(
                        body = "A linear function graphs as a straight line and changes at a constant rate. A quadratic graphs as a parabola, and its rate of change keeps changing.",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Concept(
                        body = "A parabola is symmetric about a vertical line through its lowest or highest point, called the vertex.",
                        formula = "y = ax² + bx + c has its vertex at x = -b / 2a",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Choice(
                        question = "If a is negative in y = ax² + bx + c, the parabola...",
                        options = listOf("opens upwards", "opens downwards", "becomes a straight line", "never crosses the y-axis"),
                        correctIndex = 1,
                        explanation = "A negative a turns the U upside down.",
                    ),
                    Numeric(
                        question = "Find the x-coordinate of the vertex of y = x² - 6x + 5.",
                        answer = "3",
                        explanation = "-b / 2a = 6 / 2 = 3.",
                    ),
                    Concept(
                        body = "Where a graph crosses the x-axis, y is zero - those crossings are the roots. A parabola can cross twice, touch once, or miss the axis entirely.",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Choice(
                        question = "How many times does y = x² + 1 cross the x-axis?",
                        options = listOf("never", "once", "twice", "infinitely often"),
                        correctIndex = 0,
                        explanation = "x² is never negative, so y is always at least 1.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-functions-transformations",
                title = "Transforming graphs",
                summary = "Four moves that work on every graph.",
                steps = listOf(
                    Concept(
                        body = "Changing a function's formula moves its graph in predictable ways. Learn the four moves once and they apply to every graph you ever meet.",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Concept(
                        body = "Adding outside the function shifts it vertically. Adding inside shifts it horizontally - and inside changes work backwards.",
                        formula = "f(x) + 3 goes up 3; f(x + 3) goes left 3",
                        visual = LearnVisual.PARABOLA,
                    ),
                    Choice(
                        question = "How does the graph of y = (x - 2)² compare with y = x²?",
                        options = listOf("2 to the left", "2 to the right", "2 up", "2 down"),
                        correctIndex = 1,
                        explanation = "Inside changes move the opposite way to their sign.",
                    ),
                    Concept(
                        body = "Multiplying outside stretches the graph vertically, and a minus sign outside reflects it in the x-axis.",
                        formula = "-f(x) is f(x) flipped upside down",
                    ),
                    Numeric(
                        question = "The graph of y = x² is shifted 5 up. What is y when x = 0?",
                        answer = "5",
                        explanation = "The new rule is y = x² + 5, and at x = 0 that leaves 5.",
                    ),
                    Choice(
                        question = "Compared with y = f(x), the graph of y = 3f(x) is...",
                        options = listOf("3 units up", "3 times taller", "3 units right", "3 times wider"),
                        correctIndex = 1,
                        explanation = "Every output is tripled, which stretches the graph vertically.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "If f(x) = 3x - 4, what is f(5)?",
                options = listOf("11", "15", "19", "-1"),
                correctIndex = 0,
                explanation = "15 - 4 = 11.",
            ),
            QuizQuestion(
                prompt = "If f(x) = x² + 2, what is f(-3)?",
                options = listOf("-7", "7", "11", "-11"),
                correctIndex = 2,
                explanation = "(-3)² = 9, and 9 + 2 = 11.",
            ),
            QuizQuestion(
                prompt = "What is the x-coordinate of the vertex of y = x² - 4x + 1?",
                options = listOf("-4", "-2", "2", "4"),
                correctIndex = 2,
                explanation = "-b / 2a = 4 / 2 = 2.",
            ),
            QuizQuestion(
                prompt = "The graph of y = -x² opens...",
                options = listOf("upwards", "downwards", "sideways", "as a straight line"),
                correctIndex = 1,
                explanation = "The negative coefficient flips the parabola.",
            ),
            QuizQuestion(
                prompt = "Compared with y = f(x), the graph of y = f(x) - 4 is...",
                options = listOf("4 up", "4 down", "4 left", "4 right"),
                correctIndex = 1,
                explanation = "Subtracting outside the function lowers every output.",
            ),
            QuizQuestion(
                prompt = "Compared with y = f(x), the graph of y = f(x - 3) is...",
                options = listOf("3 left", "3 right", "3 up", "3 down"),
                correctIndex = 1,
                explanation = "Inside changes move the graph the opposite way to their sign.",
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(algebra, geometry, trigonometry, functions)
}
