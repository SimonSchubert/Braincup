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

/** Grades 1-2: counting, first sums, everyday measures and the shapes around us. */
internal object Grade1To2Content {

    private val level = GradeLevel.GRADES_1_2

    private val arithmetic = learnUnit(
        level = level,
        topic = MathTopic.ARITHMETIC,
        summary = "Counting on, sums to 20, and tens and ones.",
        lessons = listOf(
            LessonSpec(
                id = "g12-arithmetic-counting",
                title = "Counting to 100",
                summary = "Find any number on the number line.",
                steps = listOf(
                    Concept(
                        body = "Numbers sit in a fixed order. On a number line every step to the right is one more, and every step to the left is one less.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Concept(
                        body = "Counting on is the same as adding. Start at 7 and count three more: 8, 9, 10.",
                        formula = "7 + 3 = 10",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Choice(
                        question = "You start at 12 and count on 4 more. Where do you land?",
                        options = listOf("14", "15", "16", "17"),
                        correctIndex = 2,
                        explanation = "13, 14, 15, 16 - four steps to the right of 12.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Concept(
                        body = "Skip counting jumps by the same amount every time: 2, 4, 6, 8 or 5, 10, 15, 20 or 10, 20, 30, 40. Skipping by ten is the fastest way through big numbers.",
                    ),
                    Numeric(
                        question = "Skip count by tens: 30, 40, 50, ... What comes next?",
                        answer = "60",
                        explanation = "Every jump adds ten, so 50 + 10 = 60.",
                    ),
                    Choice(
                        question = "Which number comes just before 40?",
                        options = listOf("30", "39", "41", "44"),
                        correctIndex = 1,
                        explanation = "One less than 40 is 39. One more would be 41.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-addsub",
                title = "Adding and taking away",
                summary = "Make ten, and count up to subtract.",
                steps = listOf(
                    Concept(
                        body = "Adding joins two groups into one. Four counters and three counters make a single group of seven.",
                        formula = "4 + 3 = 7",
                        visual = LearnVisual.COUNTERS,
                    ),
                    Worked(
                        problem = "6 + 7 = ?",
                        lines = listOf(
                            "Sums that cross ten are easier if you make ten first.",
                            "6 needs 4 more to reach 10, so split the 7 into 4 and 3.",
                            "6 + 4 = 10.",
                            "10 + 3 = 13.",
                        ),
                        result = "6 + 7 = 13",
                        visual = LearnVisual.COUNTERS,
                    ),
                    Choice(
                        question = "Use 'make ten' for 8 + 5. Which split of the 5 helps?",
                        options = listOf("1 and 4", "2 and 3", "3 and 2", "4 and 1"),
                        correctIndex = 1,
                        explanation = "8 needs 2 to reach 10, so split 5 into 2 and 3: 10 + 3 = 13.",
                    ),
                    Concept(
                        body = "Subtraction is the gap between two numbers. To work out 15 - 8 you can count up from 8 until you reach 15.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Numeric(
                        question = "15 - 8 = ?",
                        answer = "7",
                        explanation = "From 8 up to 10 is 2, then 10 up to 15 is 5. 2 + 5 = 7.",
                    ),
                    Concept(
                        body = "Three numbers that belong together make a fact family. From 4, 9 and 13 you get 4 + 9 = 13, 9 + 4 = 13, 13 - 4 = 9 and 13 - 9 = 4. Learn one fact and you get four.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-arithmetic-tens",
                title = "Tens and ones",
                summary = "Read, build and compare two-digit numbers.",
                steps = listOf(
                    Concept(
                        body = "A two-digit number is built from tens and ones. In 37 the 3 means three tens and the 7 means seven ones.",
                        formula = "37 = 30 + 7",
                        visual = LearnVisual.PLACE_VALUE_BLOCKS,
                    ),
                    Choice(
                        question = "How many tens are in 68?",
                        options = listOf("6", "8", "60", "68"),
                        correctIndex = 0,
                        explanation = "68 is 6 tens and 8 ones.",
                        visual = LearnVisual.PLACE_VALUE_BLOCKS,
                    ),
                    Worked(
                        problem = "25 + 13 = ?",
                        lines = listOf(
                            "Add the tens: 20 + 10 = 30.",
                            "Add the ones: 5 + 3 = 8.",
                            "Put the parts back together: 30 + 8.",
                        ),
                        result = "25 + 13 = 38",
                        visual = LearnVisual.PLACE_VALUE_BLOCKS,
                    ),
                    Numeric(
                        question = "4 tens and 2 ones make which number?",
                        answer = "42",
                        explanation = "4 tens is 40, and 2 more ones makes 42.",
                    ),
                    Concept(
                        body = "To compare two-digit numbers, look at the tens first. 47 and 74 use the same digits, but 74 has seven tens and 47 has only four.",
                    ),
                    Choice(
                        question = "Which of these numbers is the largest?",
                        options = listOf("58", "85", "61", "49"),
                        correctIndex = 1,
                        explanation = "85 has 8 tens - more than any of the others.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What comes next: 20, 30, 40, ...?",
                options = listOf("41", "45", "50", "60"),
                correctIndex = 2,
                explanation = "The jumps are tens, so 40 + 10 = 50.",
            ),
            QuizQuestion(
                prompt = "9 + 6 = ?",
                options = listOf("14", "15", "16", "13"),
                correctIndex = 1,
                explanation = "9 needs 1 to make ten, leaving 5: 10 + 5 = 15.",
            ),
            QuizQuestion(
                prompt = "14 - 6 = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "From 6 up to 10 is 4, then 10 up to 14 is 4. 4 + 4 = 8.",
            ),
            QuizQuestion(
                prompt = "How many ones are in 53?",
                options = listOf("5", "3", "50", "53"),
                correctIndex = 1,
                explanation = "53 is 5 tens and 3 ones.",
            ),
            QuizQuestion(
                prompt = "Which number is smaller, 62 or 26?",
                options = listOf("62", "26", "They are equal", "You cannot tell"),
                correctIndex = 1,
                explanation = "26 has only 2 tens; 62 has 6.",
            ),
            QuizQuestion(
                prompt = "Skip count by five: 15, 20, 25, ...",
                options = listOf("26", "30", "35", "40"),
                correctIndex = 1,
                explanation = "Each jump adds five, so 25 + 5 = 30.",
            ),
        ),
    )

    private val measurement = learnUnit(
        level = level,
        topic = MathTopic.MEASUREMENT,
        summary = "Length with a ruler, time on a clock, and counting coins.",
        lessons = listOf(
            LessonSpec(
                id = "g12-measurement-length",
                title = "Measuring length",
                summary = "Use a unit that never changes size.",
                steps = listOf(
                    Concept(
                        body = "Measuring means counting how many copies of a unit fit along something. The unit has to be the same size every time, with no gaps and no overlaps.",
                        visual = LearnVisual.RULER,
                    ),
                    Concept(
                        body = "A ruler is a number line with the unit built in. Line the object up with 0 and read the number at the other end.",
                        visual = LearnVisual.RULER,
                    ),
                    Choice(
                        question = "A pencil starts at 0 cm and ends at 9 cm. How long is it?",
                        options = listOf("8 cm", "9 cm", "10 cm", "You cannot tell"),
                        correctIndex = 1,
                        explanation = "The tip sits at 9, so nine centimetres fit along the pencil.",
                        visual = LearnVisual.RULER,
                    ),
                    Concept(
                        body = "Comparing is easier than measuring. If two ribbons start at the same line, the one that reaches further is longer - no numbers needed at all.",
                    ),
                    Numeric(
                        question = "A worm is 6 cm long. A stick is 4 cm longer. How long is the stick, in cm?",
                        answer = "10",
                        explanation = "4 cm more than 6 cm is 6 + 4 = 10 cm.",
                    ),
                    Choice(
                        question = "Which unit fits the length of a classroom best?",
                        options = listOf("Millimetres", "Centimetres", "Metres", "Kilometres"),
                        correctIndex = 2,
                        explanation = "A room is a handful of metres across. Millimetres would need thousands of units and kilometres far too few.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-measurement-time",
                title = "Telling the time",
                summary = "Hours, half past and quarter past.",
                steps = listOf(
                    Concept(
                        body = "A clock face is a circle split into 12 hours. The short hand points to the hour; the long hand counts minutes as it sweeps around.",
                        visual = LearnVisual.CLOCK,
                    ),
                    Concept(
                        body = "A full turn of the long hand is 60 minutes - one hour. Half a turn is 30 minutes, which we call 'half past'. A quarter turn is 15 minutes.",
                        visual = LearnVisual.CLOCK,
                    ),
                    Choice(
                        question = "The short hand is just past 3 and the long hand points straight down. What time is it?",
                        options = listOf("3 o'clock", "Half past 3", "Quarter past 3", "Half past 6"),
                        correctIndex = 1,
                        explanation = "Straight down is half a turn - 30 minutes past the hour the short hand has passed.",
                        visual = LearnVisual.CLOCK,
                    ),
                    Numeric(
                        question = "How many minutes are there in a quarter of an hour?",
                        answer = "15",
                        explanation = "60 minutes split into four equal parts gives 15 minutes each.",
                    ),
                    Worked(
                        problem = "It is 2 o'clock. What time will it be in 3 hours?",
                        lines = listOf(
                            "The hour hand moves on one number for every hour.",
                            "From 2, move on three numbers: 3, 4, 5.",
                        ),
                        result = "5 o'clock",
                        visual = LearnVisual.CLOCK,
                    ),
                    Choice(
                        question = "Which of these takes about one minute?",
                        options = listOf("Sleeping at night", "Counting to sixty", "Reading a whole book", "A school day"),
                        correctIndex = 1,
                        explanation = "One number a second, sixty numbers - about a minute.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-measurement-money",
                title = "Counting coins",
                summary = "Add coin values and work out change.",
                steps = listOf(
                    Concept(
                        body = "Coins are worth different amounts, so you count their value, not how many there are. Three small coins can be worth less than one big one.",
                        visual = LearnVisual.COINS,
                    ),
                    Concept(
                        body = "Start with the largest coin and count on from there. 50, 70, 90, 95 is much quicker than adding the coins in a jumble.",
                        visual = LearnVisual.COINS,
                    ),
                    Numeric(
                        question = "You have a 50c coin, a 20c coin and a 10c coin. How many cents is that?",
                        answer = "80",
                        explanation = "Start at 50, count on 20 to reach 70, then 10 more to reach 80.",
                    ),
                    Choice(
                        question = "Which set of coins is worth the most?",
                        options = listOf("Three 10c coins", "Two 20c coins", "One 50c coin", "Four 5c coins"),
                        correctIndex = 2,
                        explanation = "The sets are worth 30c, 40c, 50c and 20c, so the single 50c coin wins.",
                        visual = LearnVisual.COINS,
                    ),
                    Worked(
                        problem = "A sticker costs 35c and you pay with 50c. What is your change?",
                        lines = listOf(
                            "Change is the gap between the price and what you paid.",
                            "Count up from 35 to 40: that is 5c.",
                            "Count up from 40 to 50: that is 10c more.",
                            "5c + 10c = 15c.",
                        ),
                        result = "15c change",
                    ),
                    Choice(
                        question = "You have 40c and want something that costs 65c. How much more do you need?",
                        options = listOf("15c", "20c", "25c", "30c"),
                        correctIndex = 2,
                        explanation = "From 40 up to 65 is 25c.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "A pen lines up from 0 cm to 12 cm on the ruler. How long is it?",
                options = listOf("10 cm", "11 cm", "12 cm", "13 cm"),
                correctIndex = 2,
                explanation = "Reading the ruler at the far end of the pen gives 12 cm.",
            ),
            QuizQuestion(
                prompt = "How many minutes are in half an hour?",
                options = listOf("15", "30", "45", "60"),
                correctIndex = 1,
                explanation = "An hour is 60 minutes, and half of 60 is 30.",
            ),
            QuizQuestion(
                prompt = "The long hand points straight up and the short hand at 8. What time is it?",
                options = listOf("Half past 8", "8 o'clock", "Quarter past 8", "12 o'clock"),
                correctIndex = 1,
                explanation = "Straight up means no minutes have passed yet, so it is exactly 8 o'clock.",
            ),
            QuizQuestion(
                prompt = "20c + 20c + 5c = ?",
                options = listOf("40c", "45c", "50c", "25c"),
                correctIndex = 1,
                explanation = "Two twenties make 40c, and 5c more makes 45c.",
            ),
            QuizQuestion(
                prompt = "Which unit fits the distance between two towns?",
                options = listOf("Centimetres", "Metres", "Kilometres", "Millimetres"),
                correctIndex = 2,
                explanation = "Towns are thousands of metres apart, which is what a kilometre packages up.",
            ),
            QuizQuestion(
                prompt = "You pay 50c for a 30c pencil. What change do you get?",
                options = listOf("10c", "15c", "20c", "25c"),
                correctIndex = 2,
                explanation = "From 30 up to 50 is 20c.",
            ),
        ),
    )

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Flat shapes, solid shapes, and splitting things into equal parts.",
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-flat-shapes",
                title = "Flat shapes",
                summary = "Count sides and corners to name a shape.",
                steps = listOf(
                    Concept(
                        body = "Flat shapes are named by how many straight sides they have: a triangle has 3, a square and a rectangle have 4, a pentagon has 5 and a hexagon has 6.",
                        visual = LearnVisual.SHAPES_2D,
                    ),
                    Concept(
                        body = "Where two sides meet you get a corner. A flat shape always has exactly as many corners as sides.",
                        visual = LearnVisual.SHAPES_2D,
                    ),
                    Choice(
                        question = "A shape has 4 straight sides, all the same length, and 4 square corners. What is it?",
                        options = listOf("Rectangle", "Square", "Triangle", "Circle"),
                        correctIndex = 1,
                        explanation = "All four sides equal plus square corners makes a square. A rectangle only needs its opposite sides to match.",
                    ),
                    Numeric(
                        question = "How many corners does a hexagon have?",
                        answer = "6",
                        explanation = "A hexagon has 6 sides, and sides and corners always come in equal numbers.",
                    ),
                    Concept(
                        body = "A circle has no straight sides and no corners at all. Its edge curves the whole way round, staying the same distance from the middle.",
                        visual = LearnVisual.CIRCLE,
                    ),
                    Choice(
                        question = "Which of these is NOT true of every rectangle?",
                        options = listOf("It has four sides", "It has four square corners", "All four sides are the same length", "Opposite sides are the same length"),
                        correctIndex = 2,
                        explanation = "Only squares have all four sides equal - a long thin rectangle does not.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-solid-shapes",
                title = "Solid shapes",
                summary = "Faces, edges and corners in three dimensions.",
                steps = listOf(
                    Concept(
                        body = "Solid shapes take up space. The ones you meet most are the cube, the sphere, the cylinder and the cone.",
                        visual = LearnVisual.SOLIDS,
                    ),
                    Concept(
                        body = "Solids are described by faces (the flat surfaces), edges (where two faces meet) and corners. A cube has 6 faces, 12 edges and 8 corners.",
                        visual = LearnVisual.SOLIDS,
                    ),
                    Choice(
                        question = "Which solid rolls, but also has two flat circular faces?",
                        options = listOf("Sphere", "Cube", "Cylinder", "Cone"),
                        correctIndex = 2,
                        explanation = "A cylinder rolls on its curved side and stands on either circle. A sphere has no flat face and a cone has only one.",
                        visual = LearnVisual.SOLIDS,
                    ),
                    Numeric(
                        question = "How many faces does a cube have?",
                        answer = "6",
                        explanation = "Top, bottom and four sides.",
                    ),
                    Concept(
                        body = "Every solid shows flat shapes when you look at it straight on. A cube from the front looks like a square; a cylinder from above looks like a circle.",
                    ),
                    Choice(
                        question = "A tin of soup is closest to which solid?",
                        options = listOf("Cube", "Cone", "Sphere", "Cylinder"),
                        correctIndex = 3,
                        explanation = "Two circular ends joined by a curved side - a cylinder.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-equal-parts",
                title = "Halves and quarters",
                summary = "Fair shares are equal shares.",
                steps = listOf(
                    Concept(
                        body = "A fraction is what you get when you split something into equal parts. The parts have to be the same size, or they are not halves at all.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Concept(
                        body = "Split into 2 equal parts and each one is a half. Split into 4 and each one is a quarter. The more parts you make, the smaller each part gets.",
                        formula = "1 half = 2 quarters",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Choice(
                        question = "A chocolate bar is cut into 4 equal pieces and you eat 2. How much have you eaten?",
                        options = listOf("A quarter", "A half", "Three quarters", "All of it"),
                        correctIndex = 1,
                        explanation = "2 of the 4 equal pieces is the same amount as one half.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Numeric(
                        question = "A pizza is cut into 4 equal slices and you eat 1. How many slices are left?",
                        answer = "3",
                        explanation = "4 slices take away 1 leaves 3 - three quarters of the pizza.",
                    ),
                    Concept(
                        body = "Halving twice gives quarters, because half of a half is a quarter. That is why a quarter is smaller than a half, even though 4 is bigger than 2.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Choice(
                        question = "Which is the bigger share of the same cake?",
                        options = listOf("A quarter", "A half", "They are the same", "It depends on the shape"),
                        correctIndex = 1,
                        explanation = "One of two equal parts is always bigger than one of four equal parts.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many sides does a pentagon have?",
                options = listOf("4", "5", "6", "8"),
                correctIndex = 1,
                explanation = "Penta means five.",
            ),
            QuizQuestion(
                prompt = "Which shape has no corners?",
                options = listOf("Triangle", "Square", "Circle", "Hexagon"),
                correctIndex = 2,
                explanation = "A circle is one smooth curve, so no two sides ever meet.",
            ),
            QuizQuestion(
                prompt = "How many edges does a cube have?",
                options = listOf("6", "8", "10", "12"),
                correctIndex = 3,
                explanation = "Four around the top, four around the bottom and four uprights.",
            ),
            QuizQuestion(
                prompt = "Which solid has a point at the top and one circular face?",
                options = listOf("Cylinder", "Cone", "Sphere", "Cube"),
                correctIndex = 1,
                explanation = "A cone rises from its circle to a single point.",
            ),
            QuizQuestion(
                prompt = "A cake is cut into 2 equal pieces. Each piece is...",
                options = listOf("a quarter", "a half", "a third", "a whole"),
                correctIndex = 1,
                explanation = "Two equal parts means each one is a half.",
            ),
            QuizQuestion(
                prompt = "Which of these is NOT true of a square?",
                options = listOf("4 equal sides", "4 square corners", "4 corners", "Curved edges"),
                correctIndex = 3,
                explanation = "Every side of a square is straight.",
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, measurement, geometry)
}
