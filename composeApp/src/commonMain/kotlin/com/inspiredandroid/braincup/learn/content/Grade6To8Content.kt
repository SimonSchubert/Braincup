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
                        body = "The number line carries on to the left of zero. Negative numbers count how far below zero you are: temperature, an overdraft, floors under ground level.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Concept(
                        body = "Adding moves you right and subtracting moves you left. Subtracting a negative moves right too, because taking away a debt leaves you better off.",
                        formula = "5 - (-3) = 5 + 3 = 8",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Choice(
                        question = "The temperature is -4 degrees and drops another 6. What is it now?",
                        options = listOf("2", "-2", "-10", "10"),
                        correctIndex = 2,
                        explanation = "Dropping means moving left: -4 - 6 = -10.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Concept(
                        body = "Multiplying two numbers with the same sign gives a positive result; two different signs give a negative one.",
                        formula = "(-3) x (-4) = 12",
                    ),
                    Numeric(
                        question = "What is -7 + 12?",
                        answer = "5",
                        explanation = "Start at -7 and move 12 right: you reach zero after 7 steps, with 5 still to go.",
                    ),
                    Choice(
                        question = "Which of these is the largest number?",
                        options = listOf("-10", "-1", "0", "-100"),
                        correctIndex = 2,
                        explanation = "The further left on the number line, the smaller. Zero beats every negative.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-arithmetic-ratio",
                title = "Ratio and proportion",
                summary = "Comparing by division, and scaling recipes.",
                steps = listOf(
                    Concept(
                        body = "A ratio compares two amounts by division rather than subtraction. Mixing squash 1 : 4 means one part syrup to four parts water, so 200 ml : 800 ml is the very same mix.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Concept(
                        body = "Two ratios are in proportion when one scales to the other. Multiply or divide both parts by the same number and the mix is unchanged.",
                        formula = "3 : 5 = 12 : 20",
                    ),
                    Choice(
                        question = "A recipe for 4 people uses 300 g of rice. How much is needed for 6 people?",
                        options = listOf("350 g", "400 g", "450 g", "600 g"),
                        correctIndex = 2,
                        explanation = "One person needs 300 / 4 = 75 g, so six need 75 x 6 = 450 g.",
                    ),
                    Worked(
                        problem = "Share 60 euro in the ratio 2 : 3.",
                        lines = listOf(
                            "Count the parts: 2 + 3 = 5 parts altogether.",
                            "One part is 60 / 5 = 12 euro.",
                            "The two shares are 2 x 12 and 3 x 12.",
                        ),
                        result = "24 euro and 36 euro",
                    ),
                    Numeric(
                        question = "A map has scale 1 : 50000. A road measures 4 cm on the map. How many centimetres is it in reality?",
                        answer = "200000",
                        explanation = "Multiply by the scale: 4 x 50000 = 200000 cm, which is 2 km.",
                    ),
                    Choice(
                        question = "Which ratio is equivalent to 6 : 9?",
                        options = listOf("2 : 3", "3 : 2", "6 : 3", "1 : 2"),
                        correctIndex = 0,
                        explanation = "Divide both parts by 3.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-arithmetic-percent",
                title = "Percentages",
                summary = "Parts of a hundred, increases and decreases.",
                steps = listOf(
                    Concept(
                        body = "Per cent means 'out of a hundred'. So 35% is just 35 hundredths, which is 0.35 as a decimal.",
                        formula = "35% = 35/100 = 0.35",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Concept(
                        body = "To find a percentage of an amount, turn the percentage into a decimal and multiply.",
                        formula = "20% of 80 = 0.2 x 80 = 16",
                    ),
                    Numeric(
                        question = "What is 15% of 200?",
                        answer = "30",
                        explanation = "10% is 20 and 5% is half of that, 10. Together 30.",
                    ),
                    Choice(
                        question = "A 40 euro jacket is reduced by 25%. What do you pay?",
                        options = listOf("10 euro", "15 euro", "30 euro", "35 euro"),
                        correctIndex = 2,
                        explanation = "25% of 40 is 10, so you pay 40 - 10 = 30 euro.",
                    ),
                    Worked(
                        problem = "A price rises from 50 to 65. What is the percentage increase?",
                        lines = listOf(
                            "Find the change: 65 - 50 = 15.",
                            "Compare it with the original price, not the new one: 15/50.",
                            "15/50 = 0.3.",
                            "Multiply by 100 to get a percentage.",
                        ),
                        result = "A 30% increase",
                    ),
                    Choice(
                        question = "A price rises 10% and then falls 10%. Compared with the start you are...",
                        options = listOf("back where you started", "slightly better off", "slightly worse off", "unable to tell"),
                        correctIndex = 2,
                        explanation = "100 becomes 110, and 10% of 110 is 11, so you end at 99. The fall is taken from a bigger number.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "-6 + 9 = ?",
                options = listOf("-15", "-3", "3", "15"),
                correctIndex = 2,
                explanation = "Move 9 to the right of -6 and you land on 3.",
            ),
            QuizQuestion(
                prompt = "(-4) x (-5) = ?",
                options = listOf("-20", "-9", "9", "20"),
                correctIndex = 3,
                explanation = "Two negatives multiply to a positive.",
            ),
            QuizQuestion(
                prompt = "Simplify the ratio 15 : 25",
                options = listOf("1 : 2", "3 : 5", "5 : 3", "15 : 5"),
                correctIndex = 1,
                explanation = "Divide both parts by 5.",
            ),
            QuizQuestion(
                prompt = "Share 80 euro in the ratio 3 : 1. What is the larger share?",
                options = listOf("20 euro", "40 euro", "60 euro", "75 euro"),
                correctIndex = 2,
                explanation = "4 parts of 20 euro each; the larger share is 3 of them.",
            ),
            QuizQuestion(
                prompt = "What is 12% of 150?",
                options = listOf("12", "15", "18", "24"),
                correctIndex = 2,
                explanation = "0.12 x 150 = 18.",
            ),
            QuizQuestion(
                prompt = "A price falls from 80 to 60. What is the percentage decrease?",
                options = listOf("20%", "25%", "30%", "33%"),
                correctIndex = 1,
                explanation = "The change is 20, and 20/80 = 0.25.",
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
                summary = "Collecting terms, substituting and expanding.",
                steps = listOf(
                    Concept(
                        body = "A letter stands for a number you do not know yet. 'Three more than a number' becomes n + 3, and that stays true whatever n turns out to be.",
                        visual = LearnVisual.BALANCE_SCALE,
                    ),
                    Concept(
                        body = "Terms with the same letter can be collected together, but terms with different letters cannot. 3a + 5a is 8a, while 3a + 5b has to stay as it is.",
                        formula = "3a + 5a = 8a",
                    ),
                    Choice(
                        question = "Simplify 4x + 3 + 2x - 1",
                        options = listOf("6x + 2", "6x + 4", "9x", "5x + 2"),
                        correctIndex = 0,
                        explanation = "4x + 2x = 6x, and 3 - 1 = 2.",
                    ),
                    Concept(
                        body = "Substituting means putting a number back in place of the letter. If x = 4 then 3x + 2 means 3 x 4 + 2.",
                    ),
                    Numeric(
                        question = "If y = 5, what is 2y + 7?",
                        answer = "17",
                        explanation = "2 x 5 = 10, and 10 + 7 = 17.",
                    ),
                    Choice(
                        question = "Expand 3(x + 4)",
                        options = listOf("3x + 4", "x + 12", "3x + 12", "3x + 7"),
                        correctIndex = 2,
                        explanation = "Everything inside the bracket is multiplied by 3.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-algebra-equations",
                title = "Solving equations",
                summary = "Keep the scale balanced and undo step by step.",
                steps = listOf(
                    Concept(
                        body = "An equation says two things are equal, like a balanced scale. Whatever you do to one side you must also do to the other, or the balance tips.",
                        visual = LearnVisual.BALANCE_SCALE,
                    ),
                    Concept(
                        body = "To solve, undo the operations in reverse order until the letter stands alone on one side.",
                        formula = "x + 7 = 12 gives x = 5",
                        visual = LearnVisual.BALANCE_SCALE,
                    ),
                    Worked(
                        problem = "Solve 3x + 4 = 19",
                        lines = listOf(
                            "The x is multiplied by 3 and then 4 is added, so undo the adding first.",
                            "Subtract 4 from both sides: 3x = 15.",
                            "Now divide both sides by 3.",
                        ),
                        result = "x = 5",
                        visual = LearnVisual.BALANCE_SCALE,
                    ),
                    Numeric(
                        question = "Solve 5x - 3 = 22. What is x?",
                        answer = "5",
                        explanation = "Add 3 to both sides for 5x = 25, then divide by 5.",
                    ),
                    Choice(
                        question = "Solve 2(x - 3) = 10",
                        options = listOf("x = 2", "x = 5", "x = 8", "x = 13"),
                        correctIndex = 2,
                        explanation = "Divide both sides by 2 to get x - 3 = 5, so x = 8.",
                    ),
                    Concept(
                        body = "Always check by putting your answer back into the original equation. If both sides come out equal, the solution is right.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-algebra-slope",
                title = "Straight-line graphs",
                summary = "Gradient, intercept and the shape of y = mx + c.",
                steps = listOf(
                    Concept(
                        body = "A pair of coordinates (x, y) fixes one point on the grid: x tells you how far across, y how far up.",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Concept(
                        body = "An equation such as y = 2x + 1 gives a y for every x. Plot those pairs and they line up perfectly straight.",
                        formula = "y = mx + c",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Concept(
                        body = "In y = mx + c the number m is the gradient - how much y climbs for each step to the right - and c is where the line crosses the y-axis.",
                        visual = LearnVisual.COORDINATE_GRID,
                    ),
                    Choice(
                        question = "Where does y = 3x - 2 cross the y-axis?",
                        options = listOf("(0, 3)", "(0, -2)", "(-2, 0)", "(3, 0)"),
                        correctIndex = 1,
                        explanation = "At x = 0 the 3x term vanishes, leaving y = -2.",
                    ),
                    Numeric(
                        question = "A line has gradient 4. If x increases by 3, by how much does y increase?",
                        answer = "12",
                        explanation = "The gradient is the rise per step right, so 4 x 3 = 12.",
                    ),
                    Choice(
                        question = "Which line is steeper: y = 2x + 9 or y = 5x - 4?",
                        options = listOf("y = 2x + 9", "y = 5x - 4", "They are equally steep", "The one with the bigger constant"),
                        correctIndex = 1,
                        explanation = "Steepness is the gradient alone, and 5 beats 2. The constant only slides the line up or down.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Simplify 5a + 2 - 3a",
                options = listOf("2a + 2", "8a", "2a - 2", "5a"),
                correctIndex = 0,
                explanation = "5a - 3a = 2a, and the 2 has no partner.",
            ),
            QuizQuestion(
                prompt = "Expand 4(x - 2)",
                options = listOf("4x - 2", "4x - 8", "x - 8", "4x + 8"),
                correctIndex = 1,
                explanation = "Multiply both terms inside the bracket by 4.",
            ),
            QuizQuestion(
                prompt = "Solve 2x + 5 = 17",
                options = listOf("x = 5", "x = 6", "x = 11", "x = 22"),
                correctIndex = 1,
                explanation = "2x = 12, so x = 6.",
            ),
            QuizQuestion(
                prompt = "If x = 3, what is 4x - 5?",
                options = listOf("2", "7", "12", "17"),
                correctIndex = 1,
                explanation = "4 x 3 = 12, and 12 - 5 = 7.",
            ),
            QuizQuestion(
                prompt = "What is the gradient of y = -3x + 7?",
                options = listOf("7", "3", "-3", "-7"),
                correctIndex = 2,
                explanation = "The gradient is the number multiplying x, sign included.",
            ),
            QuizQuestion(
                prompt = "Where does y = 2x + 5 cross the y-axis?",
                options = listOf("(0, 2)", "(0, 5)", "(5, 0)", "(2, 0)"),
                correctIndex = 1,
                explanation = "Set x = 0 and y is left as 5.",
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
                summary = "The rule that links the sides of a right triangle.",
                steps = listOf(
                    Concept(
                        body = "In a right-angled triangle the side opposite the right angle is called the hypotenuse, and it is always the longest of the three.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Concept(
                        body = "Pythagoras' theorem links the three sides: the squares on the two short sides add up to the square on the hypotenuse.",
                        formula = "a² + b² = c²",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Worked(
                        problem = "A right triangle has short sides 3 and 4. How long is the hypotenuse?",
                        lines = listOf(
                            "Square the short sides: 3² = 9 and 4² = 16.",
                            "Add them: 9 + 16 = 25.",
                            "That is the hypotenuse squared, so take the square root.",
                        ),
                        result = "c = 5",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Numeric(
                        question = "A right triangle has legs of 6 and 8. How long is the hypotenuse?",
                        answer = "10",
                        explanation = "36 + 64 = 100, and the square root of 100 is 10.",
                    ),
                    Choice(
                        question = "A 13 m ladder leans with its foot 5 m from the wall. How high up the wall does it reach?",
                        options = listOf("8 m", "10 m", "12 m", "14 m"),
                        correctIndex = 2,
                        explanation = "Here the hypotenuse is known, so subtract: 169 - 25 = 144, and the root of 144 is 12.",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Concept(
                        body = "The theorem only works for right-angled triangles - but it also works backwards: if three sides fit the formula, the triangle must contain a right angle.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-circles",
                title = "Circles",
                summary = "Circumference and area, and where pi comes from.",
                steps = listOf(
                    Concept(
                        body = "The radius runs from the centre to the edge. The diameter crosses the whole circle through the centre, so it is twice the radius.",
                        formula = "d = 2r",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Concept(
                        body = "Divide any circle's circumference by its diameter and you always get the same number. That number is pi, roughly 3.14.",
                        formula = "C = pi x d = 2 x pi x r",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Numeric(
                        question = "A circle has a radius of 5 cm. What is its diameter in cm?",
                        answer = "10",
                        explanation = "The diameter is twice the radius.",
                    ),
                    Choice(
                        question = "A wheel has a diameter of 70 cm. Roughly how far does it roll in one full turn?",
                        options = listOf("110 cm", "220 cm", "350 cm", "440 cm"),
                        correctIndex = 1,
                        explanation = "One turn covers the circumference: 3.14 x 70 is about 220 cm.",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Concept(
                        body = "Area needs the radius rather than the diameter, and the radius gets squared - so doubling the radius quadruples the area.",
                        formula = "A = pi x r²",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Choice(
                        question = "What is the area of a circle with radius 10 cm, taking pi as 3.14?",
                        options = listOf("31.4", "62.8", "314", "628"),
                        correctIndex = 2,
                        explanation = "3.14 x 10² = 3.14 x 100 = 314 square cm.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-volume",
                title = "Volume of prisms",
                summary = "Cross-section times length, every time.",
                steps = listOf(
                    Concept(
                        body = "Volume counts the unit cubes that fit inside a solid, so its units are cubed: cubic centimetres, cubic metres.",
                        visual = LearnVisual.SOLIDS,
                    ),
                    Concept(
                        body = "A prism has the same cross-section all the way along. Its volume is the area of that cross-section multiplied by the length.",
                        formula = "V = base area x height",
                        visual = LearnVisual.SOLIDS,
                    ),
                    Numeric(
                        question = "A box is 4 cm by 3 cm by 2 cm. What is its volume in cubic cm?",
                        answer = "24",
                        explanation = "4 x 3 x 2 = 24.",
                    ),
                    Choice(
                        question = "A cylinder has base radius r and height h. Which expression gives its volume?",
                        options = listOf("2 x pi x r x h", "pi x r² x h", "pi x d x h", "r² + h"),
                        correctIndex = 1,
                        explanation = "The cross-section is a circle of area pi x r², repeated all the way up the height.",
                    ),
                    Worked(
                        problem = "A triangular prism has a base triangle of area 6 cm² and a length of 9 cm.",
                        lines = listOf(
                            "The cross-section is the same triangle all along the prism.",
                            "Volume is cross-section area times length.",
                            "6 x 9 = 54.",
                        ),
                        result = "V = 54 cm³",
                    ),
                    Choice(
                        question = "Doubling every side of a cube multiplies its volume by...",
                        options = listOf("2", "4", "6", "8"),
                        correctIndex = 3,
                        explanation = "All three dimensions double: 2 x 2 x 2 = 8.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A right triangle has legs of 5 and 12. How long is the hypotenuse?",
                options = listOf("13", "14", "15", "17"),
                correctIndex = 0,
                explanation = "25 + 144 = 169, and the root of 169 is 13.",
            ),
            QuizQuestion(
                prompt = "A circle has a diameter of 18 cm. What is its radius?",
                options = listOf("6 cm", "9 cm", "18 cm", "36 cm"),
                correctIndex = 1,
                explanation = "The radius is half the diameter.",
            ),
            QuizQuestion(
                prompt = "Circumference of a circle with diameter 10 cm, taking pi as 3.14?",
                options = listOf("15.7 cm", "31.4 cm", "78.5 cm", "314 cm"),
                correctIndex = 1,
                explanation = "C = pi x d = 3.14 x 10.",
            ),
            QuizQuestion(
                prompt = "Area of a circle with radius 4 cm, taking pi as 3.14?",
                options = listOf("12.56", "25.12", "50.24", "100.48"),
                correctIndex = 2,
                explanation = "3.14 x 16 = 50.24 square cm.",
            ),
            QuizQuestion(
                prompt = "What is the volume of a box 5 cm by 4 cm by 3 cm?",
                options = listOf("12", "20", "47", "60"),
                correctIndex = 3,
                explanation = "5 x 4 x 3 = 60 cubic cm.",
            ),
            QuizQuestion(
                prompt = "Which gives the volume of any prism?",
                options = listOf("perimeter x height", "cross-section area x length", "pi x r²", "2 x pi x r"),
                correctIndex = 1,
                explanation = "The cross-section repeats unchanged along the whole length.",
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
                summary = "Three middles, and one measure of spread.",
                steps = listOf(
                    Concept(
                        body = "Three averages describe the middle of a data set in different ways: the mean shares the total out, the median is the middle value once sorted, and the mode is the most common value.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Worked(
                        problem = "Find the median of 7, 3, 9, 4, 6.",
                        lines = listOf(
                            "Put the values in order: 3, 4, 6, 7, 9.",
                            "There are 5 values, so the middle one is the third.",
                        ),
                        result = "Median = 6",
                    ),
                    Numeric(
                        question = "Find the median of 2, 5, 8 and 11.",
                        answer = "6.5",
                        explanation = "With an even count, take the mean of the two middle values: (5 + 8) / 2 = 6.5.",
                    ),
                    Choice(
                        question = "Five salaries are 20k, 22k, 21k, 23k and 200k. Which average best describes a typical salary?",
                        options = listOf("The mean", "The median", "The mode", "The range"),
                        correctIndex = 1,
                        explanation = "The single huge value drags the mean above every ordinary salary. The median ignores it.",
                    ),
                    Concept(
                        body = "The range measures spread rather than middle: largest minus smallest. Two classes can share a mean and still have completely different ranges.",
                    ),
                    Choice(
                        question = "What is the range of 4, 9, 2 and 15?",
                        options = listOf("4", "9", "13", "15"),
                        correctIndex = 2,
                        explanation = "15 - 2 = 13.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-data-probability",
                title = "Probability",
                summary = "Counting outcomes on a scale from 0 to 1.",
                steps = listOf(
                    Concept(
                        body = "Probability measures how likely something is on a scale from 0 (impossible) to 1 (certain). It can be written as a fraction, a decimal or a percentage.",
                        visual = LearnVisual.PIE_CHART,
                    ),
                    Concept(
                        body = "When every outcome is equally likely, probability is just a count.",
                        formula = "P = favourable outcomes / all outcomes",
                        visual = LearnVisual.PIE_CHART,
                    ),
                    Choice(
                        question = "What is the probability of rolling an even number on a fair six-sided die?",
                        options = listOf("1/6", "1/3", "1/2", "2/3"),
                        correctIndex = 2,
                        explanation = "Three of the six faces (2, 4 and 6) are even.",
                    ),
                    Numeric(
                        question = "A bag holds 3 red and 7 blue balls. What is the probability of drawing red, as a decimal?",
                        answer = "0.3",
                        explanation = "3 favourable out of 10 possible is 0.3.",
                    ),
                    Concept(
                        body = "The probabilities of all possible outcomes add up to 1, so the chance of something not happening is 1 minus the chance that it does.",
                        formula = "P(not A) = 1 - P(A)",
                    ),
                    Choice(
                        question = "The chance of rain is 0.35. What is the chance of no rain?",
                        options = listOf("0.35", "0.55", "0.65", "0.75"),
                        correctIndex = 2,
                        explanation = "1 - 0.35 = 0.65.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-data-charts",
                title = "Choosing a chart",
                summary = "Match the chart to the question - and spot the tricks.",
                steps = listOf(
                    Concept(
                        body = "The chart has to match the question. Bar charts compare separate categories, line graphs show change over time, and pie charts show shares of a single whole.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Concept(
                        body = "A pie chart's slices are fractions of a full 360 degree turn, so a category holding a quarter of the data gets a 90 degree slice.",
                        formula = "angle = fraction x 360",
                        visual = LearnVisual.PIE_CHART,
                    ),
                    Numeric(
                        question = "A pie chart slice covers 25% of the data. How many degrees is it?",
                        answer = "90",
                        explanation = "0.25 x 360 = 90 degrees.",
                    ),
                    Choice(
                        question = "Which chart best shows a town's population every year from 2000 to 2020?",
                        options = listOf("Pie chart", "Line graph", "Tally chart", "Pictogram"),
                        correctIndex = 1,
                        explanation = "Time along the horizontal axis and a connecting line makes the trend visible.",
                    ),
                    Concept(
                        body = "Charts can mislead. A bar chart whose vertical axis starts at 90 instead of 0 makes tiny differences look enormous.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Choice(
                        question = "A bar chart's vertical axis starts at 50 rather than 0. What is the effect?",
                        options = listOf("Nothing changes", "Differences look smaller", "Differences look bigger", "The bars change order"),
                        correctIndex = 2,
                        explanation = "Cutting off the bottom of every bar exaggerates the gaps between them.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What is the median of 3, 8, 5, 10, 6?",
                options = listOf("5", "6", "6.4", "8"),
                correctIndex = 1,
                explanation = "Sorted they read 3, 5, 6, 8, 10 and the middle value is 6.",
            ),
            QuizQuestion(
                prompt = "What is the range of 12, 4, 9 and 20?",
                options = listOf("8", "11", "16", "20"),
                correctIndex = 2,
                explanation = "20 - 4 = 16.",
            ),
            QuizQuestion(
                prompt = "What is the probability of rolling a 5 or a 6 on a fair die?",
                options = listOf("1/6", "1/3", "1/2", "2/3"),
                correctIndex = 1,
                explanation = "Two faces out of six is one third.",
            ),
            QuizQuestion(
                prompt = "If P(A) = 0.2, what is P(not A)?",
                options = listOf("0.2", "0.5", "0.8", "1.2"),
                correctIndex = 2,
                explanation = "All outcomes together make 1.",
            ),
            QuizQuestion(
                prompt = "A pie chart slice measures 90 degrees. What share of the data is that?",
                options = listOf("10%", "25%", "50%", "90%"),
                correctIndex = 1,
                explanation = "90 out of 360 is a quarter.",
            ),
            QuizQuestion(
                prompt = "Which chart shows change over time best?",
                options = listOf("Pie chart", "Line graph", "Bar chart of categories", "Tally chart"),
                correctIndex = 1,
                explanation = "A line makes a trend across time immediately visible.",
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, algebra, geometry, data)
}
