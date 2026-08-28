package com.inspiredandroid.braincup.learn

import braincup.composeapp.generated.resources.*
import braincup.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.StringResource

/**
 * The shape guide: every shape a learner is expected to know by name, drawn and named in one
 * place.
 *
 * It is a reference, not a lesson. Geometry's sub-topics teach the shapes a few at a time and in
 * the order they are taught at school, which is the wrong shape for the question "what is the
 * one with five sides called again?". This answers that in a single scroll, and every figure is
 * the same animated one the lessons use, so tapping a shape draws it again.
 *
 * Names and facts go through `strings.xml` like the lessons themselves, keyed by the entry ids
 * below: `learn_shape_hexagon_name`, `learn_shape_hexagon_fact`. Only the ids stay here, because
 * they key the grid cells rather than being read.
 */
object ShapeGuide {

    /** One shape: what it is called, the fact that pins it down, and the figure that draws it. */
    data class Entry(
        val id: String,
        val name: StringResource,
        /** The one line that separates this shape from its neighbours in the same section. */
        val fact: CatalogText,
        val visual: LearnVisual,
    )

    /** A run of shapes that belong together, with the line that says what they have in common. */
    data class Section(
        val id: String,
        val title: StringResource,
        val blurb: StringResource,
        val shapes: List<Entry>,
    )

    /**
     * Polygons named for their side count.
     *
     * Every one is drawn regular, which is what a name like "hexagon" calls to mind, and the
     * section blurb says so: an irregular six-sided shape is a hexagon too, and a guide that
     * shows only the regular one without saying it is showing the special case teaches a
     * half-truth the quadrilateral section then has to undo.
     */
    private val polygons = Section(
        id = "polygons",
        title = Res.string.learn_shapeguide_polygons_title,
        blurb = Res.string.learn_shapeguide_polygons_blurb,
        shapes = listOf(
            polygon("triangle", Res.string.learn_shape_triangle_name, 3),
            polygon("quadrilateral", Res.string.learn_shape_quadrilateral_name, 4),
            polygon("pentagon", Res.string.learn_shape_pentagon_name, 5),
            polygon("hexagon", Res.string.learn_shape_hexagon_name, 6),
            polygon("heptagon", Res.string.learn_shape_heptagon_name, 7),
            polygon("octagon", Res.string.learn_shape_octagon_name, 8),
            polygon("nonagon", Res.string.learn_shape_nonagon_name, 9),
            polygon("decagon", Res.string.learn_shape_decagon_name, 10),
            polygon("hendecagon", Res.string.learn_shape_hendecagon_name, 11),
            polygon("dodecagon", Res.string.learn_shape_dodecagon_name, 12),
        ),
    )

    private val triangles = Section(
        id = "triangles",
        title = Res.string.learn_shapeguide_triangles_title,
        blurb = Res.string.learn_shapeguide_triangles_blurb,
        shapes = listOf(
            Entry(
                id = "equilateral",
                name = Res.string.learn_shape_equilateral_name,
                fact = words(Res.string.learn_shape_equilateral_fact),
                visual = LearnVisual.Triangle(kind = TriKind.EQUILATERAL),
            ),
            Entry(
                id = "isosceles",
                name = Res.string.learn_shape_isosceles_name,
                fact = words(Res.string.learn_shape_isosceles_fact),
                visual = LearnVisual.Triangle(kind = TriKind.ISOSCELES),
            ),
            Entry(
                id = "scalene",
                name = Res.string.learn_shape_scalene_name,
                fact = words(Res.string.learn_shape_scalene_fact),
                visual = LearnVisual.Triangle(kind = TriKind.SCALENE),
            ),
            Entry(
                id = "right-triangle",
                name = Res.string.learn_shape_right_triangle_name,
                fact = words(Res.string.learn_shape_right_triangle_fact),
                // Bare, because the guide names the shape and side lengths would read as part of
                // the definition. The little square at the corner is the definition.
                visual = LearnVisual.RightTriangle(a = 4, b = 3, labels = false),
            ),
        ),
    )

    private val quadrilaterals = Section(
        id = "quadrilaterals",
        title = Res.string.learn_shapeguide_quadrilaterals_title,
        blurb = Res.string.learn_shapeguide_quadrilaterals_blurb,
        shapes = listOf(
            quad("square", Res.string.learn_shape_square_name, words(Res.string.learn_shape_square_fact), QuadKind.SQUARE),
            quad("rectangle", Res.string.learn_shape_rectangle_name, words(Res.string.learn_shape_rectangle_fact), QuadKind.RECTANGLE),
            quad("rhombus", Res.string.learn_shape_rhombus_name, words(Res.string.learn_shape_rhombus_fact), QuadKind.RHOMBUS),
            quad("parallelogram", Res.string.learn_shape_parallelogram_name, words(Res.string.learn_shape_parallelogram_fact), QuadKind.PARALLELOGRAM),
            quad("trapezium", Res.string.learn_shape_trapezium_name, words(Res.string.learn_shape_trapezium_fact), QuadKind.TRAPEZIUM),
            quad("kite", Res.string.learn_shape_kite_name, words(Res.string.learn_shape_kite_fact), QuadKind.KITE),
        ),
    )

