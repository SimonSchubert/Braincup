package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object GeometryContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "geometry-angles",
            topic = MathTopic.GEOMETRY,
            title = "Angles & Lines",
            summary = "The turns that add up: 90°, 180°, 360° and the rules built on them.",
            steps = listOf(
                LessonStep.Concept(
                    body = "An angle measures a turn. A quarter turn is a right angle, a half " +
                        "turn is a straight line, and a full turn brings you back where you started.",
                    formula = "right = 90°   straight = 180°   full turn = 360°",
                    visual = LearnVisual.ANGLES,
                ),
                LessonStep.Choice(
                    question = "Two angles sit side by side on a straight line. One is 125°. What is the other?",
                    options = listOf("55°", "65°", "235°", "75°"),
                    correctIndex = 0,
                    explanation = "Angles on a straight line add to 180°, so 180 − 125 = 55°.",
                ),
                LessonStep.Concept(
                    body = "When a line crosses two parallel lines, the angles repeat. " +
                        "Corresponding angles (the F shape) are equal, alternate angles (the Z " +
                        "shape) are equal, and co-interior angles (the C shape) add to 180°.",
                    formula = "F equal   |   Z equal   |   C sum to 180°",
                ),
                LessonStep.Worked(
                    problem = "A triangle has angles of 40° and 75°. Find the third angle.",
                    lines = listOf(
                        "The angles of any triangle sum to 180°",
                        "40 + 75 = 115",
                        "180 − 115",
                    ),
                    result = "65°",
                ),
                LessonStep.Numeric(
                    question = "The four angles of any quadrilateral add up to how many degrees?",
                    answer = "360",
                    explanation = "A quadrilateral splits into two triangles, and 2 × 180° = 360°.",
                ),
                LessonStep.Choice(
                    question = "An angle of 137° is called…",
                    options = listOf("acute", "right", "obtuse", "reflex"),
                    correctIndex = 2,
                    explanation = "Acute is under 90°, right is exactly 90°, obtuse is between 90° " +
                        "and 180°, reflex is over 180°.",
                ),
            ),
        ),
        LearnLesson(
            id = "geometry-triangles",
            topic = MathTopic.GEOMETRY,
            title = "Triangles & Pythagoras",
            summary = "Naming triangles, and the one equation every right triangle obeys.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Triangles are named twice over: by their sides (equilateral — all " +
                        "equal, isosceles — two equal, scalene — none equal) and by their largest " +
                        "angle (acute, right, obtuse).",
                ),
                LessonStep.Concept(
                    body = "In a right triangle, the side opposite the right angle is the " +
                        "hypotenuse and is always the longest. The squares on the two shorter " +
                        "sides add up to the square on the hypotenuse.",
                    formula = "a² + b² = c²",
                    visual = LearnVisual.RIGHT_TRIANGLE,
                ),
                LessonStep.Worked(
                    problem = "A right triangle has legs of 3 and 4. Find the hypotenuse.",
                    lines = listOf(
                        "c² = a² + b²",
                        "c² = 3² + 4² = 9 + 16 = 25",
                        "c = √25",
                    ),
                    result = "c = 5",
                ),
                LessonStep.Numeric(
                    question = "A right triangle has legs of 6 and 8. How long is the hypotenuse?",
                    answer = "10",
                    explanation = "c² = 36 + 64 = 100, so c = 10. (It is the 3-4-5 triangle doubled.)",
                ),
                LessonStep.Choice(
                    question = "A right triangle has hypotenuse 13 and one leg 5. How long is the other leg?",
                    options = listOf("8", "12", "144", "18"),
                    correctIndex = 1,
                    explanation = "Rearrange: b² = c² − a² = 169 − 25 = 144, so b = 12. " +
                        "Subtract the squares, never the sides.",
                ),
                LessonStep.Choice(
                    question = "Which set of side lengths forms a right triangle?",
                    options = listOf("2, 3, 4", "5, 12, 13", "4, 5, 6", "7, 8, 9"),
                    correctIndex = 1,
                    explanation = "5² + 12² = 25 + 144 = 169 = 13². The others fail the test.",
                ),
            ),
        ),
        LearnLesson(
            id = "geometry-circles",
            topic = MathTopic.GEOMETRY,
            title = "Circles",
            summary = "Radius, diameter, π — and why doubling the radius quadruples the area.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Every point of a circle is the same distance — the radius — from the " +
                        "centre. A diameter runs right through the centre, so it is two radii long.",
                    formula = "d = 2r",
                    visual = LearnVisual.CIRCLE,
                ),
                LessonStep.Concept(
                    body = "Divide any circle's circumference by its diameter and you always get " +
                        "the same number, π ≈ 3.14159. That single fact gives both circle formulas.",
                    formula = "C = 2πr = πd    A = πr²",
                ),
                LessonStep.Choice(
                    question = "A circle has a diameter of 10 cm. What is its radius?",
                    options = listOf("20 cm", "5 cm", "10π cm", "2.5 cm"),
                    correctIndex = 1,
                    explanation = "The radius is half the diameter: 10 ÷ 2 = 5 cm.",
                ),
                LessonStep.Worked(
                    problem = "Find the circumference of a circle of radius 3 cm (π ≈ 3.14).",
                    lines = listOf(
                        "C = 2πr",
                        "C = 2 × 3.14 × 3",
                    ),
                    result = "C ≈ 18.8 cm",
                ),
                LessonStep.Numeric(
                    question = "A circle has a radius of 7 cm. What is its diameter, in centimetres?",
                    answer = "14",
                    explanation = "d = 2r = 2 × 7 = 14 cm.",
                ),
                LessonStep.Choice(
                    question = "If a circle's radius doubles, its area…",
                    options = listOf("doubles", "triples", "quadruples", "stays the same"),
                    correctIndex = 2,
                    explanation = "Area depends on r², and (2r)² = 4r². Doubling the radius " +
                        "multiplies the area by four.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.GEOMETRY,
        questions = listOf(
            QuizQuestion(
                prompt = "Two angles of a triangle are 55° and 65°. What is the third?",
                options = listOf("50°", "60°", "70°", "120°"),
                correctIndex = 1,
                explanation = "180 − (55 + 65) = 180 − 120 = 60°.",
            ),
            QuizQuestion(
                prompt = "A right triangle has legs 9 and 12. What is the hypotenuse?",
                options = listOf("15", "21", "18", "13"),
                correctIndex = 0,
                explanation = "81 + 144 = 225, and √225 = 15.",
            ),
            QuizQuestion(
                prompt = "What is the area of a circle with radius 4 (π ≈ 3.14)?",
                options = listOf("≈ 12.6", "≈ 25.1", "≈ 50.2", "≈ 16"),
                correctIndex = 2,
                explanation = "A = πr² = 3.14 × 16 ≈ 50.2. (25.1 would be the circumference.)",
            ),
            QuizQuestion(
                prompt = "An angle measures 90°. It is…",
                options = listOf("acute", "right", "obtuse", "reflex"),
                correctIndex = 1,
                explanation = "Exactly 90° is a right angle.",
            ),
            QuizQuestion(
                prompt = "The angles of a pentagon add up to…",
                options = listOf("360°", "450°", "540°", "720°"),
                correctIndex = 2,
                explanation = "A polygon with n sides splits into n − 2 triangles: 3 × 180° = 540°.",
            ),
            QuizQuestion(
                prompt = "A circle has circumference 2πr. If r = 6, the circumference is…",
                options = listOf("6π", "12π", "36π", "3π"),
                correctIndex = 1,
                explanation = "C = 2π × 6 = 12π.",
            ),
            QuizQuestion(
                prompt = "In an isosceles triangle the apex angle is 40°. Each base angle is…",
                options = listOf("40°", "60°", "70°", "80°"),
                correctIndex = 2,
                explanation = "The two base angles are equal: (180 − 40) ÷ 2 = 70°.",
            ),
            QuizQuestion(
                prompt = "Which set of sides does NOT form a right triangle?",
                options = listOf("8, 15, 17", "7, 24, 25", "6, 8, 10", "5, 6, 8"),
                correctIndex = 3,
                explanation = "25 + 36 = 61, but 8² = 64. The other three satisfy a² + b² = c².",
            ),
        ),
    )
}
