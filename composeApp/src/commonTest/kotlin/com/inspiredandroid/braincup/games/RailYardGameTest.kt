package com.inspiredandroid.braincup.games

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RailYardGameTest {

    private val frame = 1f / 60f

    private fun newGame(seed: Long): RailYardGame = RailYardGame(Random(seed)).apply { nextRound() }

    /** Flips whatever a perfect player would, then advances one frame. */
    private fun RailYardGame.perfectFrame(): RailYardGame.StepResult {
        while (true) toggleSwitch(switchToFlip(lookahead = Float.MAX_VALUE) ?: break)
        return step(frame)
    }

    private fun RailYardGame.runFor(seconds: Float, eachFrame: RailYardGame.() -> RailYardGame.StepResult): List<Boolean> {
        val arrivals = mutableListOf<Boolean>()
        repeat((seconds / frame).toInt()) { arrivals += eachFrame().arrivals }
        return arrivals
    }

    private fun RailYardGame.stepUntilFirstTrain() {
        while (trains.isEmpty()) step(frame)
    }

    private fun RailYardGame.setRouteTo(stationIndex: Int) {
        routeTo(nodes[stationIndex].stationColor!!).forEach { (switch, branch) ->
            if (branchOf(switch) != branch) toggleSwitch(switch)
        }
    }

    private fun RailYardGame.flashedStations(): List<Int> {
        val state = toUiState()
        return stationIndices().filterIndexed { i, _ -> state.stations[i].flash != RailYardGame.StationFlash.NONE }
    }

    @Test
    fun `every stage lays out the stations it asks for, each in its own colour`() {
        for (seed in 0L until 30L) {
            val game = newGame(seed)
            var lastStage = -1
            game.runFor(60f) {
                if (stageIndex != lastStage) {
                    lastStage = stageIndex
                    val colors = stationIndices().map { nodes[it].stationColor!! }
                    assertEquals(stageIndex + 3, colors.size)
                    assertEquals(colors.size, colors.toSet().size)
                    assertEquals(colors.size - 1, switchIndices().size)
                }
                perfectFrame()
            }
            assertTrue(lastStage >= 2, "seed $seed only reached stage $lastStage")
        }
    }

    @Test
    fun `tracks stay on the board and never cross`() {
        for (seed in 0L until 200L) {
            val game = newGame(seed)
            repeat(6) { stage ->
                val state = game.toUiState()
                val segments = state.tracks.flatMap { track -> track.points.zipWithNext() }
                for ((a, b) in segments) {
                    assertTrue(a.x == b.x || a.y == b.y, "diagonal segment $a-$b")
                    for (p in listOf(a, b)) assertTrue(p.x in 0f..1f && p.y in 0f..1f, "$p off the board")
                }
                for (i in segments.indices) {
                    for (j in i + 1 until segments.size) {
                        val shared = segments[i].toList().intersect(segments[j].toList().toSet())
                        if (shared.isEmpty()) {
                            assertFalse(intersects(segments[i], segments[j]), "seed $seed stage $stage: ${segments[i]} x ${segments[j]}")
                        }
                    }
                }
                // Jump to the next stage by delivering trains.
                while (game.stageIndex == stage && stage < 5) game.perfectFrame()
            }
        }
    }

    private fun intersects(
        s: Pair<RailYardGame.Point, RailYardGame.Point>,
        t: Pair<RailYardGame.Point, RailYardGame.Point>,
    ): Boolean {
        val sx = min(s.first.x, s.second.x)..max(s.first.x, s.second.x)
        val sy = min(s.first.y, s.second.y)..max(s.first.y, s.second.y)
        val tx = min(t.first.x, t.second.x)..max(t.first.x, t.second.x)
        val ty = min(t.first.y, t.second.y)..max(t.first.y, t.second.y)
        return sx.start <= tx.endInclusive &&
            tx.start <= sx.endInclusive &&
            sy.start <= ty.endInclusive &&
            ty.start <= sy.endInclusive
    }

    @Test
    fun `each station has a route that leads to it`() {
        val game = newGame(4L)
        for (station in game.stationIndices()) {
            game.setRouteTo(station)
            var node = game.nodes[0].children.single()
            while (game.nodes[node].isSwitch) node = game.nodes[node].children[game.branchOf(node)]
            assertEquals(station, node)
        }
    }

    @Test
    fun `a train takes the station the switches send it to`() {
        for (seed in 0L until 20L) {
            val game = newGame(seed)
            game.stepUntilFirstTrain()
            val train = game.trains.single()
            val target = game.stationIndices().first { game.nodes[it].stationColor != train.color }
            game.setRouteTo(target)

            val arrivals = mutableListOf<Boolean>()
            while (arrivals.isEmpty()) arrivals += game.step(frame).arrivals

            assertEquals(listOf(false), arrivals)
            assertEquals(listOf(target), game.flashedStations())
            assertFalse(game.answeredAllCorrect)
            assertEquals(0, game.delivered)
        }
    }

    @Test
    fun `a switch flipped behind a train no longer moves it`() {
        val game = newGame(9L)
        game.stepUntilFirstTrain()
        val train = game.trains.single()
        val home = game.stationIndices().first { game.nodes[it].stationColor == train.color }
        game.setRouteTo(home)
        val root = game.nodes[0].children.single()
        while (train.edgeTo == root) game.step(frame)
        game.toggleSwitch(root)

        val arrivals = mutableListOf<Boolean>()
        while (arrivals.isEmpty()) arrivals += game.step(frame).arrivals

        assertEquals(listOf(true), arrivals)
        assertEquals(1, game.delivered)
        assertTrue(game.answeredAllCorrect)
    }

    @Test
    fun `trains only come in colours that have a station`() {
        for (seed in 0L until 10L) {
            val game = newGame(seed)
            game.runFor(60f) {
                val colors = stationIndices().map { nodes[it].stationColor }.toSet()
                trains.forEach { assertTrue(it.color in colors, "${it.color} has no station") }
                perfectFrame()
            }
        }
    }

    @Test
    fun `trains never bunch up`() {
        val game = newGame(2L)
        game.runFor(60f) {
            val frames = frames()
            for (i in frames.indices) {
                for (j in i + 1 until frames.size) {
                    val gap = abs(frames[i].x - frames[j].x) + abs(frames[i].y - frames[j].y)
                    // A car is under a tenth of the board wide on the largest map.
                    assertTrue(gap > 0.15f, "trains $gap apart")
                }
            }
            perfectFrame()
        }
    }

    @Test
    fun `the map grows only after enough deliveries and once the board is empty`() {
        val game = newGame(5L)
        var hadTrains = false
        var stage = 0
        game.runFor(60f) {
            val result = perfectFrame()
            if (stageIndex != stage) {
                assertFalse(hadTrains, "the map changed under a moving train")
                assertTrue(delivered >= deliveredToReach(stageIndex), "stage $stageIndex after $delivered")
                stage = stageIndex
            }
            hadTrains = trains.isNotEmpty()
            result
        }
        assertTrue(stage > 0)
    }

    private fun deliveredToReach(stage: Int): Int = listOf(0, 3, 7, 12, 17, 22)[stage]

    @Test
    fun `wrong deliveries do not grow the map`() {
        val game = newGame(6L)
        game.runFor(30f) {
            // Turn each switch away from the nearest train heading into it.
            val decided = mutableSetOf<Int>()
            for (train in trains.sortedByDescending { it.distance }) {
                val node = train.edgeTo
                if (!nodes[node].isSwitch || !decided.add(node)) continue
                val needed = routeTo(train.color)[node] ?: continue
                if (branchOf(node) == needed) toggleSwitch(node)
            }
            step(frame)
        }
        assertEquals(0, game.stageIndex)
        assertEquals(0, game.delivered)
    }

    @Test
    fun `winding down lets the trains already out arrive and score, then finishes`() {
        for (seed in 0L until 10L) {
            val game = newGame(seed)
            game.runFor(9f) { perfectFrame() }
            val stage = game.stageIndex
            val deliveredBefore = game.delivered
            val trainsOut = game.trains.size
            assertTrue(trainsOut > 0, "seed $seed: nothing on the board to finish")

            game.windDown()
            assertFalse(game.isFinished)
            val arrivals = mutableListOf<Boolean>()
            var frames = 0
            while (!game.isFinished) {
                assertTrue(game.trains.size <= trainsOut, "seed $seed: a train left the tunnel after the clock")
                arrivals += game.perfectFrame().arrivals
                assertTrue(++frames < 60 * 30, "seed $seed: never finished")
            }

            assertEquals(trainsOut, arrivals.size)
            assertEquals(deliveredBefore + trainsOut, game.delivered)
            assertEquals(stage, game.stageIndex)
        }
    }

    @Test
    fun `winding down on an empty board finishes once the last mark fades`() {
        val game = newGame(1L)
        game.windDown()
        assertTrue(game.isFinished)
        repeat(60 * 5) { game.step(frame) }
        assertTrue(game.trains.isEmpty())
    }

    @Test
    fun `only switches toggle`() {
        val game = newGame(3L)
        val switch = game.switchIndices().first()
        val before = game.branchOf(switch)
        assertTrue(game.toggleSwitch(switch))
        assertEquals(1 - before, game.branchOf(switch))
        assertFalse(game.toggleSwitch(0))
        assertFalse(game.toggleSwitch(game.stationIndices().first()))
    }

    @Test
    fun `perfect play reaches gold and leaving the switches alone stays under silver`() {
        for (seed in 0L until 20L) {
            val perfect = newGame(seed).runFor(60f) { perfectFrame() }.count { it }
            val idle = newGame(seed).runFor(60f) { step(frame) }.count { it }
            assertTrue(perfect >= GameType.RAIL_YARD.goldScore, "seed $seed: perfect play delivered $perfect")
            assertTrue(idle < GameType.RAIL_YARD.silverScore, "seed $seed: idle delivered $idle")
        }
    }
}
