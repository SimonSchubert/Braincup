package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnQuiz
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion

internal object DataContent {

    val lessons: List<LearnLesson> = listOf(
        LearnLesson(
            id = "data-averages",
            topic = MathTopic.DATA,
            title = "Mean, Median & Mode",
            summary = "Three different \"averages\", and when each one tells the truth.",
            steps = listOf(
                LessonStep.Concept(
                    body = "\"Average\" is three different measures. The mean shares the total " +
                        "out evenly, the median is the middle value once the data is sorted, and " +
                        "the mode is the value that appears most often.",
                    formula = "mean = sum ÷ count",
                ),
                LessonStep.Worked(
                    problem = "Find the mean, median and mode of 4, 8, 6, 4, 3.",
                    lines = listOf(
                        "Mean: (4 + 8 + 6 + 4 + 3) ÷ 5 = 25 ÷ 5",
                        "Sorted: 3, 4, 4, 6, 8 → the middle value is 4",
                        "4 appears twice, every other value once",
                    ),
                    result = "mean 5,  median 4,  mode 4",
                ),
                LessonStep.Numeric(
                    question = "What is the mean of 12, 15, 9 and 8?",
                    answer = "11",
                    explanation = "The total is 44 and there are 4 values: 44 ÷ 4 = 11.",
                ),
                LessonStep.Choice(
                    question = "What is the median of 7, 3, 9, 4, 10, 6?",
                    options = listOf("6", "6.5", "7", "9"),
                    correctIndex = 1,
                    explanation = "Sort first: 3, 4, 6, 7, 9, 10. With an even count, take the mean " +
                        "of the two middle values: (6 + 7) ÷ 2 = 6.5.",
                ),
                LessonStep.Concept(
                    body = "One extreme value drags the mean a long way but barely moves the " +
                        "median. When data has outliers, the median usually describes \"typical\" better.",
                ),
                LessonStep.Choice(
                    question = "Five salaries: 20k, 22k, 23k, 25k, 300k. Which average best describes a typical salary?",
                    options = listOf("The mean", "The median", "The mode", "The range"),
                    correctIndex = 1,
                    explanation = "The mean is 78k — higher than four of the five salaries, because " +
                        "the 300k outlier pulls it up. The median, 23k, is the honest summary.",
                ),
            ),
        ),
        LearnLesson(
            id = "data-spread-charts",
            topic = MathTopic.DATA,
            title = "Spread & Charts",
            summary = "How widely data varies, and which chart shows it fairly.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Two data sets can share a mean and still look nothing alike. The " +
                        "range — largest minus smallest — is the simplest measure of how spread out they are.",
                    formula = "range = max − min",
                    visual = LearnVisual.BAR_CHART,
                ),
                LessonStep.Choice(
                    question = "What is the range of 4, 19, 7, 11?",
                    options = listOf("4", "11", "15", "19"),
                    correctIndex = 2,
                    explanation = "19 − 4 = 15.",
                ),
                LessonStep.Concept(
                    body = "Each chart answers a different question. Bar charts compare separate " +
                        "categories, line graphs show change over time, pie charts split a whole " +
                        "into parts, and scatter plots reveal a relationship between two variables.",
                ),
                LessonStep.Choice(
                    question = "Which chart best shows how a city's population changed each year from 1990 to 2020?",
                    options = listOf("Pie chart", "Line graph", "Scatter plot", "Pictogram"),
                    correctIndex = 1,
                    explanation = "The data is one quantity measured over time, which is exactly " +
                        "what a line graph is for.",
                ),
                LessonStep.Numeric(
                    question = "In a pie chart, one slice is 25% of the whole. How many degrees is its angle?",
                    answer = "90",
                    explanation = "A full circle is 360°, and 25% of 360 is 90°.",
                ),
                LessonStep.Concept(
                    body = "Charts can mislead without stating a single falsehood. A vertical axis " +
                        "that starts above zero makes small differences look dramatic, so always " +
                        "read the axis before believing the picture.",
                ),
            ),
        ),
        LearnLesson(
            id = "data-probability",
            topic = MathTopic.DATA,
            title = "Probability Basics",
            summary = "Counting outcomes, and combining chances that do not affect each other.",
            steps = listOf(
                LessonStep.Concept(
                    body = "Probability runs from 0 (impossible) to 1 (certain). When every " +
                        "outcome is equally likely, it is simply a count of the outcomes you want " +
                        "over the count of all outcomes.",
                    formula = "P = favourable ÷ total",
                ),
                LessonStep.Worked(
                    problem = "A fair die is rolled. What is the probability of an even number?",
                    lines = listOf(
                        "Total outcomes: 1, 2, 3, 4, 5, 6 → 6",
                        "Even outcomes: 2, 4, 6 → 3",
                        "P = 3/6",
                    ),
                    result = "P = 1/2",
                ),
                LessonStep.Choice(
                    question = "What is the probability of drawing a red card from a standard 52-card deck?",
                    options = listOf("1/4", "1/2", "1/13", "1/26"),
                    correctIndex = 1,
                    explanation = "26 of the 52 cards are red: 26/52 = 1/2. (1/4 would be one suit.)",
                ),
                LessonStep.Concept(
                    body = "Everything that is not the event is its complement, so the two must " +
                        "add to 1. And when two events do not affect each other, the chance of " +
                        "both happening is the product of their chances.",
                    formula = "P(not A) = 1 − P(A)    P(A and B) = P(A) × P(B)",
                ),
                LessonStep.Numeric(
                    question = "Two coins are tossed. How many different outcomes are there in total?",
                    answer = "4",
                    explanation = "HH, HT, TH, TT — two choices for the first coin times two for the second.",
                ),
                LessonStep.Choice(
                    question = "What is the probability of getting two heads when tossing two fair coins?",
                    options = listOf("1/2", "1/3", "1/4", "3/4"),
                    correctIndex = 2,
                    explanation = "The tosses are independent: ½ × ½ = ¼. Only HH out of four " +
                        "equally likely outcomes.",
                ),
            ),
        ),
    )

    val quiz = LearnQuiz(
        topic = MathTopic.DATA,
        questions = listOf(
            QuizQuestion(
                prompt = "What is the mean of 3, 7, 7, 11?",
                options = listOf("7", "6", "7.5", "28"),
                correctIndex = 0,
                explanation = "28 ÷ 4 = 7.",
            ),
            QuizQuestion(
                prompt = "What is the mode of 5, 2, 9, 5, 3, 9, 5?",
                options = listOf("9", "5", "3", "4"),
                correctIndex = 1,
                explanation = "5 appears three times, more often than any other value.",
            ),
            QuizQuestion(
                prompt = "What is the median of 8, 2, 5?",
                options = listOf("2", "5", "8", "15"),
                correctIndex = 1,
                explanation = "Sorted: 2, 5, 8. The middle value is 5.",
            ),
            QuizQuestion(
                prompt = "A bag holds 3 red and 5 blue balls. What is P(red)?",
                options = listOf("3/5", "3/8", "5/8", "1/3"),
                correctIndex = 1,
                explanation = "3 favourable out of 8 total.",
            ),
            QuizQuestion(
                prompt = "If P(rain) = 0.3, what is P(no rain)?",
                options = listOf("0.3", "0.5", "0.7", "1.3"),
                correctIndex = 2,
                explanation = "The complement: 1 − 0.3 = 0.7.",
            ),
            QuizQuestion(
                prompt = "A die is rolled twice. What is the probability of two sixes?",
                options = listOf("1/6", "1/12", "1/36", "2/6"),
                correctIndex = 2,
                explanation = "Independent rolls multiply: 1/6 × 1/6 = 1/36.",
            ),
            QuizQuestion(
                prompt = "Which chart is best for showing what share of a budget each department takes?",
                options = listOf("Line graph", "Pie chart", "Scatter plot", "Histogram of time"),
                correctIndex = 1,
                explanation = "Parts of a single whole is exactly what a pie chart shows.",
            ),
            QuizQuestion(
                prompt = "Adding a value of 200 to the data set 4, 5, 6 mainly changes…",
                options = listOf("the mode", "the median more than the mean", "the mean more than the median", "nothing"),
                correctIndex = 2,
                explanation = "The mean jumps from 5 to about 53.75, while the median only moves from 5 to 5.5.",
            ),
        ),
    )
}
