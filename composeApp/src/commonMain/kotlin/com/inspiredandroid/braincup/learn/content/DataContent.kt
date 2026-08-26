package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.NormalCurve
import com.inspiredandroid.braincup.learn.LearnVisual.Pictogram
import com.inspiredandroid.braincup.learn.LearnVisual.PieChart
import com.inspiredandroid.braincup.learn.LearnVisual.SetDiagram
import com.inspiredandroid.braincup.learn.LearnVisual.Tally
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/** Data and probability: reading charts, then averages and chance, then distributions. */
internal object DataContent {

    private val graphsAndAverages = learnUnit(
        topic = MathTopic.DATA,
        urlSlug = "graphs-and-averages",
        title = "Reading graphs and the mean",
        summary = "Reading graphs, collecting data and finding the mean.",
        level = GradeLevel.GRADES_3_5,
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

    private val averagesAndProbability = learnUnit(
        topic = MathTopic.DATA,
        urlSlug = "averages-and-probability",
        title = "Averages, probability and honest charts",
        summary = "Mean, median and range, first probability, and honest charts.",
        level = GradeLevel.GRADES_6_8,
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

    private val distributions = learnUnit(
        topic = MathTopic.DATA,
        urlSlug = "distributions",
        title = "Spread, the normal curve and conditional probability",
        summary = "Standard deviation, the normal distribution and conditional probability.",
        level = GradeLevel.GRADES_11_12,
        lessons = listOf(
            LessonSpec(
                id = "g1112-data-distributions",
                title = "Spread and z-scores",
                summary = "Same mean, different worlds.",
                steps = listOf(
                    Concept(
                        body = "Standard deviation measures the typical distance from the mean.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Concept(
                        body = "Two sets can share a mean and still look nothing alike.",
                        visual = BarChart(values = listOf(48, 52, 50, 51, 49), showMean = true),
                    ),
                    Choice(
                        question = "Two classes both average 70%. One has sd 3, the other sd 15. Which is more consistent?",
                        options = listOf("sd 3", "sd 15", "equally", "cannot tell"),
                        correctIndex = 0,
                        explanation = "Smaller spread means results cluster near the mean.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Concept(
                        body = "A z-score counts standard deviations from the mean, so any two scales become comparable.",
                        formula = "z = (value - mean) / sd",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Numeric(
                        question = "Mean 60, sd 10. What is the z-score of 80?",
                        answer = "2",
                        explanation = "20 above the mean is two standard deviations.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Choice(
                        question = "A z-score of -1.5 means the value is...",
                        options = listOf("1.5 above the mean", "1.5 sd below the mean", "1.5% of the data", "impossible"),
                        correctIndex = 1,
                        explanation = "Negative z-scores sit below the mean.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-normal",
                title = "The normal distribution",
                summary = "The bell curve and the 68-95-99.7 rule.",
                steps = listOf(
                    Concept(
                        body = "Many natural measurements pile up symmetrically around the mean.",
                        visual = NormalCurve(shadeSd = 1, percent = "68%"),
                    ),
                    Concept(
                        body = "Two standard deviations either side already covers almost everything.",
                        visual = NormalCurve(shadeSd = 2, percent = "95%"),
                    ),
                    Choice(
                        question = "Heights are normal, mean 170 cm, sd 10 cm. What percent lie between 160 and 180?",
                        options = listOf("50%", "68%", "95%", "99.7%"),
                        correctIndex = 1,
                        explanation = "That band is one standard deviation either side.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Numeric(
                        question = "What percent lies within two standard deviations of the mean?",
                        answer = "95",
                        explanation = "The middle step of the 68-95-99.7 rule.",
                        visual = NormalCurve(shadeSd = 2),
                    ),
                    Concept(
                        body = "The curve is symmetric, so mean, median and mode all sit together.",
                        visual = NormalCurve(shadeSd = 1),
                    ),
                    Choice(
                        question = "Values more than three standard deviations out are...",
                        options = listOf("common", "about a third of the data", "very rare", "impossible"),
                        correctIndex = 2,
                        explanation = "About 0.3% of the data.",
                        visual = NormalCurve(shadeSd = 3),
                    ),
                ),
            ),
            LessonSpec(
                id = "g1112-data-conditional",
                title = "Conditional probability",
                summary = "What changes once you know something.",
                steps = listOf(
                    Concept(
                        body = "Knowing B shrinks the pool you are choosing from.",
                        formula = "P(A given B) = P(A and B) / P(B)",
                        visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
                    ),
                    Worked(
                        problem = "12 take French, 8 of those also take German. A French student is picked.",
                        lines = listOf(
                            "The pool is now only the 12 French students.",
                            "Of those, 8 also take German.",
                            "So 8 out of 12.",
                        ),
                        result = "2/3",
                        visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
                    ),
                    Choice(
                        question = "Two events are independent when...",
                        options = listOf(
                            "they cannot both happen",
                            "knowing one tells you nothing about the other",
                            "they are equally likely",
                            "they are opposites",
                        ),
                        correctIndex = 1,
                        explanation = "The conditional probability equals the plain one.",
                        visual = SetDiagram(aOnly = 6, both = 3, bOnly = 6, aLabel = "A", bLabel = "B"),
                    ),
                    Concept(
                        body = "Independent probabilities multiply, which is why long lucky runs are so rare.",
                        formula = "P(A and B) = P(A) x P(B)",
                        visual = PieChart(shares = listOf(1, 3), labels = listOf("both heads", "anything else")),
                    ),
                    Numeric(
                        question = "Two fair coins. What is the probability of two heads, as a decimal?",
                        answer = "0.25",
                        explanation = "0.5 x 0.5.",
                        visual = PieChart(shares = listOf(1, 3), reveal = false),
                    ),
                    Choice(
                        question = "Drawing a second card without replacing the first makes it...",
                        options = listOf("independent", "dependent on the first", "impossible", "identical"),
                        correctIndex = 1,
                        explanation = "The deck has changed.",
                        visual = SetDiagram(aOnly = 12, both = 1, bOnly = 12, aLabel = "1st", bLabel = "2nd"),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Mean 50, sd 5. What is the z-score of 60?",
                options = listOf("1", "2", "10", "0.5"),
                correctIndex = 1,
                explanation = "10 above the mean is two standard deviations.",
                visual = NormalCurve(shadeSd = 2),
            ),
            QuizQuestion(
                prompt = "What percent of a normal distribution lies in the shaded band?",
                options = listOf("50%", "68%", "95%", "99.7%"),
                correctIndex = 1,
                explanation = "One standard deviation either side.",
                visual = NormalCurve(shadeSd = 1),
            ),
            QuizQuestion(
                prompt = "A smaller standard deviation means the data is...",
                options = listOf("higher on average", "more spread out", "more consistent", "smaller in count"),
                correctIndex = 2,
                explanation = "Values cluster near the mean.",
                visual = NormalCurve(shadeSd = 1),
            ),
            QuizQuestion(
                prompt = "For independent events, P(A and B) equals...",
                options = listOf("P(A) + P(B)", "P(A) x P(B)", "P(A) - P(B)", "P(A) / P(B)"),
                correctIndex = 1,
                explanation = "Independent probabilities multiply.",
                visual = PieChart(shares = listOf(1, 3), reveal = false),
            ),
            QuizQuestion(
                prompt = "Two fair dice. What is the probability of two sixes?",
                options = listOf("1/6", "1/12", "1/36", "2/6"),
                correctIndex = 2,
                explanation = "1/6 x 1/6.",
                visual = PieChart(shares = listOf(1, 35), reveal = false),
            ),
            QuizQuestion(
                prompt = "A French student is picked. What is the chance they also take German?",
                options = listOf("8/20", "8/12", "12/20", "4/12"),
                correctIndex = 1,
                explanation = "The condition restricts the pool to the 12 French students.",
                visual = SetDiagram(aOnly = 4, both = 8, bOnly = 5, aLabel = "French", bLabel = "German"),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(graphsAndAverages, averagesAndProbability, distributions)
}
