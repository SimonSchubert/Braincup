package com.inspiredandroid.braincup.learn

/**
 * The shape guide: every shape a learner is expected to know by name, drawn and named in one
 * place.
 *
 * It is a reference, not a lesson. Geometry's sub-topics teach the shapes a few at a time and in
 * the order they are taught at school, which is the wrong shape for the question "what is the
 * one with five sides called again?". This answers that in a single scroll, and every figure is
 * the same animated one the lessons use, so tapping a shape draws it again.
 *
 * Names and facts are authored here in English, exactly like the lesson prose in
 * `learn/content` and for the same reason: this is catalog content, not UI chrome. The screen
 * around it - its title, the button that opens it - goes through `strings.xml` and stays
 * translatable.
 */
object ShapeGuide {

    /** One shape: what it is called, the fact that pins it down, and the figure that draws it. */
    data class Entry(
        val id: String,
        val name: String,
        /** The one line that separates this shape from its neighbours in the same section. */
        val fact: String,
        val visual: LearnVisual,
    )

    /** A run of shapes that belong together, with the line that says what they have in common. */
    data class Section(
        val id: String,
        val title: String,
        val blurb: String,
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
        title = "Polygons",
        blurb = "Named for how many sides they have. Drawn regular here, with every side and angle equal.",
        shapes = listOf(
            polygon("triangle", "Triangle", 3),
            polygon("quadrilateral", "Quadrilateral", 4),
            polygon("pentagon", "Pentagon", 5),
            polygon("hexagon", "Hexagon", 6),
            polygon("heptagon", "Heptagon", 7),
            polygon("octagon", "Octagon", 8),
            polygon("nonagon", "Nonagon", 9),
            polygon("decagon", "Decagon", 10),
            polygon("hendecagon", "Hendecagon", 11),
            polygon("dodecagon", "Dodecagon", 12),
        ),
    )

    private val triangles = Section(
        id = "triangles",
        title = "Triangles",
        blurb = "Three sides, sorted by which of them match. Ticks mark the sides of equal length.",
        shapes = listOf(
            Entry(
                id = "equilateral",
                name = "Equilateral",
                fact = "3 equal sides · every angle 60°",
                visual = LearnVisual.Triangle(kind = TriKind.EQUILATERAL),
            ),
            Entry(
                id = "isosceles",
                name = "Isosceles",
                fact = "2 equal sides · 2 equal angles",
                visual = LearnVisual.Triangle(kind = TriKind.ISOSCELES),
            ),
            Entry(
                id = "scalene",
                name = "Scalene",
                fact = "No two sides the same length",
                visual = LearnVisual.Triangle(kind = TriKind.SCALENE),
            ),
            Entry(
                id = "right-triangle",
                name = "Right triangle",
                fact = "One corner of exactly 90°",
                // Bare, because the guide names the shape and side lengths would read as part of
                // the definition. The little square at the corner is the definition.
                visual = LearnVisual.RightTriangle(a = 4, b = 3, labels = false),
            ),
        ),
    )

    private val quadrilaterals = Section(
        id = "quadrilaterals",
        title = "Quadrilaterals",
        blurb = "Four sides, six families. Ticks mark equal sides and arrows mark parallel ones.",
        shapes = listOf(
            quad("square", "Square", "4 equal sides · 4 right angles", QuadKind.SQUARE),
            quad("rectangle", "Rectangle", "Opposite sides equal · 4 right angles", QuadKind.RECTANGLE),
            quad("rhombus", "Rhombus", "4 equal sides, leaning over", QuadKind.RHOMBUS),
            quad("parallelogram", "Parallelogram", "Both pairs of opposite sides parallel", QuadKind.PARALLELOGRAM),
            quad("trapezium", "Trapezium", "Exactly one pair of parallel sides", QuadKind.TRAPEZIUM),
            quad("kite", "Kite", "Two pairs of equal sides, next to each other", QuadKind.KITE),
        ),
    )

    private val curves = Section(
        id = "curves",
        title = "Round shapes",
        blurb = "The shapes no count of straight sides describes.",
        shapes = listOf(
            Entry(
                id = "circle",
                name = "Circle",
                fact = "Every point the same distance from the centre",
                visual = LearnVisual.CircleFigure(showRadius = true, reveal = false),
            ),
            Entry(
                id = "oval",
                name = "Oval",
                fact = "A stretched circle: two different widths",
                visual = LearnVisual.FlatShape(kind = FlatShapeKind.OVAL),
            ),
            Entry(
                id = "semicircle",
                name = "Semicircle",
                fact = "Half a circle, cut along its diameter",
                visual = LearnVisual.FlatShape(kind = FlatShapeKind.SEMICIRCLE),
            ),
            Entry(
                id = "star",
                name = "Star",
                fact = "5 points · 10 sides, turning in and out",
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
        title = "Solids",
        blurb = "Shapes you can hold. A face is a flat side, an edge is where two faces meet.",
        shapes = listOf(
            solid("cube", "Cube", "6 square faces · 12 edges · 8 corners", SolidKind.CUBE),
            solid("cuboid", "Cuboid", "A box: 6 rectangular faces", SolidKind.PRISM),
            solid(
                "triangular-prism",
                "Triangular prism",
                "Two triangle ends, three rectangles between",
                SolidKind.TRIANGULAR_PRISM,
            ),
            solid("pyramid", "Pyramid", "A square base rising to one point", SolidKind.PYRAMID),
            solid("cylinder", "Cylinder", "Two circle ends and one curved face", SolidKind.CYLINDER),
            solid("cone", "Cone", "One circle base rising to a point", SolidKind.CONE),
            solid("sphere", "Sphere", "No faces, no edges, no corners", SolidKind.SPHERE),
        ),
    )

    val sections: List<Section> = listOf(polygons, triangles, quadrilaterals, curves, solids)

    val shapeCount: Int = sections.sumOf { it.shapes.size }

    /**
     * A polygon cell. The corner numbering the lessons use is off here: at cell size the numbers
     * around a dodecagon are a smudge, and the side count is written under the shape anyway.
     */
    private fun polygon(id: String, name: String, sides: Int) = Entry(
        id = id,
        name = name,
        fact = "$sides sides · $sides corners",
        visual = LearnVisual.Polygon(sides = sides, countCorners = false, reveal = false),
    )

    private fun quad(id: String, name: String, fact: String, kind: QuadKind) = Entry(
        id = id,
        name = name,
        fact = fact,
        visual = LearnVisual.Quadrilateral(kind = kind),
    )

    private fun solid(id: String, name: String, fact: String, kind: SolidKind) = Entry(
        id = id,
        name = name,
        fact = fact,
        // The figure's own caption would name the solid a second time, right under the name.
        visual = LearnVisual.Solid(kind = kind, reveal = false),
    )
}
