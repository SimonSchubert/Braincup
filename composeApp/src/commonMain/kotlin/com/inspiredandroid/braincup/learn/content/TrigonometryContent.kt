package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.UnitCircleFigure
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.learnUnit

/** Trigonometry: the right-triangle ratios, then radians, waves and identities. */
internal object TrigonometryContent {

    private val rightTriangleTrig = learnUnit(
        topic = MathTopic.TRIGONOMETRY,
        urlSlug = "right-triangle-trig",
        title = "Sine, cosine and tangent",
        summary = "Sine, cosine and tangent in right triangles, and where they are used.",
        level = GradeLevel.GRADES_9_10,
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

    private val unitCircle = learnUnit(
        topic = MathTopic.TRIGONOMETRY,
        urlSlug = "unit-circle",
        title = "Radians, waves and identities",
        summary = "Radians and the unit circle, wave graphs and identities.",
        level = GradeLevel.GRADES_11_12,
        lessons = listOf(
            LessonSpec(
                id = "g1112-trigonometry-unit-circle",
                title = "Radians and the unit circle",
                summary = "Trig for every angle, not just acute ones.",
                steps = listOf(
                    Concept(
                        body = "The point at angle x sits at (cos x, sin x) - and this works for any angle at all.",
                        visual = UnitCircleFigure(degrees = 52),
                    ),
                    Concept(
                        body = "Radians measure the arc itself, so a full turn is 2 pi.",
                        formula = "180 degrees = pi radians",
                        visual = UnitCircleFigure(degrees = 180, label = "pi"),
                    ),
                    Choice(
                        question = "How many degrees is this angle?",
                        options = listOf("45", "90", "180", "360"),
                        correctIndex = 1,
                        explanation = "pi/2 radians is a quarter turn.",
                        visual = UnitCircleFigure(degrees = 90, label = "pi/2", reveal = false),
                    ),
                    Concept(
                        body = "In the second quadrant the point is up and left, so sine is positive and cosine negative.",
                        visual = UnitCircleFigure(degrees = 140),
                    ),
                    Numeric(
                        question = "What is cos 0?",
                        answer = "1",
                        explanation = "The point sits at (1, 0), and cosine is the x-coordinate.",
                        visual = UnitCircleFigure(degrees = 0, reveal = false),
                    ),
                    Choice(
                        question = "What is sin 150?",
                        options = listOf("-0.5", "0.5", "1", "-1"),
                        correctIndex = 1,
                        explanation = "It mirrors 30 degrees, and sine stays positive up there.",
                        visual = UnitCircleFigure(degrees = 150, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-graphs",
                title = "Graphs of sine and cosine",
                summary = "Amplitude, period and why it repeats.",
                steps = listOf(
                    Concept(
                        body = "Plot the height as you walk round the circle and you trace the sine wave.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                    Concept(
                        body = "Cosine is the same wave, shifted a quarter turn.",
                        formula = "cos x = sin(x + 90 degrees)",
                        visual = Plot(
                            curve = Curve.Sine(amplitude = 2f),
                            second = Curve.Sine(amplitude = 2f, cosine = true),
                        ),
                    ),
                    Choice(
                        question = "What is the period of y = sin x, in degrees?",
                        options = listOf("90", "180", "360", "720"),
                        correctIndex = 2,
                        explanation = "One complete circuit of the circle.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                    Concept(
                        body = "The number in front sets the amplitude; the number inside squeezes the period.",
                        formula = "y = a sin(bx)",
                        visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
                    ),
                    Numeric(
                        question = "What is the amplitude of the taller wave?",
                        answer = "2.5",
                        explanation = "It runs from -2.5 up to 2.5.",
                        visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
                    ),
                    Choice(
                        question = "The green wave has double the frequency. Its period is...",
                        options = listOf("doubled", "halved", "unchanged", "zero"),
                        correctIndex = 1,
                        explanation = "Twice as many waves in the same space.",
                        visual = Plot(
                            curve = Curve.Sine(amplitude = 2f),
                            second = Curve.Sine(amplitude = 2f, frequency = 2f),
                        ),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-trigonometry-identities",
                title = "Identities and equations",
                summary = "Pythagoras on the unit circle.",
                steps = listOf(
                    Concept(
                        body = "An identity holds for every angle; an equation only for particular ones.",
                        visual = UnitCircleFigure(degrees = 52),
                    ),
                    Concept(
                        body = "The sine and cosine legs form a right triangle with hypotenuse 1.",
                        formula = "sin²x + cos²x = 1",
                        visual = UnitCircleFigure(degrees = 37),
                    ),
                    Choice(
                        question = "If sin x = 0.6, what is cos²x?",
                        options = listOf("0.36", "0.4", "0.64", "0.8"),
                        correctIndex = 2,
                        explanation = "1 - 0.36.",
                        visual = UnitCircleFigure(degrees = 37, reveal = false),
                    ),
                    Concept(
                        body = "Tangent is the ratio of the two legs, so it blows up where cosine is zero.",
                        formula = "tan x = sin x / cos x",
                        visual = UnitCircleFigure(degrees = 80),
                    ),
                    Numeric(
                        question = "What is sin²30 + cos²30?",
                        answer = "1",
                        explanation = "The identity holds for every angle.",
                        visual = UnitCircleFigure(degrees = 30, reveal = false),
                    ),
                    Choice(
                        question = "How many solutions does sin x = 0.5 have between 0 and 360?",
                        options = listOf("none", "one", "two", "four"),
                        correctIndex = 2,
                        explanation = "Sine is positive in the first two quadrants: 30 and 150.",
                        visual = Plot(curve = Curve.Sine(amplitude = 2f)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many radians are in a full turn?",
                options = listOf("pi", "2 pi", "180", "360"),
                correctIndex = 1,
                explanation = "One full circuit of the unit circle.",
                visual = UnitCircleFigure(degrees = 300, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is cos 180?",
                options = listOf("1", "0", "-1", "0.5"),
                correctIndex = 2,
                explanation = "The point sits at (-1, 0).",
                visual = UnitCircleFigure(degrees = 180, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the period of this wave, in degrees?",
                options = listOf("90", "180", "360", "720"),
                correctIndex = 2,
                explanation = "It repeats after one full turn.",
                visual = Plot(curve = Curve.Sine(amplitude = 2f)),
            ),
            QuizQuestion(
                prompt = "What is the amplitude of the taller wave?",
                options = listOf("1", "2", "2.5", "360"),
                correctIndex = 2,
                explanation = "The amplitude is the factor outside the sine.",
                visual = Plot(curve = Curve.Sine(amplitude = 1f), second = Curve.Sine(amplitude = 2.5f)),
            ),
            QuizQuestion(
                prompt = "sin²x + cos²x equals...",
                options = listOf("0", "1", "2", "x"),
                correctIndex = 1,
                explanation = "Pythagoras with hypotenuse 1.",
                visual = UnitCircleFigure(degrees = 37),
            ),
            QuizQuestion(
                prompt = "How many degrees is pi/3 radians?",
                options = listOf("30", "45", "60", "90"),
                correctIndex = 2,
                explanation = "180 / 3.",
                visual = UnitCircleFigure(degrees = 60, label = "pi/3", reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(rightTriangleTrig, unitCircle)
}
