package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object ArithmeticContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "arithmetic-place-value",
            topic = MathTopic.ARITHMETIC,
            title = "Place Value & Rounding",
            summary = "What each digit is worth, and how to round to a chosen place.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A digit's value depends on where it sits. Moving one place to the " +
                        "left multiplies its value by 10; one place to the right divides it by 10.",
                    formula = "3482 = 3000 + 400 + 80 + 2",
                    visual = LearnVisual.NUMBER_LINE,
                ),
                LessonStep.Choice(
                    question = "In 5164, what is the digit 1 worth?",
                    options = listOf("1", "10", "100", "1000"),
                    correctIndex = 2,
                    explanation = "The 1 sits in the hundreds place, so it is worth 1 × 100 = 100.",
                ),
                LessonStep.Concept(
                    body = "To round to a place, look at the single digit just to its right. " +
                        "If that digit is 5 or more, round up; if it is 4 or less, round down. " +
                        "Everything to the right becomes zero.",
                    formula = "digit ≥ 5 → up   |   digit ≤ 4 → down",
                ),
                LessonStep.Worked(
                    problem = "Round 3482 to the nearest hundred.",
                    lines = listOf(
                        "The hundreds digit is 4 → 34|82",
                        "The digit just to its right is 8",
                        "8 ≥ 5, so the hundreds digit goes up from 4 to 5",
                    ),
                    result = "3500",
                ),
                LessonStep.Numeric(
                    question = "Round 6749 to the nearest hundred.",
                    answer = "6700",
                    explanation = "The digit right of the hundreds place is 4, and 4 ≤ 4, so the " +
                        "hundreds digit stays at 7: 6700.",
                ),
                LessonStep.Choice(
                    question = "Which of these numbers is the largest?",
                    options = listOf("0.7", "0.68", "0.7001", "0.099"),
                    correctIndex = 2,
                    explanation = "Compare place by place. All start 0.7 or lower; 0.7001 beats " +
                        "0.7 by the extra ten-thousandth. Extra digits do not make 0.099 bigger.",
                ),
            ),
        ),
        LearnLesson(
            id = "arithmetic-order-of-operations",
            topic = MathTopic.ARITHMETIC,
            title = "Order of Operations",
            summary = "Why 6 + 2 × 5 is 16 and never 40.",
            steps = listOf(
                LessonStep.Concept(
                    body = "An expression is evaluated in a fixed order, not left to right. " +
                        "Brackets first, then powers, then multiplication and division, and " +
                        "finally addition and subtraction.",
                    formula = "( ) → xⁿ → × ÷ → + −",
                ),
                LessonStep.Worked(
                    problem = "Evaluate 6 + 2 × 5",
                    lines = listOf(
                        "Multiplication outranks addition",
                        "2 × 5 = 10",
                        "6 + 10",
                    ),
                    result = "16",
                ),
                LessonStep.Choice(
                    question = "What is 12 − 8 ÷ 4?",
                    options = listOf("1", "10", "4", "2"),
                    correctIndex = 1,
                    explanation = "Division first: 8 ÷ 4 = 2. Then 12 − 2 = 10.",
                ),
                LessonStep.Concept(
                    body = "Operations of the same rank are done left to right. × and ÷ share a " +
                        "rank, and so do + and −, so their order on the page decides.",
                    formula = "20 ÷ 5 × 2 = 4 × 2 = 8",
                ),
                LessonStep.Numeric(
                    question = "Evaluate (3 + 5) × 2²",
                    answer = "32",
                    explanation = "Brackets first: 3 + 5 = 8. Then the power: 2² = 4. Finally 8 × 4 = 32.",
                ),
                LessonStep.Choice(
                    question = "Which expression equals 20?",
                    options = listOf("5 + 2 × 8", "(4 + 2) × 8", "4 × (2 + 3)", "4 × 2 + 3"),
                    correctIndex = 2,
                    explanation = "4 × (2 + 3) = 4 × 5 = 20. The others give 21, 48 and 11.",
                ),
            ),
        ),
        LearnLesson(
            id = "arithmetic-fractions-percents",
            topic = MathTopic.ARITHMETIC,
            title = "Fractions, Decimals & Percents",
            summary = "Three notations for the same amount, and how to move between them.",
            steps = listOf(
                LessonStep.Concept(
                    body = "A fraction splits a whole into equal parts. The denominator says how " +
                        "many parts the whole was cut into; the numerator says how many you take.",
                    formula = "3/4 = 3 parts out of 4",
                    visual = LearnVisual.FRACTION_BAR,
                ),
                LessonStep.Concept(
                    body = "Dividing the numerator by the denominator gives the decimal, and " +
                        "multiplying that by 100 gives the percent. All three describe one amount.",
                    formula = "3 ÷ 4 = 0.75 = 75%",
                ),
                LessonStep.Choice(
                    question = "Which decimal equals 2/5?",
                    options = listOf("0.25", "0.4", "2.5", "0.45"),
                    correctIndex = 1,
                    explanation = "2 ÷ 5 = 0.4. (2/5 is also 40%.)",
                ),
                LessonStep.Worked(
                    problem = "What is 15% of 60?",
                    lines = listOf(
                        "\"Percent\" means per hundred: 15% = 15/100 = 0.15",
                        "\"of\" means multiply",
                        "0.15 × 60",
                    ),
                    result = "9",
                ),
                LessonStep.Numeric(
                    question = "A 40 € jacket is reduced by 25%. What do you pay, in euros?",
                    answer = "30",
                    explanation = "25% of 40 is 10, so the discount is 10 € and you pay 40 − 10 = 30 €. " +
                        "Shortcut: paying 75% of 40 is 0.75 × 40 = 30.",
                ),
                LessonStep.Choice(
                    question = "What is 1/3 + 1/6?",
                    options = listOf("2/9", "1/2", "2/6", "1/9"),
                    correctIndex = 1,
                    explanation = "Give both the same denominator: 1/3 = 2/6. Then 2/6 + 1/6 = 3/6 = 1/2. " +
                        "Never add denominators.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.ARITHMETIC,
        questions = listOf(
            QuizQuestion(
                prompt = "Round 2851 to the nearest hundred.",
                options = listOf("2800", "2850", "2900", "3000"),
                correctIndex = 2,
                explanation = "The digit right of the hundreds place is 5, so the 8 rounds up to 9: 2900.",
            ),
            QuizQuestion(
                prompt = "Evaluate 7 + 3 × (10 − 6)",
                options = listOf("19", "40", "22", "16"),
                correctIndex = 0,
                explanation = "Brackets: 10 − 6 = 4. Then 3 × 4 = 12, and 7 + 12 = 19.",
            ),
            QuizQuestion(
                prompt = "In 40 728, what is the digit 7 worth?",
                options = listOf("7", "70", "700", "7000"),
                correctIndex = 2,
                explanation = "It sits in the hundreds place: 7 × 100 = 700.",
            ),
            QuizQuestion(
                prompt = "Which is equal to 0.6?",
                options = listOf("1/6", "3/5", "6/100", "5/3"),
                correctIndex = 1,
                explanation = "3 ÷ 5 = 0.6. (1/6 ≈ 0.167, 6/100 = 0.06, 5/3 ≈ 1.67.)",
            ),
            QuizQuestion(
                prompt = "What is 12% of 250?",
                options = listOf("24", "30", "36", "3"),
                correctIndex = 1,
                explanation = "0.12 × 250 = 30.",
            ),
            QuizQuestion(
                prompt = "What is 3/4 − 1/2?",
                options = listOf("2/2", "1/4", "1/2", "2/4"),
                correctIndex = 1,
                explanation = "1/2 = 2/4, so 3/4 − 2/4 = 1/4.",
            ),
            QuizQuestion(
                prompt = "Evaluate 36 ÷ 6 ÷ 3",
                options = listOf("18", "2", "6", "1"),
                correctIndex = 1,
                explanation = "Same rank, so left to right: 36 ÷ 6 = 6, then 6 ÷ 3 = 2.",
            ),
            QuizQuestion(
                prompt = "A price rises from 80 to 100. By what percent did it rise?",
                options = listOf("20%", "25%", "80%", "125%"),
                correctIndex = 1,
                explanation = "The rise is 20, measured against the original 80: 20/80 = 0.25 = 25%.",
            ),
        ),
    )
}
