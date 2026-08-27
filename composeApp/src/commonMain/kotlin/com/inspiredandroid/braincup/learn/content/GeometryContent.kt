package com.inspiredandroid.braincup.learn.content

import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnVisual.AngleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.AreaGrid
import com.inspiredandroid.braincup.learn.LearnVisual.CircleFigure
import com.inspiredandroid.braincup.learn.LearnVisual.CyclicQuad
import com.inspiredandroid.braincup.learn.LearnVisual.Plot
import com.inspiredandroid.braincup.learn.LearnVisual.Polygon
import com.inspiredandroid.braincup.learn.LearnVisual.Quadrilateral
import com.inspiredandroid.braincup.learn.LearnVisual.RightTriangle
import com.inspiredandroid.braincup.learn.LearnVisual.Solid
import com.inspiredandroid.braincup.learn.LearnVisual.Symmetry
import com.inspiredandroid.braincup.learn.LearnVisual.Triangle
import com.inspiredandroid.braincup.learn.LessonSpec
import com.inspiredandroid.braincup.learn.LessonStep.Choice
import com.inspiredandroid.braincup.learn.LessonStep.Concept
import com.inspiredandroid.braincup.learn.LessonStep.Numeric
import com.inspiredandroid.braincup.learn.LessonStep.Worked
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.learn.PlotPoint
import com.inspiredandroid.braincup.learn.QuadKind
import com.inspiredandroid.braincup.learn.QuizQuestion
import com.inspiredandroid.braincup.learn.Side
import com.inspiredandroid.braincup.learn.SolidKind
import com.inspiredandroid.braincup.learn.TriKind
import com.inspiredandroid.braincup.learn.learnUnit

/** Geometry: shapes and their names, then angles, area, Pythagoras, circles and formal proof. */
internal object GeometryContent {