    private val curves = Section(
        id = "curves",
        title = Res.string.learn_shapeguide_curves_title,
        blurb = Res.string.learn_shapeguide_curves_blurb,
        shapes = listOf(
            Entry(
                id = "circle",
                name = Res.string.learn_shape_circle_name,
                fact = words(Res.string.learn_shape_circle_fact),
                visual = LearnVisual.CircleFigure(showRadius = true, reveal = false),
            ),
            Entry(
                id = "oval",
                name = Res.string.learn_shape_oval_name,
                fact = words(Res.string.learn_shape_oval_fact),
                visual = LearnVisual.FlatShape(kind = FlatShapeKind.OVAL),
            ),
            Entry(
                id = "semicircle",
                name = Res.string.learn_shape_semicircle_name,
                fact = words(Res.string.learn_shape_semicircle_fact),
                visual = LearnVisual.FlatShape(kind = FlatShapeKind.SEMICIRCLE),
            ),
            Entry(
                id = "star",
                name = Res.string.learn_shape_star_name,
                fact = words(Res.string.learn_shape_star_fact),
                visual = LearnVisual.FlatShape(kind = FlatShapeKind.STAR),
            ),
        ),
    )

    /**
     * Solids, captioned with the counts that tell them apart.
     *
     * The cuboid is drawn by `SolidKind.PRISM`, which is the box the geometry lessons use for a
     * prism. A cuboid is one, so the figure is honest; the triangular prism beside it is the one
     * that shows why the family is called that.
     */
    private val solids = Section(
        id = "solids",
        title = Res.string.learn_shapeguide_solids_title,
        blurb = Res.string.learn_shapeguide_solids_blurb,
        shapes = listOf(
            solid("cube", Res.string.learn_shape_cube_name, words(Res.string.learn_shape_cube_fact), SolidKind.CUBE),
            solid("cuboid", Res.string.learn_shape_cuboid_name, words(Res.string.learn_shape_cuboid_fact), SolidKind.PRISM),
            solid(
                "triangular-prism",
                Res.string.learn_shape_triangular_prism_name,
                words(Res.string.learn_shape_triangular_prism_fact),
                SolidKind.TRIANGULAR_PRISM,
            ),
            solid("pyramid", Res.string.learn_shape_pyramid_name, words(Res.string.learn_shape_pyramid_fact), SolidKind.PYRAMID),
            solid("cylinder", Res.string.learn_shape_cylinder_name, words(Res.string.learn_shape_cylinder_fact), SolidKind.CYLINDER),
            solid("cone", Res.string.learn_shape_cone_name, words(Res.string.learn_shape_cone_fact), SolidKind.CONE),
            solid("sphere", Res.string.learn_shape_sphere_name, words(Res.string.learn_shape_sphere_fact), SolidKind.SPHERE),
        ),
    )

    val sections: List<Section> = listOf(polygons, triangles, quadrilaterals, curves, solids)

    val shapeCount: Int = sections.sumOf { it.shapes.size }

    /**
     * A polygon cell. The corner numbering the lessons use is off here: at cell size the numbers
     * around a dodecagon are a smudge, and the side count is written under the shape anyway.
     */
    private fun polygon(id: String, name: StringResource, sides: Int) = Entry(
        id = id,
        name = name,
        // Ten polygons whose fact differs only in a number is one sentence, and a language that
        // inflects "side" cannot write it from a fixed string.
        fact = counted(Res.plurals.learn_shape_polygon_fact, sides),
        visual = LearnVisual.Polygon(sides = sides, countCorners = false, reveal = false),
    )

    private fun quad(id: String, name: StringResource, fact: CatalogText, kind: QuadKind) = Entry(
        id = id,
        name = name,
        fact = fact,
        visual = LearnVisual.Quadrilateral(kind = kind),
    )

    private fun solid(id: String, name: StringResource, fact: CatalogText, kind: SolidKind) = Entry(
        id = id,
        name = name,
        fact = fact,
        // The figure's own caption would name the solid a second time, right under the name.
        visual = LearnVisual.Solid(kind = kind, reveal = false),
    )
}
