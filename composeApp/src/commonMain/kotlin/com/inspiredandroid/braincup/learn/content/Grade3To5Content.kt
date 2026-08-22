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

/** Grades 3-5: times tables, fractions and decimals, metric units, angles and first statistics. */
internal object Grade3To5Content {

    private val level = GradeLevel.GRADES_3_5

    private val arithmetic = learnUnit(
        level = level,
        topic = MathTopic.ARITHMETIC,
        summary = "Multiplication and division, equivalent fractions and decimals.",
        lessons = listOf(
            LessonSpec(
                id = "g35-arithmetic-multiplication",
                title = "Multiplication as an array",
                summary = "Rows and columns make the times tables make sense.",
                steps = listOf(
                    Concept(
                        body = "Multiplication is repeated addition arranged in rows. Four rows of six is 6 + 6 + 6 + 6, which we write as 4 x 6.",
                        formula = "4 x 6 = 24",
                        visual = LearnVisual.ARRAY_GRID,
                    ),
                    Concept(
                        body = "Turning the array on its side does not change how many dots there are, so 4 x 6 = 6 x 4. That halves the number of facts you have to learn.",
                        visual = LearnVisual.ARRAY_GRID,
                    ),
                    Choice(
                        question = "Which addition does 7 x 3 stand for?",
                        options = listOf("7 + 3", "3 + 3 + 3 + 3 + 3 + 3 + 3", "7 + 7 + 7 + 7", "3 x 3 x 3"),
                        correctIndex = 1,
                        explanation = "Seven threes, which come to 21.",
                    ),
                    Worked(
                        problem = "6 x 8 = ?",
                        lines = listOf(
                            "If you do not know a fact, break it into ones you do know.",
                            "6 x 8 = 5 x 8 + 1 x 8.",
                            "5 x 8 = 40 and 1 x 8 = 8.",
                            "40 + 8 = 48.",
                        ),
                        result = "6 x 8 = 48",
                        visual = LearnVisual.ARRAY_GRID,
                    ),
                    Numeric(
                        question = "A tray holds 4 rows of 7 cakes. How many cakes is that?",
                        answer = "28",
                        explanation = "4 x 7 = 28.",
                    ),
                    Concept(
                        body = "Division undoes multiplication. Because 4 x 7 = 28, you also know 28 divided by 7 is 4 and 28 divided by 4 is 7.",
                        formula = "28 / 7 = 4",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-arithmetic-fractions",
                title = "Fractions that match",
                summary = "Equivalent fractions and how to compare them.",
                steps = listOf(
                    Concept(
                        body = "The bottom number of a fraction says how many equal parts the whole is cut into. The top number says how many of those parts you take.",
                        formula = "3/4 = 3 parts out of 4",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Concept(
                        body = "Cut every part in half and you have twice as many parts, each half the size. The amount has not changed, so 1/2 = 2/4 = 4/8. These are equivalent fractions.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Choice(
                        question = "Which fraction is equal to 2/3?",
                        options = listOf("3/4", "4/6", "2/6", "6/3"),
                        correctIndex = 1,
                        explanation = "Double top and bottom: 2 x 2 = 4 and 3 x 2 = 6.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Concept(
                        body = "Fractions with the same bottom number are easy to compare - just look at the tops, so 3/5 beats 2/5. With different bottoms, rewrite them until the bottoms match.",
                    ),
                    Numeric(
                        question = "You eat 3/8 of a pizza cut into 8 slices. How many slices are left?",
                        answer = "5",
                        explanation = "8 - 3 = 5 slices, which is 5/8 of the pizza.",
                    ),
                    Choice(
                        question = "Which is larger, 2/3 or 3/4?",
                        options = listOf("2/3", "3/4", "They are equal", "You cannot compare them"),
                        correctIndex = 1,
                        explanation = "Rewrite both in twelfths: 2/3 = 8/12 and 3/4 = 9/12.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-arithmetic-decimals",
                title = "Decimals",
                summary = "Tenths, hundredths and how they line up.",
                steps = listOf(
                    Concept(
                        body = "Place value carries on to the right of the decimal point: first tenths, then hundredths. So 0.7 is seven tenths while 0.07 is only seven hundredths.",
                        formula = "2.35 = 2 + 3/10 + 5/100",
                        visual = LearnVisual.PLACE_VALUE_BLOCKS,
                    ),
                    Concept(
                        body = "Decimals and fractions are two spellings of the same amount: 1/2 = 0.5, 1/4 = 0.25 and 3/4 = 0.75.",
                        visual = LearnVisual.FRACTION_BAR,
                    ),
                    Choice(
                        question = "Which is bigger, 0.4 or 0.35?",
                        options = listOf("0.4", "0.35", "They are equal", "You cannot tell"),
                        correctIndex = 0,
                        explanation = "Compare tenths first: 4 tenths beats 3 tenths. Extra digits do not make a number bigger.",
                    ),
                    Worked(
                        problem = "2.4 + 1.35 = ?",
                        lines = listOf(
                            "Line up the decimal points, not the last digits.",
                            "Write 2.4 as 2.40 so both numbers have two decimal places.",
                            "Hundredths: 0 + 5 = 5. Tenths: 4 + 3 = 7.",
                            "Wholes: 2 + 1 = 3.",
                        ),
                        result = "2.4 + 1.35 = 3.75",
                    ),
                    Numeric(
                        question = "Write 3/4 as a decimal.",
                        answer = "0.75",
                        explanation = "3 divided by 4 is 0.75, or three copies of 0.25.",
                    ),
                    Choice(
                        question = "Money is decimals in disguise. What does 3.05 euro mean?",
                        options = listOf("3 euro 50 cents", "3 euro 5 cents", "30 euro 5 cents", "3 euro 500 cents"),
                        correctIndex = 1,
                        explanation = "The 0 holds the tenths place, so the 5 sits in hundredths - five cents.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "8 x 7 = ?",
                options = listOf("54", "56", "48", "64"),
                correctIndex = 1,
                explanation = "8 x 7 = 56.",
            ),
            QuizQuestion(
                prompt = "72 divided by 9 = ?",
                options = listOf("6", "7", "8", "9"),
                correctIndex = 2,
                explanation = "9 x 8 = 72.",
            ),
            QuizQuestion(
                prompt = "Which fraction equals 1/2?",
                options = listOf("3/5", "4/8", "2/5", "5/12"),
                correctIndex = 1,
                explanation = "4 is half of 8.",
            ),
            QuizQuestion(
                prompt = "Which of these is the largest?",
                options = listOf("0.5", "0.45", "0.09", "0.499"),
                correctIndex = 0,
                explanation = "All the others have 4 or fewer tenths; 0.5 has five.",
            ),
            QuizQuestion(
                prompt = "3/5 + 1/5 = ?",
                options = listOf("4/10", "4/5", "3/10", "4/25"),
                correctIndex = 1,
                explanation = "The parts are the same size, so add the tops only.",
            ),
            QuizQuestion(
                prompt = "Write 0.25 as a fraction.",
                options = listOf("1/2", "1/4", "2/5", "1/5"),
                correctIndex = 1,
                explanation = "25 hundredths simplifies to one quarter.",
            ),
        ),
    )

    private val measurement = learnUnit(
        level = level,
        topic = MathTopic.MEASUREMENT,
        summary = "Converting metric units, perimeter and area.",
        lessons = listOf(
            LessonSpec(
                id = "g35-measurement-units",
                title = "Metric units",
                summary = "Every conversion is a jump of ten.",
                steps = listOf(
                    Concept(
                        body = "Metric units step by ten: 10 mm = 1 cm, 100 cm = 1 m and 1000 m = 1 km. Converting is only ever multiplying or dividing by a power of ten.",
                        visual = LearnVisual.RULER,
                    ),
                    Concept(
                        body = "Moving to a smaller unit means you need more of them, so multiply. Moving to a bigger unit means fewer, so divide.",
                        formula = "2.5 m = 250 cm",
                    ),
                    Choice(
                        question = "How many millimetres are in 7 cm?",
                        options = listOf("0.7", "7", "70", "700"),
                        correctIndex = 2,
                        explanation = "Millimetres are smaller, so multiply: 7 x 10 = 70.",
                        visual = LearnVisual.RULER,
                    ),
                    Numeric(
                        question = "A ribbon is 1.2 m long. How many centimetres is that?",
                        answer = "120",
                        explanation = "1 m = 100 cm, so 1.2 x 100 = 120 cm.",
                    ),
                    Concept(
                        body = "Mass and capacity follow exactly the same pattern: 1000 g = 1 kg and 1000 ml = 1 litre.",
                    ),
                    Choice(
                        question = "A bottle holds 1.5 litres. How many millilitres is that?",
                        options = listOf("15", "150", "1500", "15000"),
                        correctIndex = 2,
                        explanation = "1 litre = 1000 ml, so 1.5 x 1000 = 1500 ml.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-perimeter",
                title = "Perimeter",
                summary = "The distance all the way around.",
                steps = listOf(
                    Concept(
                        body = "Perimeter is the distance around the outside of a shape. Walk the edge and add up every side you cross.",
                        visual = LearnVisual.AREA_RECTANGLE,
                    ),
                    Concept(
                        body = "A rectangle has two pairs of equal sides, so add one length and one width, then double the result.",
                        formula = "P = 2 x (length + width)",
                        visual = LearnVisual.AREA_RECTANGLE,
                    ),
                    Worked(
                        problem = "A rectangle is 8 cm by 5 cm. What is its perimeter?",
                        lines = listOf(
                            "Add one length and one width: 8 + 5 = 13.",
                            "Every side has an equal partner, so double it: 13 x 2 = 26.",
                        ),
                        result = "Perimeter = 26 cm",
                    ),
                    Numeric(
                        question = "A square has sides of 9 cm. What is its perimeter in cm?",
                        answer = "36",
                        explanation = "Four equal sides: 4 x 9 = 36 cm.",
                    ),
                    Choice(
                        question = "Which of these has a perimeter of 20 cm?",
                        options = listOf("A 6 cm by 4 cm rectangle", "A 5 cm by 5 cm square", "Both of them", "Neither of them"),
                        correctIndex = 2,
                        explanation = "6 + 4 doubled is 20, and 4 x 5 is also 20. Same perimeter, very different shapes.",
                    ),
                    Concept(
                        body = "Perimeter is a length, so it is measured in cm, m or km - never in square units.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-area",
                title = "Area of rectangles",
                summary = "Count the squares - or multiply instead.",
                steps = listOf(
                    Concept(
                        body = "Area is how much surface a shape covers, counted in unit squares. A square 1 cm on each side covers one square centimetre.",
                        visual = LearnVisual.AREA_RECTANGLE,
                    ),
                    Concept(
                        body = "In a rectangle the squares line up in equal rows, so instead of counting them one by one you can multiply.",
                        formula = "A = length x width",
                        visual = LearnVisual.AREA_RECTANGLE,
                    ),
                    Numeric(
                        question = "A rug is 6 m long and 3 m wide. What is its area in square metres?",
                        answer = "18",
                        explanation = "6 x 3 = 18 square metres.",
                    ),
                    Choice(
                        question = "Which unit belongs to an area?",
                        options = listOf("cm", "square cm", "cubic cm", "ml"),
                        correctIndex = 1,
                        explanation = "Area counts squares, so the unit is squared.",
                    ),
                    Worked(
                        problem = "An L-shape is a 5 cm by 2 cm rectangle joined to a 3 cm by 2 cm rectangle. What is its area?",
                        lines = listOf(
                            "Split the shape into rectangles you already know how to handle.",
                            "First piece: 5 x 2 = 10 square cm.",
                            "Second piece: 3 x 2 = 6 square cm.",
                            "Add the pieces: 10 + 6.",
                        ),
                        result = "Area = 16 square cm",
                    ),
                    Choice(
                        question = "A rectangle has an area of 24 square cm and one side of 4 cm. How long is the other side?",
                        options = listOf("4 cm", "6 cm", "8 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = "Area divided by the known side: 24 / 4 = 6 cm.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many centimetres are in 3 metres?",
                options = listOf("30", "300", "3000", "0.03"),
                correctIndex = 1,
                explanation = "1 m = 100 cm, so 3 x 100 = 300.",
            ),
            QuizQuestion(
                prompt = "How many grams are in 2.5 kg?",
                options = listOf("250", "2500", "25", "25000"),
                correctIndex = 1,
                explanation = "1 kg = 1000 g, so 2.5 x 1000 = 2500.",
            ),
            QuizQuestion(
                prompt = "What is the perimeter of a 7 cm by 3 cm rectangle?",
                options = listOf("10 cm", "20 cm", "21 cm", "14 cm"),
                correctIndex = 1,
                explanation = "(7 + 3) x 2 = 20 cm.",
            ),
            QuizQuestion(
                prompt = "What is the area of a 7 cm by 3 cm rectangle?",
                options = listOf("10 square cm", "20 square cm", "21 square cm", "14 square cm"),
                correctIndex = 2,
                explanation = "7 x 3 = 21 square cm.",
            ),
            QuizQuestion(
                prompt = "A square has a perimeter of 32 cm. How long is one side?",
                options = listOf("4 cm", "6 cm", "8 cm", "16 cm"),
                correctIndex = 2,
                explanation = "32 / 4 = 8 cm.",
            ),
            QuizQuestion(
                prompt = "Which of these measurements is an area?",
                options = listOf("12 m", "12 square m", "12 ml", "12 kg"),
                correctIndex = 1,
                explanation = "Only the squared unit measures a surface.",
            ),
        ),
    )

    private val geometry = learnUnit(
        level = level,
        topic = MathTopic.GEOMETRY,
        summary = "Angles and turns, families of quadrilaterals, and symmetry.",
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-angles",
                title = "Angles and turns",
                summary = "Degrees measure turn, not length.",
                steps = listOf(
                    Concept(
                        body = "An angle measures turn. Two lines meeting at a corner make an angle, and the further one line has turned from the other, the bigger it is - however long the lines are.",
                        visual = LearnVisual.ANGLES,
                    ),
                    Concept(
                        body = "A full turn is 360 degrees, a half turn 180 and a quarter turn 90. A 90 degree angle is called a right angle and is marked with a small square.",
                        formula = "quarter turn = 90 degrees",
                        visual = LearnVisual.ANGLES,
                    ),
                    Choice(
                        question = "An angle smaller than a right angle is called...",
                        options = listOf("obtuse", "acute", "reflex", "straight"),
                        correctIndex = 1,
                        explanation = "Acute angles are under 90 degrees; obtuse ones sit between 90 and 180.",
                    ),
                    Numeric(
                        question = "Two angles sit on a straight line. One is 130 degrees. How many degrees is the other?",
                        answer = "50",
                        explanation = "Angles on a straight line add to 180, so 180 - 130 = 50.",
                    ),
                    Concept(
                        body = "The three angles inside any triangle always add up to 180 degrees, no matter what the triangle looks like.",
                        formula = "a + b + c = 180 degrees",
                        visual = LearnVisual.RIGHT_TRIANGLE,
                    ),
                    Choice(
                        question = "A triangle has angles of 40 and 75 degrees. What is the third angle?",
                        options = listOf("55 degrees", "65 degrees", "75 degrees", "105 degrees"),
                        correctIndex = 1,
                        explanation = "180 - 40 - 75 = 65.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-quadrilaterals",
                title = "Sorting quadrilaterals",
                summary = "Parallel sides and equal sides decide the name.",
                steps = listOf(
                    Concept(
                        body = "A quadrilateral is any flat shape with four straight sides. Squares, rectangles, rhombuses, parallelograms and trapeziums all belong to the family.",
                        visual = LearnVisual.SHAPES_2D,
                    ),
                    Concept(
                        body = "They are sorted by which sides are parallel and which sides or angles are equal. A parallelogram has two pairs of parallel sides; a trapezium has just one.",
                        visual = LearnVisual.SHAPES_2D,
                    ),
                    Choice(
                        question = "Which is always true of a rhombus?",
                        options = listOf("It has four right angles", "All four sides are equal", "It has exactly one pair of parallel sides", "It has three sides"),
                        correctIndex = 1,
                        explanation = "A rhombus is a pushed-over square: equal sides, but the angles need not be right angles.",
                    ),
                    Concept(
                        body = "Every square is also a rectangle (four right angles) and also a rhombus (four equal sides). Shape categories can sit inside one another.",
                    ),
                    Numeric(
                        question = "The angles inside any quadrilateral add up to how many degrees?",
                        answer = "360",
                        explanation = "Split it into two triangles: 180 + 180 = 360.",
                    ),
                    Choice(
                        question = "A quadrilateral has angles of 90, 90 and 100 degrees. What is the fourth?",
                        options = listOf("70 degrees", "80 degrees", "90 degrees", "100 degrees"),
                        correctIndex = 1,
                        explanation = "360 - 90 - 90 - 100 = 80.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-symmetry",
                title = "Symmetry",
                summary = "Fold lines and turns that leave a shape unchanged.",
                steps = listOf(
                    Concept(
                        body = "A shape has a line of symmetry if folding along that line makes the two halves land exactly on top of each other.",
                        visual = LearnVisual.SYMMETRY,
                    ),
                    Concept(
                        body = "Some shapes have several lines. A rectangle has 2, a square has 4, and a circle has more than you could ever count.",
                        visual = LearnVisual.SYMMETRY,
                    ),
                    Numeric(
                        question = "How many lines of symmetry does an equilateral triangle have?",
                        answer = "3",
                        explanation = "One from each corner to the middle of the opposite side.",
                    ),
                    Choice(
                        question = "How many lines of symmetry does a rectangle that is not a square have?",
                        options = listOf("0", "1", "2", "4"),
                        correctIndex = 2,
                        explanation = "One horizontal and one vertical. The diagonals do not work - the halves do not match.",
                        visual = LearnVisual.SYMMETRY,
                    ),
                    Concept(
                        body = "A shape has rotational symmetry if it looks the same after being turned less than a full circle. A square looks identical after every quarter turn.",
                    ),
                    Choice(
                        question = "Which capital letter has a vertical line of symmetry?",
                        options = listOf("F", "A", "R", "P"),
                        correctIndex = 1,
                        explanation = "Fold A down the middle and the two halves match exactly.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many degrees are in a right angle?",
                options = listOf("45", "90", "180", "360"),
                correctIndex = 1,
                explanation = "A right angle is a quarter of a full 360 degree turn.",
            ),
            QuizQuestion(
                prompt = "A triangle has angles of 90 and 35 degrees. What is the third?",
                options = listOf("45 degrees", "55 degrees", "65 degrees", "125 degrees"),
                correctIndex = 1,
                explanation = "180 - 90 - 35 = 55.",
            ),
            QuizQuestion(
                prompt = "Which shape has two pairs of parallel sides and four equal sides?",
                options = listOf("Trapezium", "Rhombus", "Kite", "Triangle"),
                correctIndex = 1,
                explanation = "That is exactly the definition of a rhombus.",
            ),
            QuizQuestion(
                prompt = "The angles of a quadrilateral add up to...",
                options = listOf("180 degrees", "270 degrees", "360 degrees", "540 degrees"),
                correctIndex = 2,
                explanation = "Two triangles' worth: 180 + 180.",
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry does a square have?",
                options = listOf("1", "2", "4", "8"),
                correctIndex = 2,
                explanation = "Two through the middles of opposite sides and two along the diagonals.",
            ),
            QuizQuestion(
                prompt = "An angle of 120 degrees is...",
                options = listOf("acute", "right", "obtuse", "reflex"),
                correctIndex = 2,
                explanation = "It sits between 90 and 180 degrees.",
            ),
        ),
    )

    private val data = learnUnit(
        level = level,
        topic = MathTopic.DATA,
        summary = "Reading graphs, collecting data and finding the mean.",
        lessons = listOf(
            LessonSpec(
                id = "g35-data-bar-graphs",
                title = "Reading bar graphs",
                summary = "Always check the scale first.",
                steps = listOf(
                    Concept(
                        body = "A bar graph turns counts into heights so you can compare them at a glance. Every bar starts from the same baseline and uses the same scale.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Concept(
                        body = "Read the scale before anything else. If each gridline stands for 2, a bar reaching the fourth line means 8, not 4.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Choice(
                        question = "Each gridline stands for 5 and a bar reaches the third gridline. What value is that?",
                        options = listOf("3", "5", "10", "15"),
                        correctIndex = 3,
                        explanation = "Three lines at 5 each: 3 x 5 = 15.",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Numeric(
                        question = "Four bars show 12, 8, 15 and 5 books read. How many books altogether?",
                        answer = "40",
                        explanation = "12 + 8 + 15 + 5 = 40.",
                    ),
                    Concept(
                        body = "The difference between two bars answers 'how many more'. Subtract the shorter one from the taller one.",
                    ),
                    Choice(
                        question = "The tallest bar shows 15 and the shortest shows 5. How many more does the tallest show?",
                        options = listOf("3", "5", "10", "20"),
                        correctIndex = 2,
                        explanation = "15 - 5 = 10.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-data-tally-line-plot",
                title = "Tally charts and pictograms",
                summary = "Recording data while it happens.",
                steps = listOf(
                    Concept(
                        body = "A tally chart records data as it arrives. Marks are grouped in fives - four uprights and one stroke across them - so a long list stays quick to count.",
                        visual = LearnVisual.PICTOGRAM,
                    ),
                    Concept(
                        body = "A pictogram uses one symbol for a fixed number of items. If one symbol stands for 4 people, three and a half symbols stand for 14.",
                        visual = LearnVisual.PICTOGRAM,
                    ),
                    Numeric(
                        question = "A tally shows three complete groups of five plus two extra marks. How many is that?",
                        answer = "17",
                        explanation = "3 x 5 = 15, plus the 2 loose marks.",
                    ),
                    Choice(
                        question = "On a pictogram one apple stands for 10 apples. What do two and a half apples mean?",
                        options = listOf("12", "20", "25", "50"),
                        correctIndex = 2,
                        explanation = "2 x 10 = 20, and half a symbol is 5 more.",
                        visual = LearnVisual.PICTOGRAM,
                    ),
                    Concept(
                        body = "A line plot stacks one dot per value above a number line, so repeated values pile up and the shape of the data becomes visible.",
                        visual = LearnVisual.NUMBER_LINE,
                    ),
                    Choice(
                        question = "Which is best for recording results while you count passing cars?",
                        options = listOf("Bar graph", "Tally chart", "Pie chart", "Mean"),
                        correctIndex = 1,
                        explanation = "Tallies are made to be written one mark at a time as things happen.",
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-data-mean",
                title = "The mean",
                summary = "The fair-share value of a set of numbers.",
                steps = listOf(
                    Concept(
                        body = "The mean is the fair-share value: pour all the data together, then split it evenly between the items.",
                        formula = "mean = total / how many",
                        visual = LearnVisual.BAR_CHART,
                    ),
                    Worked(
                        problem = "Find the mean of 4, 7, 6 and 3.",
                        lines = listOf(
                            "Add every value: 4 + 7 + 6 + 3 = 20.",
                            "Count how many values there are: 4.",
                            "Divide the total by the count: 20 / 4.",
                        ),
                        result = "Mean = 5",
                    ),
                    Numeric(
                        question = "Find the mean of 10, 12 and 14.",
                        answer = "12",
                        explanation = "36 / 3 = 12.",
                    ),
                    Choice(
                        question = "Five children have a mean of 6 sweets each. How many sweets are there altogether?",
                        options = listOf("11", "24", "30", "36"),
                        correctIndex = 2,
                        explanation = "Total = mean x how many = 6 x 5 = 30.",
                    ),
                    Concept(
                        body = "One extreme value drags the mean a long way. The mean of 2, 3 and 4 is 3, but the mean of 2, 3 and 40 is 15 - higher than two of the three values.",
                    ),
                    Choice(
                        question = "Which statement about the mean is true?",
                        options = listOf("It is always one of the values", "It can be a decimal", "It is always the largest value", "It only works for even counts"),
                        correctIndex = 1,
                        explanation = "The mean of 3 and 4 is 3.5, which is not in the data at all.",
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Each gridline stands for 2. A bar reaching the fifth gridline shows...",
                options = listOf("5", "7", "10", "12"),
                correctIndex = 2,
                explanation = "5 x 2 = 10.",
            ),
            QuizQuestion(
                prompt = "A tally has two full groups of five plus three marks. How many is that?",
                options = listOf("10", "12", "13", "15"),
                correctIndex = 2,
                explanation = "10 + 3 = 13.",
            ),
            QuizQuestion(
                prompt = "One symbol stands for 6 cars. How many cars do 4 symbols show?",
                options = listOf("10", "18", "24", "46"),
                correctIndex = 2,
                explanation = "4 x 6 = 24.",
            ),
            QuizQuestion(
                prompt = "What is the mean of 5, 9 and 10?",
                options = listOf("7", "8", "9", "24"),
                correctIndex = 1,
                explanation = "24 / 3 = 8.",
            ),
            QuizQuestion(
                prompt = "Four scores have a mean of 7. What is their total?",
                options = listOf("11", "21", "28", "74"),
                correctIndex = 2,
                explanation = "7 x 4 = 28.",
            ),
            QuizQuestion(
                prompt = "Which is best for recording data as it happens?",
                options = listOf("Pie chart", "Tally chart", "The mean", "Line graph"),
                correctIndex = 1,
                explanation = "You can add one mark at a time without redrawing anything.",
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(arithmetic, measurement, geometry, data)
}
