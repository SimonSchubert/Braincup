package com.inspiredandroid.braincup.ui.components.learn

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The figures caption themselves while drawing, so they fill their own placeholders instead of
 * going through `stringResource`'s formatting overload. That makes [fillIn] the one piece of
 * formatting in the app nothing else checks.
 */
class LearnVisualStringsTest {

    @Test
    fun fillsPlaceholdersInOrder() {
        assertEquals("area = 12 sq cm", "area = %1\$d sq %2\$s".fillIn(12, "cm"))
        assertEquals("7 in each", "%1\$d in each".fillIn(7))
    }

    /** "4 sides, 4 corners" counts the same number twice from one argument. */
    @Test
    fun repeatsAPlaceholder() {
        assertEquals("4 sides, 4 corners", "%1\$d sides, %1\$d corners".fillIn(4))
    }

    /** Translations reorder freely, and the caption has to follow the numbers rather than the slots. */
    @Test
    fun followsReorderedPlaceholders() {
        assertEquals("cm 12", "%2\$s %1\$d".fillIn(12, "cm"))
    }

    /**
     * The percent caption prints a literal percent sign next to a placeholder, so an escape would
     * be one more thing every translator could get wrong.
     */
    @Test
    fun leavesALonePercentAlone() {
        assertEquals("25% of 60", "%1\$s% of %2\$s".fillIn("25", "60"))
        assertEquals("100%", "%1\$s%".fillIn("100"))
    }

    /** A stray token is drawn as written rather than swallowed, so a bad string is visible. */
    @Test
    fun keepsTextThatOnlyLooksLikeAPlaceholder() {
        assertEquals("50%d", "50%d".fillIn(1))
        assertEquals("a % b", "a % b".fillIn(1))
        assertEquals("ends in %", "ends in %".fillIn(1))
        assertEquals("cut off %1", "cut off %1".fillIn(1))
        assertEquals("cut off %1\$", "cut off %1\$".fillIn(1))
    }

    /** An argument the caller forgot drops out rather than printing the placeholder. */
    @Test
    fun dropsAMissingArgument() {
        assertEquals("a  b", "a %1\$s b".fillIn())
        assertEquals("first ", "%1\$s %2\$s".fillIn("first"))
    }

    @Test
    fun leavesAPlaceholderlessTemplateUntouched() {
        assertEquals("mean", "mean".fillIn())
        assertEquals("mean", "mean".fillIn(3))
    }
}
