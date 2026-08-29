package com.inspiredandroid.braincup.learn.content

import braincup.composeapp.generated.resources.*
import braincup.composeapp.generated.resources.Res
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
import com.inspiredandroid.braincup.learn.counted
import com.inspiredandroid.braincup.learn.filled
import com.inspiredandroid.braincup.learn.learnUnit
import com.inspiredandroid.braincup.learn.math
import com.inspiredandroid.braincup.learn.mathOptions
import com.inspiredandroid.braincup.learn.wordOptions
import com.inspiredandroid.braincup.learn.words

/** Geometry: shapes and their names, then angles, area, Pythagoras, circles and formal proof. */
internal object GeometryContent {

    private val flatShapes = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "flat-shapes",
        title = Res.string.learn_unit_geometry_flat_shapes_title,
        summary = Res.string.learn_unit_geometry_flat_shapes_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-flat-shapes",
                title = Res.string.learn_g12_geometry_flat_shapes_title,
                summary = Res.string.learn_g12_geometry_flat_shapes_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g12_geometry_flat_shapes_s1_body),
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_geometry_flat_shapes_s2_body),
                        visual = Polygon(sides = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_how_many_sides_does_shape),
                        options = mathOptions("4", "5", "6", "8"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g12_geometry_flat_shapes_s3_explanation),
                        visual = Polygon(sides = 5, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g12_geometry_flat_shapes_s4_question),
                        answer = "6",
                        explanation = words(Res.string.learn_g12_geometry_flat_shapes_s4_explanation),
                        visual = Polygon(sides = 6, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_geometry_flat_shapes_s5_body),
                        visual = Polygon(sides = 6),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_geometry_flat_shapes_s6_question),
                        options = wordOptions(
                            Res.string.learn_shape_triangle_name,
                            Res.string.learn_shape_pentagon_name,
                            Res.string.learn_shape_hexagon_name,
                            Res.string.learn_shape_octagon_name,
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_g12_geometry_flat_shapes_s6_explanation),
                        visual = Polygon(sides = 8, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-flat-shapes-curves",
                title = Res.string.learn_geometry_flat_shapes_curves_title,
                summary = Res.string.learn_geometry_flat_shapes_curves_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_curves_s1_body),
                        visual = CircleFigure(showRadius = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_curves_s2_body),
                        visual = Polygon(sides = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_flat_shapes_curves_s3_question),
                        options = wordOptions(
                            Res.string.learn_shape_triangle_name,
                            Res.string.learn_shape_square_name,
                            Res.string.learn_shape_circle_name,
                            Res.string.learn_shape_hexagon_name,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_shared_one_smooth_curve_so_no),
                        visual = CircleFigure(showRadius = false, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_flat_shapes_curves_s4_question),
                        answer = "0",
                        explanation = words(Res.string.learn_geometry_flat_shapes_curves_s4_explanation),
                        visual = CircleFigure(showRadius = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_curves_s5_body),
                        visual = AreaGrid(cols = 7, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_flat_shapes_curves_s6_question),
                        options = wordOptions(
                            Res.string.learn_geometry_flat_shapes_curves_s6_o1,
                            Res.string.learn_geometry_flat_shapes_curves_s6_o2,
                            Res.string.learn_shared_all_four_sides_equal,
                            Res.string.learn_shared_opposite_sides_equal,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_flat_shapes_curves_s6_explanation),
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-flat-shapes-sorting",
                title = Res.string.learn_geometry_flat_shapes_sorting_title,
                summary = Res.string.learn_geometry_flat_shapes_sorting_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_sorting_s1_body),
                        visual = Polygon(sides = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_sorting_s2_body),
                        visual = Polygon(sides = 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_flat_shapes_sorting_s3_question),
                        options = wordOptions(
                            Res.string.learn_shape_square_name,
                            Res.string.learn_shape_triangle_name,
                            Res.string.learn_shape_hexagon_name,
                            Res.string.learn_shape_octagon_name,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_flat_shapes_sorting_s3_explanation),
                        visual = Polygon(sides = 6, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_flat_shapes_sorting_s4_question),
                        answer = "3",
                        explanation = words(Res.string.learn_geometry_flat_shapes_sorting_s4_explanation),
                        visual = Polygon(sides = 3, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_flat_shapes_sorting_s5_body),
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_not_true_square),
                        options = listOf(
                            counted(Res.plurals.learn_opt_equal_sides, 4),
                            counted(Res.plurals.learn_opt_square_corners, 4),
                            counted(Res.plurals.learn_opt_corners, 4),
                            words(Res.string.learn_shared_curved_edges),
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_shared_every_side_square_straight),
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_many_sides_does_shape),
                options = mathOptions("4", "5", "6", "8"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_flat_shapes_q1_explanation),
                visual = Polygon(sides = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_flat_shapes_q2_prompt),
                options = wordOptions(
                    Res.string.learn_shape_pentagon_name,
                    Res.string.learn_shape_hexagon_name,
                    Res.string.learn_shape_octagon_name,
                    Res.string.learn_shape_square_name,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_flat_shapes_q2_explanation),
                visual = Polygon(sides = 6, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_flat_shapes_q3_prompt),
                options = wordOptions(
                    Res.string.learn_shape_triangle_name,
                    Res.string.learn_shape_square_name,
                    Res.string.learn_shape_circle_name,
                    Res.string.learn_shape_hexagon_name,
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_shared_one_smooth_curve_so_no),
                visual = CircleFigure(showRadius = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_flat_shapes_q4_prompt),
                options = mathOptions("4", "6", "8", "16"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_flat_shapes_q4_explanation),
                visual = Polygon(sides = 8, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_not_true_square),
                options = listOf(
                    counted(Res.plurals.learn_opt_equal_sides, 4),
                    counted(Res.plurals.learn_opt_square_corners, 4),
                    counted(Res.plurals.learn_opt_corners, 4),
                    words(Res.string.learn_shared_curved_edges),
                ),
                correctIndex = 3,
                explanation = words(Res.string.learn_shared_every_side_square_straight),
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_flat_shapes_q6_prompt),
                options = wordOptions(
                    Res.string.learn_shared_all_four_sides_equal,
                    Res.string.learn_shared_opposite_sides_equal,
                    Res.string.learn_unit_geometry_flat_shapes_q6_o3,
                    Res.string.learn_unit_geometry_flat_shapes_q6_o4,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_flat_shapes_q6_explanation),
                visual = AreaGrid(cols = 5, rows = 3, showArea = false, reveal = false),
            ),
        ),
    )

    private val solidShapes = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "solid-shapes",
        title = Res.string.learn_unit_geometry_solid_shapes_title,
        summary = Res.string.learn_unit_geometry_solid_shapes_summary,
        level = GradeLevel.GRADES_1_2,
        lessons = listOf(
            LessonSpec(
                id = "g12-geometry-solid-shapes",
                title = Res.string.learn_g12_geometry_solid_shapes_title,
                summary = Res.string.learn_g12_geometry_solid_shapes_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g12_geometry_solid_shapes_s1_body),
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_geometry_solid_shapes_s2_body),
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g12_geometry_solid_shapes_s3_question),
                        answer = "6",
                        explanation = words(Res.string.learn_shared_top_bottom_four_sides),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g12_geometry_solid_shapes_s4_body),
                        visual = Solid(kind = SolidKind.CONE, counts = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_g12_geometry_solid_shapes_s5_question),
                        options = wordOptions(
                            Res.string.learn_shape_sphere_name,
                            Res.string.learn_shape_cube_name,
                            Res.string.learn_shape_cylinder_name,
                            Res.string.learn_shape_cone_name,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g12_geometry_solid_shapes_s5_explanation),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_solid_called),
                        options = wordOptions(
                            Res.string.learn_shape_cylinder_name,
                            Res.string.learn_shape_cone_name,
                            Res.string.learn_shape_sphere_name,
                            Res.string.learn_shape_cube_name,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_shared_one_circle_rising_point),
                        visual = Solid(kind = SolidKind.CONE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-solid-shapes-counting",
                title = Res.string.learn_geometry_solid_shapes_counting_title,
                summary = Res.string.learn_geometry_solid_shapes_counting_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_counting_s1_body),
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_shared_how_many_edges_does_solid),
                        answer = "12",
                        explanation = words(Res.string.learn_shared_four_on_top_four_underneath),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_counting_s3_body),
                        visual = Solid(kind = SolidKind.SPHERE, counts = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_solid_shapes_counting_s4_question),
                        options = mathOptions("0", "1", "2", "4"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_solid_shapes_counting_s4_explanation),
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_counting_s5_body),
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_solid_shapes_counting_s6_question),
                        options = wordOptions(
                            Res.string.learn_shape_cube_name,
                            Res.string.learn_shape_cylinder_name,
                            Res.string.learn_shape_cone_name,
                            Res.string.learn_shape_sphere_name,
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_geometry_solid_shapes_counting_s6_explanation),
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-solid-shapes-around",
                title = Res.string.learn_geometry_solid_shapes_around_title,
                summary = Res.string.learn_geometry_solid_shapes_around_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_around_s1_body),
                        visual = Solid(kind = SolidKind.CYLINDER),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_tin_soup_closest_which_solid),
                        options = wordOptions(
                            Res.string.learn_shape_cube_name,
                            Res.string.learn_shape_cone_name,
                            Res.string.learn_shape_sphere_name,
                            Res.string.learn_shape_cylinder_name,
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_shared_two_circular_ends_curved_side),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_around_s3_body),
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_solid_shapes_around_s4_question),
                        options = wordOptions(
                            Res.string.learn_shape_cylinder_name,
                            Res.string.learn_shape_sphere_name,
                            Res.string.learn_shape_cube_name,
                            Res.string.learn_shared_prism,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_solid_shapes_around_s4_explanation),
                        visual = Solid(kind = SolidKind.SPHERE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_solid_shapes_around_s5_body),
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_solid_shapes_around_s6_question),
                        answer = "2",
                        explanation = words(Res.string.learn_geometry_solid_shapes_around_s6_explanation),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_many_edges_does_solid),
                options = mathOptions("6", "8", "10", "12"),
                correctIndex = 3,
                explanation = words(Res.string.learn_shared_four_on_top_four_underneath),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_what_solid_called),
                options = wordOptions(
                    Res.string.learn_shape_cylinder_name,
                    Res.string.learn_shape_cone_name,
                    Res.string.learn_shape_sphere_name,
                    Res.string.learn_shape_cube_name,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_shared_one_circle_rising_point),
                visual = Solid(kind = SolidKind.CONE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_solid_shapes_q3_prompt),
                options = mathOptions("4", "6", "8", "12"),
                correctIndex = 1,
                explanation = words(Res.string.learn_shared_top_bottom_four_sides),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_solid_shapes_q4_prompt),
                options = wordOptions(
                    Res.string.learn_shape_cube_name,
                    Res.string.learn_shape_cone_name,
                    Res.string.learn_shape_sphere_name,
                    Res.string.learn_shared_prism,
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_solid_shapes_q4_explanation),
                visual = Solid(kind = SolidKind.SPHERE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_tin_soup_closest_which_solid),
                options = wordOptions(
                    Res.string.learn_shape_cube_name,
                    Res.string.learn_shape_cone_name,
                    Res.string.learn_shape_sphere_name,
                    Res.string.learn_shape_cylinder_name,
                ),
                correctIndex = 3,
                explanation = words(Res.string.learn_shared_two_circular_ends_curved_side),
                visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_solid_shapes_q6_prompt),
                options = wordOptions(
                    Res.string.learn_shape_cube_name,
                    Res.string.learn_shape_circle_name,
                    Res.string.learn_shape_sphere_name,
                    Res.string.learn_shape_cylinder_name,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_solid_shapes_q6_explanation),
                visual = CircleFigure(showRadius = false, reveal = false),
            ),
        ),
    )

    private val angles = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "angles",
        title = Res.string.learn_unit_geometry_angles_title,
        summary = Res.string.learn_unit_geometry_angles_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-angles",
                title = Res.string.learn_g35_geometry_angles_title,
                summary = Res.string.learn_g35_geometry_angles_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_geometry_angles_s1_body),
                        visual = AngleFigure(degrees = 50),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_angles_s2_body),
                        formula = words(Res.string.learn_g35_geometry_angles_s2_formula),
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_kind_angle),
                        options = wordOptions(
                            Res.string.learn_shared_obtuse,
                            Res.string.learn_shared_acute,
                            Res.string.learn_shared_reflex,
                            Res.string.learn_g35_geometry_angles_s3_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_geometry_angles_s3_explanation),
                        visual = AngleFigure(degrees = 40, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_angles_s4_body),
                        visual = AngleFigure(degrees = 120),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_what_kind_angle),
                        options = wordOptions(
                            Res.string.learn_shared_acute,
                            Res.string.learn_shared_right_2,
                            Res.string.learn_shared_obtuse,
                            Res.string.learn_shared_reflex,
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g35_geometry_angles_s5_explanation),
                        visual = AngleFigure(degrees = 120, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_geometry_angles_s6_question),
                        answer = "90",
                        explanation = words(Res.string.learn_g35_geometry_angles_s6_explanation),
                        visual = AngleFigure(degrees = 90, labels = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-angles-adding",
                title = Res.string.learn_geometry_angles_adding_title,
                summary = Res.string.learn_geometry_angles_adding_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_angles_adding_s1_body),
                        formula = math("{a:a} + {b:b} = 180"),
                        visual = AngleFigure(degrees = 130, supplement = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_angles_adding_s2_question),
                        answer = "50",
                        explanation = math("180 - {a:130}"),
                        visual = AngleFigure(degrees = 130, supplement = true, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_angles_adding_s3_question),
                        formula = math("65 + ? = 180"),
                        options = mathOptions("25", "115", "125", "295"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_angles_adding_s3_explanation),
                        visual = AngleFigure(degrees = 65, supplement = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_angles_adding_s4_body),
                        formula = math("a + b + c = 360"),
                        visual = AngleFigure(degrees = 100),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_angles_adding_s5_body),
                        formula = math("a + b + c = 180"),
                        visual = RightTriangle(a = 4, b = 3, angle = 37, labels = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_angles_adding_s6_question),
                        options = mathOptions("55", "65", "75", "105"),
                        correctIndex = 1,
                        explanation = math("180 - 40 - 75"),
                        visual = Triangle(kind = TriKind.SCALENE),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-angles-turns",
                title = Res.string.learn_geometry_angles_turns_title,
                summary = Res.string.learn_geometry_angles_turns_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_angles_turns_s1_body),
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_angles_turns_s2_question),
                        options = mathOptions("2", "3", "4", "6"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_angles_turns_s2_explanation),
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_angles_turns_s3_body),
                        visual = AngleFigure(degrees = 90, supplement = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_angles_turns_s4_question),
                        answer = "270",
                        explanation = words(Res.string.learn_geometry_angles_turns_s4_explanation),
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_angles_turns_s5_body),
                        formula = math("360 - 170 = 190"),
                        visual = AngleFigure(degrees = 170, wholeTurn = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_angles_turns_s6_question),
                        formula = math("360 - 170 = ?"),
                        options = mathOptions("10", "100", "190", "350"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_angles_turns_s6_explanation),
                        visual = AngleFigure(degrees = 170, wholeTurn = true, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_angles_q1_prompt),
                options = mathOptions("45", "90", "180", "360"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_angles_q1_explanation),
                visual = AngleFigure(degrees = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_what_kind_angle),
                options = wordOptions(
                    Res.string.learn_shared_acute,
                    Res.string.learn_shared_right_2,
                    Res.string.learn_shared_obtuse,
                    Res.string.learn_shared_reflex,
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_angles_q2_explanation),
                visual = AngleFigure(degrees = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_angles_q3_prompt),
                options = mathOptions("70", "80", "90", "250"),
                correctIndex = 0,
                explanation = filled(Res.string.learn_t_take_away, "180", "110"),
                visual = AngleFigure(degrees = 110, supplement = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_angles_q4_prompt),
                options = mathOptions("45", "55", "65", "125"),
                correctIndex = 1,
                explanation = math("180 - 125"),
                visual = RightTriangle(a = 7, b = 5, labels = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_angles_q5_prompt),
                options = mathOptions("90", "180", "270", "360"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_geometry_angles_q5_explanation),
                visual = AngleFigure(degrees = 100, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_angles_q6_prompt),
                options = mathOptions("30", "110", "210", "310"),
                correctIndex = 2,
                explanation = filled(Res.string.learn_t_take_away, "360", "150"),
                visual = AngleFigure(degrees = 150, reveal = false),
            ),
        ),
    )

    private val quadrilaterals = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "quadrilaterals",
        title = Res.string.learn_unit_geometry_quadrilaterals_title,
        summary = Res.string.learn_unit_geometry_quadrilaterals_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-quadrilaterals",
                title = Res.string.learn_g35_geometry_quadrilaterals_title,
                summary = Res.string.learn_g35_geometry_quadrilaterals_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_geometry_quadrilaterals_s1_body),
                        visual = Polygon(sides = 4),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_quadrilaterals_s2_body),
                        visual = Quadrilateral(kind = QuadKind.PARALLELOGRAM),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_geometry_quadrilaterals_s3_question),
                        options = wordOptions(
                            Res.string.learn_g35_geometry_quadrilaterals_s3_o1,
                            Res.string.learn_shared_all_four_sides_equal,
                            Res.string.learn_shared_one_pair_parallel_sides,
                            Res.string.learn_g35_geometry_quadrilaterals_s3_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_geometry_quadrilaterals_s3_explanation),
                        visual = Quadrilateral(kind = QuadKind.RHOMBUS),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_quadrilaterals_s4_body),
                        formula = math("180 + {b:180} = 360"),
                        visual = Polygon(sides = 4),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_geometry_quadrilaterals_s5_question),
                        answer = "360",
                        explanation = words(Res.string.learn_g35_geometry_quadrilaterals_s5_explanation),
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_geometry_quadrilaterals_s6_question),
                        formula = math("90 + 90 + 100 + ? = 360"),
                        options = mathOptions("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = math("360 - 280"),
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-quadrilaterals-triangles",
                title = Res.string.learn_geometry_quadrilaterals_triangles_title,
                summary = Res.string.learn_geometry_quadrilaterals_triangles_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_triangles_s1_body),
                        visual = Triangle(kind = TriKind.SCALENE),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_triangles_s2_body),
                        visual = RightTriangle(a = 4, b = 3, labels = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_quadrilaterals_triangles_s3_question),
                        options = wordOptions(
                            Res.string.learn_shape_equilateral_name,
                            Res.string.learn_shape_isosceles_name,
                            Res.string.learn_shape_scalene_name,
                            Res.string.learn_shared_right,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_quadrilaterals_triangles_s3_explanation),
                        visual = Triangle(kind = TriKind.ISOSCELES),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_triangles_s4_body),
                        formula = math("180 / {b:3} = 60"),
                        visual = Symmetry(sides = 3, lines = 3),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_quadrilaterals_triangles_s5_question),
                        answer = "60",
                        explanation = filled(Res.string.learn_t_shared_equally_between_three, "180"),
                        visual = Triangle(kind = TriKind.EQUILATERAL),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_quadrilaterals_triangles_s6_question),
                        options = wordOptions(
                            Res.string.learn_geometry_quadrilaterals_triangles_s6_o1,
                            Res.string.learn_geometry_quadrilaterals_triangles_s6_o2,
                            Res.string.learn_geometry_quadrilaterals_triangles_s6_o3,
                            Res.string.learn_geometry_quadrilaterals_triangles_s6_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_quadrilaterals_triangles_s6_explanation),
                        visual = RightTriangle(a = 4, b = 4, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-quadrilaterals-family",
                title = Res.string.learn_geometry_quadrilaterals_family_title,
                summary = Res.string.learn_geometry_quadrilaterals_family_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_family_s1_body),
                        visual = AreaGrid(cols = 4, rows = 4, showArea = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_shape_two_pairs_parallel),
                        options = wordOptions(
                            Res.string.learn_shape_trapezium_name,
                            Res.string.learn_shape_rhombus_name,
                            Res.string.learn_shape_kite_name,
                            Res.string.learn_shape_triangle_name,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_quadrilaterals_family_s2_explanation),
                        visual = Quadrilateral(kind = QuadKind.RHOMBUS),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_family_s3_body),
                        visual = Quadrilateral(kind = QuadKind.KITE),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_quadrilaterals_family_s4_question),
                        options = wordOptions(
                            Res.string.learn_geometry_quadrilaterals_family_s4_o1,
                            Res.string.learn_geometry_quadrilaterals_family_s4_o2,
                            Res.string.learn_geometry_quadrilaterals_family_s4_o3,
                            Res.string.learn_geometry_quadrilaterals_family_s4_o4,
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_quadrilaterals_family_s4_explanation),
                        visual = Quadrilateral(kind = QuadKind.RECTANGLE),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_quadrilaterals_family_s5_body),
                        visual = Quadrilateral(kind = QuadKind.TRAPEZIUM),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_quadrilaterals_family_s6_question),
                        answer = "2",
                        explanation = words(Res.string.learn_geometry_quadrilaterals_family_s6_explanation),
                        visual = Quadrilateral(kind = QuadKind.PARALLELOGRAM),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_shape_two_pairs_parallel),
                options = wordOptions(
                    Res.string.learn_shape_trapezium_name,
                    Res.string.learn_shape_rhombus_name,
                    Res.string.learn_shape_kite_name,
                    Res.string.learn_shape_triangle_name,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_quadrilaterals_q1_explanation),
                visual = Quadrilateral(kind = QuadKind.RHOMBUS),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_quadrilaterals_q2_prompt),
                options = mathOptions("180", "270", "360", "540"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_quadrilaterals_q2_explanation),
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_quadrilaterals_q3_prompt),
                options = wordOptions(
                    Res.string.learn_shape_scalene_name,
                    Res.string.learn_shape_isosceles_name,
                    Res.string.learn_shape_equilateral_name,
                    Res.string.learn_shared_right,
                ),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_quadrilaterals_q3_explanation),
                visual = Polygon(sides = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_quadrilaterals_q4_prompt),
                options = mathOptions("60", "70", "80", "90"),
                correctIndex = 1,
                explanation = filled(Res.string.learn_t_take_away, "360", "290"),
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_quadrilaterals_q5_prompt),
                options = wordOptions(
                    Res.string.learn_unit_geometry_quadrilaterals_q5_o1,
                    Res.string.learn_unit_geometry_quadrilaterals_q5_o2,
                    Res.string.learn_shared_one_pair_parallel_sides,
                    Res.string.learn_unit_geometry_quadrilaterals_q5_o4,
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_quadrilaterals_q5_explanation),
                visual = AreaGrid(cols = 4, rows = 4, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_quadrilaterals_q6_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_degrees, 45),
                    counted(Res.plurals.learn_opt_degrees, 60),
                    counted(Res.plurals.learn_opt_degrees, 90),
                    counted(Res.plurals.learn_opt_degrees, 120),
                ),
                correctIndex = 1,
                explanation = filled(Res.string.learn_t_shared_equally_between_three, "180"),
                visual = Polygon(sides = 3, reveal = false),
            ),
        ),
    )

    private val symmetry = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "symmetry",
        title = Res.string.learn_unit_geometry_symmetry_title,
        summary = Res.string.learn_unit_geometry_symmetry_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-geometry-symmetry",
                title = Res.string.learn_g35_geometry_symmetry_title,
                summary = Res.string.learn_g35_geometry_symmetry_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_geometry_symmetry_s1_body),
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_symmetry_s2_body),
                        visual = Symmetry(sides = 4, lines = 4),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_geometry_symmetry_s3_question),
                        answer = "3",
                        explanation = words(Res.string.learn_shared_one_from_each_corner_opposite),
                        visual = Symmetry(sides = 3, lines = 3, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_geometry_symmetry_s4_question),
                        options = mathOptions("0", "1", "2", "4"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g35_geometry_symmetry_s4_explanation),
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_geometry_symmetry_s5_body),
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_geometry_symmetry_s6_question),
                        options = mathOptions("3", "4", "5", "10"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g35_geometry_symmetry_s6_explanation),
                        visual = Symmetry(sides = 5, lines = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-symmetry-rotational",
                title = Res.string.learn_geometry_symmetry_rotational_title,
                summary = Res.string.learn_geometry_symmetry_rotational_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_rotational_s1_body),
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_rotational_s2_body),
                        visual = Symmetry(sides = 4, lines = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_symmetry_rotational_s3_question),
                        options = mathOptions("1", "2", "3", "6"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_symmetry_rotational_s3_explanation),
                        visual = Symmetry(sides = 3, lines = 3, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_rotational_s4_body),
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_symmetry_rotational_s5_question),
                        answer = "6",
                        explanation = words(Res.string.learn_geometry_symmetry_rotational_s5_explanation),
                        visual = Symmetry(sides = 6, lines = 6, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_symmetry_rotational_s6_question),
                        options = listOf(
                            filled(Res.string.learn_opt_always, "1"),
                            words(Res.string.learn_geometry_symmetry_rotational_s6_o2),
                            words(Res.string.learn_geometry_symmetry_rotational_s6_o3),
                            words(Res.string.learn_geometry_symmetry_rotational_s6_o4),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_symmetry_rotational_s6_explanation),
                        visual = Symmetry(sides = 5, lines = 5, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-symmetry-around",
                title = Res.string.learn_geometry_symmetry_around_title,
                summary = Res.string.learn_geometry_symmetry_around_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_around_s1_body),
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_capital_letter_vertical_line),
                        options = mathOptions("F", "A", "R", "P"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_shared_fold_down_middle_halves_match),
                        visual = Symmetry(sides = 3, lines = 1, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_around_s3_body),
                        visual = Symmetry(sides = 3, lines = 1),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_symmetry_around_s4_question),
                        options = mathOptions("L", "N", "E", "S"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_symmetry_around_s4_explanation),
                        visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_symmetry_around_s5_body),
                        visual = Symmetry(sides = 5, lines = 5),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_symmetry_around_s6_question),
                        answer = "1",
                        explanation = words(Res.string.learn_geometry_symmetry_around_s6_explanation),
                        visual = Symmetry(sides = 3, lines = 1, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_symmetry_q1_prompt),
                options = mathOptions("1", "2", "4", "8"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_symmetry_q1_explanation),
                visual = Symmetry(sides = 4, lines = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_symmetry_q2_prompt),
                options = mathOptions("0", "1", "2", "4"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_symmetry_q2_explanation),
                visual = Symmetry(sides = 4, lines = 2, rectangle = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_symmetry_q3_prompt),
                options = mathOptions("1", "2", "3", "6"),
                correctIndex = 2,
                explanation = words(Res.string.learn_shared_one_from_each_corner_opposite),
                visual = Symmetry(sides = 3, lines = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_symmetry_q4_prompt),
                options = mathOptions("3", "4", "5", "10"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_symmetry_q4_explanation),
                visual = Symmetry(sides = 5, lines = 5, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_capital_letter_vertical_line),
                options = mathOptions("F", "A", "R", "P"),
                correctIndex = 1,
                explanation = words(Res.string.learn_shared_fold_down_middle_halves_match),
                visual = Symmetry(sides = 3, lines = 1, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_symmetry_q6_prompt),
                options = mathOptions("3", "4", "6", "12"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_symmetry_q6_explanation),
                visual = Symmetry(sides = 6, lines = 6, reveal = false),
            ),
        ),
    )

    private val perimeterAndArea = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "perimeter-and-area",
        title = Res.string.learn_unit_geometry_perimeter_and_area_title,
        summary = Res.string.learn_unit_geometry_perimeter_and_area_summary,
        level = GradeLevel.GRADES_3_5,
        lessons = listOf(
            LessonSpec(
                id = "g35-measurement-perimeter",
                title = Res.string.learn_g35_measurement_perimeter_title,
                summary = Res.string.learn_g35_measurement_perimeter_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_measurement_perimeter_s1_body),
                        visual = AreaGrid(cols = 6, rows = 4, showArea = false, showPerimeter = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_measurement_perimeter_s2_body),
                        formula = words(Res.string.learn_g35_measurement_perimeter_s2_formula),
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Worked(
                        problem = words(Res.string.learn_g35_measurement_perimeter_s3_problem),
                        lines = listOf(
                            math("8 + 5 = 13"),
                            words(Res.string.learn_g35_measurement_perimeter_s3_l2),
                            math("13 x 2 = 26"),
                        ),
                        result = words(Res.string.learn_g35_measurement_perimeter_s3_result),
                        visual = AreaGrid(cols = 8, rows = 5, showArea = false, showPerimeter = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_measurement_perimeter_s4_question),
                        formula = math("4 x {b:9} = ?"),
                        answer = "36",
                        explanation = words(Res.string.learn_g35_measurement_perimeter_s4_explanation),
                        visual = AreaGrid(cols = 9, rows = 9, showArea = false, showPerimeter = true, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_measurement_perimeter_s5_question),
                        options = listOf(
                            filled(Res.string.learn_opt_rectangle_by, "6", "4"),
                            filled(Res.string.learn_opt_square_by, "5", "5"),
                            words(Res.string.learn_shared_both_them),
                            words(Res.string.learn_g35_measurement_perimeter_s5_o4),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g35_measurement_perimeter_s5_explanation),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_measurement_perimeter_s6_body),
                        visual = AreaGrid(cols = 5, rows = 3, showArea = false, showPerimeter = true),
                    ),
                ),
            ),
            LessonSpec(
                id = "g35-measurement-area",
                title = Res.string.learn_g35_measurement_area_title,
                summary = Res.string.learn_g35_measurement_area_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g35_measurement_area_s1_body),
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_measurement_area_s2_body),
                        formula = words(Res.string.learn_g35_measurement_area_s2_formula),
                        visual = AreaGrid(cols = 6, rows = 3),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g35_measurement_area_s3_question),
                        formula = math("6 x {b:3} = ?"),
                        answer = "18",
                        explanation = filled(Res.string.learn_t_rows, "3", "6"),
                        visual = AreaGrid(cols = 6, rows = 3, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_measurement_area_s4_question),
                        options = listOf(
                            math("cm"),
                            words(Res.string.learn_g35_measurement_area_s4_o2),
                            words(Res.string.learn_g35_measurement_area_s4_o3),
                            math("ml"),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g35_measurement_area_s4_explanation),
                        visual = AreaGrid(cols = 4, rows = 4, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g35_measurement_area_s5_body),
                        visual = AreaGrid(cols = 4, rows = 4, unit = "m"),
                    ),
                    Choice(
                        question = words(Res.string.learn_g35_measurement_area_s6_question),
                        formula = math("24 / {b:4} = ?"),
                        options = mathOptions("4 cm", "6 cm", "8 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = filled(Res.string.learn_t_divided, "24", "4"),
                        visual = AreaGrid(cols = 6, rows = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-area-compound",
                title = Res.string.learn_geometry_area_compound_title,
                summary = Res.string.learn_geometry_area_compound_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_area_compound_s1_body),
                        visual = AreaGrid(cols = 7, rows = 4),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_area_compound_s2_body),
                        formula = math("A = A1 + A2"),
                        visual = AreaGrid(cols = 4, rows = 3),
                    ),
                    Worked(
                        problem = words(Res.string.learn_geometry_area_compound_s3_problem),
                        lines = listOf(
                            words(Res.string.learn_geometry_area_compound_s3_l1),
                            math("5 x 2 = 10"),
                            math("3 x 2 = 6"),
                            math("10 + 6"),
                        ),
                        result = words(Res.string.learn_geometry_area_compound_s3_result),
                        visual = AreaGrid(cols = 5, rows = 2),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_area_compound_s4_question),
                        formula = math("5 x 4 - 2 x 2 = ?"),
                        answer = "16",
                        explanation = words(Res.string.learn_geometry_area_compound_s4_explanation),
                        visual = AreaGrid(cols = 5, rows = 4, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_area_compound_s5_question),
                        formula = math("3 x 2 + 4 x 2 = ?"),
                        options = listOf(
                            counted(Res.plurals.learn_opt_square_cm, 12),
                            counted(Res.plurals.learn_opt_square_cm, 14),
                            counted(Res.plurals.learn_opt_square_cm, 16),
                            counted(Res.plurals.learn_opt_square_cm, 20),
                        ),
                        correctIndex = 1,
                        explanation = filled(Res.string.learn_t_squares_squares, "6", "8"),
                        visual = AreaGrid(cols = 4, rows = 2, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_area_compound_s6_body),
                        visual = AreaGrid(cols = 6, rows = 4),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q1_prompt),
                options = mathOptions("10 cm", "20 cm", "21 cm", "14 cm"),
                correctIndex = 1,
                explanation = filled(Res.string.learn_t_doubled, "7", "3"),
                visual = AreaGrid(cols = 7, rows = 3, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q2_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_square_cm, 10),
                    counted(Res.plurals.learn_opt_square_cm, 20),
                    counted(Res.plurals.learn_opt_square_cm, 21),
                    counted(Res.plurals.learn_opt_square_cm, 14),
                ),
                correctIndex = 2,
                explanation = filled(Res.string.learn_t_rows, "3", "7"),
                visual = AreaGrid(cols = 7, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q3_prompt),
                options = mathOptions("4 cm", "6 cm", "8 cm", "16 cm"),
                correctIndex = 2,
                explanation = filled(Res.string.learn_t_divided, "32", "4"),
                visual = AreaGrid(cols = 8, rows = 8, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q4_prompt),
                options = listOf(
                    math("12 m"),
                    counted(Res.plurals.learn_opt_square_m, 12),
                    math("12 ml"),
                    math("12 kg"),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_perimeter_and_area_q4_explanation),
                visual = AreaGrid(cols = 4, rows = 3, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q5_prompt),
                options = mathOptions("11 cm", "18 cm", "20 cm", "22 cm"),
                correctIndex = 3,
                explanation = filled(Res.string.learn_t_doubled, "9", "2"),
                visual = AreaGrid(cols = 9, rows = 2, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_perimeter_and_area_q6_prompt),
                options = listOf(
                    counted(Res.plurals.learn_opt_square_cm, 12),
                    counted(Res.plurals.learn_opt_square_cm, 14),
                    counted(Res.plurals.learn_opt_square_cm, 16),
                    counted(Res.plurals.learn_opt_square_cm, 18),
                ),
                correctIndex = 2,
                explanation = filled(Res.string.learn_t_squares_squares, "12", "4"),
            ),
        ),
    )

    private val pythagoras = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "pythagoras",
        title = Res.string.learn_unit_geometry_pythagoras_title,
        summary = Res.string.learn_unit_geometry_pythagoras_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-pythagoras",
                title = Res.string.learn_g68_geometry_pythagoras_title,
                summary = Res.string.learn_g68_geometry_pythagoras_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_geometry_pythagoras_s1_body),
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_geometry_pythagoras_s2_body),
                        formula = math("{a:a}² + {b:b}² = c²"),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Worked(
                        problem = words(Res.string.learn_g68_geometry_pythagoras_s3_problem),
                        lines = listOf(
                            words(Res.string.learn_g68_geometry_pythagoras_s3_l1),
                            math("{b:9} + 16 = 25"),
                            words(Res.string.learn_shared_take_square_root),
                        ),
                        result = math("c = 5"),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_shared_how_long_hypotenuse),
                        answer = "10",
                        explanation = math("36 + 64 = 100"),
                        visual = RightTriangle(a = 8, b = 6, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_geometry_pythagoras_s5_body),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_these_could_be_three),
                        options = mathOptions("2, 3, 4", "5, 12, 13", "4, 5, 6", "1, 2, 3"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g68_geometry_pythagoras_s6_explanation),
                        visual = RightTriangle(a = 12, b = 5, labels = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-pythagoras-finding",
                title = Res.string.learn_geometry_pythagoras_finding_title,
                summary = Res.string.learn_geometry_pythagoras_finding_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_pythagoras_finding_s1_body),
                        formula = math("{a:a}² = c² - {b:b}²"),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_pythagoras_finding_s2_question),
                        answer = "8",
                        explanation = math("100 - 36 = 64"),
                        visual = RightTriangle(a = 6, b = 8, unknown = Side.B),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_pythagoras_finding_s3_question),
                        options = mathOptions("8 m", "10 m", "12 m", "14 m"),
                        correctIndex = 2,
                        explanation = math("169 - 25 = 144"),
                        visual = RightTriangle(a = 5, b = 12, unknown = Side.B),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_pythagoras_finding_s4_body),
                        visual = RightTriangle(a = 5, b = 12),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_hypotenuse_short_side_long, "25", "7"),
                        options = mathOptions("18", "24", "26", "32"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_pythagoras_finding_s5_explanation),
                        visual = RightTriangle(a = 7, b = 24, unknown = Side.B),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_hypotenuse_short_side_long, "17", "8"),
                        answer = "15",
                        explanation = math("289 - 64 = 225"),
                        visual = RightTriangle(a = 8, b = 15, unknown = Side.B),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-pythagoras-using",
                title = Res.string.learn_geometry_pythagoras_using_title,
                summary = Res.string.learn_geometry_pythagoras_using_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_pythagoras_using_s1_body),
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Worked(
                        problem = filled(Res.string.learn_t_rectangle_cm_cm_long, "12", "9"),
                        lines = listOf(
                            words(Res.string.learn_geometry_pythagoras_using_s2_l1),
                            words(Res.string.learn_geometry_pythagoras_using_s2_l2),
                            math("144 + {b:81} = 225"),
                            words(Res.string.learn_shared_take_square_root),
                        ),
                        result = math("15 cm"),
                        visual = RightTriangle(a = 12, b = 9, showSquares = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_pythagoras_using_s3_question),
                        options = mathOptions("10 m", "20 m", "30 m", "50 m"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_pythagoras_using_s3_explanation),
                        visual = RightTriangle(a = 40, b = 30, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_pythagoras_using_s4_body),
                        formula = math("{b:9} + 16 = 25"),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_pythagoras_using_s5_question),
                        options = wordOptions(
                            Res.string.learn_geometry_pythagoras_using_s5_o1,
                            Res.string.learn_geometry_pythagoras_using_s5_o2,
                            Res.string.learn_geometry_pythagoras_using_s5_o3,
                            Res.string.learn_geometry_pythagoras_using_s5_o4,
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_pythagoras_using_s5_explanation),
                        visual = RightTriangle(a = 15, b = 8, labels = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_pythagoras_using_s6_question),
                        answer = "20",
                        explanation = math("256 + 144 = 400"),
                        visual = RightTriangle(a = 16, b = 12, unknown = Side.HYPOTENUSE),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_shared_how_long_hypotenuse),
                options = mathOptions("13", "14", "15", "17"),
                correctIndex = 0,
                explanation = math("25 + 144 = 169"),
                visual = RightTriangle(a = 12, b = 5, unknown = Side.HYPOTENUSE),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_pythagoras_q2_prompt),
                options = mathOptions("6", "8", "10", "12"),
                correctIndex = 1,
                explanation = math("100 - 36 = 64"),
                visual = RightTriangle(a = 6, b = 8, unknown = Side.B),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_these_could_be_three),
                options = mathOptions("2, 3, 4", "6, 8, 10", "4, 5, 6", "1, 2, 3"),
                correctIndex = 1,
                explanation = math("36 + 64 = 100"),
                visual = RightTriangle(a = 8, b = 6, labels = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_rectangle_cm_cm_long, "12", "9"),
                options = mathOptions("13 cm", "15 cm", "21 cm", "144 cm"),
                correctIndex = 1,
                explanation = math("144 + 81 = 225"),
                visual = RightTriangle(a = 12, b = 9, unknown = Side.HYPOTENUSE),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_pythagoras_q5_prompt),
                options = wordOptions(
                    Res.string.learn_unit_geometry_pythagoras_q5_o1,
                    Res.string.learn_unit_geometry_pythagoras_q5_o2,
                    Res.string.learn_unit_geometry_pythagoras_q5_o3,
                    Res.string.learn_unit_geometry_pythagoras_q5_o4,
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_pythagoras_q5_explanation),
                visual = RightTriangle(a = 4, b = 3, labels = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_hypotenuse_short_side_long, "26", "10"),
                options = mathOptions("16", "20", "24", "28"),
                correctIndex = 2,
                explanation = math("676 - 100 = 576"),
                visual = RightTriangle(a = 10, b = 24, unknown = Side.B),
            ),
        ),
    )

    private val circles = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "circles",
        title = Res.string.learn_unit_geometry_circles_title,
        summary = Res.string.learn_unit_geometry_circles_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-circles",
                title = Res.string.learn_g68_geometry_circles_title,
                summary = Res.string.learn_g68_geometry_circles_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_geometry_circles_s1_body),
                        formula = math("{b:d} = 2r"),
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_geometry_circles_s2_question),
                        formula = math("2 x 5 = ?"),
                        answer = "10",
                        explanation = words(Res.string.learn_g68_geometry_circles_s2_explanation),
                        visual = CircleFigure(radius = 5, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_geometry_circles_s3_body),
                        formula = math("C = pi x d"),
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_geometry_circles_s4_body),
                        visual = CircleFigure(radius = 3, sweepCircumference = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_geometry_circles_s5_question),
                        options = mathOptions("78.5 cm", "157 cm", "250 cm", "314 cm"),
                        correctIndex = 1,
                        explanation = math("3.14 x 50"),
                        visual = CircleFigure(sweepCircumference = true, reveal = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_geometry_circles_s6_question),
                        formula = math("18 / {b:2} = ?"),
                        answer = "9",
                        explanation = words(Res.string.learn_shared_half_diameter),
                        visual = CircleFigure(showDiameter = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circles-area",
                title = Res.string.learn_geometry_circles_area_title,
                summary = Res.string.learn_geometry_circles_area_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_circles_area_s1_body),
                        formula = math("A = pi x r²"),
                        visual = CircleFigure(radius = 10, fillArea = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circles_area_s2_body),
                        formula = math("A = pi x (r x r)"),
                        visual = CircleFigure(radius = 4, fillArea = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circles_area_s3_question),
                        options = mathOptions("31.4", "62.8", "314", "628"),
                        correctIndex = 2,
                        explanation = math("3.14 x 100"),
                        visual = CircleFigure(fillArea = true, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circles_area_s4_question),
                        formula = math("3.14 x 4 x 4 = ?"),
                        options = mathOptions("12.56", "25.12", "50.24", "200.96"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_circles_area_s4_explanation),
                        visual = CircleFigure(radius = 4, fillArea = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circles_area_s5_body),
                        formula = math("C = 2 pi r, A = pi r²"),
                        visual = CircleFigure(radius = 5, fillArea = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_circle_s_radius_doubled_area),
                        options = mathOptions("2", "3", "4", "8"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_circles_area_s6_explanation),
                        visual = CircleFigure(radius = 8, fillArea = true, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circles-using",
                title = Res.string.learn_geometry_circles_using_title,
                summary = Res.string.learn_geometry_circles_using_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_circles_using_s1_body),
                        visual = CircleFigure(radius = 6, showDiameter = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circles_using_s2_question),
                        options = mathOptions("5 cm", "10 cm", "15.7 cm", "20 cm"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_circles_using_s2_explanation),
                        visual = CircleFigure(sweepCircumference = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circles_using_s3_body),
                        formula = math("d = C / pi"),
                        visual = CircleFigure(radius = 5, sweepCircumference = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_circles_using_s4_question),
                        formula = math("78.5 / {b:3.14} = ?"),
                        answer = "25",
                        explanation = words(Res.string.learn_geometry_circles_using_s4_explanation),
                        visual = CircleFigure(radius = 5, fillArea = true, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circles_using_s5_body),
                        visual = CircleFigure(radius = 5, showDiameter = true),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circles_using_s6_question),
                        options = mathOptions("15.7 cm", "31.4 cm", "5 cm", "10 cm"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_circles_using_s6_explanation),
                        visual = CircleFigure(radius = 5, sweepCircumference = true, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circles_q1_prompt),
                options = mathOptions("6 cm", "9 cm", "18 cm", "36 cm"),
                correctIndex = 1,
                explanation = words(Res.string.learn_shared_half_diameter),
                visual = CircleFigure(showDiameter = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circles_q2_prompt),
                options = mathOptions("15.7 cm", "31.4 cm", "78.5 cm", "314 cm"),
                correctIndex = 1,
                explanation = math("pi x d"),
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circles_q3_prompt),
                options = mathOptions("12.56", "25.12", "50.24", "100.48"),
                correctIndex = 2,
                explanation = math("3.14 x 16"),
                visual = CircleFigure(fillArea = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circles_q4_prompt),
                options = mathOptions("pi x r²", "2 x pi x r", "pi x r", "4 x pi x r"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_circles_q4_explanation),
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_circle_s_radius_doubled_area),
                options = mathOptions("2", "3", "4", "8"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_circles_q5_explanation),
                visual = CircleFigure(radius = 8, fillArea = true, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circles_q6_prompt),
                options = mathOptions("10 cm", "20 cm", "31.4 cm", "40 cm"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_circles_q6_explanation),
                visual = CircleFigure(sweepCircumference = true, reveal = false),
            ),
        ),
    )

    private val volume = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "volume",
        title = Res.string.learn_unit_geometry_volume_title,
        summary = Res.string.learn_unit_geometry_volume_summary,
        level = GradeLevel.GRADES_6_8,
        lessons = listOf(
            LessonSpec(
                id = "g68-geometry-volume",
                title = Res.string.learn_g68_geometry_volume_title,
                summary = Res.string.learn_g68_geometry_volume_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g68_geometry_volume_s1_body),
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Concept(
                        body = words(Res.string.learn_g68_geometry_volume_s2_body),
                        formula = words(Res.string.learn_g68_geometry_volume_s2_formula),
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g68_geometry_volume_s3_question),
                        formula = math("4 x 3 x 2 = ?"),
                        answer = "24",
                        explanation = words(Res.string.learn_g68_geometry_volume_s3_explanation),
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
                    ),
                    Worked(
                        problem = words(Res.string.learn_g68_geometry_volume_s4_problem),
                        lines = listOf(
                            words(Res.string.learn_g68_geometry_volume_s4_l1),
                            words(Res.string.learn_g68_geometry_volume_s4_l2),
                            math("6 x 9"),
                        ),
                        result = math("V = 54 cm³"),
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_geometry_volume_s5_question),
                        options = mathOptions("2 x pi x r x h", "pi x r² x h", "pi x d x h", "r² + h"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g68_geometry_volume_s5_explanation),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_g68_geometry_volume_s6_question),
                        options = mathOptions("2", "4", "6", "8"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_g68_geometry_volume_s6_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-volume-surface",
                title = Res.string.learn_geometry_volume_surface_title,
                summary = Res.string.learn_geometry_volume_surface_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_volume_surface_s1_body),
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_volume_surface_s2_body),
                        formula = words(Res.string.learn_geometry_volume_surface_s2_formula),
                        visual = Solid(kind = SolidKind.PRISM, counts = true),
                    ),
                    Numeric(
                        question = filled(Res.string.learn_t_cube_edges_cm_surface, "3"),
                        formula = math("6 x 3 x 3 = ?"),
                        answer = "54",
                        explanation = words(Res.string.learn_geometry_volume_surface_s3_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_volume_surface_s4_question),
                        options = mathOptions("40", "76", "80", "100"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_volume_surface_s4_explanation),
                        visual = Solid(kind = SolidKind.PRISM, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_volume_surface_s5_body),
                        visual = Solid(kind = SolidKind.PRISM),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_volume_surface_s6_question),
                        options = wordOptions(
                            Res.string.learn_geometry_volume_surface_s6_o1,
                            Res.string.learn_geometry_volume_surface_s6_o2,
                            Res.string.learn_shared_both_them,
                            Res.string.learn_geometry_volume_surface_s6_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_volume_surface_s6_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-volume-cylinders",
                title = Res.string.learn_geometry_volume_cylinders_title,
                summary = Res.string.learn_geometry_volume_cylinders_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_volume_cylinders_s1_body),
                        formula = math("V = pi x r² x h"),
                        visual = Solid(kind = SolidKind.CYLINDER, counts = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_volume_cylinders_s2_question),
                        formula = math("20 x {b:6} = ?"),
                        answer = "120",
                        explanation = words(Res.string.learn_geometry_volume_cylinders_s2_explanation),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_volume_cylinders_s3_body),
                        formula = words(Res.string.learn_geometry_volume_cylinders_s3_formula),
                        visual = Solid(kind = SolidKind.CUBE),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_volume_cylinders_s4_question),
                        options = mathOptions("0.1", "1", "10", "100"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_volume_cylinders_s4_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_volume_cylinders_s5_body),
                        visual = Solid(kind = SolidKind.CYLINDER),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_volume_cylinders_s6_question),
                        options = mathOptions("2", "4", "6", "8"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_volume_cylinders_s6_explanation),
                        visual = Solid(kind = SolidKind.CYLINDER, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_volume_q1_prompt),
                options = mathOptions("12", "20", "47", "60"),
                correctIndex = 3,
                explanation = math("5 x 4 x 3"),
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_volume_q2_prompt),
                options = listOf(
                    words(Res.string.learn_unit_geometry_volume_q2_o1),
                    words(Res.string.learn_unit_geometry_volume_q2_o2),
                    math("pi x r²"),
                    math("2 x pi x r"),
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_volume_q2_explanation),
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_volume_q3_prompt),
                options = mathOptions("4", "6", "8", "12"),
                correctIndex = 2,
                explanation = math("2 x 2 x 2"),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_cube_edges_cm_surface, "4"),
                options = mathOptions("16", "64", "96", "128"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_volume_q4_explanation),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_volume_q5_prompt),
                options = wordOptions(
                    Res.string.learn_unit_geometry_volume_q5_o1,
                    Res.string.learn_unit_geometry_volume_q5_o2,
                    Res.string.learn_unit_geometry_volume_q5_o3,
                    Res.string.learn_unit_geometry_volume_q5_o4,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_volume_q5_explanation),
                visual = Solid(kind = SolidKind.PRISM, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_volume_q6_prompt),
                options = mathOptions("10", "100", "1000", "10000"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_volume_q6_explanation),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
        ),
    )

    private val similarity = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "similarity",
        title = Res.string.learn_unit_geometry_similarity_title,
        summary = Res.string.learn_unit_geometry_similarity_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-similarity",
                title = Res.string.learn_g910_geometry_similarity_title,
                summary = Res.string.learn_g910_geometry_similarity_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g910_geometry_similarity_s1_body),
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_similarity_s2_body),
                        formula = words(Res.string.learn_g910_geometry_similarity_s2_formula),
                        visual = RightTriangle(a = 8, b = 6),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_matching_sides_cm_cm, "4", "10"),
                        options = mathOptions("0.4", "2.5", "6", "14"),
                        correctIndex = 1,
                        explanation = math("10 / 4"),
                        visual = RightTriangle(a = 10, b = 6, labels = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g910_geometry_similarity_s4_question),
                        answer = "15",
                        explanation = math("5 x 3"),
                        visual = RightTriangle(a = 4, b = 3, unknown = Side.HYPOTENUSE),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_similarity_s5_body),
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_g910_geometry_similarity_s6_question),
                        options = wordOptions(
                            Res.string.learn_shared_any_two_rectangles,
                            Res.string.learn_shared_any_two_squares,
                            Res.string.learn_shared_any_two_triangles,
                            Res.string.learn_g910_geometry_similarity_s6_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g910_geometry_similarity_s6_explanation),
                        visual = Polygon(sides = 4, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-similarity-area",
                title = Res.string.learn_geometry_similarity_area_title,
                summary = Res.string.learn_geometry_similarity_area_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_area_s1_body),
                        formula = words(Res.string.learn_geometry_similarity_area_s1_formula),
                        visual = AreaGrid(cols = 4, rows = 4),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_similarity_area_s2_question),
                        options = mathOptions("4", "8", "12", "16"),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_geometry_similarity_area_s2_explanation),
                        visual = AreaGrid(cols = 4, rows = 4, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_area_s3_body),
                        formula = words(Res.string.learn_geometry_similarity_area_s3_formula),
                        visual = Solid(kind = SolidKind.CUBE, counts = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_similarity_area_s4_question),
                        formula = math("3 x 3 x 3 = ?"),
                        answer = "27",
                        explanation = words(Res.string.learn_geometry_similarity_area_s4_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_area_s5_body),
                        formula = math("k = √9 = 3"),
                        visual = AreaGrid(cols = 3, rows = 3),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_similarity_area_s6_question),
                        options = mathOptions("2", "4", "8", "56"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_similarity_area_s6_explanation),
                        visual = Solid(kind = SolidKind.CUBE, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-similarity-using",
                title = Res.string.learn_geometry_similarity_using_title,
                summary = Res.string.learn_geometry_similarity_using_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_using_s1_body),
                        visual = RightTriangle(a = 4, b = 3),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_using_s2_body),
                        visual = RightTriangle(a = 8, b = 6),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_similarity_using_s3_question),
                        options = mathOptions("10", "12", "13", "16"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_similarity_using_s3_explanation),
                        visual = RightTriangle(a = 9, b = 12, labels = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_similarity_using_s4_question),
                        formula = math("30 / {b:3} x {b:2} = ?"),
                        answer = "20",
                        explanation = words(Res.string.learn_geometry_similarity_using_s4_explanation),
                        visual = RightTriangle(a = 3, b = 2, labels = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_similarity_using_s5_body),
                        visual = RightTriangle(a = 6, b = 4),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_similar_triangles_have_areas, "20", "45"),
                        options = mathOptions("1.5", "2", "2.25", "2.5"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_similarity_using_s6_explanation),
                        visual = AreaGrid(cols = 4, rows = 5, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_similarity_q1_prompt),
                options = mathOptions("3", "6", "9", "27"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_similarity_q1_explanation),
                // A 3 x 3 grid is the k² rule already worked: nine squares to count, under sides
                // labelled with the factor itself. This one only says what an area is made of -
                // and neither its sides nor its cell count is any of the four options.
                visual = AreaGrid(cols = 4, rows = 2, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_matching_sides_cm_cm, "4", "10"),
                options = mathOptions("0.4", "2.5", "6", "14"),
                correctIndex = 1,
                explanation = math("10 / 4"),
                visual = RightTriangle(a = 10, b = 6, labels = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_similarity_q3_prompt),
                options = mathOptions("2", "4", "6", "8"),
                correctIndex = 3,
                explanation = words(Res.string.learn_unit_geometry_similarity_q3_explanation),
                visual = Solid(kind = SolidKind.CUBE, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_similarity_q4_prompt),
                options = wordOptions(
                    Res.string.learn_shared_any_two_rectangles,
                    Res.string.learn_shared_any_two_squares,
                    Res.string.learn_shared_any_two_triangles,
                    Res.string.learn_unit_geometry_similarity_q4_o4,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_similarity_q4_explanation),
                visual = Polygon(sides = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_similar_triangles_have_areas, "16", "36"),
                options = mathOptions("1.5", "2", "2.25", "2.5"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_similarity_q5_explanation),
                visual = AreaGrid(cols = 4, rows = 4, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_similarity_q6_prompt),
                options = mathOptions("9", "15", "20", "25"),
                correctIndex = 2,
                explanation = math("5 x 4"),
                visual = RightTriangle(a = 4, b = 3, labels = false),
            ),
        ),
    )

    private val transformations = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "transformations",
        title = Res.string.learn_unit_geometry_transformations_title,
        summary = Res.string.learn_unit_geometry_transformations_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-transformations",
                title = Res.string.learn_g910_geometry_transformations_title,
                summary = Res.string.learn_g910_geometry_transformations_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g910_geometry_transformations_s1_body),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_transformations_s2_body),
                        formula = math("(3, -2)"),
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = -1f, y = 1f, label = "A"),
                                PlotPoint(x = 2f, y = -1f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_translate_point_land, "1", "-2"),
                        options = mathOptions("(3, -1)", "(3, 3)", "(1, -1)", "(2, -2)"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g910_geometry_transformations_s3_explanation),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 2f, y = 1f, label = "(2, 1)")),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_transformations_s4_body),
                        formula = words(Res.string.learn_g910_geometry_transformations_s4_formula),
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = 2f, y = 2f, label = "A"),
                                PlotPoint(x = -2f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_g910_geometry_transformations_s5_question),
                        options = mathOptions("-2", "-1", "1", "2"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_g910_geometry_transformations_s5_explanation),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 1f, y = 2f, label = "A")),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_g910_geometry_transformations_s6_question),
                        options = wordOptions(
                            Res.string.learn_g910_geometry_transformations_s6_o1,
                            Res.string.learn_g910_geometry_transformations_s6_o2,
                            Res.string.learn_g910_geometry_transformations_s6_o3,
                            Res.string.learn_g910_geometry_transformations_s6_o4,
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g910_geometry_transformations_s6_explanation),
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
                title = Res.string.learn_geometry_transformations_rotations_title,
                summary = Res.string.learn_geometry_transformations_rotations_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_rotations_s1_body),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 1f, y = 1f, label = "A")),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_rotations_s2_body),
                        formula = words(Res.string.learn_geometry_transformations_rotations_s2_formula),
                        visual = Plot(
                            points = listOf(
                                PlotPoint(x = 2f, y = 1f, label = "A"),
                                PlotPoint(x = -1f, y = 2f, label = "A'"),
                            ),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_transformations_rotations_s3_question),
                        options = mathOptions("(-3, -1)", "(-1, 3)", "(1, -3)", "(3, -1)"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_transformations_rotations_s3_explanation),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 3f, y = 1f, label = "(3, 1)")),
                        ),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_transformations_rotations_s4_question),
                        options = mathOptions("-2", "0", "2", "4"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_shared_half_turn_flips_sign_both),
                        visual = Plot(
                            points = listOf(PlotPoint(x = 2f, y = 0f, label = "(2, 0)")),
                        ),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_rotations_s5_body),
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_transformations_rotations_s6_question),
                        options = listOf(
                            filled(Res.string.learn_opt_enlargement_by, "2"),
                            filled(Res.string.learn_opt_enlargement_by, "0.5"),
                            words(Res.string.learn_shared_rotation),
                            filled(Res.string.learn_opt_enlargement_by, "3"),
                        ),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_geometry_transformations_rotations_s6_explanation),
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-transformations-enlargements",
                title = Res.string.learn_geometry_transformations_enlargements_title,
                summary = Res.string.learn_geometry_transformations_enlargements_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_enlargements_s1_body),
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_enlargements_s2_body),
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_transformations_enlargements_s3_question),
                        options = listOf(
                            words(Res.string.learn_geometry_transformations_enlargements_s3_o1),
                            words(Res.string.learn_geometry_transformations_enlargements_s3_o2),
                            words(Res.string.learn_geometry_transformations_enlargements_s3_o3),
                            filled(Res.string.learn_opt_turns_through_degrees, "90"),
                        ),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_transformations_enlargements_s3_explanation),
                        visual = AreaGrid(cols = 2, rows = 2, showArea = false, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_transformations_enlargements_s4_body),
                        formula = words(Res.string.learn_geometry_transformations_enlargements_s4_formula),
                        visual = AreaGrid(cols = 3, rows = 3, showArea = false),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_transformations_enlargements_s5_question),
                        formula = math("3 x {b:4} = ?"),
                        answer = "12",
                        explanation = words(Res.string.learn_geometry_transformations_enlargements_s5_explanation),
                        visual = AreaGrid(cols = 3, rows = 2, showArea = false, reveal = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_which_transformation_does_not_keep),
                        options = wordOptions(
                            Res.string.learn_shared_translation,
                            Res.string.learn_shared_reflection,
                            Res.string.learn_shared_rotation,
                            Res.string.learn_shared_enlargement,
                        ),
                        correctIndex = 3,
                        explanation = words(Res.string.learn_shared_other_three_congruence_transformations),
                        visual = AreaGrid(cols = 4, rows = 3, showArea = false, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = filled(Res.string.learn_t_translate_point_land, "2", "-3"),
                options = mathOptions("(3, -1)", "(3, 5)", "(-1, -1)", "(2, 6)"),
                correctIndex = 0,
                explanation = math("(1 + 2, 2 - 3)"),
                visual = Plot(
                    points = listOf(PlotPoint(x = 1f, y = 2f, label = "(1, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_transformations_q2_prompt),
                options = mathOptions("(-2, 2)", "(2, -2)", "(-2, -2)", "(2, 2)"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_transformations_q2_explanation),
                visual = Plot(
                    points = listOf(PlotPoint(x = 2f, y = 2f, label = "(2, 2)")),
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_transformations_q3_prompt),
                options = mathOptions("(-3, -1)", "(-1, 3)", "(1, -3)", "(3, -1)"),
                correctIndex = 0,
                explanation = words(Res.string.learn_shared_half_turn_flips_sign_both),
                visual = Plot(
                    points = listOf(PlotPoint(x = 3f, y = 1f, label = "(3, 1)")),
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_which_transformation_does_not_keep),
                options = wordOptions(
                    Res.string.learn_shared_translation,
                    Res.string.learn_shared_reflection,
                    Res.string.learn_shared_rotation,
                    Res.string.learn_shared_enlargement,
                ),
                correctIndex = 3,
                explanation = words(Res.string.learn_shared_other_three_congruence_transformations),
                visual = AreaGrid(cols = 4, rows = 3, showArea = false, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_transformations_q5_prompt),
                options = listOf(
                    filled(Res.string.learn_opt_move_right_down, "3", "2"),
                    filled(Res.string.learn_opt_move_up_right, "3", "2"),
                    filled(Res.string.learn_opt_move_left_up, "3", "2"),
                    filled(Res.string.learn_opt_move_right_down, "2", "3"),
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_transformations_q5_explanation),
                visual = Plot(
                    points = listOf(
                        PlotPoint(x = -1f, y = 1f, label = "A"),
                        PlotPoint(x = 2f, y = -1f, label = "A'"),
                    ),
                ),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_transformations_q6_prompt),
                options = wordOptions(
                    Res.string.learn_unit_geometry_transformations_q6_o1,
                    Res.string.learn_unit_geometry_transformations_q6_o2,
                    Res.string.learn_unit_geometry_transformations_q6_o3,
                    Res.string.learn_unit_geometry_transformations_q6_o4,
                ),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_transformations_q6_explanation),
                visual = AreaGrid(cols = 2, rows = 2, showArea = false, reveal = false),
            ),
        ),
    )

    private val circleTheorems = learnUnit(
        topic = MathTopic.GEOMETRY,
        urlSlug = "circle-theorems",
        title = Res.string.learn_unit_geometry_circle_theorems_title,
        summary = Res.string.learn_unit_geometry_circle_theorems_summary,
        level = GradeLevel.GRADES_9_10,
        lessons = listOf(
            LessonSpec(
                id = "g910-geometry-circle-theorems",
                title = Res.string.learn_g910_geometry_circle_theorems_title,
                summary = Res.string.learn_g910_geometry_circle_theorems_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_g910_geometry_circle_theorems_s1_body),
                        visual = CircleFigure(centreAngle = 80),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_centre_angle_angle_at, "80"),
                        options = mathOptions("20", "40", "80", "160"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_g910_geometry_circle_theorems_s2_explanation),
                        visual = CircleFigure(centreAngle = 80, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_circle_theorems_s3_body),
                        visual = CircleFigure(centreAngle = 180),
                    ),
                    Choice(
                        question = words(Res.string.learn_g910_geometry_circle_theorems_s4_question),
                        options = mathOptions("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_g910_geometry_circle_theorems_s4_explanation),
                        visual = CircleFigure(centreAngle = 180, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_g910_geometry_circle_theorems_s5_body),
                        visual = CircleFigure(centreAngle = 100),
                    ),
                    Numeric(
                        question = words(Res.string.learn_g910_geometry_circle_theorems_s6_question),
                        formula = math("140 / {b:2} = ?"),
                        answer = "70",
                        explanation = words(Res.string.learn_g910_geometry_circle_theorems_s6_explanation),
                        visual = CircleFigure(centreAngle = 140, reveal = false),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circle-theorems-cyclic",
                title = Res.string.learn_geometry_circle_theorems_cyclic_title,
                summary = Res.string.learn_geometry_circle_theorems_cyclic_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_cyclic_s1_body),
                        visual = CyclicQuad(),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_cyclic_s2_body),
                        formula = math("a + c = 180"),
                        visual = CyclicQuad(angles = listOf("a", "", "c", ""), highlightPair = 0),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_circle_theorems_cyclic_s3_question),
                        answer = "70",
                        explanation = math("180 - 110"),
                        visual = CyclicQuad(angles = listOf("110", "", "?", ""), highlightPair = 0),
                    ),
                    Choice(
                        question = filled(Res.string.learn_t_angle_cyclic_quadrilateral_opposite, "95"),
                        options = mathOptions("85", "95", "105", "185"),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_circle_theorems_cyclic_s4_explanation),
                        visual = CyclicQuad(angles = listOf("95", "", "?", ""), highlightPair = 0),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_cyclic_s5_body),
                        formula = math("b + d = 180"),
                        visual = CyclicQuad(angles = listOf("", "b", "", "d"), highlightPair = 1),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circle_theorems_cyclic_s6_question),
                        options = mathOptions("70", "80", "90", "100"),
                        correctIndex = 1,
                        explanation = words(Res.string.learn_geometry_circle_theorems_cyclic_s6_explanation),
                        visual = CyclicQuad(angles = listOf("70", "100", "110", "?"), highlightPair = 1),
                    ),
                ),
            ),
            LessonSpec(
                id = "geometry-circle-theorems-tangents",
                title = Res.string.learn_geometry_circle_theorems_tangents_title,
                summary = Res.string.learn_geometry_circle_theorems_tangents_summary,
                steps = listOf(
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_tangents_s1_body),
                        visual = CircleFigure(radius = 5),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_tangents_s2_body),
                        formula = words(Res.string.learn_geometry_circle_theorems_tangents_s2_formula),
                        visual = AngleFigure(degrees = 90),
                    ),
                    Choice(
                        question = words(Res.string.learn_shared_tangent_meets_radius_at_angle),
                        options = mathOptions("45", "60", "90", "180"),
                        correctIndex = 2,
                        explanation = words(Res.string.learn_shared_tangent_always_perpendicular_radius_touches),
                        visual = AngleFigure(degrees = 90, reveal = false),
                    ),
                    Concept(
                        body = words(Res.string.learn_geometry_circle_theorems_tangents_s4_body),
                        visual = RightTriangle(a = 4, b = 3, showSquares = true),
                    ),
                    Numeric(
                        question = words(Res.string.learn_geometry_circle_theorems_tangents_s5_question),
                        formula = math("180 - {b:90} - {b:35} = ?"),
                        answer = "55",
                        explanation = words(Res.string.learn_geometry_circle_theorems_tangents_s5_explanation),
                        visual = RightTriangle(a = 7, b = 5, labels = false),
                    ),
                    Choice(
                        question = words(Res.string.learn_geometry_circle_theorems_tangents_s6_question),
                        options = wordOptions(
                            Res.string.learn_geometry_circle_theorems_tangents_s6_o1,
                            Res.string.learn_geometry_circle_theorems_tangents_s6_o2,
                            Res.string.learn_geometry_circle_theorems_tangents_s6_o3,
                            Res.string.learn_geometry_circle_theorems_tangents_s6_o4,
                        ),
                        correctIndex = 0,
                        explanation = words(Res.string.learn_geometry_circle_theorems_tangents_s6_explanation),
                        visual = CircleFigure(radius = 5, reveal = false),
                    ),
                ),
            ),
        ),
        questions = listOf(
            QuizQuestion(
                prompt = filled(Res.string.learn_t_centre_angle_angle_at, "120"),
                options = mathOptions("30", "60", "120", "240"),
                correctIndex = 1,
                explanation = words(Res.string.learn_unit_geometry_circle_theorems_q1_explanation),
                visual = CircleFigure(centreAngle = 120, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circle_theorems_q2_prompt),
                options = mathOptions("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_circle_theorems_q2_explanation),
                visual = CircleFigure(centreAngle = 180, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circle_theorems_q3_prompt),
                options = mathOptions("17.5", "35", "70", "145"),
                correctIndex = 2,
                explanation = words(Res.string.learn_unit_geometry_circle_theorems_q3_explanation),
                visual = CircleFigure(centreAngle = 70, reveal = false),
            ),
            QuizQuestion(
                prompt = filled(Res.string.learn_t_angle_cyclic_quadrilateral_opposite, "95"),
                options = mathOptions("85", "95", "105", "185"),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_circle_theorems_q4_explanation),
                visual = CyclicQuad(angles = listOf("95", "", "?", ""), highlightPair = 0),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_shared_tangent_meets_radius_at_angle),
                options = mathOptions("45", "60", "90", "180"),
                correctIndex = 2,
                explanation = words(Res.string.learn_shared_tangent_always_perpendicular_radius_touches),
                visual = AngleFigure(degrees = 90, reveal = false),
            ),
            QuizQuestion(
                prompt = words(Res.string.learn_unit_geometry_circle_theorems_q6_prompt),
                options = wordOptions(
                    Res.string.learn_unit_geometry_circle_theorems_q6_o1,
                    Res.string.learn_unit_geometry_circle_theorems_q6_o2,
                    Res.string.learn_unit_geometry_circle_theorems_q6_o3,
                    Res.string.learn_unit_geometry_circle_theorems_q6_o4,
                ),
                correctIndex = 0,
                explanation = words(Res.string.learn_unit_geometry_circle_theorems_q6_explanation),
                visual = CircleFigure(radius = 5, reveal = false),
            ),
        ),
    )

    val units: List<LearnUnit> = listOf(
        flatShapes, solidShapes, angles, quadrilaterals, symmetry, perimeterAndArea, pythagoras, circles, volume,
        similarity, transformations, circleTheorems,
    )
}
