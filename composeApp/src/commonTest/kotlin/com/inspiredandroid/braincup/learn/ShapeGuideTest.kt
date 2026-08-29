package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShapeGuideTest {

    @Test
    fun everySectionHasShapes() {
        ShapeGuide.sections.forEach { section ->
            assertTrue(section.entries.isNotEmpty(), "${section.id} has no shapes")
        }
    }

    /** Ids key the grid cells, so a duplicate would silently drop a shape off the screen. */
    @Test
    fun shapeIdsAreUnique() {
        val ids = ShapeGuide.sections.flatMap { it.entries }.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate shape ids: $ids")
    }

    /**
     * The guide names each shape underneath its figure, so a figure that captions itself with the
     * same name says it twice. The variants that can are the ones with a `reveal` to turn off.
     */
    @Test
    fun figuresDoNotNameThemselves() {
        ShapeGuide.sections.flatMap { it.entries }.forEach { shape ->
            val captions = when (val visual = shape.visual) {
                is LearnVisual.Polygon -> visual.reveal
                is LearnVisual.Solid -> visual.reveal
                is LearnVisual.CircleFigure -> visual.reveal
                else -> false
            }
            assertTrue(!captions, "${shape.id} captions itself under its own name")
        }
    }

    @Test
    fun shapeCountMatchesTheSections() {
        assertEquals(ShapeGuide.sections.sumOf { it.entries.size }, ShapeGuide.shapeCount)
    }
}
