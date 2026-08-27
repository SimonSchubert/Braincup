package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.ArrayDots
import com.inspiredandroid.braincup.learn.LearnVisual.BarChart
import com.inspiredandroid.braincup.learn.LearnVisual.DecimalGrid
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.NumberLine
import com.inspiredandroid.braincup.learn.LearnVisual.PlaceValue
import com.inspiredandroid.braincup.learn.LearnVisual.RatioBar
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Steps
import com.inspiredandroid.braincup.learn.LearnVisual.TenFrame
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.learnUnit

/**
 * Arithmetic: counting and first sums, then multiplication, fractions and decimals, then ratio and
 * percent, and finally the numbers that need rules of their own - standard form, surds and bounds.
 */
internal object ArithmeticContent {

    private val counting = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "counting",
        title = "Counting and first sums",
        summary = "Counting on, sums to 20, and tens and ones.",
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-arithmetic-counting",
                title = "Counting to 100",
                summary = "Find any number on the number line.",
                steps = listOf(
                    Concept(
                        body = "One step right is one more. One step left is one less.",
                        visual = NumberLine(from = 0, to = 10, start = 6, jump = 1, thenJump = -1),
                    ),
                    Concept(
                        body = "Counting on is adding. Watch three hops land on ten.",
                        formula = "7 + {b:3} = 10",
                        visual = NumberLine(from = 0, to = 12, start = 7, jump = 3, hops = 3),
                    ),
                    Choice(
                        question = "Start at 12 and count on four hops.",
                        formula = "12 + {b:4} = ?",
                        options = listOf("14", "15", "16", "17"),
                        correctIndex = 2,
                        explanation = "13, 14, 15, 16 - four hops to the right lands on 16.",
                        visual = NumberLine(from = 10, to = 20, start = 12, jump = 4, hops = 4, reveal = false),
                    ),
                    Concept(
                        body = "Skip counting means jumping by the same amount each time instead of one by one. These jumps are all ten.",
                        formula = "10, 20, 30, 40, 50",
                        visual = Steps(terms = listOf(10, 20, 30, 40, 50)),
                    ),
                    Choice(
                        question = "Each jump adds the same amount. Keep going.",
                        formula = "30, 40, 50, ?",
                        options = listOf("51", "55", "60", "70"),
                        correctIndex = 2,
                        explanation = "Every jump adds ten, so 50 + 10 = 60.",
                        visual = Steps(terms = listOf(30, 40, 50)),
                    ),
                    Choice(
                        question = "Count back one from 40.",
                        formula = "40 - {b:1} = ?",
                        options = listOf("30", "39", "41", "44"),
                        correctIndex = 1,
                        explanation = "Counting back moves one step left, so 40 becomes 39, not 30.",
                        visual = NumberLine(from = 35, to = 45, start = 40, jump = -1, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-addsub",
                title = "Adding and taking away",
                summary = "Make ten, and count back to subtract.",
                steps = listOf(
                    Worked(
                        problem = "{a:6} + {b:7} = ?",
                        lines = listOf(
                            "Ten is the easy number, so fill the frame up to ten first.",
                            "{a:6} needs {b:4} more to fill it.",
                            "Split the {b:7} into {b:4} and {b:3}.",
                            "{a:6} + {b:4} = 10.",
                            "10 + {b:3} = 13.",
                        ),
                        result = "13",
                        visual = TenFrame(filled = 6, added = 7, reveal = false),
                    ),
                    Choice(
                        question = "Count the empty squares.",
                        formula = "{a:7} + ? = 10",
                        options = listOf("2", "3", "4", "5"),
                        correctIndex = 1,
                        explanation = "The frame holds ten and {a:7} are filled, so {b:3} squares are still empty.",
                        visual = TenFrame(filled = 7, added = 0, reveal = false),
                    ),
                    Choice(
                        question = "Fill the ten first, then add what is left over.",
                        formula = "{a:8} + {b:5} = ?",
                        options = listOf("12", "13", "14", "15"),
                        correctIndex = 1,
                        explanation = "{a:8} needs {b:2} to reach ten, and {b:3} of the {b:5} are left over.",
                        visual = TenFrame(filled = 8, added = 5, reveal = false),
                    ),
                    Concept(
                        body = "Taking away hops back the same way: down to ten first, then the rest.",
                        formula = "15 - {b:8} = 15 - {b:5} - {b:3}",
                        visual = NumberLine(from = 0, to = 16, start = 15, hopSteps = listOf(-5, -3)),
                    ),
                    Choice(
                        question = "Hop back to ten first, then hop what is left.",
                        formula = "13 - {b:5} = ?",
                        options = listOf("6", "7", "8", "9"),
                        correctIndex = 2,
                        explanation = "13 - 3 lands on ten, and 10 - 2 = 8.",
                        visual = NumberLine(from = 5, to = 15, start = 13, hopSteps = listOf(-3, -2), reveal = false),
                    ),
                    Choice(
                        question = "Same trick, one bigger hop back.",
                        formula = "15 - {b:9} = ?",
                        options = listOf("5", "6", "7", "8"),
                        correctIndex = 1,
                        explanation = "15 - 5 lands on ten, and 10 - 4 = 6.",
                        visual = NumberLine(from = 5, to = 16, start = 15, hopSteps = listOf(-5, -4), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-tens",
                title = "Tens and ones",
                summary = "Build and compare two-digit numbers.",
                steps = listOf(
                    Concept(
                        body = "A two-digit number is rods of ten plus loose ones.",
                        formula = "37 = {a:30} + {b:7}",
                        visual = PlaceValue(tens = 3, ones = 7),
                    ),
                    Choice(
                        question = "How many rods of ten are here?",
                        options = listOf("6", "8", "60", "68"),
                        correctIndex = 0,
                        explanation = "The question counts the rods, not what they are worth: six rods, worth 60.",
                        visual = PlaceValue(tens = 6, ones = 8, reveal = false),
                    ),
                    Worked(
                        problem = "{a:25} + {b:13} = ?",
                        lines = listOf(
                            "Add the rods: {a:2} tens and {b:1} ten make 3 tens.",
                            "Add the ones: {a:5} and {b:3} make 8.",
                            "Three rods and eight ones.",
                        ),
                        result = "38",
                        visual = PlaceValue(tens = 2, ones = 5, plus = 1 to 3),
                    ),
                    Choice(
                        question = "What number is built here?",
                        options = listOf("6", "24", "42", "62"),
                        correctIndex = 2,
                        explanation = "4 rods is 40, and 2 loose ones make 42.",
                        visual = PlaceValue(tens = 4, ones = 2, reveal = false),
                    ),
                    Concept(
                        body = "Compare the rods first. Seven rods beat four, so the ones never get a say.",
                        visual = PlaceValue(tens = 7, ones = 4, compare = 4 to 7),
                    ),
                    Choice(
                        question = "Both have the same rods, so the loose ones decide. Which is larger?",
                        options = listOf("63", "68", "They are equal", "You cannot tell"),
                        correctIndex = 1,
                        explanation = "The rods tie at six each, so the loose ones decide it, and 8 beats 3.",
                        visual = PlaceValue(tens = 6, ones = 3, compare = 6 to 8, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What comes next?",
                options = listOf("41", "45", "50", "60"),
                correctIndex = 2,
                explanation = "Each jump adds ten, so the one after 40 is 50.",
                visual = Steps(terms = listOf(20, 30, 40)),
            ),
            QuizQuestion(
                prompt = "9 + {b:6} = ?",
                options = listOf("14", "15", "16", "13"),
                correctIndex = 1,
                explanation = "9 takes 1 of the 6 to make ten, and the other 5 carry on past it.",
                // The frame stops at the nine it is handed. Filled to fifteen it draws the
                // regrouping the question is asking for, ten dots and five, and the answer is
                // read off rather than worked out. One gap in the frame is the hint the method
                // needs; the six the learner has to place is on the card above.
                visual = TenFrame(filled = 9, added = 0, reveal = false),
            ),
            QuizQuestion(
                prompt = "14 - {b:6} = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "Hop back four to land on ten, and the last two hops reach 8.",
                // Where the count starts, not where it finishes. The hops that bridge through ten
                // are the working, and a test figure that draws them lands on the answer itself.
                visual = NumberLine(from = 0, to = 16, start = 14, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many loose ones are here?",
                options = listOf("5", "3", "50", "53"),
                correctIndex = 1,
                explanation = "The loose ones are the single cubes, so three; the five rods are worth 50.",
                visual = PlaceValue(tens = 5, ones = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which number is smaller, 62 or 26?",
                options = listOf("62", "26", "They are equal", "You cannot tell"),
                correctIndex = 1,
                explanation = "26 has two rods against six, so it is the smaller whatever its loose ones do.",
                visual = PlaceValue(tens = 6, ones = 2, compare = 2 to 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "These jumps are all the same size. What comes next?",
                options = listOf("26", "30", "35", "40"),
                correctIndex = 1,
                explanation = "Each jump adds five, not one, so 25 + 5 = 30.",
                visual = Steps(terms = listOf(15, 20, 25)),
            ),
        ),
    )

    private val multiplication = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "multiplication",
        title = "Multiplication and division",
        summary = "Equal rows, the facts that fall out of them, and sharing back out.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-multiplication",
                title = "Multiplication as an array",
                summary = "Rows and columns make the times tables make sense.",
                steps = listOf(
                    Concept(
                        body = "Multiplication is equal rows. Count how many rows, count what is in each, and the dots do the rest.",
                        formula = "4 x {b:6} = 24",
                        visual = ArrayDots(rows = 4, cols = 6),
                    ),
                    Concept(
                        body = "Turn the array on its side and not one dot is lost, so every fact you learn hands you a second one free.",
                        formula = "4 x {b:6} = 6 x {b:4}",
                        visual = ArrayDots(rows = 6, cols = 4),
                    ),
                    Choice(
                        question = "Seven rows, three dots in each.",
                        formula = "7 x {b:3} = ?",
                        options = listOf("18", "21", "24", "27"),
                        correctIndex = 1,
                        explanation = "3, 6, 9, 12, 15, 18, 21 - seven threes counted on.",
                        visual = ArrayDots(rows = 7, cols = 3),
                    ),
                    Concept(
                        body = "Counting the rows on is skip counting: one row of three, then two rows, then three.",
                        formula = "3, 6, 9, 12",
                        visual = Steps(terms = listOf(3, 6, 9, 12)),
                    ),
                    Concept(
                        body = "Lay {a:3} rows down twice and the total doubles, because the second block is a copy of the first.",
                        formula = "3 x 6 = 18, so 6 x 6 = 36",
                        visual = ArrayDots(rows = 6, cols = 6, split = 3),
                    ),
                    Numeric(
                        question = "A baking tray holds 4 rows of 7 cakes.",
                        formula = "4 x {b:7} = ?",
                        answer = "28",
                        explanation = "Four rows of seven: 7, 14, 21, 28.",
                        visual = ArrayDots(rows = 4, cols = 7),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-multiplication-facts",
                title = "Facts you already know",
                summary = "Break a hard times fact into two easy ones.",
                steps = listOf(
                    Concept(
                        body = "Nobody counts seven rows one dot at a time. Take the {a:5} rows everyone knows, then add the {b:2} that are left.",
                        formula = "7 x 4 = 5 x 4 + 2 x 4",
                        visual = ArrayDots(rows = 7, cols = 4, split = 5),
                    ),
                    Worked(
                        problem = "{a:6} x 8 = ?",
                        lines = listOf(
                            "Fives are the easy ones, so peel off {a:5} rows first.",
                            "{a:5} x 8 = 40.",
                            "That leaves {b:1} row of eight.",
                            "40 + {b:8}.",
                        ),
                        result = "48",
                        visual = ArrayDots(rows = 6, cols = 8, split = 5),
                    ),
                    Concept(
                        body = "Ten rows is the easiest count of all: the number with a nought written after it.",
                        formula = "10 x {b:6} = 60",
                        visual = ArrayDots(rows = 10, cols = 6),
                    ),
                    Choice(
                        question = "Ten rows of six would be 60, and this array is one row short of that.",
                        formula = "9 x {b:6} = ?",
                        options = listOf("45", "48", "54", "56"),
                        correctIndex = 2,
                        explanation = "One row of six comes back off the sixty: 60 - 6 = 54.",
                        visual = ArrayDots(rows = 9, cols = 6),
                    ),
                    Numeric(
                        question = "{a:5} rows of eight, and {b:2} more rows of eight.",
                        formula = "7 x {b:8} = ?",
                        answer = "56",
                        explanation = "{a:5} eights are {a:40} and {b:2} more are {b:16}, so the array holds 56.",
                        visual = ArrayDots(rows = 7, cols = 8, split = 5),
                    ),
                    Choice(
                        question = "Double the {a:2} rows, then double the answer.",
                        formula = "4 x {b:9} = ?",
                        options = listOf("13", "18", "27", "36"),
                        correctIndex = 3,
                        explanation = "Nine doubled is 18, and 18 doubled is 36.",
                        visual = ArrayDots(rows = 4, cols = 9, split = 2),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-multiplication-division",
                title = "Sharing back out",
                summary = "Division undoes multiplication, and sometimes leaves a bit over.",
                steps = listOf(
                    Concept(
                        body = "Division takes the total back apart into equal rows. It is the same array, read the other way round.",
                        formula = "28 / {b:7} = 4",
                        visual = ArrayDots(rows = 4, cols = 7),
                    ),
                    Concept(
                        body = "Every times fact hands you two division facts, because the array can be cut either way.",
                        formula = "6 x 5 = 30, so 30 / 5 = 6",
                        visual = ArrayDots(rows = 6, cols = 5),
                    ),
                    Choice(
                        question = "How many rows of eight does it take?",
                        formula = "56 / {b:8} = ?",
                        options = listOf("6", "7", "8", "9"),
                        correctIndex = 1,
                        explanation = "Eight sevens are 56, so 56 splits into exactly seven rows of eight.",
                        visual = ArrayDots(rows = 7, cols = 8),
                    ),
                    Concept(
                        body = "Not every total shares out evenly. Whatever will not fill another row is the remainder.",
                        formula = "17 / 5 = 3 r 2",
                        visual = ArrayDots(rows = 3, cols = 5, leftover = 2),
                    ),
                    Worked(
                        problem = "Share 29 sweets between 4 friends",
                        lines = listOf(
                            "Deal them out in rows of {a:4}.",
                            "4, 8, 12, 16, 20, 24, 28 - that is {a:7} rows.",
                            "{b:1} sweet will not make an eighth row.",
                        ),
                        result = "7 each, with 1 left over",
                        visual = ArrayDots(rows = 7, cols = 4, leftover = 1),
                    ),
                    Numeric(
                        question = "A crate holds 5 bottles. How many crates can you fill from 38 bottles?",
                        answer = "7",
                        explanation = "Seven full rows of five, and three bottles with nowhere to go.",
                        visual = ArrayDots(rows = 7, cols = 5, leftover = 3),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many dots are here?",
                options = listOf("54", "56", "48", "64"),
                correctIndex = 1,
                explanation = "Eight rows of seven, and eight sevens are 56.",
                visual = ArrayDots(rows = 8, cols = 7),
            ),
            QuizQuestion(
                prompt = "6 x {b:9} = ?",
                options = listOf("45", "54", "56", "63"),
                correctIndex = 1,
                explanation = "Five nines is 45, and one more nine makes 54.",
                // The nines set off, three of the six. A 6 x 9 array draws 54 dots to count and
                // captions itself "5 rows" and "1 more", which is not the question posed but the
                // method and the answer together. The ladder stops well short of any option, so
                // reading it is counting on rather than reading off.
                visual = Steps(terms = listOf(0, 9, 18, 27)),
            ),
            QuizQuestion(
                prompt = "Which has the same total as 7 x 4?",
                options = listOf("7 + 4", "4 x 7", "7 x 7", "4 + 4 + 4"),
                correctIndex = 1,
                explanation = "Turning the array on its side changes nothing.",
                visual = ArrayDots(rows = 4, cols = 7),
            ),
            QuizQuestion(
                prompt = "72 / {b:9} = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "Counting on in nines reaches 72 in exactly eight hops.",
                // A division answer is the row count, and an array prints its row count: drawn as
                // 8 x 9 this question captioned itself "8 rows". The ladder climbs the nines and
                // stops short of 72, so the eight has to be counted rather than read.
                visual = Steps(terms = listOf(18, 27, 36, 45)),
            ),
            QuizQuestion(
                prompt = "23 / {b:4} = ?",
                options = listOf("5 r 1", "5 r 3", "6 r 1", "4 r 7"),
                correctIndex = 1,
                explanation = "Counting on in fours reaches 20 in five hops, and 3 is left over.",
                // Drawn as five rows of four with three dots underneath, the figure spelled the
                // whole answer out: "5 rows" in words and the remainder alongside it.
                visual = Steps(terms = listOf(0, 4, 8, 12)),
            ),
            QuizQuestion(
                prompt = "A tray holds 8 rows of 6 buns. How many buns?",
                options = listOf("14", "42", "48", "54"),
                correctIndex = 2,
                explanation = "Five sixes is 30 and three more sixes is 18, so the tray holds 48.",
                visual = ArrayDots(rows = 8, cols = 6, split = 5),
            ),
        ),
    )

    private val fractions = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "fractions",
        title = "Fractions",
        summary = "What the two numbers mean, matching them up, and adding them.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-fractions",
                title = "Naming the parts",
                summary = "What each of the two numbers is for.",
                steps = listOf(
                    Concept(
                        body = "The bottom number says how many equal pieces the bar was cut into. The top number says how many of them you take.",
                        formula = "3/4",
                        visual = Fraction(numerator = 3, denominator = 4),
                    ),
                    Choice(
                        question = "How much of this bar is shaded?",
                        options = listOf("2/5", "3/5", "2/3", "5/2"),
                        correctIndex = 0,
                        explanation = "Two shaded out of five pieces in all, so the five goes underneath.",
                        visual = Fraction(numerator = 2, denominator = 5, reveal = false),
                    ),
                    Concept(
                        body = "The more pieces the bar is cut into, the thinner each piece gets. An eighth is a narrower slice than a quarter.",
                        visual = Fraction(numerator = 1, denominator = 4, compare = 1 to 8),
                    ),
                    Choice(
                        question = "Which bar is shaded more?",
                        options = listOf("3/8", "5/8", "They are equal", "You cannot tell"),
                        correctIndex = 1,
                        explanation = "Same bottom number, so the pieces are the same size: five of them beats three.",
                        visual = Fraction(numerator = 3, denominator = 8, compare = 5 to 8, reveal = false),
                    ),
                    Numeric(
                        question = "A cake is cut into 8 slices and 3 are eaten. How many eighths are still on the plate?",
                        formula = "8/8 - {b:3/8} = ?/8",
                        answer = "5",
                        explanation = "Three of the eight pieces are gone, so 5/8 of the cake is left.",
                        visual = Fraction(numerator = 3, denominator = 8, reveal = false),
                    ),
                    Choice(
                        question = "Which of these is a whole one?",
                        options = listOf("4/4", "3/4", "4/3", "1/4"),
                        correctIndex = 0,
                        explanation = "Take every piece there is and you have the whole bar back.",
                        visual = Fraction(numerator = 4, denominator = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-fractions-equivalent",
                title = "Fractions that match",
                summary = "Different names for exactly the same amount.",
                steps = listOf(
                    Concept(
                        body = "Cut every piece in half and you get twice as many, each half the size. The shading never moved.",
                        formula = "1/2 = 4/8",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 4 to 8),
                    ),
                    Concept(
                        body = "Whatever you do to the bottom you do to the top, and the amount stays exactly where it was.",
                        formula = "2/3 = 4/6",
                        visual = Fraction(numerator = 2, denominator = 3, compare = 4 to 6),
                    ),
                    Choice(
                        question = "Which fraction shades the same amount as this bar?",
                        options = listOf("3/4", "4/6", "2/6", "6/3"),
                        correctIndex = 1,
                        explanation = "Doubling both numbers renames 2/3 as 4/6 without moving the shading.",
                        visual = Fraction(numerator = 2, denominator = 3, reveal = false),
                    ),
                    Concept(
                        body = "Cancelling runs the trick backwards: divide top and bottom by the same number and the fraction gets a simpler name.",
                        formula = "6/8 = 3/4",
                        visual = Fraction(numerator = 6, denominator = 8, compare = 3 to 4),
                    ),
                    Choice(
                        question = "Which bar is shaded more?",
                        options = listOf("2/3", "3/4", "They are equal", "You cannot compare them"),
                        correctIndex = 1,
                        explanation = "Cut both into twelfths and they are 8/12 against 9/12.",
                        visual = Fraction(numerator = 2, denominator = 3, compare = 3 to 4, reveal = false),
                    ),
                    Numeric(
                        question = "How many tenths shade the same as this bar?",
                        formula = "3/5 = ?/10",
                        answer = "6",
                        explanation = "Both numbers doubled: five pieces become ten, three become six.",
                        visual = Fraction(numerator = 3, denominator = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-fractions-add",
                title = "Adding fractions",
                summary = "Same-size pieces add; different ones have to be matched first.",
                steps = listOf(
                    Concept(
                        body = "Pieces of the same size simply add: {a:3} fifths and {b:1} fifth are four fifths. The pieces never change size, so the bottom number never changes either.",
                        formula = "3/5 + {b:1/5} = 4/5",
                        visual = Fraction(numerator = 3, denominator = 5, plus = 1 to 5),
                    ),
                    Choice(
                        question = "The pieces already match, so add the tops and leave the bottom alone.",
                        formula = "2/7 + {b:3/7} = ?",
                        options = listOf("5/7", "5/14", "6/7", "1/7"),
                        correctIndex = 0,
                        explanation = "Two sevenths and three sevenths are five sevenths. Nothing was cut any thinner.",
                        visual = Fraction(numerator = 2, denominator = 7, plus = 3 to 7, reveal = false),
                    ),
                    Concept(
                        body = "Taking away works the same way. Five sixths with two sixths gone leaves three of them.",
                        formula = "5/6 - {b:2/6} = 3/6",
                        visual = Fraction(numerator = 5, denominator = 6, compare = 3 to 6),
                    ),
                    Concept(
                        body = "Pieces of different sizes cannot be added as they stand. Rename one bar until both are cut the same way.",
                        formula = "1/2 = 2/4",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 2 to 4),
                    ),
                    Worked(
                        problem = "{a:1}/2 + {b:1}/4 = ?",
                        lines = listOf(
                            "A half and a quarter are different sizes, so nothing can be added yet.",
                            "Cut the half into quarters: {a:1}/2 is {a:2}/4.",
                            "Now both bars are in quarters.",
                            "{a:2}/4 + {b:1}/4.",
                        ),
                        result = "3/4",
                        visual = Fraction(numerator = 2, denominator = 4, plus = 1 to 4),
                    ),
                    Choice(
                        question = "Sixths and thirds. Match the pieces first.",
                        formula = "1/6 + {b:1/3} = ?",
                        options = listOf("2/9", "1/2", "2/6", "1/3"),
                        correctIndex = 1,
                        explanation = "1/3 is 2/6, so the sum is 3/6, which is one half.",
                        visual = Fraction(numerator = 1, denominator = 6, plus = 1 to 3, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How much of this bar is shaded?",
                options = listOf("2/5", "3/5", "2/3", "5/2"),
                correctIndex = 0,
                explanation = "The bottom number counts all five pieces, the top counts the two shaded.",
                visual = Fraction(numerator = 2, denominator = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which fraction shades the same as this bar?",
                options = listOf("3/5", "4/8", "2/5", "5/12"),
                correctIndex = 1,
                explanation = "4 is half of 8, and half the bar is shaded.",
                visual = Fraction(numerator = 1, denominator = 2, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which bar is shaded more?",
                options = listOf("3/8", "1/2", "They are equal", "You cannot tell"),
                correctIndex = 1,
                explanation = "A half is 4/8, and four eighths beat three.",
                visual = Fraction(numerator = 3, denominator = 8, compare = 1 to 2, reveal = false),
            ),
            QuizQuestion(
                prompt = "3/5 + {b:1/5} = ?",
                options = listOf("4/10", "4/5", "3/10", "4/25"),
                correctIndex = 1,
                explanation = "The pieces are the same size, so only the tops add.",
                visual = Fraction(numerator = 3, denominator = 5, plus = 1 to 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "5/6 - {b:2/6} = ?",
                options = listOf("3/6", "3/12", "7/6", "1/6"),
                correctIndex = 0,
                explanation = "Two of the five shaded sixths go, and the pieces stay sixths.",
                visual = Fraction(numerator = 5, denominator = 6, compare = 2 to 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "1/2 + {b:1/4} = ?",
                options = listOf("2/6", "3/4", "1/6", "2/4"),
                correctIndex = 1,
                explanation = "A half is two quarters, and two quarters plus one more is 3/4.",
                visual = Fraction(numerator = 1, denominator = 2, plus = 1 to 4, reveal = false),
            ),
        ),
    )

    private val decimals = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "decimals",
        title = "Decimals",
        summary = "Reading, comparing and adding the places past the point.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-decimals",
                title = "Tenths and hundredths",
                summary = "Place value carries on past the point.",
                steps = listOf(
                    Concept(
                        body = "The square is one whole. A column of it is a tenth, and a single cell is a hundredth.",
                        formula = "0.35 = 3 tenths + 5 hundredths",
                        visual = DecimalGrid(value = 0.35),
                    ),
                    Concept(
                        body = "A fraction and a decimal can be the very same amount, written two ways.",
                        formula = "1/4 = 0.25",
                        visual = DecimalGrid(value = 0.25),
                    ),
                    Choice(
                        question = "Write the shaded amount as a decimal.",
                        options = listOf("0.075", "0.7", "0.75", "7.5"),
                        correctIndex = 2,
                        explanation = "75 cells out of the hundred, which is 75 hundredths: 0.75.",
                        visual = DecimalGrid(value = 0.75, reveal = false),
                    ),
                    Concept(
                        body = "The nought is not decoration. It holds the tenths place empty so the 5 lands in hundredths.",
                        formula = "3.05 = 3 + 5 hundredths",
                        visual = DecimalGrid(value = 0.05),
                    ),
                    Choice(
                        question = "What does 3.05 euro mean?",
                        options = listOf("3 euro 50 cents", "3 euro 5 cents", "30 euro 5 cents", "3 euro 500 cents"),
                        correctIndex = 1,
                        explanation = "A cent is a hundredth of a euro, and the nought keeps the tenths empty.",
                        visual = DecimalGrid(value = 0.05, reveal = false),
                    ),
                    Choice(
                        question = "How much of the square is shaded?",
                        options = listOf("0.4", "0.04", "4", "0.44"),
                        correctIndex = 0,
                        explanation = "Four whole columns, and a column is a tenth, so 0.4 and not 0.04.",
                        visual = DecimalGrid(value = 0.4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-decimals-compare",
                title = "Comparing decimals",
                summary = "A longer decimal is not a bigger one.",
                steps = listOf(
                    Concept(
                        body = "Compare the tenths first. Only when those tie do the hundredths get a say.",
                        formula = "0.4 > 0.35",
                        visual = DecimalGrid(value = 0.4, compare = 0.35),
                    ),
                    Choice(
                        question = "Which square is shaded more?",
                        options = listOf("0.4", "0.35", "They are equal", "You cannot tell"),
                        correctIndex = 0,
                        explanation = "Four tenths beats three tenths. Extra digits do not make a number bigger.",
                        visual = DecimalGrid(value = 0.4, compare = 0.35, reveal = false),
                    ),
                    Concept(
                        body = "Writing a nought on the end changes nothing at all: the same cells are shaded either way.",
                        formula = "0.4 = 0.40",
                        visual = DecimalGrid(value = 0.4, compare = 0.40),
                    ),
                    Concept(
                        body = "Money is a decimal you already read without thinking. Half a euro is fifty of its hundred cents.",
                        formula = "0.5 euro = 50 cents",
                        visual = DecimalGrid(value = 0.5),
                    ),
                    Numeric(
                        question = "How many hundredths shade the same as this square?",
                        formula = "0.3 = ? hundredths",
                        answer = "30",
                        explanation = "Three columns of ten cells each, so 0.3 is 30 hundredths.",
                        visual = DecimalGrid(value = 0.3, reveal = false),
                    ),
                    Choice(
                        question = "Smallest first. Which order is right?",
                        options = listOf(
                            "0.09, 0.7, 0.71",
                            "0.7, 0.09, 0.71",
                            "0.71, 0.7, 0.09",
                            "0.09, 0.71, 0.7",
                        ),
                        correctIndex = 0,
                        explanation = "0.09 has no tenths at all, so it is the smallest however long it looks.",
                        visual = DecimalGrid(value = 0.09, compare = 0.7, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-decimals-add",
                title = "Adding decimals",
                summary = "Tenths onto tenths, hundredths onto hundredths.",
                steps = listOf(
                    Concept(
                        body = "Adding decimals is adding same-sized pieces, exactly as fractions do: {a:4} tenths take on {b:35} hundredths.",
                        formula = "0.4 + {b:0.35} = 0.75",
                        visual = DecimalGrid(value = 0.4, plus = 0.35),
                    ),
                    Worked(
                        problem = "{a:0.6} + {b:0.25} = ?",
                        lines = listOf(
                            "Write {a:0.6} as {a:0.60} so both numbers reach the hundredths.",
                            "Hundredths: 0 + {b:5}.",
                            "Tenths: {a:6} + {b:2}.",
                        ),
                        result = "0.85",
                        visual = DecimalGrid(value = 0.6, plus = 0.25),
                    ),
                    Choice(
                        question = "Line the points up, then add one column at a time.",
                        formula = "0.3 + {b:0.45} = ?",
                        options = listOf("0.48", "0.75", "0.78", "0.15"),
                        correctIndex = 1,
                        explanation = "Write 0.3 as 0.30, then five hundredths and seven tenths make 0.75.",
                        visual = DecimalGrid(value = 0.3, plus = 0.45, reveal = false),
                    ),
                    Concept(
                        body = "A subtraction is the same picture read backwards: {a:35} hundredths are already there, and {b:4} tenths bring it back up to 0.75.",
                        formula = "0.75 - {b:0.4} = 0.35",
                        visual = DecimalGrid(value = 0.35, plus = 0.4),
                    ),
                    Choice(
                        question = "A pencil costs 0.45 euro and a rubber 0.30 euro. What do they cost together?",
                        formula = "0.45 + {b:0.3} = ?",
                        options = listOf("0.15", "0.48", "0.75", "7.5"),
                        correctIndex = 2,
                        explanation = "Forty-five cents and thirty cents make seventy-five.",
                        visual = DecimalGrid(value = 0.45, plus = 0.3, reveal = false),
                    ),
                    Choice(
                        question = "How much more shading would fill the square?",
                        formula = "0.35 + ? = 1",
                        options = listOf("0.65", "0.75", "0.6", "0.7"),
                        correctIndex = 0,
                        explanation = "35 cells are shaded, so 65 of the hundred are not.",
                        visual = DecimalGrid(value = 0.35, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Write the shaded amount as a decimal.",
                options = listOf("0.25", "0.4", "0.75", "0.025"),
                correctIndex = 0,
                explanation = "25 cells out of the hundred, which is 25 hundredths: 0.25.",
                visual = DecimalGrid(value = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which square is shaded more?",
                options = listOf("0.5", "0.45", "They match", "You cannot tell"),
                correctIndex = 0,
                explanation = "Five tenths beats four tenths and five hundredths.",
                visual = DecimalGrid(value = 0.5, compare = 0.45, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is the smallest?",
                options = listOf("0.08", "0.6", "0.61", "0.7"),
                correctIndex = 0,
                explanation = "0.08 has no tenths at all, so it sits below every number here that has one.",
                visual = DecimalGrid(value = 0.08, compare = 0.6, reveal = false),
            ),
            QuizQuestion(
                prompt = "0.4 + {b:0.25} = ?",
                options = listOf("0.29", "0.65", "0.6", "0.425"),
                correctIndex = 1,
                explanation = "Write 0.4 as 0.40, then tenths onto tenths and hundredths onto hundredths give 0.65.",
                visual = DecimalGrid(value = 0.4, plus = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which fraction is the same amount as 0.25?",
                options = listOf("1/2", "1/4", "2/5", "1/5"),
                correctIndex = 1,
                explanation = "25 hundredths is one quarter of the square.",
                visual = DecimalGrid(value = 0.25, reveal = false),
            ),
            QuizQuestion(
                prompt = "0.7 - {b:0.35} = ?",
                options = listOf("0.35", "0.42", "0.45", "0.28"),
                correctIndex = 0,
                explanation = "0.70 take away 0.35 leaves half of it.",
                visual = DecimalGrid(value = 0.7, reveal = false),
            ),
        ),
    )

    private val negatives = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "negatives",
        title = "Negative numbers",
        summary = "Below zero, and what the two signs do to each other.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-negatives",
                title = "Left of zero",
                summary = "The line carries on, and so does the order.",
                steps = listOf(
                    Concept(
                        body = "Zero is not the end of the line. Right is still more and left is still less, and to the left of zero the numbers wear a minus.",
                        visual = NumberLine(from = -6, to = 6, start = 2, jump = 3, thenJump = -7),
                    ),
                    Concept(
                        body = "A negative is a shortfall, not a quantity. Minus four is four steps below zero, and there is nothing you can have four of.",
                        formula = "-4 is 4 left of 0",
                        visual = NumberLine(from = -6, to = 6, start = 0, jump = -4),
                    ),
                    Choice(
                        question = "Which of these is the largest?",
                        options = listOf("-10", "-6", "-1", "0"),
                        correctIndex = 3,
                        explanation = "Furthest right on the line wins, and zero sits to the right of every negative.",
                        visual = NumberLine(from = -10, to = 5, compare = listOf(-10, -6, -1, 0), reveal = false),
                    ),
                    Concept(
                        body = "The further left, the smaller. That is why -10 is less than -1, even though 10 is more than 1.",
                        formula = "-10 < -1",
                        visual = NumberLine(from = -12, to = 2, start = -10, jump = 9),
                    ),
                    Choice(
                        question = "A freezer sits at -18 degrees and a fridge at -2. Which is colder?",
                        options = listOf("the freezer", "the fridge", "they match", "you cannot tell"),
                        correctIndex = 0,
                        explanation = "-18 is further left, so it is the smaller number and the colder one.",
                        visual = NumberLine(from = -20, to = 2, start = -18, tickStep = 2, reveal = false),
                    ),
                    Numeric(
                        question = "How many degrees warmer is the fridge than the freezer?",
                        formula = "-2 - {b:(-18)} = ?",
                        answer = "16",
                        explanation = "From -18 up to -2 is 16 steps, which is why the two minus signs come out positive.",
                        visual = NumberLine(from = -20, to = 2, start = -18, tickStep = 2, hopSteps = listOf(16), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-negatives-addsub",
                title = "Adding and taking away",
                summary = "Hopping across zero, and what two minus signs do.",
                steps = listOf(
                    Concept(
                        body = "Adding moves right and taking away moves left. Crossing zero changes nothing about that.",
                        visual = NumberLine(from = -6, to = 6, start = -4, jump = 3),
                    ),
                    Numeric(
                        question = "Hop up to zero first, then carry on.",
                        formula = "{a:-7} + {b:12} = ?",
                        answer = "5",
                        explanation = "{b:7} of the twelve get you to zero, and the other {b:5} carry on past it.",
                        visual = NumberLine(from = -10, to = 5, start = -7, hopSteps = listOf(7, 5), reveal = false),
                    ),
                    Choice(
                        question = "The temperature is -4 and drops six more.",
                        formula = "{a:-4} - {b:6} = ?",
                        options = listOf("2", "-2", "-10", "10"),
                        correctIndex = 2,
                        explanation = "Six steps left of {a:-4} lands on -10.",
                        visual = NumberLine(from = -10, to = 5, start = -4, hopSteps = listOf(-6), reveal = false),
                    ),
                    Concept(
                        body = "Taking away a negative moves right, the same way adding does, because losing a debt leaves you better off.",
                        formula = "{a:5} - ({b:-3}) = 8",
                        visual = NumberLine(from = -4, to = 10, start = 5, jump = 3),
                    ),
                    Choice(
                        question = "Two minus signs in a row cancel each other out.",
                        formula = "{a:-2} - ({b:-9}) = ?",
                        options = listOf("-11", "-7", "7", "11"),
                        correctIndex = 2,
                        explanation = "Taking away {b:-9} moves nine steps right of {a:-2}, which lands on 7.",
                        visual = NumberLine(from = -4, to = 10, start = -2, hopSteps = listOf(9), reveal = false),
                    ),
                    Concept(
                        body = "Adding a negative is the other half of the same rule: it moves left, so the two signs collapse into one.",
                        formula = "6 + {b:(-4)} = 6 - {b:4}",
                        visual = NumberLine(from = -2, to = 8, start = 6, jump = -4),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-negatives-multiply",
                title = "Multiplying and dividing",
                summary = "Why two negatives make a positive.",
                steps = listOf(
                    Concept(
                        body = "Three lots of a debt is a bigger debt, so a negative times a positive stays negative.",
                        formula = "3 x {b:(-5)} = -15",
                        visual = Steps(terms = listOf(-15, -10, -5, 0)),
                    ),
                    Choice(
                        question = "Three rows of a debt of six.",
                        formula = "3 x {b:(-6)} = ?",
                        options = listOf("-18", "-9", "9", "18"),
                        correctIndex = 0,
                        explanation = "Three steps of -6 land on -18.",
                        visual = Steps(terms = listOf(-18, -12, -6, 0)),
                    ),
                    Concept(
                        body = "Walk a times table down past zero: 3 lots of -4, then 2, then 1, then none. Every step adds the same {b:4}, so once the first number goes negative the answers have to carry on up into the positives.",
                        formula = "{a:-3} x (-4) = {b:12}",
                        visual = Steps(terms = listOf(-12, -8, -4, 0, 4, 8, 12)),
                    ),
                    Concept(
                        body = "That is the whole of the rule. Two signs the same give a positive, two different give a negative, and division behaves exactly as multiplication does.",
                        formula = "(-4) x {b:(-5)} = 20",
                        visual = Steps(terms = listOf(-10, -5, 0, 5, 10, 15, 20)),
                    ),
                    Choice(
                        question = "Both signs are the same here.",
                        formula = "(-30) / {b:(-6)} = ?",
                        options = listOf("-5", "-36", "5", "36"),
                        correctIndex = 2,
                        explanation = "Thirty shares into sixes five times, and matching signs give a positive.",
                        visual = Steps(terms = listOf(-30, -24, -18, -12, -6, 0)),
                    ),
                    Choice(
                        question = "Divide first, then decide the sign.",
                        formula = "{a:-45} / {b:9} = ?",
                        options = listOf("-6", "-5", "5", "6"),
                        correctIndex = 1,
                        explanation = "Forty-five shares into nines five times, and the two signs differ, so the answer is negative.",
                        visual = Steps(terms = listOf(-45, -36, -27, -18, -9, 0)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "-6 + {b:9} = ?",
                options = listOf("-15", "-3", "3", "15"),
                correctIndex = 2,
                explanation = "Six of the nine carry you to zero, and the other three carry on past it to 3.",
                visual = NumberLine(from = -10, to = 5, start = -6, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is the smallest?",
                options = listOf("-9", "-2", "0", "3"),
                correctIndex = 0,
                explanation = "Furthest left is smallest, so -9 is below -2 even though 9 is more than 2.",
                visual = NumberLine(from = -10, to = 5, compare = listOf(-9, -2, 0, 3), reveal = false),
            ),
            QuizQuestion(
                prompt = "-3 - {b:8} = ?",
                options = listOf("-11", "-5", "5", "11"),
                correctIndex = 0,
                explanation = "Taking away moves left, and eight steps left of -3 is -11.",
                visual = NumberLine(from = -12, to = 2, start = -3, reveal = false),
            ),
            QuizQuestion(
                prompt = "7 - {b:(-5)} = ?",
                options = listOf("-12", "-2", "2", "12"),
                correctIndex = 3,
                explanation = "Taking away a negative moves right, so this is 7 + 5.",
                visual = NumberLine(from = -2, to = 14, start = 7, reveal = false),
            ),
            QuizQuestion(
                prompt = "(-4) x (-5) = ?",
                options = listOf("-20", "-9", "9", "20"),
                correctIndex = 3,
                explanation = "Each step up the ladder adds five, so past zero the answers turn positive.",
                visual = Steps(terms = listOf(-10, -5, 0, 5)),
            ),
            QuizQuestion(
                prompt = "(-36) / 9 = ?",
                options = listOf("-4", "4", "-27", "27"),
                correctIndex = 0,
                explanation = "Thirty-six shares into nines four times, and the signs differ, so it is -4.",
                visual = Steps(terms = listOf(-36, -27, -18, -9, 0)),
            ),
        ),
    )

    private val ratio = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "ratio",
        title = "Ratio and proportion",
        summary = "Counting parts against parts, sharing them out, and scaling them up.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-ratio",
                title = "What a ratio counts",
                summary = "Parts against parts, not parts against the whole.",
                steps = listOf(
                    Concept(
                        body = "A ratio counts parts against parts, not parts against the whole. One part to four parts is one in every five, not one in four.",
                        formula = "{a:1} : {b:4}",
                        visual = RatioBar(parts = listOf(1, 4)),
                    ),
                    Concept(
                        body = "Cut every part again and the mix is unchanged: more pieces, each one smaller, the same two colours side by side.",
                        formula = "{a:3} : {b:5} = 12 : 20",
                        visual = RatioBar(parts = listOf(3, 5), scale = 4),
                    ),
                    Choice(
                        question = "Which ratio matches the bar?",
                        options = listOf("2 : 3", "3 : 2", "6 : 3", "1 : 2"),
                        correctIndex = 0,
                        explanation = "The bar is {a:6} against {b:9}, and both divide by three.",
                        visual = RatioBar(parts = listOf(6, 9), reveal = false),
                    ),
                    Concept(
                        body = "A ratio carries no units and no total. Two to three describes the mix whether the bucket holds 5 litres or 500.",
                        visual = RatioBar(parts = listOf(2, 3)),
                    ),
                    Choice(
                        question = "One part sand to three parts gravel. What fraction of the mix is sand?",
                        options = listOf("1/3", "1/4", "3/4", "1/2"),
                        correctIndex = 1,
                        explanation = "One part out of the four parts in the bar, which is exactly the trap a ratio sets.",
                        visual = RatioBar(parts = listOf(1, 3), reveal = false),
                    ),
                    Numeric(
                        question = "How many parts are there altogether in this bar?",
                        formula = "{a:2} + {b:5} = ?",
                        answer = "7",
                        explanation = "Counting the parts is always the first move, because it is what one part gets measured against.",
                        visual = RatioBar(parts = listOf(2, 5), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-ratio-sharing",
                title = "Sharing in a ratio",
                summary = "Count the parts, price one part, hand them out.",
                steps = listOf(
                    Concept(
                        body = "To share in a ratio, count the parts first, then work out what a single part is worth.",
                        formula = "2 : 3 is 5 parts",
                        visual = RatioBar(parts = listOf(2, 3), total = 60),
                    ),
                    Worked(
                        problem = "Share 60 euro in the ratio 2 : 3.",
                        lines = listOf(
                            "{a:2} + {b:3} = 5 parts in all.",
                            "60 / 5 = 12 for one part.",
                            "{a:2} parts and {b:3} parts.",
                        ),
                        result = "24 euro and 36 euro",
                        visual = RatioBar(parts = listOf(2, 3), total = 60),
                    ),
                    Choice(
                        question = "Share 80 euro in the ratio 3 : 1. What is the larger share?",
                        options = listOf("20 euro", "40 euro", "60 euro", "75 euro"),
                        correctIndex = 2,
                        explanation = "Four parts of 20 euro, and the larger share takes three of them.",
                        visual = RatioBar(parts = listOf(3, 1), total = 80, reveal = false),
                    ),
                    Concept(
                        body = "There is no reason to stop at two parts. Three to two to one is six parts, and the method does not change.",
                        visual = RatioBar(parts = listOf(3, 2, 1), total = 180),
                    ),
                    Numeric(
                        question = "Share 180 g in the ratio 3 : 2 : 1. How many grams are in the largest share?",
                        formula = "180 / 6 x {a:3} = ?",
                        answer = "90",
                        explanation = "Six parts of 30 g, and the largest share takes three of them.",
                        visual = RatioBar(parts = listOf(3, 2, 1), total = 180, reveal = false),
                    ),
                    Choice(
                        question = "A 2 : 3 mix holds 12 litres of the first colour. How much of the second?",
                        options = listOf("8 litres", "15 litres", "18 litres", "20 litres"),
                        correctIndex = 2,
                        explanation = "Two parts are 12, so one part is 6, and the second colour takes three of them.",
                        visual = RatioBar(parts = listOf(2, 3), reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-ratio-scaling",
                title = "Scaling and proportion",
                summary = "Find what one is worth, then multiply.",
                steps = listOf(
                    Concept(
                        body = "In direct proportion, double one amount and the other doubles with it.",
                        formula = "4 people need {a:300} g",
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Concept(
                        body = "Work out what a single one is worth first. That one step answers every scaling question there is.",
                        formula = "300 / 4 = 75 each",
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Choice(
                        question = "Rice for 4 people is 300 g. How much for 6?",
                        formula = "{a:300} g for 4, ? g for {b:6}",
                        options = listOf("350 g", "400 g", "450 g", "600 g"),
                        correctIndex = 2,
                        explanation = "One person needs {a:75} g, so {b:6} of them need 450 g.",
                        visual = Steps(terms = listOf(75, 150, 225, 300)),
                    ),
                    Choice(
                        question = "8 pens cost 6 euro. What do 12 cost?",
                        options = listOf("8 euro", "9 euro", "10 euro", "12 euro"),
                        correctIndex = 1,
                        explanation = "Four pens cost 3 euro, and twelve is three fours.",
                        visual = Steps(terms = listOf(3, 6, 9)),
                    ),
                    Numeric(
                        question = "A map is drawn to a scale of 1 : 50000. A road measures 4 cm on it.",
                        formula = "{a:4} cm x {b:50000} = ? cm",
                        answer = "200000",
                        explanation = "Every 1 cm on the map stands for {b:50000} cm on the ground.",
                        visual = Steps(terms = listOf(1, 50000), multiply = true),
                    ),
                    Concept(
                        body = "Not everything scales that way. Two painters take half the time rather than twice as long, which is inverse proportion.",
                        formula = "2 painters x 6 days = 12",
                        visual = Steps(terms = listOf(12, 6, 4, 3)),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which ratio matches the bar?",
                options = listOf("1 : 2", "3 : 5", "5 : 3", "15 : 5"),
                correctIndex = 1,
                explanation = "The first colour takes three parts and the second five, in that order.",
                visual = RatioBar(parts = listOf(3, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = "In the ratio 2 : 7, what fraction of the whole is the first part?",
                options = listOf("2/7", "2/9", "7/9", "1/2"),
                correctIndex = 1,
                explanation = "Two parts out of the nine in the bar, not out of the seven.",
                visual = RatioBar(parts = listOf(2, 7), reveal = false),
            ),
            QuizQuestion(
                prompt = "Share 100 euro in the ratio 1 : 4. What is the smaller share?",
                options = listOf("20 euro", "25 euro", "40 euro", "80 euro"),
                correctIndex = 0,
                explanation = "Five parts of 20 euro, and the smaller share takes one.",
                visual = RatioBar(parts = listOf(1, 4), total = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = "Simplify the ratio 15 : 25.",
                options = listOf("3 : 5", "5 : 3", "1 : 2", "15 : 5"),
                correctIndex = 0,
                explanation = "Both sides divide by five, and 3 : 5 will not go any further.",
                visual = RatioBar(parts = listOf(3, 5), reveal = false),
            ),
            QuizQuestion(
                prompt = "A recipe for 6 people uses 240 g of flour. How much for 9?",
                options = listOf("300 g", "320 g", "360 g", "400 g"),
                correctIndex = 2,
                explanation = "240 g shared by 6 is 40 g each, so 9 people take 360 g.",
                visual = Steps(terms = listOf(40, 80, 120, 160, 200, 240)),
            ),
            QuizQuestion(
                prompt = "A model is built to a scale of 1 : 200. How tall is the model of a 30 m tower, in cm?",
                options = listOf("10 cm", "15 cm", "20 cm", "150 cm"),
                correctIndex = 1,
                explanation = "30 m is 3000 cm, and 3000 shared into 200 is 15 cm.",
                visual = Steps(terms = listOf(1, 200), multiply = true),
            ),
        ),
    )

    private val percent = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "percent",
        title = "Percentages",
        summary = "Parts of a hundred: taking them, adding them on, and working them out.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-arithmetic-percent",
                title = "Out of a hundred",
                summary = "A percentage is a hundred-square with some of it shaded.",
                steps = listOf(
                    Concept(
                        body = "Per cent means out of a hundred, so a percentage is a hundred-square with some of its squares shaded.",
                        formula = "35% = 0.35",
                        visual = DecimalGrid(value = 0.35, percent = true),
                    ),
                    Concept(
                        body = "A percentage, a decimal and a fraction are three names for one amount of shading.",
                        formula = "25% = 0.25 = 1/4",
                        visual = DecimalGrid(value = 0.25, percent = true),
                    ),
                    Concept(
                        body = "To take a percentage of an amount, share the amount over the hundred squares and count the shaded ones.",
                        formula = "{a:20}% of {b:80} = 16",
                        visual = DecimalGrid(value = 0.2, of = 80),
                    ),
                    Choice(
                        question = "Half the square is shaded.",
                        formula = "50% of 64 = ?",
                        options = listOf("16", "32", "3.2", "128"),
                        correctIndex = 1,
                        explanation = "50% is one half of anything, and half of 64 is 32.",
                        visual = DecimalGrid(value = 0.5, of = 64, reveal = false),
                    ),
                    Numeric(
                        question = "Ten per cent first, then half of it again.",
                        formula = "{a:15}% of {b:200} = ?",
                        answer = "30",
                        explanation = "{a:10}% is {b:20} and {a:5}% is half of that, so {a:15}% comes to 30.",
                        visual = DecimalGrid(value = 0.15, of = 200, reveal = false),
                    ),
                    Choice(
                        question = "Which of these is the largest?",
                        options = listOf("0.4", "35%", "1/3", "30%"),
                        correctIndex = 0,
                        explanation = "0.4 is 40 squares out of the hundred, and nothing else on the list reaches that.",
                        visual = DecimalGrid(value = 0.4, percent = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-percent-change",
                title = "Adding it on, taking it off",
                summary = "Rises, discounts, and why they do not cancel.",
                steps = listOf(
                    Concept(
                        body = "An increase adds the percentage on top; a decrease takes it off.",
                        formula = "{a:100} + {b:20}% = 120",
                        visual = DecimalGrid(value = 0.2, of = 100),
                    ),
                    Choice(
                        question = "A 40 euro jacket is 25% off. What do you pay?",
                        options = listOf("10 euro", "15 euro", "30 euro", "35 euro"),
                        correctIndex = 2,
                        explanation = "A quarter of {b:40} is {a:10}, and that {a:10} is what comes off.",
                        visual = DecimalGrid(value = 0.25, of = 40, reveal = false),
                    ),
                    Concept(
                        body = "One multiplication does it in a single step, because 20% off is the same as keeping 80%.",
                        formula = "x 0.8",
                        visual = DecimalGrid(value = 0.8, of = 40),
                    ),
                    Numeric(
                        question = "A 60 euro fare rises by 15%. What is the new fare, in euro?",
                        formula = "{a:60} x {b:1.15} = ?",
                        answer = "69",
                        explanation = "15% of {a:60} is 9, and that goes on top.",
                        visual = DecimalGrid(value = 0.15, of = 60, reveal = false),
                    ),
                    Choice(
                        question = "Up 10%, then down 10%. Where do you end up?",
                        options = listOf("back at the start", "slightly better off", "slightly worse off", "cannot tell"),
                        correctIndex = 2,
                        explanation = "The rise was 10% of 100, but the fall is 10% of {b:110}, which is 11. More comes off than went on.",
                        visual = DecimalGrid(value = 0.1, of = 110, reveal = false),
                    ),
                    Concept(
                        body = "That is the whole reason they never cancel: the second percentage is taken of a different number from the first.",
                        formula = "110 x {b:0.9} = 99",
                        visual = DecimalGrid(value = 0.1, of = 110),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-percent-finding",
                title = "Working the percentage out",
                summary = "Measure the change against what you started from.",
                steps = listOf(
                    Concept(
                        body = "To find what percentage one number is of another, divide the one by the other and multiply by a hundred.",
                        formula = "{b:15} / {a:50} x 100 = 30",
                        visual = BarChart(values = listOf(50, 65), labels = listOf("before", "after"), gridStep = 10),
                    ),
                    Worked(
                        problem = "A price rises from 50 to 65.",
                        lines = listOf(
                            "The change is {b:15}.",
                            "Measure it against the original, not the new price.",
                            "{b:15} / {a:50} = 0.3.",
                            "x 100.",
                        ),
                        result = "A 30% increase",
                        visual = BarChart(values = listOf(50, 65), labels = listOf("before", "after"), gridStep = 10),
                    ),
                    Concept(
                        body = "Always measure against what you started from. Against the new price you would get a different answer to a different question.",
                        visual = BarChart(values = listOf(80, 60), labels = listOf("before", "after"), gridStep = 20),
                    ),
                    Choice(
                        question = "A price falls from 80 to 60. What is the percentage decrease?",
                        options = listOf("20%", "25%", "30%", "33%"),
                        correctIndex = 1,
                        explanation = "The change is 20, and 20 against the original 80 is 0.25.",
                        visual = BarChart(values = listOf(80, 60), labels = listOf("before", "after"), gridStep = 20, reveal = false),
                    ),
                    Numeric(
                        question = "A test is marked 18 out of 24. What percentage is that?",
                        formula = "{a:18} / {b:24} x 100 = ?",
                        answer = "75",
                        explanation = "18 out of 24 is three quarters of the paper.",
                        visual = BarChart(values = listOf(18, 24), labels = listOf("score", "total"), gridStep = 6, reveal = false),
                    ),
                    Choice(
                        question = "A shop's takings go from 200 to 250. By what percentage did they rise?",
                        options = listOf("20%", "25%", "50%", "80%"),
                        correctIndex = 1,
                        explanation = "The change is 50, and 50 against the original 200 is a quarter.",
                        visual = BarChart(values = listOf(200, 250), labels = listOf("before", "after"), gridStep = 50, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What is 12% of 150?",
                options = listOf("12", "15", "18", "24"),
                correctIndex = 2,
                explanation = "10% of 150 is 15 and 2% is 3, so 12% is 18.",
                visual = DecimalGrid(value = 0.12, of = 150, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is 75% of 40?",
                options = listOf("25", "30", "35", "3"),
                correctIndex = 1,
                explanation = "75% is three quarters, and three quarters of 40 is 30.",
                visual = DecimalGrid(value = 0.75, of = 40, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is the same amount as 45%?",
                options = listOf("4.5", "0.45", "0.045", "45"),
                correctIndex = 1,
                explanation = "Per cent means out of a hundred, so 45% is 45 hundredths: 0.45.",
                visual = DecimalGrid(value = 0.45, percent = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "A 90 euro coat is 30% off. What do you pay?",
                options = listOf("27 euro", "60 euro", "63 euro", "70 euro"),
                correctIndex = 2,
                explanation = "30% of 90 is 27, and that is what comes off.",
                visual = DecimalGrid(value = 0.3, of = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = "A price rises from 40 to 50. What is the percentage increase?",
                options = listOf("10%", "20%", "25%", "50%"),
                correctIndex = 2,
                explanation = "The change is 10, and 10 against the original 40 is a quarter.",
                visual = BarChart(values = listOf(40, 50), labels = listOf("before", "after"), gridStep = 10, reveal = false),
            ),
            QuizQuestion(
                prompt = "100 euro goes up 20%, then down 20%. What is left?",
                options = listOf("96 euro", "100 euro", "104 euro", "80 euro"),
                correctIndex = 0,
                explanation = "The rise is 20% of 100, but the fall is 20% of 120, which is 24.",
                visual = DecimalGrid(value = 0.2, of = 120, reveal = false),
            ),
        ),
    )

    private val standardForm = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "standard-form",
        title = "Standard form",
        summary = "Huge and tiny numbers written as one digit and a power of ten.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-standard-form",
                title = "One digit and a power of ten",
                summary = "Counting jumps instead of counting noughts.",
                steps = listOf(
                    Concept(
                        body = "Every jump along this line multiplies by ten. Counting the jumps is quicker than counting noughts, and that count is the power.",
                        formula = "8 000 = 8 * 10^3",
                        visual = Steps(terms = listOf(8, 80, 800, 8000), multiply = true),
                    ),
                    Concept(
                        body = "The front number always sits between 1 and 10. 52 is three jumps below 52 000, so 5.2 is four, and four is the power.",
                        formula = "52 000 = 5.2 * 10^4",
                        visual = Steps(terms = listOf(52, 520, 5200, 52000), multiply = true),
                    ),
                    Choice(
                        question = "Two of these are the right digits in the wrong shape.",
                        formula = "720 000 = ?",
                        options = listOf("7.2 * 10^5", "72 * 10^4", "7.2 * 10^6", "0.72 * 10^6"),
                        correctIndex = 0,
                        explanation = "72 and 0.72 are outside the 1 to 10 window. From 7.2 the point moves five places.",
                        visual = Steps(terms = listOf(72, 720, 7200), multiply = true),
                    ),
                    Concept(
                        body = "The power counts jumps, so it decides the size long before the front number gets a say. Anything times a bigger power of ten wins.",
                        formula = "3 * 10^8 > 9 * 10^7",
                        visual = Steps(terms = listOf(3, 30, 300, 3000), multiply = true),
                    ),
                    Numeric(
                        question = "Walk the point three jumps to the right.",
                        formula = "4.7 * 10^3 = ?",
                        answer = "4700",
                        explanation = "Three jumps right of 4.7: 47, 470, 4 700.",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Choice(
                        question = "All four have the right digits. Only one is in standard form and worth 12 000.",
                        options = listOf("12 * 10^3", "1.2 * 10^4", "0.12 * 10^5", "1.2 * 10^3"),
                        correctIndex = 1,
                        explanation = "12 and 0.12 are outside the window, and 1.2 * 10^3 is only 1 200.",
                        visual = Steps(terms = listOf(12, 120, 1200, 12000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-standard-form-small",
                title = "Tiny numbers",
                summary = "The same line walked backwards, with negative powers.",
                steps = listOf(
                    Concept(
                        body = "Small numbers are the same line walked backwards. Each step divides by ten, and the power counts those steps as negatives: 6, 0.6, 0.06, 0.006.",
                        formula = "0.006 = 6 * 10^-3",
                        visual = Steps(terms = listOf(6000, 600, 60, 6), multiply = true),
                    ),
                    Concept(
                        body = "The minus in the power says which way to walk, not that the number is negative. A number in standard form is negative only when the front number is.",
                        formula = "3 * 10^-4 = 0.0003",
                        visual = Steps(terms = listOf(3000, 300, 30, 3), multiply = true),
                    ),
                    Choice(
                        question = "Count the jumps from 8.2 down to this number.",
                        formula = "0.00082 = ?",
                        options = listOf("8.2 * 10^-4", "8.2 * 10^-3", "82 * 10^-5", "8.2 * 10^4"),
                        correctIndex = 0,
                        explanation = "Four steps left of 8.2, and 82 is outside the 1 to 10 window.",
                        visual = Steps(terms = listOf(8200, 820, 82, 8), multiply = true),
                    ),
                    Choice(
                        question = "Walk the point four jumps to the left.",
                        formula = "5.6 * 10^-4 = ?",
                        options = listOf("0.000056", "0.00056", "0.0056", "56000"),
                        correctIndex = 1,
                        explanation = "Four jumps left of 5.6: 0.56, 0.056, 0.0056, 0.00056.",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                    Concept(
                        body = "A negative front number and a negative power mean two different things, and either can happen without the other.",
                        formula = "-4 * 10^3 = -4000",
                        visual = Steps(terms = listOf(4, 40, 400, 4000), multiply = true),
                    ),
                    Choice(
                        question = "The most negative power walks furthest left, so it is the smallest.",
                        options = listOf("2 * 10^-3", "9 * 10^-4", "5 * 10^-3", "1 * 10^-2"),
                        correctIndex = 1,
                        explanation = "Only one of these has a power of -4, and 0.0009 is below every other number here.",
                        visual = Steps(terms = listOf(9000, 900, 90, 9), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-standard-form-calculating",
                title = "Calculating in standard form",
                summary = "Front numbers multiply, powers add.",
                steps = listOf(
                    Concept(
                        body = "The two halves travel separately. The front numbers multiply with each other, and the powers of ten add.",
                        formula = "10^4 x 10^3 = 10^7",
                        visual = Steps(terms = listOf(10, 100, 1000, 10000), multiply = true),
                    ),
                    Worked(
                        problem = "(3 * 10^4) * (2 * 10^3) = ?",
                        lines = listOf(
                            "The two halves travel separately.",
                            "The front numbers multiply: 3 x 2 = 6.",
                            "The powers of ten add: 10^4 x 10^3 = 10^7.",
                            "Put the two halves back together.",
                        ),
                        result = "6 * 10^7",
                        visual = Steps(terms = listOf(3, 30, 300, 3000), multiply = true),
                    ),
                    Choice(
                        question = "Dividing runs it backwards: the front numbers divide and the powers subtract.",
                        formula = "(4 * 10^6) / (2 * 10^2) = ?",
                        options = listOf("2 * 10^4", "2 * 10^3", "8 * 10^4", "2 * 10^8"),
                        correctIndex = 0,
                        explanation = "The front numbers give 4 / 2 = 2, and the powers give 6 - 2 = 4.",
                        visual = Steps(terms = listOf(4, 40, 400, 4000), multiply = true),
                    ),
                    Concept(
                        body = "The front number can come out of the multiplication too big for the window, and then it has to be walked back in.",
                        formula = "60 * 10^5 = 6 * 10^6",
                        visual = Steps(terms = listOf(6, 60, 600, 6000), multiply = true),
                    ),
                    Choice(
                        question = "The front numbers give 20, which is outside the window.",
                        formula = "(5 * 10^3) x (4 * 10^2) = ?",
                        options = listOf("20 * 10^5", "2 * 10^6", "2 * 10^5", "9 * 10^5"),
                        correctIndex = 1,
                        explanation = "20 * 10^5 has the right value in the wrong shape: walk the point one place left and the power goes up one.",
                        visual = Steps(terms = listOf(5, 50, 500, 5000), multiply = true),
                    ),
                    Numeric(
                        question = "How many times bigger is 8 * 10^7 than 8 * 10^4?",
                        formula = "10^7 / 10^4 = ?",
                        answer = "1000",
                        explanation = "The front numbers match, so only the three extra jumps count.",
                        visual = Steps(terms = listOf(1, 10, 100, 1000), multiply = true),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which one is 45 000 in standard form?",
                options = listOf("4.5 * 10^4", "45 * 10^3", "4.5 * 10^5", "0.45 * 10^5"),
                correctIndex = 0,
                explanation = "The front number has to sit between 1 and 10, and 4.5 is four jumps below 45 000.",
                visual = Steps(terms = listOf(45, 450, 4500), multiply = true),
            ),
            QuizQuestion(
                prompt = "Which one of these is not in standard form?",
                options = listOf("3.2 * 10^5", "1.0 * 10^-3", "12 * 10^4", "9.9 * 10^2"),
                correctIndex = 2,
                explanation = "The front number has to sit between 1 and 10, and 12 does not.",
                visual = Steps(terms = listOf(12, 120, 1200), multiply = true),
            ),
            QuizQuestion(
                prompt = "Which one is 0.0004 in standard form?",
                options = listOf("4 * 10^-4", "4 * 10^-3", "4 * 10^4", "0.4 * 10^-3"),
                correctIndex = 0,
                explanation = "0.0004 is four jumps left of 4, so the power is -4.",
                visual = Steps(terms = listOf(4000, 400, 40, 4), multiply = true),
            ),
            QuizQuestion(
                prompt = "(2 x 10^5) x (4 x 10^2) = ?",
                options = listOf("8 * 10^7", "8 * 10^10", "6 * 10^7", "8 * 10^3"),
                correctIndex = 0,
                explanation = "The front numbers multiply to 8 and the powers add to 10^7.",
                visual = Steps(terms = listOf(2, 20, 200), multiply = true),
            ),
            QuizQuestion(
                prompt = "(9 * 10^8) / (3 * 10^5) = ?",
                options = listOf("3 * 10^3", "3 * 10^13", "6 * 10^3", "3 * 10^4"),
                correctIndex = 0,
                explanation = "The front numbers give 9 / 3 = 3, and the powers give 8 - 5 = 3.",
                visual = Steps(terms = listOf(9, 90, 900), multiply = true),
            ),
            QuizQuestion(
                prompt = "Which is larger, 7 * 10^5 or 2 * 10^6?",
                options = listOf("7 * 10^5", "2 * 10^6", "they are equal", "you cannot tell"),
                correctIndex = 1,
                explanation = "The bigger power wins whatever the front numbers are: 700 000 against 2 000 000.",
                visual = Steps(terms = listOf(2, 20, 200, 2000), multiply = true),
            ),
        ),
    )

    private val surds = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "surds",
        title = "Surds",
        summary = "Roots with no whole answer, and how to keep them exact.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-surds",
                title = "Roots that never finish",
                summary = "When a root is better left exactly as it is.",
                steps = listOf(
                    Concept(
                        body = "A square root asks for the side of a square with that area. 36 tiles make a tidy 6 by 6, so this root lands on a whole number.",
                        formula = "√36 = 6",
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = "Most numbers make no tidy square. Step 5 across and 5 up, and the diagonal you have just cut is √50: nothing whole squares to 50, so its decimal runs on for ever without ever repeating.",
                        formula = "5² + 5² = 50",
                        visual = RightTriangle(a = 5, b = 5),
                    ),
                    Choice(
                        question = "Which of these roots never finishes?",
                        options = listOf("√49", "√64", "√70", "√81"),
                        correctIndex = 2,
                        explanation = "49, 64 and 81 are square numbers. 70 sits between 64 and 81, so its root sits between 8 and 9 and never settles.",
                        visual = AreaGrid(cols = 8, rows = 8, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = "A root that never finishes is called a surd. Leaving it under the sign is the only way to write it exactly, which is why the sign is an answer and not an unfinished sum.",
                        formula = "√2 is exact, 1.41 is not",
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = "Between which two whole numbers does √30 sit?",
                        options = listOf("3 and 4", "4 and 5", "5 and 6", "6 and 7"),
                        correctIndex = 2,
                        explanation = "25 and 36 are the square numbers either side of 30, so the root sits between their roots.",
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = "√7 x √7 = ?",
                        answer = "7",
                        explanation = "A root multiplied by itself rebuilds the number it came from, exactly the way 3 rows of 3 rebuild 9. It makes no difference that this root never finishes.",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-surds-simplify",
                title = "Simplifying a surd",
                summary = "Split off the largest square factor.",
                steps = listOf(
                    Concept(
                        body = "Roots multiply straight across. That is what lets an untidy root be broken up: split off the largest square factor and take its root exactly.",
                        formula = "√a * √b = √(ab)",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false, unit = ""),
                    ),
                    Worked(
                        problem = "√50 = ?",
                        lines = listOf(
                            "Look for a square factor: 50 is 25 x 2.",
                            "The root splits in two: √25 x √2.",
                            "√25 is exactly 5.",
                            "2 has no square factor left, so it stays under the root.",
                        ),
                        result = "5√2",
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = "Find the square factor first.",
                        formula = "√18 = ?",
                        options = listOf("3√2", "2√3", "9√2", "6√3"),
                        correctIndex = 0,
                        explanation = "18 is 9 x 2, so √9 comes out as 3 and the 2 stays under the sign.",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = "Take the largest square factor you can find. Splitting √72 through 4 leaves 2√18, which still has work left in it; through 36 it finishes in one step.",
                        formula = "√72 = 6√2",
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = "√12 simplifies to a√3. What is a?",
                        answer = "2",
                        explanation = "12 is 4 x 3, and √4 is exactly 2.",
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = "Three of these still have a square factor hiding in them.",
                        options = listOf("√8", "√12", "√15", "√20"),
                        correctIndex = 2,
                        explanation = "8, 12 and 20 all divide by 4. 15 is 3 x 5, and neither is a square.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false, unit = ""),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-surds-arithmetic",
                title = "Adding and multiplying surds",
                summary = "Matching roots collect; different ones do not.",
                steps = listOf(
                    Concept(
                        body = "Matching roots collect the way matching units do. Three metres and four metres make seven metres, and the metre never changes.",
                        formula = "2√3 + 4√3 = 6√3",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = "The roots match, so only the numbers in front add.",
                        formula = "3√5 + 2√5 = ?",
                        options = listOf("5√5", "5√10", "6√5", "5"),
                        correctIndex = 0,
                        explanation = "Three of something and two more of it are five of it. Nothing happens under the root.",
                        visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
                    ),
                    Concept(
                        body = "√2 + √3 will not collect at all. The two roots are different lengths, so there is nothing to add and the sum is already finished.",
                        formula = "√2 + √3 stays as it is",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false, unit = ""),
                    ),
                    Worked(
                        problem = "√3 x √12 = ?",
                        lines = listOf(
                            "Roots multiply straight across.",
                            "√3 x √12 = √36.",
                            "36 is a square number.",
                        ),
                        result = "6",
                        visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
                    ),
                    Numeric(
                        question = "√5 x √20 = ?",
                        answer = "10",
                        explanation = "Straight across gives √100, and that root does finish.",
                        visual = AreaGrid(cols = 10, rows = 10, showArea = false, unit = ""),
                    ),
                    Choice(
                        question = "A root squared rebuilds the number underneath it. What is √7 squared?",
                        options = listOf("7", "14", "49", "√7"),
                        correctIndex = 0,
                        explanation = "Squaring and rooting undo each other, so the number comes back untouched.",
                        visual = AreaGrid(cols = 7, rows = 7, showArea = false, unit = ""),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which one of these is not a whole number?",
                options = listOf("√16", "√25", "√30", "√36"),
                correctIndex = 2,
                explanation = "16, 25 and 36 are square numbers. 30 is not, so its root never finishes.",
                visual = AreaGrid(cols = 5, rows = 5, showArea = false, unit = ""),
            ),
            QuizQuestion(
                prompt = "Simplify √32",
                options = listOf("4√2", "8√2", "2√16", "16√2"),
                correctIndex = 0,
                explanation = "32 is 16 x 2, so √16 comes out as 4 and the 2 stays under the sign.",
                visual = Steps(terms = listOf(2, 4, 8, 16, 32), multiply = true),
            ),
            QuizQuestion(
                prompt = "Simplify √45",
                options = listOf("3√5", "5√3", "9√5", "15√3"),
                correctIndex = 0,
                explanation = "45 is 9 x 5, so √9 comes out as 3 and the 5 stays under the sign.",
                visual = AreaGrid(cols = 3, rows = 3, showArea = false, unit = ""),
            ),
            QuizQuestion(
                prompt = "√6 x √6 = ?",
                options = listOf("6", "12", "36", "√12"),
                correctIndex = 0,
                explanation = "A root times itself rebuilds the number under it.",
                visual = AreaGrid(cols = 6, rows = 6, showArea = false, unit = ""),
            ),
            QuizQuestion(
                prompt = "5√2 + 3√2 = ?",
                options = listOf("8√2", "8√4", "15√2", "8"),
                correctIndex = 0,
                explanation = "The roots match, so only the numbers in front add.",
                visual = AreaGrid(cols = 2, rows = 2, showArea = false, unit = ""),
            ),
            QuizQuestion(
                prompt = "Between which two whole numbers does √50 sit?",
                options = listOf("5 and 6", "6 and 7", "7 and 8", "8 and 9"),
                correctIndex = 2,
                explanation = "49 and 64 are the square numbers either side of 50, so the root sits between 7 and 8.",
                visual = AreaGrid(cols = 7, rows = 7, showArea = false, unit = ""),
            ),
        ),
    )

    private val bounds = learnUnit(
        topic = MathTopic.ARITHMETIC,
        urlSlug = "bounds",
        title = "Rounding and bounds",
        summary = "The range a rounded number stands for, and how it travels through a sum.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "arithmetic-bounds",
                title = "What a rounded number hides",
                summary = "Every rounded measurement stands for a range.",
                steps = listOf(
                    Concept(
                        body = "Rounding throws information away. A wall given as 40 cm to the nearest 10 cm was never exactly 40: anything from 35 cm up rounds to it, and so does anything below 45 cm.",
                        formula = "35 <= length < 45",
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 40, jump = -5, thenJump = 5),
                    ),
                    Choice(
                        question = "A crowd is reported as 500, to the nearest 100. What is the smallest it could have been?",
                        options = listOf("400", "450", "495", "499"),
                        correctIndex = 1,
                        explanation = "Half of a hundred below 500. From there up, everything rounds to 500.",
                        visual = NumberLine(from = 300, to = 700, tickStep = 100, start = 500, reveal = false),
                    ),
                    Concept(
                        body = "The two ends are not treated alike. The bottom one is included, because 450 does round up to 500. The top one is not: 550 rounds up to 600 instead, so the range stops just short of it.",
                        formula = "450 <= crowd < 550",
                        visual = NumberLine(from = 400, to = 600, tickStep = 50, start = 500, jump = -50, thenJump = 50),
                    ),
                    Concept(
                        body = "The two ends have names. The lower bound is the smallest the measurement can be, and the upper bound is the number it stops just short of.",
                        formula = "lower 35, upper 45",
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 35, jump = 10),
                    ),
                    Numeric(
                        question = "A sack is 60 kg to the nearest 10 kg. What is the least it can weigh, in kilograms?",
                        answer = "55",
                        explanation = "Half of ten below 60. At 55 it still rounds up to 60.",
                        visual = NumberLine(from = 50, to = 70, tickStep = 5, start = 60, reveal = false),
                    ),
                    Choice(
                        question = "A field is given as 300 m to the nearest 50 m. Where does its range start?",
                        options = listOf("250 m", "275 m", "295 m", "299 m"),
                        correctIndex = 1,
                        explanation = "Half the rounding unit either side: half of 50 is 25, so the range runs from 275 m up to just under 325 m.",
                        visual = NumberLine(from = 200, to = 400, tickStep = 25, start = 300, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-bounds-significant",
                title = "Significant figures",
                summary = "Counting from the first digit that means something.",
                steps = listOf(
                    Concept(
                        body = "Significant figures count from the first digit that is not a nought, wherever the point happens to sit.",
                        formula = "0.00427 has 3 significant figures",
                        visual = Steps(terms = listOf(427, 4270, 42700), multiply = true),
                    ),
                    Concept(
                        body = "Rounding to a significant figure works exactly like rounding to a place: keep the digits you want and look at the next one along to decide.",
                        formula = "3 872 = 3 900 to 2 s.f.",
                        visual = Steps(terms = listOf(39, 390, 3900), multiply = true),
                    ),
                    Choice(
                        question = "Round 0.04617 to 2 significant figures. The leading noughts only hold the point, so counting starts at the 4.",
                        options = listOf("0.046", "0.05", "0.0462", "0.04"),
                        correctIndex = 0,
                        explanation = "4 and 6 are the two significant digits, and the 1 after them rounds nothing up.",
                        visual = Steps(terms = listOf(46, 460, 4600), multiply = true),
                    ),
                    Numeric(
                        question = "Round 27 486 to 3 significant figures.",
                        answer = "27500",
                        explanation = "2, 7 and 4 are the three digits, and the 8 behind them rounds the 4 up.",
                        visual = Steps(terms = listOf(275, 2750, 27500), multiply = true),
                    ),
                    Concept(
                        body = "A nought between two significant digits counts. A nought in front of them does not: it is only holding the point, which is why 0.00308 has three.",
                        formula = "0.00308 has 3 s.f.",
                        visual = Steps(terms = listOf(308, 3080, 30800), multiply = true),
                    ),
                    Choice(
                        question = "How many significant figures has 0.0250?",
                        options = listOf("1", "2", "3", "4"),
                        correctIndex = 2,
                        explanation = "The two leading noughts only hold the point, but the last one was written on purpose, so it counts: 2, 5 and 0.",
                        visual = Steps(terms = listOf(250, 2500, 25000), multiply = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "arithmetic-bounds-calculating",
                title = "Calculating with bounds",
                summary = "A range in, a range out.",
                steps = listOf(
                    Concept(
                        body = "Every measurement carries a range, so anything worked out from one carries a range too. There is no single answer to give.",
                        visual = NumberLine(from = 30, to = 50, tickStep = 5, start = 40, jump = -5, thenJump = 5),
                    ),
                    Worked(
                        problem = "Two boards, each 40 cm to the nearest 10 cm, are laid end to end. How long is the pair?",
                        lines = listOf(
                            "Each board is at least 35 cm.",
                            "Two of those give 70 cm.",
                            "Each board is under 45 cm.",
                            "Two of those stay under 90 cm.",
                        ),
                        result = "70 cm up to just under 90 cm",
                        visual = NumberLine(from = 60, to = 100, tickStep = 10, start = 80, jump = -10, thenJump = 10),
                    ),
                    Concept(
                        body = "For a sum, take both upper bounds to reach as high as possible, and both lower bounds to reach as low as possible.",
                        visual = NumberLine(from = 80, to = 120, tickStep = 10, start = 90, jump = 20),
                    ),
                    Choice(
                        question = "Two shelves, each 50 cm to the nearest 10 cm, go end to end. How far can they reach?",
                        options = listOf("100 cm", "105 cm", "110 cm", "just under 110 cm"),
                        correctIndex = 3,
                        explanation = "Each shelf stops just short of 55, so the pair stops just short of 110 without ever arriving.",
                        visual = NumberLine(from = 80, to = 120, tickStep = 10, start = 100, reveal = false),
                    ),
                    Numeric(
                        question = "A journey is 120 km to the nearest 10 km. What is its lower bound, in kilometres?",
                        answer = "115",
                        explanation = "Half of ten below 120, because 115 still rounds up to 120.",
                        visual = NumberLine(from = 100, to = 140, tickStep = 5, start = 120, reveal = false),
                    ),
                    Choice(
                        question = "A subtraction is the odd one out. Which pair of bounds gives the largest answer?",
                        options = listOf("both upper bounds", "both lower bounds", "upper take away lower", "lower take away upper"),
                        correctIndex = 2,
                        explanation = "Start as high as you can and take away as little as you can.",
                        visual = NumberLine(from = 0, to = 40, tickStep = 5, start = 30, jump = -10, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A mass is 8 kg to the nearest kilogram. Where does its range start?",
                options = listOf("7 kg", "7.5 kg", "7.9 kg", "8 kg"),
                correctIndex = 1,
                explanation = "Half a kilogram below 8, because 7.5 rounds up to 8.",
                visual = NumberLine(from = 6, to = 10, tickStep = 1, start = 8, reveal = false),
            ),
            QuizQuestion(
                prompt = "A shelf is 200 cm to the nearest 10 cm. Which lengths round to that?",
                options = listOf(
                    "195 up to just under 205",
                    "190 up to just under 200",
                    "199 up to 201",
                    "195 up to 210",
                ),
                correctIndex = 0,
                explanation = "Half of ten either side, and the top end is not included.",
                visual = NumberLine(from = 180, to = 220, tickStep = 5, start = 200, reveal = false),
            ),
            QuizQuestion(
                prompt = "Round 5 748 to 2 significant figures.",
                options = listOf("5 700", "5 800", "5 750", "6 000"),
                correctIndex = 0,
                explanation = "5 and 7 are the two digits, and the 4 behind them rounds nothing up.",
                visual = Steps(terms = listOf(57, 570, 5700), multiply = true),
            ),
            QuizQuestion(
                prompt = "How many significant figures has 0.00680?",
                options = listOf("2", "3", "4", "5"),
                correctIndex = 1,
                explanation = "Counting starts at the 6, and the last nought was written on purpose: 6, 8 and 0.",
                visual = Steps(terms = listOf(68, 680, 6800), multiply = true),
            ),
            QuizQuestion(
                prompt = "A length is 46 cm to the nearest centimetre. What is its upper bound, in centimetres?",
                options = listOf("46.5", "46", "47", "45.5"),
                correctIndex = 0,
                explanation = "Half a centimetre above 46, and the length stops just short of it.",
                visual = NumberLine(from = 44, to = 48, tickStep = 1, start = 46, reveal = false),
            ),
            QuizQuestion(
                prompt = "Two masses, each 20 g to the nearest gram, are added. What is the lower bound of the total, in grams?",
                options = listOf("38", "39", "39.5", "40"),
                correctIndex = 1,
                explanation = "Each one is at least 19.5 g, so the pair is at least 39 g.",
                visual = NumberLine(from = 36, to = 44, tickStep = 2, start = 40, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        counting,
        multiplication,
        fractions,
        decimals,
        negatives,
        ratio,
        percent,
        standardForm,
        surds,
        bounds,
    )
}
