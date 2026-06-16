package com.inspiredandroid.braincup.games

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.TimeSource

class NurikabeMeasureTest {

    @Test
    fun measure() {
        val report = StringBuilder("\n")
        for (level in listOf(1, 4, 7, 10)) {
            var unique = 0
            var solvable = 0
            var rebuildFail = 0
            var degenerate = 0
            var minIslands = Int.MAX_VALUE
            var maxIsland = 0
            val start = TimeSource.Monotonic.markNow()
            val seeds = 40
            for (seed in 0L until seeds) {
                val game = NurikabeGame(level = level, random = Random(seed)).apply { nextRound() }
                val count = game.solutionCount(limit = 2)
                if (count >= 1) solvable++
                if (count == 1) unique++
                if (game.generatedIslands.size < 2) degenerate++
                minIslands = minOf(minIslands, game.generatedIslands.size)
                maxIsland = maxOf(maxIsland, game.generatedIslands.maxOf { it.size })
                game.setWalls(game.generatedSea.toList(), true)
                if (!game.isCorrect("")) rebuildFail++
            }
            val ms = start.elapsedNow().inWholeMilliseconds
            report.append(
                "level=$level solvable=$solvable/$seeds unique=$unique/$seeds rebuildFail=$rebuildFail " +
                    "degenerate=$degenerate minIslands=$minIslands maxIsland=$maxIsland total=${ms}ms avg=${ms / seeds}ms\n",
            )
        }
        fail(report.toString())
    }
}
