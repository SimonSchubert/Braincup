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
            assertTrue(section.title.isNotBlank(), "${section.id} has no title")
            assertTrue(section.blurb.isNotBlank(), "${section.id} has no blurb")
        }
    }

    /** Ids key the grid cells, so a duplicate would silently drop a rule off the screen. */
    @Test
    fun ruleIdsAreUnique() {
        val ids = entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate rule ids: $ids")
    }

    @Test
    fun everyRuleIsWrittenAndExplained() {
        entries.forEach { rule ->
            assertTrue(rule.rule.isNotBlank(), "${rule.id} has no rule")
            assertTrue(rule.meaning.isNotBlank(), "${rule.id} has no meaning")
            assertTrue(rule.example?.isBlank() != true, "${rule.id} has an empty example")
        }
    }

    /**
     * A negative *number* after the multiply sign has to be bracketed. `MathText` spaces out any
     * minus standing after a letter, and `x` is a letter, so "5 x -3" would come out as "5 x - 3"
     * and read as a subtraction. "5 x (-3)" is the same thing written so it survives the
     * formatter. A lone minus - the sign table's "- x - = +" - is not a number and wants that
     * spacing.
     */
    @Test
    fun negativesAfterMultiplyAreBracketed() {
        val loose = Regex("""x\s+-[0-9a-zA-Z]""")
        entries.forEach { rule ->
            listOfNotNull(rule.rule, rule.example).forEach { text ->
                assertTrue(!loose.containsMatchIn(text), "${rule.id} needs brackets round the negative in \"$text\"")
            }
        }
    }

    @Test
    fun ruleCountMatchesTheSections() {
        assertEquals(RulesGuide.sections.sumOf { it.rules.size }, RulesGuide.ruleCount)
    }
}
