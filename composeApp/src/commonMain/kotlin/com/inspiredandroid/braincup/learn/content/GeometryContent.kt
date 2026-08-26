package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.Curve
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AngleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.CircleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.Fraction
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Polygon
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Solid
import com.inspiredandroid.braincup.learn.LearnVisual.Symmetry
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.learnUnit

/** Geometry: shapes and their names, then angles, Pythagoras, circles and formal proof. */
internal object GeometryContent {

    private val shapes = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "shapes",
        title = "Flat and solid shapes",
        summary = "Flat shapes, solid shapes, and splitting things into equal parts.",
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-flat-shapes",
                title = "Flat shapes",
                summary = "Count the sides to name the shape.",
                steps = listOf(
                    Concept(
                        body = "Shapes are named by how many straight sides they have.",
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = "Every side meets another at a corner, so the counts always match.",
                        visual = Polygon(sides = 5),
                    ),
                    Choice(
                        question = "How many sides does this shape have?",
                        options = listOf("4", "5", "6", "8"),
                        correctIndex = 1,
                        explanation = "Count the corners as they light up: five.",
                        visual = Polygon(sides = 5, reveal = false),
                    ),
                    Numeric(
                        question = "How many corners does this shape have?",
                        answer = "6",
                        explanation = "Six sides means six corners.",
                        visual = Polygon(sides = 6, reveal = false),
                    ),
                    Concept(
                        body = "A circle curves the whole way round: no sides, no corners.",
                        visual = CircleFigure(showRadius = false),
                    ),
                    Choice(
                        question = "Which is NOT true of every rectangle?",
                        options = listOf(
                            "It has four sides",
                            "It has four square corners",
                            "All four sides are equal",
                            "Opposite sides are equal",
                        ),
                        correctIndex = 2,
                        explanation = "Only squares have all four sides the same.",
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-solid-shapes",
                title = "Solid shapes",
                summary = "Faces, edges and corners you can hold.",
                steps = listOf(
                    Concept(
                        body = "Solid shapes take up space.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "Faces are the flat parts, edges are where two faces meet.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Choice(
                        question = "Which solid rolls but still has two flat circles?",
                        options = listOf("Sphere", "Cube", "Cylinder", "Cone"),
                        correctIndex = 2,
                        explanation = "It rolls on its curved side and stands on either circle.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Numeric(
                        question = "How many faces does this solid have?",
                        answer = "6",
                        explanation = "Top, bottom and four sides.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = "A cone rises from one circle to a single point.",
                        visual = Solid(kind = SolidKind.CONE, counts = true),
                    ),
                    Choice(
                        question = "A tin of soup is closest to which solid?",
                        options = listOf("Cube", "Cone", "Sphere", "Cylinder"),
                        correctIndex = 3,
                        explanation = "Two circular ends and a curved side.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g12-geometry-equal-parts",
                title = "Halves and quarters",
                summary = "Fair shares are equal shares.",
                steps = listOf(
                    Concept(
                        body = "A fraction splits something into equal parts.",
                        visual = Fraction(numerator = 1, denominator = 2),
                    ),
                    Concept(
                        body = "The more parts you cut, the smaller each one gets.",
                        formula = "1 half = 2 quarters",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 2 to 4),
                    ),
                    Choice(
                        question = "You eat 2 of the 4 equal pieces. How much is that?",
                        options = listOf("A quarter", "A half", "Three quarters", "All of it"),
                        correctIndex = 1,
                        explanation = "Two of four fills exactly half the bar.",
                        visual = Fraction(numerator = 2, denominator = 4, reveal = false),
                    ),
                    Numeric(
                        question = "A pizza is cut into 4 and you eat 1. How many slices are left?",
                        answer = "3",
                        explanation = "4 take away 1.",
                        visual = Fraction(numerator = 1, denominator = 4, reveal = false),
                    ),
                    Concept(
                        body = "Half of a half is a quarter.",
                        visual = Fraction(numerator = 1, denominator = 4),
                    ),
                    Choice(
                        question = "Which is the bigger share of the same cake?",
                        options = listOf("A quarter", "A half", "They are the same", "It depends"),
                        correctIndex = 1,
                        explanation = "One of two beats one of four.",
                        visual = Fraction(numerator = 1, denominator = 2, compare = 1 to 4, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many sides does this shape have?",
                options = listOf("4", "5", "6", "8"),
                correctIndex = 1,
                explanation = "A pentagon has five.",
                visual = Polygon(sides = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which shape has no corners at all?",
                options = listOf("Triangle", "Square", "Circle", "Hexagon"),
                correctIndex = 2,
                explanation = "One smooth curve, so no two sides ever meet.",
                visual = CircleFigure(showRadius = false),
            ),
            QuizQuestion(
                prompt = "How many edges does this solid have?",
                options = listOf("6", "8", "10", "12"),
                correctIndex = 3,
                explanation = "Four on top, four underneath and four uprights.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is this solid called?",
                options = listOf("Cylinder", "Cone", "Sphere", "Cube"),
                correctIndex = 1,
                explanation = "One circle rising to a point.",
                visual = Solid(kind = SolidKind.CONE, reveal = false),
            ),
            QuizQuestion(
                prompt = "How much of the bar is shaded?",
                options = listOf("A quarter", "A half", "A third", "The whole bar"),
                correctIndex = 1,
                explanation = "One of two equal parts.",
                visual = Fraction(numerator = 1, denominator = 2, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is NOT true of a square?",
                options = listOf("4 equal sides", "4 square corners", "4 corners", "Curved edges"),
                correctIndex = 3,
                explanation = "Every side of a square is straight.",
                visual = Polygon(sides = 4, reveal = false),
            ),
        ),
    )

    private val anglesAndSymmetry = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "angles-and-symmetry",
        title = "Angles, quadrilaterals and symmetry",
        summary = "Angles and turns, families of quadrilaterals, and symmetry.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-angles",
                title = "Angles and turns",
                summary = "Degrees measure turn, not length.",
                steps = listOf(
                    Concept(
                        body = "An angle measures turn. Longer arms do not make a bigger angle.",
                        visual = AngleFigure(degrees = 50),
                    ),
                    Concept(
                        body = "A quarter turn is 90 degrees: a right angle.",
                        formula = "full turn = 360 degrees",
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = "What kind of angle is this?",
                        options = listOf("obtuse", "acute", "reflex", "straight"),
                        correctIndex = 1,
                        explanation = "Acute angles are under 90 degrees.",
                        visual = AngleFigure(degrees = 40, reveal = false),
                    ),
                    Numeric(
                        question = "Angles on a straight line. How many degrees is the other one?",
                        answer = "50",
                        explanation = "180 - 130.",
                        visual = AngleFigure(degrees = 130, supplement = true, reveal = false),
                    ),
                    Concept(
                        body = "The three angles inside any triangle always add to 180.",
                        formula = "a + b + c = 180",
                        visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
                    ),
                    Choice(
                        question = "A triangle has angles of 40 and 75 degrees. What is the third?",
                        options = listOf("55", "65", "75", "105"),
                        correctIndex = 1,
                        explanation = "180 - 40 - 75.",
                        visual = RightTriangle(a = 5, b = 3, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-quadrilaterals",
                title = "Sorting quadrilaterals",
                summary = "Parallel and equal sides decide the name.",
                steps = listOf(
                    Concept(
                        body = "Any four straight sides makes a quadrilateral.",
                        visual = Polygon(sides = 4),
                    ),
                    Concept(
                        body = "A parallelogram has two pairs of parallel sides; a trapezium has one.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = "Which is always true of a rhombus?",
                        options = listOf(
                            "It has four right angles",
                            "All four sides are equal",
                            "It has one pair of parallel sides",
                            "It has three sides",
                        ),
                        correctIndex = 1,
                        explanation = "A rhombus is a pushed-over square: equal sides, any angles.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Concept(
                        body = "Every square is also a rectangle and also a rhombus.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Numeric(
                        question = "The angles inside a quadrilateral add to how many degrees?",
                        answer = "360",
                        explanation = "Split it into two triangles: 180 + 180.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Choice(
                        question = "Three angles are 90, 90 and 100. What is the fourth?",
                        options = listOf("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = "360 - 280.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-geometry-symmetry",
                title = "Symmetry",
                summary = "Fold lines that leave a shape unchanged.",
                steps = listOf(
                    Concept(
                        body = "Fold along a line of symmetry and the two halves land on each other.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Concept(
                        body = "A square has four fold lines. A circle has more than you could count.",
                        visual = Symmetry(sides = 4, lines = 4),
                    ),
                    Numeric(
                        question = "How many lines of symmetry does this triangle have?",
                        answer = "3",
                        explanation = "One from each corner to the opposite side.",
                        visual = Symmetry(sides = 3, lines = 3, reveal = false),
                    ),
                    Choice(
                        question = "How many lines of symmetry does this rectangle have?",
                        options = listOf("0", "1", "2", "4"),
                        correctIndex = 2,
                        explanation = "One horizontal, one vertical. The diagonals do not match up.",
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
                    ),
                    Concept(
                        body = "Rotational symmetry means it looks the same after part of a turn.",
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Choice(
                        question = "Which capital letter has a vertical line of symmetry?",
                        options = listOf("F", "A", "R", "P"),
                        correctIndex = 1,
                        explanation = "Fold A down the middle and the halves match.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many degrees is this angle?",
                options = listOf("45", "90", "180", "360"),
                correctIndex = 1,
                explanation = "A quarter of a full turn.",
                visual = AngleFigure(degrees = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = "A triangle has angles of 90 and 35. What is the third?",
                options = listOf("45", "55", "65", "125"),
                correctIndex = 1,
                explanation = "180 - 125.",
                visual = RightTriangle(a = 5, b = 3, labels = false),
            ),
            QuizQuestion(
                prompt = "Which shape has two pairs of parallel sides and four equal sides?",
                options = listOf("Trapezium", "Rhombus", "Kite", "Triangle"),
                correctIndex = 1,
                explanation = "That is the definition of a rhombus.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "The angles of a quadrilateral add up to...",
                options = listOf("180", "270", "360", "540"),
                correctIndex = 2,
                explanation = "Two triangles' worth.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry does this square have?",
                options = listOf("1", "2", "4", "8"),
                correctIndex = 2,
                explanation = "Two through the sides and two along the diagonals.",
                visual = Symmetry(sides = 4, lines = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "What kind of angle is this?",
                options = listOf("acute", "right", "obtuse", "reflex"),
                correctIndex = 2,
                explanation = "It sits between 90 and 180 degrees.",
                visual = AngleFigure(degrees = 120, reveal = false),
            ),
        ),
    )

    private val pythagorasAndCircles = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "pythagoras-and-circles",
        title = "Pythagoras, circles and volume",
        summary = "Pythagoras, circle measurements and the volume of prisms.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-pythagoras",
                title = "Pythagoras' theorem",
                summary = "Two squares that fill a third.",
                steps = listOf(
                    Concept(
                        body = "The longest side always faces the right angle.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "The squares on the two short sides hold exactly as much as the square on the long one.",
                        formula = "a² + b² = c²",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Worked(
                        problem = "Short sides 3 and 4.",
                        lines = listOf(
                            "3² = 9 and 4² = 16.",
                            "9 + 16 = 25.",
                            "Take the square root.",
                        ),
                        result = "c = 5",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = "How long is the hypotenuse?",
                        answer = "10",
                        explanation = "36 + 64 = 100.",
                        visual = RightTriangle(a = 8, b = 6, unknown = Side.HYPOTENUSE),
                    ),
                    Choice(
                        question = "A 13 m ladder stands 5 m from the wall. How high does it reach?",
                        options = listOf("8 m", "10 m", "12 m", "14 m"),
                        correctIndex = 2,
                        explanation = "169 - 25 = 144.",
                        visual = RightTriangle(a = 5, b = 12, unknown = Side.B),
                    ),
                    Concept(
                        body = "It works only for right-angled triangles - but also backwards.",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-circles",
                title = "Circles",
                summary = "Where pi comes from.",
                steps = listOf(
                    Concept(
                        body = "The diameter crosses the middle, so it is twice the radius.",
                        formula = "d = 2r",
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Concept(
                        body = "Roll a circle once and it covers pi diameters.",
                        formula = "C = pi x d",
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Numeric(
                        question = "The radius is 5 cm. What is the diameter?",
                        answer = "10",
                        explanation = "Twice the radius.",
                        visual = CircleFigure(radius = 5),
                    ),
                    Choice(
                        question = "A wheel of diameter 70 cm. How far does one turn take it?",
                        options = listOf("110 cm", "220 cm", "350 cm", "440 cm"),
                        correctIndex = 1,
                        explanation = "3.14 x 70.",
                        visual = CircleFigure(sweepCircumference = true),
                    ),
                    Concept(
                        body = "Area squares the radius, so doubling r gives four times the area.",
                        formula = "A = pi x r²",
                        visual = CircleFigure(radius = 10, fillArea = true),
                    ),
                    Choice(
                        question = "Radius 10 cm, pi as 3.14. What is the area?",
                        options = listOf("31.4", "62.8", "314", "628"),
                        correctIndex = 2,
                        explanation = "3.14 x 100.",
                        visual = CircleFigure(fillArea = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g68-geometry-volume",
                title = "Volume of prisms",
                summary = "Cross-section times length.",
                steps = listOf(
                    Concept(
                        body = "Volume counts unit cubes, so its units are cubed.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "A prism keeps the same cross-section the whole way along.",
                        formula = "V = base area x length",
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = "A box is 4 by 3 by 2 cm. What is its volume in cubic cm?",
                        answer = "24",
                        explanation = "4 x 3 x 2.",
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
                    ),
                    Choice(
                        question = "A cylinder has base radius r and height h. Which is its volume?",
                        options = listOf("2 x pi x r x h", "pi x r² x h", "pi x d x h", "r² + h"),
                        correctIndex = 1,
                        explanation = "The circle of area pi r², repeated up the height.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Worked(
                        problem = "A triangular prism, cross-section 6 cm², length 9 cm.",
                        lines = listOf(
                            "Same triangle all the way along.",
                            "Volume is cross-section x length.",
                            "6 x 9.",
                        ),
                        result = "V = 54 cm³",
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Choice(
                        question = "Double every side of a cube. The volume is multiplied by...",
                        options = listOf("2", "4", "6", "8"),
                        correctIndex = 3,
                        explanation = "All three dimensions double.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How long is the hypotenuse?",
                options = listOf("13", "14", "15", "17"),
                correctIndex = 0,
                explanation = "25 + 144 = 169.",
                visual = RightTriangle(a = 12, b = 5, unknown = Side.HYPOTENUSE),
            ),
            QuizQuestion(
                prompt = "The diameter is 18 cm. What is the radius?",
                options = listOf("6 cm", "9 cm", "18 cm", "36 cm"),
                correctIndex = 1,
                explanation = "Half the diameter.",
                visual = CircleFigure(showDiameter = true),
            ),
            QuizQuestion(
                prompt = "Circumference of a circle of diameter 10 cm, pi as 3.14?",
                options = listOf("15.7 cm", "31.4 cm", "78.5 cm", "314 cm"),
                correctIndex = 1,
                explanation = "pi x d.",
                visual = CircleFigure(sweepCircumference = true),
            ),
            QuizQuestion(
                prompt = "Area of a circle of radius 4 cm, pi as 3.14?",
                options = listOf("12.56", "25.12", "50.24", "100.48"),
                correctIndex = 2,
                explanation = "3.14 x 16.",
                visual = CircleFigure(fillArea = true),
            ),
            QuizQuestion(
                prompt = "What is the volume of a 5 by 4 by 3 cm box?",
                options = listOf("12", "20", "47", "60"),
                correctIndex = 3,
                explanation = "5 x 4 x 3.",
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which gives the volume of any prism?",
                options = listOf("perimeter x height", "cross-section x length", "pi x r²", "2 x pi x r"),
                correctIndex = 1,
                explanation = "The cross-section repeats unchanged.",
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
        ),
    )

    private val similarityAndProof = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "similarity-and-proof",
        title = "Similarity, transformations and proof",
        summary = "Similarity and scale factors, transformations and circle theorems.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-similarity",
                title = "Similar figures",
                summary = "Same shape, and what that does to area.",
                steps = listOf(
                    Concept(
                        body = "Similar shapes share every angle; the lengths all scale by one factor.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "Every length doubles, and the shape is unmistakably the same.",
                        formula = "new = k x old",
                        visual = RightTriangle(a = 8, b = 6),
                    ),
                    Choice(
                        question = "Two matching sides are 4 cm and 10 cm. What is the scale factor?",
                        options = listOf("0.4", "2.5", "6", "14"),
                        correctIndex = 1,
                        explanation = "10 / 4.",
                        visual = RightTriangle(a = 10, b = 6, labels = false),
                    ),
                    Numeric(
                        question = "This 3-4-5 triangle is enlarged by 3. How long is the longest side?",
                        answer = "15",
                        explanation = "5 x 3.",
                        visual = RightTriangle(a = 4, b = 3, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = "Area does not scale like length: double the sides and you get four times the squares.",
                        formula = "length x k, area x k², volume x k³",
                        visual = AreaGrid(cols = 4, rows = 4),
                    ),
                    Choice(
                        question = "A shape is enlarged by scale factor 4. Its area is multiplied by...",
                        options = listOf("4", "8", "12", "16"),
                        correctIndex = 3,
                        explanation = "Area scales by the square of the factor.",
                        visual = AreaGrid(cols = 4, rows = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-transformations",
                title = "Transformations",
                summary = "Slide, flip and turn on the grid.",
                steps = listOf(
                    Concept(
                        body = "Translations slide, reflections flip, rotations turn, enlargements resize.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = "A translation vector reads across, then up.",
                        formula = "(3, -2)",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(
                                PlotPoint(x = -1f, y = 1f, label = "A"),
                                PlotPoint(x = 2f, y = -1f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = "Translate this point by (1, -2). Where does it land?",
                        options = listOf("(3, -1)", "(3, 3)", "(1, -1)", "(2, -2)"),
                        correctIndex = 0,
                        explanation = "Add the vector to the coordinates.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)")),
                        ),
                    ),
                    Concept(
                        body = "Reflecting in the y-axis flips the sign of x only.",
                        formula = "(x, y) becomes (-x, y)",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(
                                PlotPoint(x = 2f, y = 2f, label = "A"),
                                PlotPoint(x = -2f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Numeric(
                        question = "Reflect this point in the x-axis. What is the new y-coordinate?",
                        answer = "-2",
                        explanation = "Reflecting in the x-axis negates y.",
                        visual = Plot(
                            curve = Curve.Linear(m = 0.5f),
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "A")),
                        ),
                    ),
                    Choice(
                        question = "Which transformation does NOT keep the size?",
                        options = listOf("translation", "reflection", "rotation", "enlargement"),
                        correctIndex = 3,
                        explanation = "The other three are congruence transformations.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "g910-geometry-circle-theorems",
                title = "Circle theorems",
                summary = "Angles at the centre and on the edge.",
                steps = listOf(
                    Concept(
                        body = "The angle at the centre is exactly twice the one at the circumference on the same arc.",
                        visual = CircleFigure(centreAngle = 80),
                    ),
                    Concept(
                        body = "Stretch the centre angle to a straight 180 and the edge angle becomes a right angle.",
                        visual = CircleFigure(centreAngle = 180),
                    ),
                    Choice(
                        question = "The centre angle is 80. What is the angle at the circumference?",
                        options = listOf("20", "40", "80", "160"),
                        correctIndex = 1,
                        explanation = "Half the centre angle.",
                        visual = CircleFigure(centreAngle = 80, reveal = false),
                    ),
                    Concept(
                        body = "Opposite angles in a cyclic quadrilateral add to 180.",
                        formula = "a + c = 180",
                        visual = Polygon(sides = 4, countCorners = false),
                    ),
                    Numeric(
                        question = "One angle of a cyclic quadrilateral is 110. How many degrees is the opposite one?",
                        answer = "70",
                        explanation = "180 - 110.",
                        visual = AngleFigure(degrees = 110, supplement = true, reveal = false),
                    ),
                    Choice(
                        question = "A tangent meets the radius at an angle of...",
                        options = listOf("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = "A tangent is always perpendicular to the radius it touches.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Two similar shapes have scale factor 3. Their areas differ by a factor of...",
                options = listOf("3", "6", "9", "27"),
                correctIndex = 2,
                explanation = "Area scales by the square.",
                visual = AreaGrid(cols = 3, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Translate this point by (2, -3). Where does it land?",
                options = listOf("(3, -1)", "(3, 5)", "(-1, -1)", "(2, 6)"),
                correctIndex = 0,
                explanation = "(1 + 2, 2 - 3).",
                visual = Plot(
                    curve = Curve.Linear(m = 0.5f),
                    points = listOf(PlotPoint(x = 1f, y = 2f, label = "(1, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "Reflect this point in the y-axis.",
                options = listOf("(-2, 2)", "(2, -2)", "(-2, -2)", "(2, 2)"),
                correctIndex = 0,
                explanation = "Only the sign of x flips.",
                visual = Plot(
                    curve = Curve.Linear(m = 0.5f),
                    points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "The centre angle is 120. What is the angle at the circumference?",
                options = listOf("30", "60", "120", "240"),
                correctIndex = 1,
                explanation = "Half of the centre angle.",
                visual = CircleFigure(centreAngle = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = "One angle of a cyclic quadrilateral is 95. Its opposite angle is...",
                options = listOf("85", "95", "105", "185"),
                correctIndex = 0,
                explanation = "Opposite angles sum to 180.",
                visual = AngleFigure(degrees = 95, supplement = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "An angle in a semicircle is always...",
                options = listOf("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = "Half of the straight angle at the centre.",
                visual = CircleFigure(centreAngle = 180, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(shapes, anglesAndSymmetry, pythagorasAndCircles, similarityAndProof)
}