    private val flatShapes = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "flat-shapes",
        title = "Flat shapes",
        summary = "Counting sides and corners, and the names that follow from them.",
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-flat-shapes",
                title = "Counting the sides",
                summary = "Count the sides to name the shape.",
                steps = listOf(
                    Concept(
                        body = "Shapes are named by how many straight sides they have.",
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = "Every side meets another at a corner, so the two counts always match.",
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
                        body = "Three sides is a triangle, four a quadrilateral, five a pentagon, six a hexagon and eight an octagon.",
                        visual = Polygon(sides = 6),
                    ),
                    Choice(
                        question = "Which name belongs to this shape?",
                        options = listOf("Triangle", "Pentagon", "Hexagon", "Octagon"),
                        correctIndex = 3,
                        explanation = "Eight sides and eight corners.",
                        visual = Polygon(sides = 8, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-flat-shapes-curves",
                title = "Straight sides and curves",
                summary = "What happens when a shape has no corners at all.",
                steps = listOf(
                    Concept(
                        body = "A circle curves the whole way round: no sides, no corners, and nowhere for two edges to meet.",
                        visual = CircleFigure(showRadius = false),
                    ),
                    Concept(
                        body = "A shape made only of straight sides is called a polygon. A circle is not one, and neither is anything with a curved edge.",
                        visual = Polygon(sides = 4),
                    ),
                    Choice(
                        question = "Which of these has no corners at all?",
                        options = listOf("Triangle", "Square", "Circle", "Hexagon"),
                        correctIndex = 2,
                        explanation = "One smooth curve, so no two sides ever meet.",
                        visual = CircleFigure(showRadius = false, reveal = false),
                    ),
                    Numeric(
                        question = "How many corners does a circle have?",
                        answer = "0",
                        explanation = "There are no straight sides to meet, so there is nothing to count.",
                        visual = CircleFigure(showRadius = false, reveal = false),
                    ),
                    Concept(
                        body = "Sides do not have to be the same length. A long thin rectangle is still a four-sided shape.",
                        visual = AreaGrid(cols = 7, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = "Which of these is NOT true of every rectangle?",
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
                id = "geometry-flat-shapes-sorting",
                title = "Sorting shapes",
                summary = "Size and turning do not change what a shape is.",
                steps = listOf(
                    Concept(
                        body = "Shapes sort by their side count, whatever size they happen to be drawn.",
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = "Turning a shape round does not change what it is. A triangle standing on its point is still a triangle.",
                        visual = Polygon(sides = 3),
                    ),
                    Choice(
                        question = "Which shape has one more side than a pentagon?",
                        options = listOf("Square", "Triangle", "Hexagon", "Octagon"),
                        correctIndex = 2,
                        explanation = "A pentagon has five, so one more is six.",
                        visual = Polygon(sides = 6, reveal = false),
                    ),
                    Numeric(
                        question = "A shape has 3 corners. How many sides has it?",
                        answer = "3",
                        explanation = "Sides and corners always come in matching pairs.",
                        visual = Polygon(sides = 3, reveal = false),
                    ),
                    Concept(
                        body = "A square is a rectangle that happens to have all four sides the same, so it belongs in both piles at once.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = "Which of these is NOT true of a square?",
                        options = listOf("4 equal sides", "4 square corners", "4 corners", "Curved edges"),
                        correctIndex = 3,
                        explanation = "Every side of a square is straight.",
                        visual = Polygon(sides = 4, reveal = false),
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
                prompt = "What is this shape called?",
                options = listOf("Pentagon", "Hexagon", "Octagon", "Square"),
                correctIndex = 1,
                explanation = "Six sides and six corners.",
                visual = Polygon(sides = 6, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which shape has no corners at all?",
                options = listOf("Triangle", "Square", "Circle", "Hexagon"),
                correctIndex = 2,
                explanation = "One smooth curve, so no two sides ever meet.",
                visual = CircleFigure(showRadius = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many corners has a shape with 8 sides?",
                options = listOf("4", "6", "8", "16"),
                correctIndex = 2,
                explanation = "Sides and corners always match.",
                visual = Polygon(sides = 8, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is NOT true of a square?",
                options = listOf("4 equal sides", "4 square corners", "4 corners", "Curved edges"),
                correctIndex = 3,
                explanation = "Every side of a square is straight.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is true of every rectangle?",
                options = listOf(
                    "All four sides are equal",
                    "Opposite sides are equal",
                    "It has three corners",
                    "It has a curved edge",
                ),
                correctIndex = 1,
                explanation = "The equal sides face each other. Only a square has all four the same.",
                visual = AreaGrid(cols = 5, rows = 3, showArea = false, reveal = false),
            ),
        ),
    )

    private val solidShapes = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "solid-shapes",
        title = "Solid shapes",
        summary = "Faces, edges and corners on shapes you can hold.",
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-solid-shapes",
                title = "Shapes you can hold",
                summary = "Faces, edges and corners.",
                steps = listOf(
                    Concept(
                        body = "Solid shapes take up space, so they have a front and a back as well as a top.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "Faces are the flat parts, edges are where two faces meet, and corners are where the edges do.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
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
                        question = "Which solid rolls but still has two flat circles?",
                        options = listOf("Sphere", "Cube", "Cylinder", "Cone"),
                        correctIndex = 2,
                        explanation = "It rolls on its curved side and stands on either circle.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Choice(
                        question = "What is this solid called?",
                        options = listOf("Cylinder", "Cone", "Sphere", "Cube"),
                        correctIndex = 1,
                        explanation = "One circle rising to a point.",
                        visual = Solid(kind = SolidKind.CONE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-solid-shapes-counting",
                title = "Counting faces and edges",
                summary = "Work round the solid so nothing gets counted twice.",
                steps = listOf(
                    Concept(
                        body = "Counting goes wrong when you jump about. Work round the solid instead: the top, then the bottom, then the sides.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Numeric(
                        question = "How many edges does this solid have?",
                        answer = "12",
                        explanation = "Four on top, four underneath and four uprights.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = "A sphere has one curved surface and nothing else: no flat faces, no edges and no corners.",
                        visual = Solid(kind = SolidKind.SPHERE, counts = true),
                    ),
                    Choice(
                        question = "How many corners has a sphere?",
                        options = listOf("0", "1", "2", "4"),
                        correctIndex = 0,
                        explanation = "There are no edges to meet, so there is nothing to count.",
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                    Concept(
                        body = "A prism keeps the same shape all the way along, so both of its ends match.",
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Choice(
                        question = "Which of these has no flat face at all?",
                        options = listOf("Cube", "Cylinder", "Cone", "Sphere"),
                        correctIndex = 3,
                        explanation = "A cylinder has two circles and a cone has one. A sphere has none.",
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-solid-shapes-around",
                title = "Solids around you",
                summary = "Everyday things, once you look past what they are for.",
                steps = listOf(
                    Concept(
                        body = "Real objects are solids too, once you look past what they are for.",
                        visual = Solid(kind = SolidKind.CYLINDER),
                    ),
                    Choice(
                        question = "A tin of soup is closest to which solid?",
                        options = listOf("Cube", "Cone", "Sphere", "Cylinder"),
                        correctIndex = 3,
                        explanation = "Two circular ends and a curved side.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Concept(
                        body = "A dice is a cube: six square faces, every one the same size.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Choice(
                        question = "A football is closest to which solid?",
                        options = listOf("Cylinder", "Sphere", "Cube", "Prism"),
                        correctIndex = 1,
                        explanation = "It rolls in every direction, which nothing with a flat face can do.",
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                    Concept(
                        body = "A party hat is a cone, and a tent with matching ends is a prism.",
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Numeric(
                        question = "How many flat faces has a cylinder?",
                        answer = "2",
                        explanation = "The two circles at its ends. The curved part in between is not flat.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
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
                prompt = "How many faces has a cube?",
                options = listOf("4", "6", "8", "12"),
                correctIndex = 1,
                explanation = "Top, bottom and four sides.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which solid has no corners at all?",
                options = listOf("Cube", "Cone", "Sphere", "Prism"),
                correctIndex = 2,
                explanation = "No edges means nothing for them to meet at.",
                visual = Solid(kind = SolidKind.SPHERE, reveal = false),
            ),
            QuizQuestion(
                prompt = "A tin of soup is closest to which solid?",
                options = listOf("Cube", "Cone", "Sphere", "Cylinder"),
                correctIndex = 3,
                explanation = "Two circular ends and a curved side.",
                visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is a flat shape rather than a solid?",
                options = listOf("Cube", "Circle", "Sphere", "Cylinder"),
                correctIndex = 1,
                explanation = "A circle is drawn on the page. A sphere is the ball it would become.",
                visual = CircleFigure(showRadius = false, reveal = false),
            ),
        ),
    )

    private val angles = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "angles",
        title = "Angles and turns",
        summary = "Degrees measure turn, and the turns around a point always add up.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-angles",
                title = "Measuring a turn",
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
                    Concept(
                        body = "Under 90 is acute and over 90 is obtuse. The right angle in between is the one everything else is measured against.",
                        visual = AngleFigure(degrees = 120),
                    ),
                    Choice(
                        question = "What kind of angle is this?",
                        options = listOf("acute", "right", "obtuse", "reflex"),
                        correctIndex = 2,
                        explanation = "It has opened past the right angle but not as far as a straight line.",
                        visual = AngleFigure(degrees = 120, reveal = false),
                    ),
                    Numeric(
                        question = "How many degrees is a right angle?",
                        answer = "90",
                        explanation = "A quarter of the full 360.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-angles-adding",
                title = "Angles that add up",
                summary = "On a line, round a point, and inside a triangle.",
                steps = listOf(
                    Concept(
                        body = "Angles on a straight line make a half turn between them, so they add to 180.",
                        formula = "a + b = 180",
                        visual = AngleFigure(degrees = 130, supplement = true),
                    ),
                    Numeric(
                        question = "Angles on a straight line. How many degrees is the other one?",
                        answer = "50",
                        explanation = "180 - 130.",
                        visual = AngleFigure(degrees = 130, supplement = true, reveal = false),
                    ),
                    Choice(
                        question = "Two angles on a straight line, and one of them is 65 degrees.",
                        formula = "65 + ? = 180",
                        options = listOf("25", "115", "125", "295"),
                        correctIndex = 1,
                        explanation = "180 take away 65.",
                        visual = AngleFigure(degrees = 65, supplement = true, reveal = false),
                    ),
                    Concept(
                        body = "Angles all the way round a point make a full turn, so those add to 360 instead.",
                        formula = "a + b + c = 360",
                        visual = AngleFigure(degrees = 100),
                    ),
                    Concept(
                        body = "The three angles inside any triangle always add to 180, whatever shape it is pulled into.",
                        formula = "a + b + c = 180",
                        visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
                    ),
                    Choice(
                        question = "A triangle has angles of 40 and 75 degrees. What is the third?",
                        options = listOf("55", "65", "75", "105"),
                        correctIndex = 1,
                        explanation = "180 - 40 - 75.",
                        visual = Triangle(kind = TriKind.SCALENE),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-angles-turns",
                title = "Turning all the way round",
                summary = "Quarter, half and full turns, and what is left over.",
                steps = listOf(
                    Concept(
                        body = "A full turn brings you back where you started. That is four right angles, or 360 degrees.",
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = "How many right angles make a full turn?",
                        options = listOf("2", "3", "4", "6"),
                        correctIndex = 2,
                        explanation = "Four quarter turns, and 4 x 90 = 360.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = "Half a turn is two right angles laid end to end, which is why 180 keeps appearing.",
                        visual = AngleFigure(degrees = 90, supplement = true),
                    ),
                    Numeric(
                        question = "How many degrees are in three quarters of a turn?",
                        answer = "270",
                        explanation = "Three lots of 90.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = "Every angle has a reflex partner: whatever is left of the full turn if you go round the other way. This 170 leaves 190 behind it.",
                        formula = "360 - {b:170} = 190",
                        visual = AngleFigure(degrees = 170),
                    ),
                    Choice(
                        question = "Going round the other way, how much of the turn is left?",
                        formula = "360 - {b:170} = ?",
                        options = listOf("10", "100", "190", "350"),
                        correctIndex = 2,
                        explanation = "The two together have to make one whole turn.",
                        visual = AngleFigure(degrees = 170, reveal = false),
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
                prompt = "What kind of angle is this?",
                options = listOf("acute", "right", "obtuse", "reflex"),
                correctIndex = 2,
                explanation = "It sits between 90 and 180 degrees.",
                visual = AngleFigure(degrees = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = "Two angles sit on a straight line and one is 110 degrees. What is the other?",
                options = listOf("70", "80", "90", "250"),
                correctIndex = 0,
                explanation = "180 take away 110.",
                visual = AngleFigure(degrees = 110, supplement = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "A triangle has angles of 90 and 35. What is the third?",
                options = listOf("45", "55", "65", "125"),
                correctIndex = 1,
                explanation = "180 - 125.",
                visual = RightTriangle(a = 7, b = 5, labels = false),
            ),
            QuizQuestion(
                prompt = "Angles all the way round a point add up to...",
                options = listOf("90", "180", "270", "360"),
                correctIndex = 3,
                explanation = "One full turn.",
                visual = AngleFigure(degrees = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = "An angle is 150 degrees. Going round the other way, how much of the turn is left?",
                options = listOf("30", "110", "210", "310"),
                correctIndex = 2,
                explanation = "360 take away 150.",
                visual = AngleFigure(degrees = 150, reveal = false),
            ),
        ),
    )

    private val quadrilaterals = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "quadrilaterals",
        title = "Triangles and quadrilaterals",
        summary = "Sorting shapes by their sides and angles, and the names that follow.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-quadrilaterals",
                title = "Four-sided shapes",
                summary = "Parallel and equal sides decide the name.",
                steps = listOf(
                    Concept(
                        body = "Any four straight sides makes a quadrilateral.",
                        visual = Polygon(sides = 4),
                    ),
                    Concept(
                        body = "A parallelogram has two pairs of parallel sides; a trapezium has one.",
                        visual = Quadrilateral(kind = QuadKind.PARALLELOGRAM),
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
                        visual = Quadrilateral(kind = QuadKind.RHOMBUS),
                    ),
                    Concept(
                        body = "The angles inside a quadrilateral always add to 360, because it splits into two triangles of 180 each.",
                        formula = "180 + {b:180} = 360",
                        visual = Polygon(sides = 4),
                    ),
                    Numeric(
                        question = "The angles inside a quadrilateral add to how many degrees?",
                        answer = "360",
                        explanation = "Split it into two triangles: 180 + 180.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Choice(
                        question = "Three angles are 90, 90 and 100. What is the fourth?",
                        formula = "90 + 90 + 100 + ? = 360",
                        options = listOf("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = "360 - 280.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-quadrilaterals-triangles",
                title = "Sorting triangles",
                summary = "By their sides, and by their largest angle.",
                steps = listOf(
                    Concept(
                        body = "Triangles sort by their sides. Three equal sides is equilateral, two is isosceles, and none at all is scalene.",
                        visual = Triangle(kind = TriKind.SCALENE),
                    ),
                    Concept(
                        body = "They sort by their largest angle as well. A right triangle has one square corner, and no triangle can have two.",
                        visual = RightTriangle(a = 4, b = 3, labels = false),
                    ),
                    Choice(
                        question = "A triangle has exactly two equal sides. What is it called?",
                        options = listOf("Equilateral", "Isosceles", "Scalene", "Right"),
                        correctIndex = 1,
                        explanation = "Two equal sides, and the two angles facing them match as well.",
                        visual = Triangle(kind = TriKind.ISOSCELES),
                    ),
                    Concept(
                        body = "An equilateral triangle has three equal angles to go with its three equal sides, and 180 shared three ways leaves 60 each.",
                        formula = "180 / {b:3} = 60",
                        visual = Symmetry(sides = 3, lines = 3),
                    ),
                    Numeric(
                        question = "How many degrees is each angle of an equilateral triangle?",
                        answer = "60",
                        explanation = "180 shared equally between three.",
                        visual = Triangle(kind = TriKind.EQUILATERAL),
                    ),
                    Choice(
                        question = "Can a triangle have two right angles?",
                        options = listOf(
                            "Yes",
                            "No",
                            "Only if it is isosceles",
                            "Only if it is drawn large enough",
                        ),
                        correctIndex = 1,
                        explanation = "Two right angles already use up the whole 180, leaving nothing for the third.",
                        visual = RightTriangle(a = 4, b = 4, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-quadrilaterals-family",
                title = "The family tree",
                summary = "One shape can wear several names at once.",
                steps = listOf(
                    Concept(
                        body = "The quadrilateral names sit inside one another rather than ruling each other out. Every square is also a rectangle and also a rhombus.",
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = "Which shape has two pairs of parallel sides and four equal sides?",
                        options = listOf("Trapezium", "Rhombus", "Kite", "Triangle"),
                        correctIndex = 1,
                        explanation = "That is the definition of a rhombus, and a square is the special case with right angles.",
                        visual = Quadrilateral(kind = QuadKind.RHOMBUS),
                    ),
                    Concept(
                        body = "A kite has two pairs of equal sides too, but they sit next to each other rather than opposite, which is what keeps it out of the family.",
                        visual = Quadrilateral(kind = QuadKind.KITE),
                    ),
                    Choice(
                        question = "Is every rectangle a parallelogram?",
                        options = listOf(
                            "Yes, both pairs of sides are parallel",
                            "No, a parallelogram is never square",
                            "Only if it is a square",
                            "Only if all four sides are equal",
                        ),
                        correctIndex = 0,
                        explanation = "A parallelogram asks only for two pairs of parallel sides, and a rectangle has them.",
                        visual = Quadrilateral(kind = QuadKind.RECTANGLE),
                    ),
                    Concept(
                        body = "A trapezium has only one pair of parallel sides, and that single missing pair is what leaves it outside the parallelogram family.",
                        visual = Quadrilateral(kind = QuadKind.TRAPEZIUM),
                    ),
                    Numeric(
                        question = "How many pairs of parallel sides has a parallelogram?",
                        answer = "2",
                        explanation = "Both pairs of opposite sides run parallel.",
                        visual = Quadrilateral(kind = QuadKind.PARALLELOGRAM),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Which shape has two pairs of parallel sides and four equal sides?",
                options = listOf("Trapezium", "Rhombus", "Kite", "Triangle"),
                correctIndex = 1,
                explanation = "That is the definition of a rhombus.",
                visual = Quadrilateral(kind = QuadKind.RHOMBUS),
            ),
            QuizQuestion(
                prompt = "The angles of a quadrilateral add up to...",
                options = listOf("180", "270", "360", "540"),
                correctIndex = 2,
                explanation = "Two triangles' worth.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "A triangle with three equal sides is called...",
                options = listOf("Scalene", "Isosceles", "Equilateral", "Right"),
                correctIndex = 2,
                explanation = "Equal sides all round, and three 60 degree angles with them.",
                visual = Polygon(sides = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "Three angles of a quadrilateral are 80, 100 and 110. What is the fourth?",
                options = listOf("60", "70", "80", "90"),
                correctIndex = 1,
                explanation = "360 take away 290.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these is always true of a square?",
                options = listOf(
                    "It is a rectangle and a rhombus",
                    "It is a trapezium and nothing else",
                    "It has one pair of parallel sides",
                    "It has three right angles",
                ),
                correctIndex = 0,
                explanation = "It meets both definitions at once, which is what the family tree is for.",
                visual = AreaGrid(cols = 4, rows = 4, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "Each angle of an equilateral triangle is...",
                options = listOf("45 degrees", "60 degrees", "90 degrees", "120 degrees"),
                correctIndex = 1,
                explanation = "180 shared equally between three.",
                visual = Polygon(sides = 3, reveal = false),
            ),
        ),
    )

    private val symmetry = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "symmetry",
        title = "Symmetry",
        summary = "Fold lines, turning symmetry, and where both show up.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-symmetry",
                title = "Fold lines",
                summary = "Lines that leave a shape unchanged.",
                steps = listOf(
                    Concept(
                        body = "Fold along a line of symmetry and the two halves land exactly on each other.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Concept(
                        body = "A square has four fold lines: two through the sides and two along the diagonals.",
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
                        body = "A regular shape has one fold line for every side it has, which makes them easy to count.",
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Choice(
                        question = "How many lines of symmetry has a regular pentagon?",
                        options = listOf("3", "4", "5", "10"),
                        correctIndex = 2,
                        explanation = "One for each of its five sides.",
                        visual = Symmetry(sides = 5, lines = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-symmetry-rotational",
                title = "Turning symmetry",
                summary = "Matching part way through a turn, without folding.",
                steps = listOf(
                    Concept(
                        body = "Rotational symmetry means a shape looks the same part way through a turn, with no folding involved at all.",
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Concept(
                        body = "The order is how many times it matches during one full turn. A square matches four times.",
                        visual = Symmetry(sides = 4, lines = 4),
                    ),
                    Choice(
                        question = "What is the order of rotational symmetry of an equilateral triangle?",
                        options = listOf("1", "2", "3", "6"),
                        correctIndex = 2,
                        explanation = "It drops back onto itself every third of a turn.",
                        visual = Symmetry(sides = 3, lines = 3, reveal = false),
                    ),
                    Concept(
                        body = "The two kinds do not have to arrive together. A rectangle matches after half a turn and also folds two ways, but plenty of shapes have one without the other.",
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true),
                    ),
                    Numeric(
                        question = "What is the order of rotational symmetry of a regular hexagon?",
                        answer = "6",
                        explanation = "One match for every side, the same as its fold lines.",
                        visual = Symmetry(sides = 6, lines = 6, reveal = false),
                    ),
                    Choice(
                        question = "For a regular shape, the order of rotational symmetry is...",
                        options = listOf(
                            "always 1",
                            "the number of its sides",
                            "half the number of its sides",
                            "twice the number of its sides",
                        ),
                        correctIndex = 1,
                        explanation = "Every side can take the place of the one before it.",
                        visual = Symmetry(sides = 5, lines = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-symmetry-around",
                title = "Symmetry you can see",
                summary = "Letters and everyday shapes.",
                steps = listOf(
                    Concept(
                        body = "Letters, leaves and faces all carry symmetry, and it is a large part of why they look right.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Choice(
                        question = "Which capital letter has a vertical line of symmetry?",
                        options = listOf("F", "A", "R", "P"),
                        correctIndex = 1,
                        explanation = "Fold A down the middle and the halves match.",
                        visual = Symmetry(sides = 3, lines = 1, reveal = false),
                    ),
                    Concept(
                        body = "Some shapes fold one way but not the other. An isosceles triangle has exactly one line, straight down from its odd corner.",
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Choice(
                        question = "Which capital letter has a horizontal line of symmetry?",
                        options = listOf("L", "N", "E", "S"),
                        correctIndex = 2,
                        explanation = "Fold E across the middle and the top arm lands on the bottom one.",
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
                    ),
                    Concept(
                        body = "A shape can have turning symmetry with no fold line at all. S and N both match after half a turn, but neither will fold onto itself.",
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Numeric(
                        question = "How many lines of symmetry has an isosceles triangle?",
                        answer = "1",
                        explanation = "Only the line through the corner between its two equal sides.",
                        visual = Symmetry(sides = 3, lines = 1, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "How many lines of symmetry does this square have?",
                options = listOf("1", "2", "4", "8"),
                correctIndex = 2,
                explanation = "Two through the sides and two along the diagonals.",
                visual = Symmetry(sides = 4, lines = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry has this rectangle?",
                options = listOf("0", "1", "2", "4"),
                correctIndex = 2,
                explanation = "One horizontal and one vertical. The diagonals do not match up.",
                visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry has an equilateral triangle?",
                options = listOf("1", "2", "3", "6"),
                correctIndex = 2,
                explanation = "One from each corner to the opposite side.",
                visual = Symmetry(sides = 3, lines = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the order of rotational symmetry of a regular pentagon?",
                options = listOf("3", "4", "5", "10"),
                correctIndex = 2,
                explanation = "One match for every side.",
                visual = Symmetry(sides = 5, lines = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which capital letter has a vertical line of symmetry?",
                options = listOf("F", "A", "R", "P"),
                correctIndex = 1,
                explanation = "Fold A down the middle and the halves match.",
                visual = Symmetry(sides = 3, lines = 1, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many lines of symmetry has this shape?",
                options = listOf("3", "4", "6", "12"),
                correctIndex = 2,
                explanation = "A regular hexagon has one for each of its six sides.",
                visual = Symmetry(sides = 6, lines = 6, reveal = false),
            ),
        ),
    )

    private val perimeterAndArea = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "perimeter-and-area",
        title = "Perimeter and area",
        summary = "The walk around the outside, and the squares that fill the inside.",
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-measurement-perimeter",
                title = "Perimeter",
                summary = "The walk all the way around.",
                steps = listOf(
                    Concept(
                        body = "Perimeter is the distance around the outside.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false, showPerimeter = true),
                    ),
                    Concept(
                        body = "Opposite sides match, so add one of each and double it.",
                        formula = "P = 2 x (length + width)",
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Worked(
                        problem = "A rectangle is 8 cm by 5 cm.",
                        lines = listOf(
                            "8 + 5 = 13.",
                            "Every side has an equal partner.",
                            "13 x 2 = 26.",
                        ),
                        result = "Perimeter = 26 cm",
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Numeric(
                        question = "What is the perimeter of this square, in cm?",
                        formula = "4 x {b:9} = ?",
                        answer = "36",
                        explanation = "Four sides of 9.",
                        visual = AreaGrid(cols = 9, rows = 9, showArea = false, showPerimeter = true, reveal = false),
                    ),
                    Choice(
                        question = "Which shape has a perimeter of 20 cm?",
                        options = listOf("A 6 by 4 rectangle", "A 5 by 5 square", "Both of them", "Neither"),
                        correctIndex = 2,
                        explanation = "6 + 4 doubled is 20, and 4 x 5 is 20 as well.",
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false, reveal = false),
                    ),
                    Concept(
                        body = "Perimeter is a length, so it is never measured in square units.",
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, showPerimeter = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-area",
                title = "Area of rectangles",
                summary = "Count the squares, or multiply instead.",
                steps = listOf(
                    Concept(
                        body = "Area counts the unit squares that cover a shape.",
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Concept(
                        body = "The squares sit in equal rows, so multiply instead of counting.",
                        formula = "A = length x width",
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Numeric(
                        question = "How many squares cover this rug?",
                        formula = "6 x {b:3} = ?",
                        answer = "18",
                        explanation = "3 rows of 6.",
                        visual = AreaGrid(cols = 6, rows = 3, reveal = false),
                    ),
                    Choice(
                        question = "Which unit belongs to an area?",
                        options = listOf("cm", "square cm", "cubic cm", "ml"),
                        correctIndex = 1,
                        explanation = "Area counts squares, so the unit is squared.",
                        visual = AreaGrid(cols = 4, rows = 4, reveal = false),
                    ),
                    Concept(
                        body = "A square metre is a square one metre along each side, not a hundred square centimetres.",
                        visual = AreaGrid(cols = 4, rows = 4, unit = "m"),
                    ),
                    Choice(
                        question = "The area is 24 square cm and one side is 4 cm. How long is the other?",
                        formula = "24 / {b:4} = ?",
                        options = listOf("4 cm", "6 cm", "8 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = "24 divided by 4.",
                        visual = AreaGrid(cols = 6, rows = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-area-compound",
                title = "Shapes made of rectangles",
                summary = "Split it up, work out each piece, add them back.",
                steps = listOf(
                    Concept(
                        body = "A compound shape is rectangles joined together, so no new formula is needed.",
                        visual = AreaGrid(cols = 7, rows = 4),
                    ),
                    Concept(
                        body = "Split it into rectangles you know, find each area, then add.",
                        formula = "A = A1 + A2",
                        visual = AreaGrid(cols = 4, rows = 3),
                    ),
                    Worked(
                        problem = "An L-shape: a 5 by 2 rectangle joined to a 3 by 2 one.",
                        lines = listOf(
                            "Split it into rectangles you know.",
                            "5 x 2 = 10.",
                            "3 x 2 = 6.",
                            "10 + 6.",
                        ),
                        result = "Area = 16 square cm",
                        visual = AreaGrid(cols = 5, rows = 2),
                    ),
                    Numeric(
                        question = "A 5 by 4 rectangle has a 2 by 2 corner cut out. What area is left, in square cm?",
                        formula = "5 x 4 - 2 x 2 = ?",
                        answer = "16",
                        explanation = "5 x 4 = 20, and the missing corner takes 2 x 2 = 4 away.",
                        visual = AreaGrid(cols = 5, rows = 4, reveal = false),
                    ),
                    Choice(
                        question = "A 3 by 2 rectangle and a 4 by 2 rectangle are joined edge to edge. What is the total area?",
                        formula = "3 x 2 + 4 x 2 = ?",
                        options = listOf("12 square cm", "14 square cm", "16 square cm", "20 square cm"),
                        correctIndex = 1,
                        explanation = "6 squares and 8 squares.",
                        visual = AreaGrid(cols = 4, rows = 2, reveal = false),
                    ),
                    Concept(
                        body = "Split the shape whichever way is easiest: the total comes out the same either way.",
                        visual = AreaGrid(cols = 6, rows = 4),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "What is the perimeter of this rectangle?",
                options = listOf("10 cm", "20 cm", "21 cm", "14 cm"),
                correctIndex = 1,
                explanation = "(7 + 3) doubled.",
                visual = AreaGrid(cols = 7, rows = 3, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the area of this rectangle?",
                options = listOf("10 square cm", "20 square cm", "21 square cm", "14 square cm"),
                correctIndex = 2,
                explanation = "3 rows of 7.",
                visual = AreaGrid(cols = 7, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "A square has a perimeter of 32 cm. How long is one side?",
                options = listOf("4 cm", "6 cm", "8 cm", "16 cm"),
                correctIndex = 2,
                explanation = "32 divided by 4.",
                visual = AreaGrid(cols = 8, rows = 8, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which measurement is an area?",
                options = listOf("12 m", "12 square m", "12 ml", "12 kg"),
                correctIndex = 1,
                explanation = "Only the squared unit measures a surface.",
                visual = AreaGrid(cols = 4, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = "What is the perimeter of a 9 cm by 2 cm rectangle?",
                options = listOf("11 cm", "18 cm", "20 cm", "22 cm"),
                correctIndex = 3,
                explanation = "(9 + 2) doubled.",
                visual = AreaGrid(cols = 9, rows = 2, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "An L-shape is a 4 by 3 rectangle with a 2 by 2 square added. What is its area?",
                options = listOf("12 square cm", "14 square cm", "16 square cm", "18 square cm"),
                correctIndex = 2,
                explanation = "12 squares and 4 squares.",
                visual = AreaGrid(cols = 4, rows = 3, reveal = false),
            ),
        ),
    )

    private val pythagoras = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "pythagoras",
        title = "Pythagoras' theorem",
        summary = "Two squares that fill a third, and the sides it lets you find.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-pythagoras",
                title = "Two squares that fill a third",
                summary = "Where the theorem comes from.",
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
                    Concept(
                        body = "It works only for right-angled triangles. Take the right angle away and the two squares stop fitting.",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Choice(
                        question = "Which of these could be the three sides of a right triangle?",
                        options = listOf("2, 3, 4", "5, 12, 13", "4, 5, 6", "1, 2, 3"),
                        correctIndex = 1,
                        explanation = "25 + 144 = 169, and 169 is 13 squared. None of the others add up.",
                        visual = RightTriangle(a = 12, b = 5, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-pythagoras-finding",
                title = "Finding a shorter side",
                summary = "Take the small square away from the big one.",
                steps = listOf(
                    Concept(
                        body = "Turned around, the theorem finds a short side instead. Take the small square away from the big one rather than adding the two together.",
                        formula = "a² = c² - b²",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = "How long is the missing side?",
                        answer = "8",
                        explanation = "100 - 36 = 64.",
                        visual = RightTriangle(a = 6, b = 8, unknown = Side.B),
                    ),
                    Choice(
                        question = "A 13 m ladder stands 5 m from the wall. How high does it reach?",
                        options = listOf("8 m", "10 m", "12 m", "14 m"),
                        correctIndex = 2,
                        explanation = "169 - 25 = 144.",
                        visual = RightTriangle(a = 5, b = 12, unknown = Side.B),
                    ),
                    Concept(
                        body = "Decide which side faces the right angle before anything else. That is what says whether the squares get added or taken away.",
                        visual = RightTriangle(a = 5, b = 12),
                    ),
                    Choice(
                        question = "The hypotenuse is 25 and one short side is 7. How long is the other?",
                        options = listOf("18", "24", "26", "32"),
                        correctIndex = 1,
                        explanation = "625 - 49 = 576, and 576 is 24 squared.",
                        visual = RightTriangle(a = 7, b = 24, unknown = Side.B),
                    ),
                    Numeric(
                        question = "The hypotenuse is 17 and one short side is 8. How long is the other?",
                        answer = "15",
                        explanation = "289 - 64 = 225.",
                        visual = RightTriangle(a = 8, b = 15, unknown = Side.B),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-pythagoras-using",
                title = "Using the theorem",
                summary = "Diagonals, shortcuts, and checking for a right angle.",
                steps = listOf(
                    Concept(
                        body = "Any real problem with a right angle hidden in it can be turned into a triangle: a ladder, a diagonal, a shortcut across a field.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Worked(
                        problem = "A rectangle is 12 cm by 9 cm. How long is its diagonal?",
                        lines = listOf(
                            "The diagonal cuts the rectangle into two right triangles.",
                            "12² = 144 and 9² = 81.",
                            "144 + 81 = 225.",
                            "Take the square root.",
                        ),
                        result = "15 cm",
                        visual = RightTriangle(a = 12, b = 9, showSquares = true),
                    ),
                    Choice(
                        question = "A field is 40 m by 30 m. How much shorter is the diagonal than walking two sides?",
                        options = listOf("10 m", "20 m", "30 m", "50 m"),
                        correctIndex = 1,
                        explanation = "The diagonal is 50 m and the two sides are 70 m together.",
                        visual = RightTriangle(a = 40, b = 30, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = "The theorem also runs backwards. If the two squares add up to the third, the triangle must have a right angle in it.",
                        formula = "9 + {b:16} = 25",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Choice(
                        question = "A triangle has sides 8, 15 and 17. Is it right-angled?",
                        options = listOf(
                            "Yes, the squares add up",
                            "No, the squares do not add up",
                            "Only if it is also isosceles",
                            "There is no way to tell",
                        ),
                        correctIndex = 0,
                        explanation = "64 + 225 = 289, and 289 is 17 squared.",
                        visual = RightTriangle(a = 15, b = 8, labels = false),
                    ),
                    Numeric(
                        question = "A screen is 16 inches wide and 12 inches tall. How long is its diagonal, in inches?",
                        answer = "20",
                        explanation = "256 + 144 = 400.",
                        visual = RightTriangle(a = 16, b = 12, unknown = Side.HYPOTENUSE),
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
                prompt = "How long is the missing short side?",
                options = listOf("6", "8", "10", "12"),
                correctIndex = 1,
                explanation = "100 - 36 = 64.",
                visual = RightTriangle(a = 6, b = 8, unknown = Side.B),
            ),
            QuizQuestion(
                prompt = "Which of these could be the three sides of a right triangle?",
                options = listOf("2, 3, 4", "6, 8, 10", "4, 5, 6", "1, 2, 3"),
                correctIndex = 1,
                explanation = "36 + 64 = 100.",
                visual = RightTriangle(a = 8, b = 6, labels = false),
            ),
            QuizQuestion(
                prompt = "A rectangle is 12 cm by 9 cm. How long is its diagonal?",
                options = listOf("13 cm", "15 cm", "21 cm", "144 cm"),
                correctIndex = 1,
                explanation = "144 + 81 = 225.",
                visual = RightTriangle(a = 12, b = 9, unknown = Side.HYPOTENUSE),
            ),
            QuizQuestion(
                prompt = "Which side of a right triangle is always the longest?",
                options = listOf(
                    "The one facing the right angle",
                    "The one lying along the bottom",
                    "The shorter of the two touching the right angle",
                    "They are always equal",
                ),
                correctIndex = 0,
                explanation = "The hypotenuse faces the biggest angle, and the right angle is the biggest one there is here.",
                visual = RightTriangle(a = 4, b = 3, labels = false),
            ),
            QuizQuestion(
                prompt = "The hypotenuse is 26 and one short side is 10. How long is the other?",
                options = listOf("16", "20", "24", "28"),
                correctIndex = 2,
                explanation = "676 - 100 = 576.",
                visual = RightTriangle(a = 10, b = 24, unknown = Side.B),
            ),
        ),
    )

    private val circles = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "circles",
        title = "Circles",
        summary = "Where pi comes from, and the two formulas it feeds.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-circles",
                title = "Radius, diameter and pi",
                summary = "Where pi comes from.",
                steps = listOf(
                    Concept(
                        body = "The diameter crosses the middle, so it is twice the radius.",
                        formula = "d = 2r",
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Numeric(
                        question = "The radius is 5 cm. What is the diameter, in cm?",
                        formula = "2 x {b:5} = ?",
                        answer = "10",
                        explanation = "Twice the radius.",
                        visual = CircleFigure(radius = 5, reveal = false),
                    ),
                    Concept(
                        body = "Roll a circle along once and it covers a little over three diameters. That number is pi, and it never changes.",
                        formula = "C = pi x d",
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Concept(
                        body = "Pi is the same for a coin and for a planet. Being the same every time is exactly what makes it worth a name of its own.",
                        visual = CircleFigure(radius = 3, sweepCircumference = true),
                    ),
                    Choice(
                        question = "A wheel of diameter 50 cm. How far does one turn take it?",
                        options = listOf("78.5 cm", "157 cm", "250 cm", "314 cm"),
                        correctIndex = 1,
                        explanation = "3.14 x 50.",
                        visual = CircleFigure(sweepCircumference = true, reveal = false),
                    ),
                    Numeric(
                        question = "The diameter is 18 cm. What is the radius, in cm?",
                        formula = "18 / {b:2} = ?",
                        answer = "9",
                        explanation = "Half the diameter.",
                        visual = CircleFigure(showDiameter = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circles-area",
                title = "Area of a circle",
                summary = "Square the radius, then multiply by pi.",
                steps = listOf(
                    Concept(
                        body = "Area squares the radius, so doubling r gives four times the area rather than twice.",
                        formula = "A = pi x r²",
                        visual = CircleFigure(radius = 10, fillArea = true),
                    ),
                    Concept(
                        body = "Square the radius first, then multiply by pi. Squaring the whole of pi times r is the usual slip.",
                        formula = "A = pi x (r x r)",
                        visual = CircleFigure(radius = 4, fillArea = true),
                    ),
                    Choice(
                        question = "Radius 10 cm, pi as 3.14. What is the area?",
                        options = listOf("31.4", "62.8", "314", "628"),
                        correctIndex = 2,
                        explanation = "3.14 x 100.",
                        visual = CircleFigure(fillArea = true, reveal = false),
                    ),
                    Choice(
                        question = "A circle has radius 4 cm. Taking pi as 3.14, what is its area in square cm?",
                        formula = "3.14 x 4 x 4 = ?",
                        options = listOf("12.56", "25.12", "50.24", "200.96"),
                        correctIndex = 2,
                        explanation = "16 square units, each one worth pi.",
                        visual = CircleFigure(radius = 4, fillArea = true, reveal = false),
                    ),
                    Concept(
                        body = "Circumference uses the radius once and area uses it twice. That is the whole difference between the two formulas, and it is why their units differ.",
                        formula = "C = 2 pi r, A = pi r²",
                        visual = CircleFigure(radius = 5, fillArea = true),
                    ),
                    Choice(
                        question = "A circle's radius is doubled. Its area is multiplied by...",
                        options = listOf("2", "3", "4", "8"),
                        correctIndex = 2,
                        explanation = "The radius is squared, so doubling it doubles the answer twice over.",
                        visual = CircleFigure(radius = 8, fillArea = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circles-using",
                title = "Working backwards",
                summary = "From a circumference or an area back to the circle.",
                steps = listOf(
                    Concept(
                        body = "Every circle question starts by deciding which measurement you were given. Halving or doubling first prevents most of the mistakes.",
                        visual = CircleFigure(radius = 6, showDiameter = true),
                    ),
                    Choice(
                        question = "A circle has circumference 31.4 cm, with pi as 3.14. What is its diameter?",
                        options = listOf("5 cm", "10 cm", "15.7 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = "Circumference divided by pi gives the diameter back.",
                        visual = CircleFigure(sweepCircumference = true, reveal = false),
                    ),
                    Concept(
                        body = "The formulas run both ways. Circumference divides back to the diameter, and area divides by pi and then square-roots back to the radius.",
                        formula = "d = C / pi",
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Numeric(
                        question = "A circle has area 78.5 square cm, with pi as 3.14. What is the radius times itself?",
                        formula = "78.5 / {b:3.14} = ?",
                        answer = "25",
                        explanation = "Dividing by pi leaves r squared, and the radius itself is its square root.",
                        visual = CircleFigure(radius = 5, fillArea = true, reveal = false),
                    ),
                    Concept(
                        body = "Half a circle keeps half the curved edge but gains the diameter as a straight one, so its perimeter is not simply half the circumference.",
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Choice(
                        question = "A semicircle has diameter 10 cm. How long is its curved edge, with pi as 3.14?",
                        options = listOf("15.7 cm", "31.4 cm", "5 cm", "10 cm"),
                        correctIndex = 0,
                        explanation = "The whole way round is 31.4 cm, and the curved edge is half of it.",
                        visual = CircleFigure(radius = 5, sweepCircumference = true, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "The diameter is 18 cm. What is the radius?",
                options = listOf("6 cm", "9 cm", "18 cm", "36 cm"),
                correctIndex = 1,
                explanation = "Half the diameter.",
                visual = CircleFigure(showDiameter = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "Circumference of a circle of diameter 10 cm, pi as 3.14?",
                options = listOf("15.7 cm", "31.4 cm", "78.5 cm", "314 cm"),
                correctIndex = 1,
                explanation = "pi x d.",
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "Area of a circle of radius 4 cm, pi as 3.14?",
                options = listOf("12.56", "25.12", "50.24", "100.48"),
                correctIndex = 2,
                explanation = "3.14 x 16.",
                visual = CircleFigure(fillArea = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which formula gives the circumference?",
                options = listOf("pi x r²", "2 x pi x r", "pi x r", "4 x pi x r"),
                correctIndex = 1,
                explanation = "Two radii make the diameter, and pi diameters make the way round.",
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "A circle's radius is doubled. Its area is multiplied by...",
                options = listOf("2", "3", "4", "8"),
                correctIndex = 2,
                explanation = "The radius is squared in the area formula.",
                visual = CircleFigure(radius = 8, fillArea = true, reveal = false),
            ),
            QuizQuestion(
                prompt = "A circle has circumference 62.8 cm, pi as 3.14. What is its diameter?",
                options = listOf("10 cm", "20 cm", "31.4 cm", "40 cm"),
                correctIndex = 1,
                explanation = "62.8 divided by pi.",
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
        ),
    )

    private val volume = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "volume",
        title = "Volume and surface area",
        summary = "Cross-section times length, and the skin around it.",
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-volume",
                title = "Volume of a prism",
                summary = "Cross-section times length.",
                steps = listOf(
                    Concept(
                        body = "Volume counts unit cubes, so its units are cubed.",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = "A prism keeps the same cross-section the whole way along, so one slice repeated is all there is to it.",
                        formula = "V = base area x length",
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = "A box is 4 by 3 by 2 cm. What is its volume in cubic cm?",
                        formula = "4 x 3 x 2 = ?",
                        answer = "24",
                        explanation = "A layer of 12 cubes, twice over.",
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
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
                        question = "A cylinder has base radius r and height h. Which is its volume?",
                        options = listOf("2 x pi x r x h", "pi x r² x h", "pi x d x h", "r² + h"),
                        correctIndex = 1,
                        explanation = "The circle of area pi r², repeated up the height.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Choice(
                        question = "Double every side of a cube. The volume is multiplied by...",
                        options = listOf("2", "4", "6", "8"),
                        correctIndex = 3,
                        explanation = "All three dimensions double, so the answer doubles three times over.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-volume-surface",
                title = "Surface area",
                summary = "The skin rather than the filling.",
                steps = listOf(
                    Concept(
                        body = "Surface area is the skin rather than the filling. Add up the faces, and the units come out squared, not cubed.",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Concept(
                        body = "A cuboid has three pairs of matching faces, so work out three of them and double the total.",
                        formula = "SA = 2(lw + lh + wh)",
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = "A cube has edges of 3 cm. What is its surface area in square cm?",
                        formula = "6 x 3 x 3 = ?",
                        answer = "54",
                        explanation = "Six faces, each one 9 square cm.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Choice(
                        question = "A box is 5 by 4 by 2 cm. What is its surface area, in square cm?",
                        options = listOf("40", "76", "80", "100"),
                        correctIndex = 1,
                        explanation = "20, 10 and 8 for the three different faces, then doubled.",
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
                    ),
                    Concept(
                        body = "Volume and surface area do not grow together. A long thin box holds very little and still takes a great deal of wrapping.",
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Choice(
                        question = "Which of the two is measured in square units rather than cubic ones?",
                        options = listOf("Volume", "Surface area", "Both of them", "Neither of them"),
                        correctIndex = 1,
                        explanation = "Faces are flat, so their measure is an area.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-volume-cylinders",
                title = "Cylinders and capacity",
                summary = "A prism with a circle for its cross-section.",
                steps = listOf(
                    Concept(
                        body = "A cylinder is a prism with a circle for its cross-section, so the same rule works: the area of the circle, repeated up the height.",
                        formula = "V = pi x r² x h",
                        visual = Solid(kind = SolidKind.CYLINDER, counts = true),
                    ),
                    Numeric(
                        question = "A cylinder has base area 20 square cm and height 6 cm. What is its volume in cubic cm?",
                        formula = "20 x {b:6} = ?",
                        answer = "120",
                        explanation = "The same 20 square cm, six times over.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Concept(
                        body = "Capacity is volume seen from the inside. A litre is exactly a thousand cubic centimetres, which is a 10 cm cube.",
                        formula = "1 litre = 1000 cm³",
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Choice(
                        question = "A tank is 10 by 10 by 10 cm. How many litres does it hold?",
                        options = listOf("0.1", "1", "10", "100"),
                        correctIndex = 1,
                        explanation = "1000 cubic cm, which is exactly one litre.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = "Doubling a cylinder's radius gives four times the volume, because the radius is squared. Doubling its height only doubles it.",
                        visual = Solid(kind = SolidKind.CYLINDER),
                    ),
                    Choice(
                        question = "A cylinder's height is doubled and its radius left alone. Its volume is multiplied by...",
                        options = listOf("2", "4", "6", "8"),
                        correctIndex = 0,
                        explanation = "Height appears once in the formula, so it changes the answer once.",
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
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
            QuizQuestion(
                prompt = "A cube has edges of 2 cm. What is its volume in cubic cm?",
                options = listOf("4", "6", "8", "12"),
                correctIndex = 2,
                explanation = "2 x 2 x 2.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "A cube has edges of 4 cm. What is its surface area in square cm?",
                options = listOf("16", "64", "96", "128"),
                correctIndex = 2,
                explanation = "Six faces of 16 square cm.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "Surface area is measured in...",
                options = listOf("cubic units", "square units", "plain units", "degrees"),
                correctIndex = 1,
                explanation = "It adds up flat faces, and a face has an area.",
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = "How many cubic centimetres are in one litre?",
                options = listOf("10", "100", "1000", "10000"),
                correctIndex = 2,
                explanation = "A litre is a 10 cm cube.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
        ),
    )

    private val similarity = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "similarity",
        title = "Similarity and scale",
        summary = "Same shape at a different size, and what that does to area and volume.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-similarity",
                title = "Same shape, different size",
                summary = "One factor carried through every length.",
                steps = listOf(
                    Concept(
                        body = "Similar shapes share every angle; the lengths all scale by one factor.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "Every length multiplies by that same number, and the shape stays unmistakably itself.",
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
                        body = "Congruent is the special case where the factor is 1: the same shape at the same size.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Choice(
                        question = "Which of these pairs must be similar whatever size they are drawn?",
                        options = listOf(
                            "Any two rectangles",
                            "Any two squares",
                            "Any two triangles",
                            "Any two quadrilaterals",
                        ),
                        correctIndex = 1,
                        explanation = "Every square has four right angles and four equal sides, so only the factor can differ. A long rectangle and a short one do not match.",
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-similarity-area",
                title = "Scaling area and volume",
                summary = "Length by k, area by k squared, volume by k cubed.",
                steps = listOf(
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
                    Concept(
                        body = "Volume goes one further and scales by the cube, because all three directions stretch at once.",
                        formula = "volume x k³",
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Numeric(
                        question = "A solid is enlarged by scale factor 3. Its volume is multiplied by what?",
                        formula = "3 x 3 x 3 = ?",
                        answer = "27",
                        explanation = "Three directions, each one three times bigger.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = "It reads backwards too. If two areas are in the ratio 9 : 1, the lengths are in the ratio 3 : 1.",
                        formula = "k = √9 = 3",
                        visual = AreaGrid(cols = 3, rows = 3),
                    ),
                    Choice(
                        question = "Two similar solids have volumes of 8 cm³ and 64 cm³. What is the scale factor?",
                        options = listOf("2", "4", "8", "56"),
                        correctIndex = 0,
                        explanation = "The volumes are 8 times apart, and the cube root of 8 is 2.",
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-similarity-using",
                title = "Using similar triangles",
                summary = "One pair of sides fixes every other.",
                steps = listOf(
                    Concept(
                        body = "Two triangles with matching angles must be similar, so a single pair of sides fixes every other length between them.",
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = "Match the sides up in the same order before dividing, or the factor comes out upside down.",
                        visual = RightTriangle(a = 8, b = 6),
                    ),
                    Choice(
                        question = "A triangle has sides 3 and 4. A similar one has 9 in place of the 3. What is in place of the 4?",
                        options = listOf("10", "12", "13", "16"),
                        correctIndex = 1,
                        explanation = "The factor is 9 / 3 = 3, so the 4 becomes 12.",
                        visual = RightTriangle(a = 9, b = 12, labels = false),
                    ),
                    Numeric(
                        question = "A 2 m stick casts a 3 m shadow. A tree casts a 30 m shadow at the same moment. How tall is the tree, in metres?",
                        formula = "30 / {b:3} x {b:2} = ?",
                        answer = "20",
                        explanation = "The shadow is ten times longer, so the tree is ten times taller than the stick.",
                        visual = RightTriangle(a = 3, b = 2, labels = false),
                    ),
                    Concept(
                        body = "Shadows, maps and scale drawings are all this one idea: a single factor carried through every length in the picture.",
                        visual = RightTriangle(a = 6, b = 4),
                    ),
                    Choice(
                        question = "Two similar triangles have areas of 20 cm² and 45 cm². What is the scale factor?",
                        options = listOf("1.5", "2", "2.25", "2.5"),
                        correctIndex = 0,
                        explanation = "The areas are 2.25 times apart, and the square root of 2.25 is 1.5.",
                        visual = AreaGrid(cols = 4, rows = 5, reveal = false),
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
                prompt = "Two matching sides are 4 cm and 10 cm. What is the scale factor?",
                options = listOf("0.4", "2.5", "6", "14"),
                correctIndex = 1,
                explanation = "10 / 4.",
                visual = RightTriangle(a = 10, b = 6, labels = false),
            ),
            QuizQuestion(
                prompt = "A solid is enlarged by scale factor 2. Its volume is multiplied by...",
                options = listOf("2", "4", "6", "8"),
                correctIndex = 3,
                explanation = "Volume scales by the cube of the factor.",
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = "Which of these pairs must always be similar?",
                options = listOf(
                    "Any two rectangles",
                    "Any two squares",
                    "Any two triangles",
                    "Any two kites",
                ),
                correctIndex = 1,
                explanation = "Only the scale factor can differ between two squares.",
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "Two similar triangles have areas of 16 cm² and 36 cm². What is the scale factor?",
                options = listOf("1.5", "2", "2.25", "2.5"),
                correctIndex = 0,
                explanation = "The areas are 2.25 times apart, and 1.5 squared is 2.25.",
                visual = AreaGrid(cols = 4, rows = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = "A 3-4-5 triangle is enlarged by 4. How long is its longest side?",
                options = listOf("9", "15", "20", "25"),
                correctIndex = 2,
                explanation = "5 x 4.",
                visual = RightTriangle(a = 4, b = 3, labels = false),
            ),
        ),
    )

    private val transformations = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "transformations",
        title = "Transformations",
        summary = "Slide, flip, turn and resize, and what each one keeps.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-transformations",
                title = "Slides and flips",
                summary = "Translations and reflections on the grid.",
                steps = listOf(
                    Concept(
                        body = "Translations slide, reflections flip, rotations turn, enlargements resize.",
                        visual = Plot(
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = "A translation vector reads across, then up.",
                        formula = "(3, -2)",
                        visual = Plot(
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
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)")),
                        ),
                    ),
                    Concept(
                        body = "Reflecting in the y-axis flips the sign of x only.",
                        formula = "(x, y) becomes (-x, y)",
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = 2f, y = 2f, label = "A"),
                                PlotPoint(x = -2f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = "Reflect this point in the x-axis. What is the new y-coordinate?",
                        options = listOf("-2", "-1", "1", "2"),
                        correctIndex = 0,
                        explanation = "Reflecting in the x-axis negates y.",
                        visual = Plot(
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "A")),
                        ),
                    ),
                    Choice(
                        question = "Reflecting in the x-axis changes the sign of...",
                        options = listOf("x only", "y only", "both of them", "neither of them"),
                        correctIndex = 1,
                        explanation = "The point crosses the horizontal axis, so only its height changes side.",
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = 1f, y = 2f, label = "A"),
                                PlotPoint(x = 1f, y = -2f, label = "A'"),
                            ),
                        ),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-transformations-rotations",
                title = "Rotations",
                summary = "A centre, an angle and a direction.",
                steps = listOf(
                    Concept(
                        body = "A rotation needs three things before it means anything: a centre to turn about, an angle, and a direction.",
                        visual = Plot(
                            points = listOf(PlotPoint(x = 1f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = "A quarter turn anticlockwise about the origin sends a point across and up in one move.",
                        formula = "(x, y) becomes (-y, x)",
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = 2f, y = 1f, label = "A"),
                                PlotPoint(x = -1f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = "Rotate this point by 180 degrees about the origin. Where does it land?",
                        options = listOf("(-3, -1)", "(-1, 3)", "(1, -3)", "(3, -1)"),
                        correctIndex = 0,
                        explanation = "A half turn sends a point straight through the origin, so both signs flip.",
                        visual = Plot(
                            points = listOf(PlotPoint(x = 3f, y = 1f, label = "(3, 1)")),
                        ),
                    ),
                    Choice(
                        question = "Rotate this point by 180 degrees about the origin. What is its new x-coordinate?",
                        options = listOf("-2", "0", "2", "4"),
                        correctIndex = 0,
                        explanation = "A half turn flips the sign of both coordinates.",
                        visual = Plot(
                            points = listOf(PlotPoint(x = 2f, y = 0f, label = "(2, 0)")),
                        ),
                    ),
                    Concept(
                        body = "A rotation keeps every length and every angle, so the image is congruent to what it started as.",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = "Which of these leaves every length unchanged?",
                        options = listOf(
                            "enlargement by 2",
                            "enlargement by 0.5",
                            "rotation",
                            "enlargement by 3",
                        ),
                        correctIndex = 2,
                        explanation = "Only an enlargement changes size, and its factor is what does it.",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-transformations-enlargements",
                title = "Enlargements",
                summary = "A centre and a scale factor, in either direction.",
                steps = listOf(
                    Concept(
                        body = "An enlargement needs a centre and a scale factor. Every point moves away from that centre by the factor.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Concept(
                        body = "A factor between 0 and 1 still counts as an enlargement, even though the shape comes out smaller. The word covers both directions.",
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = "A shape is enlarged by scale factor 0.5. What happens to it?",
                        options = listOf(
                            "Every length doubles",
                            "Every length halves",
                            "Nothing changes",
                            "It turns through 90 degrees",
                        ),
                        correctIndex = 1,
                        explanation = "The factor multiplies every length, and multiplying by a half makes them shorter.",
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false, reveal = false),
                    ),
                    Concept(
                        body = "A negative factor sends the shape through the centre and out the other side, arriving upside down.",
                        formula = "k = -1 is a half turn",
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false),
                    ),
                    Numeric(
                        question = "A 3 cm side is enlarged by scale factor 4. How long is it now, in cm?",
                        formula = "3 x {b:4} = ?",
                        answer = "12",
                        explanation = "Every length is multiplied by the factor.",
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false, reveal = false),
                    ),
                    Choice(
                        question = "Which transformation does NOT keep the size?",
                        options = listOf("translation", "reflection", "rotation", "enlargement"),
                        correctIndex = 3,
                        explanation = "The other three are congruence transformations.",
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "Translate this point by (2, -3). Where does it land?",
                options = listOf("(3, -1)", "(3, 5)", "(-1, -1)", "(2, 6)"),
                correctIndex = 0,
                explanation = "(1 + 2, 2 - 3).",
                visual = Plot(
                    points = listOf(PlotPoint(x = 1f, y = 2f, label = "(1, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "Reflect this point in the y-axis.",
                options = listOf("(-2, 2)", "(2, -2)", "(-2, -2)", "(2, 2)"),
                correctIndex = 0,
                explanation = "Only the sign of x flips.",
                visual = Plot(
                    points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = "Rotate this point by 180 degrees about the origin.",
                options = listOf("(-3, -1)", "(-1, 3)", "(1, -3)", "(3, -1)"),
                correctIndex = 0,
                explanation = "A half turn flips the sign of both coordinates.",
                visual = Plot(
                    points = listOf(PlotPoint(x = 3f, y = 1f, label = "(3, 1)")),
                ),
            ),
            QuizQuestion(
                prompt = "Which transformation does NOT keep the size?",
                options = listOf("translation", "reflection", "rotation", "enlargement"),
                correctIndex = 3,
                explanation = "The other three are congruence transformations.",
                visual = AreaGrid(cols = 4, rows = 3, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = "A translation vector of (3, -2) means...",
                options = listOf(
                    "3 right and 2 down",
                    "3 up and 2 right",
                    "3 left and 2 up",
                    "2 right and 3 down",
                ),
                correctIndex = 0,
                explanation = "Across first, then up, and a minus means down.",
                visual = Plot(
                    points = listOf(
                        PlotPoint(x = -1f, y = 1f, label = "A"),
                        PlotPoint(x = 2f, y = -1f, label = "A'"),
                    ),
                ),
            ),
            QuizQuestion(
                prompt = "An enlargement by scale factor 0.5 makes a shape...",
                options = listOf("twice as long", "half as long", "the same size", "upside down"),
                correctIndex = 1,
                explanation = "The factor multiplies every length, and a half shortens them.",
                visual = AreaGrid(cols = 2, rows = 2, showArea = false, reveal = false),
            ),
        ),
    )

    private val circleTheorems = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "circle-theorems",
        title = "Circle theorems",
        summary = "Angles at the centre and the edge, cyclic quadrilaterals and tangents.",
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-circle-theorems",
                title = "Angles at the centre and the edge",
                summary = "One is always twice the other.",
                steps = listOf(
                    Concept(
                        body = "The angle at the centre is exactly twice the one at the circumference standing on the same arc.",
                        visual = CircleFigure(centreAngle = 80),
                    ),
                    Choice(
                        question = "The centre angle is 80. What is the angle at the circumference?",
                        options = listOf("20", "40", "80", "160"),
                        correctIndex = 1,
                        explanation = "Half the centre angle.",
                        visual = CircleFigure(centreAngle = 80, reveal = false),
                    ),
                    Concept(
                        body = "Stretch the centre angle out to a straight 180 and the edge angle becomes a right angle. That is the angle in a semicircle.",
                        visual = CircleFigure(centreAngle = 180),
                    ),
                    Choice(
                        question = "An angle drawn in a semicircle is always...",
                        options = listOf("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = "Half of the straight 180 at the centre.",
                        visual = CircleFigure(centreAngle = 180, reveal = false),
                    ),
                    Concept(
                        body = "Every angle standing on the same arc from the circumference is equal to every other, because each of them is half of the one centre angle.",
                        visual = CircleFigure(centreAngle = 100),
                    ),
                    Numeric(
                        question = "The centre angle is 140. How many degrees is the angle at the circumference?",
                        formula = "140 / {b:2} = ?",
                        answer = "70",
                        explanation = "Half the centre angle, whatever that angle happens to be.",
                        visual = CircleFigure(centreAngle = 140, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circle-theorems-cyclic",
                title = "Cyclic quadrilaterals",
                summary = "Opposite angles add to 180.",
                steps = listOf(
                    Concept(
                        body = "A cyclic quadrilateral has all four of its corners sitting on the circle.",
                        visual = CyclicQuad(),
                    ),
                    Concept(
                        body = "Its opposite angles add to 180.",
                        formula = "a + c = 180",
                        visual = CyclicQuad(angles = listOf("a", "", "c", ""), highlightPair = 0),
                    ),
                    Numeric(
                        question = "One angle of a cyclic quadrilateral is 110. How many degrees is the opposite one?",
                        answer = "70",
                        explanation = "180 - 110.",
                        visual = CyclicQuad(angles = listOf("110", "", "?", ""), highlightPair = 0),
                    ),
                    Choice(
                        question = "One angle of a cyclic quadrilateral is 95. Its opposite angle is...",
                        options = listOf("85", "95", "105", "185"),
                        correctIndex = 0,
                        explanation = "The two have to make 180 between them.",
                        visual = CyclicQuad(angles = listOf("95", "", "?", ""), highlightPair = 0),
                    ),
                    Concept(
                        body = "Both pairs behave the same way, so two opposite angles hand you the other two as well.",
                        formula = "b + d = 180",
                        visual = CyclicQuad(angles = listOf("", "b", "", "d"), highlightPair = 1),
                    ),
                    Choice(
                        question = "Going round a cyclic quadrilateral, three angles are 70, 100 and 110. What is the fourth?",
                        options = listOf("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = "The 70 and the 110 are already an opposite pair, so the fourth angle partners the 100.",
                        visual = CyclicQuad(angles = listOf("70", "100", "110", "?"), highlightPair = 1),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circle-theorems-tangents",
                title = "Tangents",
                summary = "Touching once, and meeting the radius square on.",
                steps = listOf(
                    Concept(
                        body = "A tangent touches the circle at exactly one point and never crosses it.",
                        visual = CircleFigure(radius = 5),
                    ),
                    Concept(
                        body = "A tangent always meets the radius drawn to that point at a right angle.",
                        formula = "tangent meets radius at 90",
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = "A tangent meets the radius at an angle of...",
                        options = listOf("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = "A tangent is always perpendicular to the radius it touches.",
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = "That right angle is what makes tangent questions solvable: it hands you a right triangle to use Pythagoras on.",
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = "A tangent, a radius and a line to the centre make a right triangle. One of its other angles is 35. How many degrees is the last one?",
                        formula = "180 - {b:90} - {b:35} = ?",
                        answer = "55",
                        explanation = "The right angle uses up 90 of the 180.",
                        visual = RightTriangle(a = 7, b = 5, labels = false),
                    ),
                    Choice(
                        question = "Two tangents are drawn to a circle from the same point outside it. What can you say about them?",
                        options = listOf(
                            "They are always equal in length",
                            "They are always different lengths",
                            "They are always perpendicular",
                            "They are always parallel",
                        ),
                        correctIndex = 0,
                        explanation = "The two right triangles they make share the same radius and the same line to the centre.",
                        visual = CircleFigure(radius = 5, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = "The centre angle is 120. What is the angle at the circumference?",
                options = listOf("30", "60", "120", "240"),
                correctIndex = 1,
                explanation = "Half of the centre angle.",
                visual = CircleFigure(centreAngle = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = "An angle in a semicircle is always...",
                options = listOf("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = "Half of the straight angle at the centre.",
                visual = CircleFigure(centreAngle = 180, reveal = false),
            ),
            QuizQuestion(
                prompt = "The angle at the circumference is 35. What is the angle at the centre on the same arc?",
                options = listOf("17.5", "35", "70", "145"),
                correctIndex = 2,
                explanation = "The centre angle is the doubled one.",
                visual = CircleFigure(centreAngle = 70, reveal = false),
            ),
            QuizQuestion(
                prompt = "One angle of a cyclic quadrilateral is 95. Its opposite angle is...",
                options = listOf("85", "95", "105", "185"),
                correctIndex = 0,
                explanation = "Opposite angles sum to 180.",
                visual = CyclicQuad(angles = listOf("95", "", "?", ""), highlightPair = 0),
            ),
            QuizQuestion(
                prompt = "A tangent meets the radius at an angle of...",
                options = listOf("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = "A tangent is always perpendicular to the radius it touches.",
                visual = AngleFigure(degrees = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = "Two tangents drawn from the same point outside a circle are...",
                options = listOf(
                    "always equal in length",
                    "always different lengths",
                    "always perpendicular",
                    "always parallel",
                ),
                correctIndex = 0,
                explanation = "The two right triangles they form are congruent.",
                visual = CircleFigure(radius = 5, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        flatShapes, solidShapes, angles, quadrilaterals, symmetry, perimeterAndArea, pythagoras, circles, volume,
        similarity, transformations, circleTheorems,
    )
}
