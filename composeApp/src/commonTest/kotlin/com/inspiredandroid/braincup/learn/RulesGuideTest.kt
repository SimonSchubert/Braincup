package com.inspiredandroid.braincup.learn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RulesGuideTest {

    private val entries = RulesGuide.sections.flatMap { it.rules }

    @Test
    fun everySectionHasRules() {
        RulesGuide.sections.forEach { section ->
            assertTrue(section.rules.isNotEmpty(), "${section.id} has no rules")
        }
    }

    /** Ids key the grid cells, so a duplicate would silently drop a rule off the screen. */
    @Test
    fun ruleIdsAreUnique() {
        val ids = entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate rule ids: $ids")
    }

    @Test
    fun ruleCountMatchesTheSections() {
        assertEquals(RulesGuide.sections.sumOf { it.rules.size }, RulesGuide.ruleCount)
    }
}
