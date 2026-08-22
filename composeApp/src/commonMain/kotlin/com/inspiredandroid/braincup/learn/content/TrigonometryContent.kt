package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object TrigonometryContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "trigonometry-ratios",
            topic = MathTopic.TRIGONOMETRY,
            title = "Right-Triangle Ratios",
            summary = "SOH-CAH-TOA: three ratios that depend only on the angle.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Scale a right triangle up or down and the angles stay the same — and " +
                        "so do the ratios between its sides. Those fixed ratios are the sine, " +
                        "cosine and tangent of the angle.",
                    formula = "sin θ = opp/hyp    cos θ = adj/hyp    tan θ = opp/adj",
                    visual = LearnVisual.RIGHT_TRIANGLE,
                ),
                LessonStep.Concept(
                    body = "\"Opposite\" is the side across from the angle you are working with, " +
                        "\"adjacent\" is the other short side, and the hypotenuse is always the " +
                        "one facing the right angle. Remember the ratios as SOH-CAH-TOA.",
                ),
                LessonStep.Choice(
                    question = "In a right triangle the side opposite θ is 3 and the hypotenuse is 5. What is sin θ?",
                    options = listOf("3/5", "4/5", "3/4", "5/3"),
                    correctIndex = 0,
                    explanation = "sin θ = opposite ÷ hypotenuse = 3/5. (4/5 would be cos θ, since " +
                        "the adjacent side is 4.)",
                ),
                LessonStep.Worked(
                    problem = "A 10 m ladder leans against a wall at 60° to the ground. How high does it reach? (sin 60° ≈ 0.87)",
                    lines = listOf(
                        "The height is opposite the 60° angle; the ladder is the hypotenuse",
                        "sin 60° = h / 10",
                        "h = 10 × 0.87",
                    ),
                    result = "≈ 8.7 m",
                ),
                LessonStep.Numeric(
                    question = "In a right triangle with a 45° angle, both legs measure 1. What is tan 45°?",
                    answer = "1",
                    explanation = "tan = opposite ÷ adjacent = 1 ÷ 1 = 1.",
                ),
                LessonStep.Choice(
                    question = "Which ratio pairs the adjacent side with the hypotenuse?",
                    options = listOf("sine", "cosine", "tangent", "none of them"),
                    correctIndex = 1,
                    explanation = "CAH: cosine = adjacent over hypotenuse.",
                ),
            ),
        ),
        LearnLesson(
            id = "trigonometry-unit-circle",
            topic = MathTopic.TRIGONOMETRY,
            title = "The Unit Circle & Radians",
            summary = "Extending sine and cosine past 90°, and measuring angles in π.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Draw a circle of radius 1 at the origin and walk anticlockwise around " +
                        "it. The point you reach at angle θ has coordinates (cos θ, sin θ) — which " +
                        "is how sine and cosine keep meaning past 90°.",
                    formula = "point at θ = (cos θ, sin θ)",
                    visual = LearnVisual.UNIT_CIRCLE,
                ),
                LessonStep.Concept(
                    body = "A radian is the angle that wraps one radius-length of arc around the " +
                        "circle. The circumference is 2π radii, so a full turn is 2π radians.",
                    formula = "360° = 2π rad    180° = π rad    degrees × π/180 = radians",
                ),
                LessonStep.Choice(
                    question = "What is 90° in radians?",
                    options = listOf("π/4", "π/3", "π/2", "π"),
                    correctIndex = 2,
                    explanation = "90° is a quarter turn, and a quarter of 2π is π/2.",
                ),
                LessonStep.Numeric(
                    question = "How many degrees is π radians?",
                    answer = "180",
                    explanation = "π radians is half a full turn: 360 ÷ 2 = 180°.",
                ),
                LessonStep.Worked(
                    problem = "Read sin 30° and cos 30° off the unit circle.",
                    lines = listOf(
                        "30° is π/6, one twelfth of the way round",
                        "The height above the axis is 1/2 → sin 30° = 0.5",
                        "The distance along the axis is √3/2 → cos 30° ≈ 0.87",
                    ),
                    result = "sin 30° = 0.5,  cos 30° ≈ 0.87",
                ),
                LessonStep.Choice(
                    question = "Which value can sin θ never take?",
                    options = listOf("0", "0.5", "−1", "1.5"),
                    correctIndex = 3,
                    explanation = "On a circle of radius 1 the height is never more than 1, so " +
                        "sine always lies between −1 and 1.",
                ),
            ),
        ),
        LearnLesson(
            id = "trigonometry-identities",
            topic = MathTopic.TRIGONOMETRY,
            title = "Identities & Triangle Laws",
            summary = "The identity that comes free with the unit circle, plus triangles without right angles.",
            steps = listOf(
                LessonStep.Concept(
                    body = "The unit circle point (cos θ, sin θ) sits exactly one unit from the " +
                        "origin, so Pythagoras applies to it directly. That gives the identity " +
                        "used everywhere in trigonometry.",
                    formula = "sin²θ + cos²θ = 1",
                ),
                LessonStep.Choice(
                    question = "If sin θ = 0.6, what is cos²θ?",
                    options = listOf("0.4", "0.64", "0.36", "0.8"),
                    correctIndex = 1,
                    explanation = "cos²θ = 1 − sin²θ = 1 − 0.36 = 0.64 (so cos θ = ±0.8).",
                ),
                LessonStep.Concept(
                    body = "Triangles without a right angle need two more tools. The law of sines " +
                        "pairs each side with the angle across from it; the law of cosines " +
                        "generalises Pythagoras to any angle.",
                    formula = "a/sin A = b/sin B = c/sin C    c² = a² + b² − 2ab·cos C",
                ),
                LessonStep.Worked(
                    problem = "A triangle has sides a = 8 and b = 5 with the angle C = 60° between them. Find c. (cos 60° = 0.5)",
                    lines = listOf(
                        "c² = a² + b² − 2ab·cos C",
                        "c² = 64 + 25 − 2(8)(5)(0.5)",
                        "c² = 89 − 40 = 49",
                    ),
                    result = "c = 7",
                ),
                LessonStep.Numeric(
                    question = "What is sin²40° + cos²40°?",
                    answer = "1",
                    explanation = "The identity holds for every angle, so you never need a calculator for this.",
                ),
                LessonStep.Choice(
                    question = "With C = 90°, the law of cosines reduces to…",
                    options = listOf("the law of sines", "the Pythagorean theorem", "sin²θ + cos²θ = 1", "nothing useful"),
                    correctIndex = 1,
                    explanation = "cos 90° = 0, so the last term vanishes and c² = a² + b² remains.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.TRIGONOMETRY,
        questions = listOf(
            QuizQuestion(
                prompt = "In a right triangle, adjacent = 4 and hypotenuse = 5. What is cos θ?",
                options = listOf("3/5", "4/5", "3/4", "5/4"),
                correctIndex = 1,
                explanation = "CAH: cos θ = adjacent ÷ hypotenuse = 4/5.",
            ),
            QuizQuestion(
                prompt = "Which ratio is opposite ÷ adjacent?",
                options = listOf("sine", "cosine", "tangent", "cosecant"),
                correctIndex = 2,
                explanation = "TOA: tangent.",
            ),
            QuizQuestion(
                prompt = "What is 180° in radians?",
                options = listOf("π/2", "π", "2π", "π/4"),
                correctIndex = 1,
                explanation = "Half a full turn: half of 2π is π.",
            ),
            QuizQuestion(
                prompt = "What is sin 90°?",
                options = listOf("0", "0.5", "1", "undefined"),
                correctIndex = 2,
                explanation = "At 90° the unit-circle point is (0, 1), so the height — the sine — is 1.",
            ),
            QuizQuestion(
                prompt = "If cos θ = 0.8, what is sin²θ?",
                options = listOf("0.2", "0.36", "0.64", "0.6"),
                correctIndex = 1,
                explanation = "sin²θ = 1 − 0.64 = 0.36.",
            ),
            QuizQuestion(
                prompt = "What is tan 45°?",
                options = listOf("0", "0.5", "1", "√2"),
                correctIndex = 2,
                explanation = "At 45° the opposite and adjacent sides are equal, so the ratio is 1.",
            ),
            QuizQuestion(
                prompt = "Which law would you use given two angles and one side?",
                options = listOf("Law of sines", "Law of cosines", "Pythagoras", "sin²+cos²=1"),
                correctIndex = 0,
                explanation = "The law of sines links each side with its opposite angle, which is " +
                    "exactly the information you have.",
            ),
            QuizQuestion(
                prompt = "What is cos 0°?",
                options = listOf("0", "1", "−1", "π"),
                correctIndex = 1,
                explanation = "At 0° the unit-circle point is (1, 0), so cos 0° = 1.",
            ),
        ),
    )
}
