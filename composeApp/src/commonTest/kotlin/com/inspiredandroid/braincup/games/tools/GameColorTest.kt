package com.inspiredandroid.braincup.games.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class GameColorTest {

    @Test
    fun everyTexturedColorHasItsOwnPattern() {
        val textured = GameColor.entries.filter { it.pattern != ColorPattern.PLAIN }
        assertEquals(textured.size, textured.map { it.pattern }.toSet().size)
    }

    @Test
    fun onlyYellowAndGreyStayPlain() {
        assertEquals(
            setOf(GameColor.YELLOW, GameColor.GREY_LIGHT),
            GameColor.entries.filter { it.pattern == ColorPattern.PLAIN }.toSet(),
        )
    }
}
