package com.inspiredandroid.braincup.games

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.app.RailYardUiState
import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.currentTimeMillis
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Rail Yard: coloured trains leave a tunnel one after another, and the player flips the switches so
 * each one ends at the station of its colour. Several trains are on the board at once, so the game
 * is about keeping all of them in mind while acting on one.
 *
 * The track is a tree rooted at the tunnel: every switch splits one track into two and tracks never
 * merge again, so each station is reached by exactly one setting of the switches along its route.
 * A train reads a switch at the moment it reaches it, so a switch flipped behind a train no longer
 * matters to that train, only to the ones following.
 *
 * Positions are in a unit square, laid out like an upside-down bracket: the tunnel at the top, each
 * node on the row of its depth, each station in its own column. Every edge runs across and then
 * down, so the drawing never crosses itself, and the UI may stretch the square to any shape without
 * bending a track off the axes.
 *
 * The run ramps inside itself rather than across sessions: after a few trains reach the right
 * station the tunnel stops, the board drains, and a map with one more station replaces it.
 */
class RailYardGame(
    private val random: Random = Random.Default,
) : Game(),
    PausableTimedPhaseGame {

    @Immutable
    data class Point(val x: Float, val y: Float)

    /** Node 0 is the tunnel. A switch has two children, left then right; a station has none. */
    class Node(
        val position: Point,
        val parent: Int,
        val children: List<Int>,
        val stationColor: GameColor?,
    ) {
        val isSwitch: Boolean get() = children.size == 2
    }

    class Train(
        val color: GameColor,
        var edgeTo: Int,
        var distance: Float,
    )

    @Immutable
    data class TrainFrame(
        val x: Float,
        val y: Float,
        val isHorizontal: Boolean,
        val color: GameColor,
    )

    data class StepResult(val arrivals: List<Boolean>, val boardChanged: Boolean)

    private data class Stage(val stations: Int, val spawnIntervalMs: Long, val speed: Float, val correctToAdvance: Int)

    enum class StationFlash {
        NONE,
        CORRECT,
        WRONG,
    }

    override val adaptiveDifficulty: Boolean = false

    var nodes: List<Node> = emptyList()
        private set

    /** Index into a switch's children; absent for every node that is not a switch. */
    private val branchBySwitch = mutableMapOf<Int, Int>()

    private val edgePaths = mutableMapOf<Int, List<Point>>()
    private val edgeLengths = mutableMapOf<Int, Float>()

    private val movingTrains = mutableListOf<Train>()
    val trains: List<Train> get() = movingTrains

    var stageIndex: Int = 0
        private set

    var layoutKey: Int = 0
        private set

    var delivered: Int = 0
        private set

    private var correctThisStage = 0
    private var isSpawning = true
    private var spawnCountdownMs = 0L
    private var stageBeatMs: Long? = null
    private var previousColor: GameColor? = null

    private var clockMs = 0L
    private val flashUntilByStation = mutableMapOf<Int, Pair<StationFlash, Long>>()

    private var animationJob: Job? = null
    private var isRunning = false
    private var onStep: ((StepResult) -> Unit)? = null

    private val stage: Stage get() = STAGES[stageIndex]

    override fun generateRound() {
        stageIndex = 0
        delivered = 0
        clockMs = 0L
        movingTrains.clear()
        startStage()
    }

    fun switchIndices(): List<Int> = nodes.indices.filter { nodes[it].isSwitch }

    fun stationIndices(): List<Int> = nodes.indices.filter { nodes[it].stationColor != null }

    fun branchOf(switchIndex: Int): Int = branchBySwitch.getValue(switchIndex)

    /** Returns false when [nodeIndex] is not a switch on the current map. */
    fun toggleSwitch(nodeIndex: Int): Boolean {
        val branch = branchBySwitch[nodeIndex] ?: return false
        branchBySwitch[nodeIndex] = 1 - branch
        return true
    }

    /** The branch every switch on the way to the [color] station must be set to, keyed by switch. */
    fun routeTo(color: GameColor): Map<Int, Int> {
        val station = nodes.indexOfFirst { it.stationColor == color }
        require(station >= 0) { "No $color station on this map" }
        val route = mutableMapOf<Int, Int>()
        var child = station
        var parent = nodes[child].parent
        while (parent > 0) {
            route[parent] = nodes[parent].children.indexOf(child)
            child = parent
            parent = nodes[child].parent
        }
        return route
    }

    /**
     * The switch a perfect player would flip now: one that is set wrong for the nearest train
     * heading into it, once that train is within [lookahead] of it. Drives the instructions demo.
     */
    fun switchToFlip(lookahead: Float): Int? {
        val decided = mutableSetOf<Int>()
        for (train in movingTrains.sortedBy { edgeLengths.getValue(it.edgeTo) - it.distance }) {
            val node = train.edgeTo
            if (!nodes[node].isSwitch || !decided.add(node)) continue
            if (edgeLengths.getValue(node) - train.distance > lookahead) continue
            val needed = routeTo(train.color)[node] ?: continue
            if (branchOf(node) != needed) return node
        }
        return null
    }

    /** Advances the board by [deltaSeconds]: spawns, moves, switches and arrivals. */
    fun step(deltaSeconds: Float): StepResult {
        val deltaMs = (deltaSeconds * 1000).toLong()
        clockMs += deltaMs
        var boardChanged = expireFlashes()
        val arrivals = mutableListOf<Boolean>()

        val beat = stageBeatMs
        if (beat != null) {
            val remaining = beat - deltaMs
            if (remaining <= 0) {
                stageIndex++
                startStage()
                return StepResult(arrivals, boardChanged = true)
            }
            stageBeatMs = remaining
            return StepResult(arrivals, boardChanged)
        }

        if (isSpawning) {
            spawnCountdownMs -= deltaMs
            if (spawnCountdownMs <= 0) {
                spawnTrain()
                spawnCountdownMs += stage.spawnIntervalMs
            }
        }

        val iterator = movingTrains.iterator()
        while (iterator.hasNext()) {
            val train = iterator.next()
            train.distance += stage.speed * deltaSeconds
            while (train.distance >= edgeLengths.getValue(train.edgeTo)) {
                train.distance -= edgeLengths.getValue(train.edgeTo)
                val node = nodes[train.edgeTo]
                val stationColor = node.stationColor
                if (stationColor != null) {
                    iterator.remove()
                    arrivals += arrive(train, train.edgeTo, stationColor)
                    boardChanged = true
                    break
                }
                train.edgeTo = node.children[branchBySwitch.getValue(train.edgeTo)]
            }
        }

        if (!isSpawning && movingTrains.isEmpty() && stageIndex < STAGES.lastIndex) {
            stageBeatMs = STAGE_BEAT_MS
        }
        return StepResult(arrivals, boardChanged)
    }

    fun frames(): List<TrainFrame> = trains.map { train ->
        val path = edgePaths.getValue(train.edgeTo)
        var remaining = train.distance
        for (i in 0 until path.lastIndex) {
            val from = path[i]
            val to = path[i + 1]
            val length = segmentLength(from, to)
            val isHorizontal = from.y == to.y
            if (remaining <= length || i == path.lastIndex - 1) {
                val t = if (length == 0f) 0f else (remaining / length).coerceIn(0f, 1f)
                return@map TrainFrame(
                    x = from.x + (to.x - from.x) * t,
                    y = from.y + (to.y - from.y) * t,
                    isHorizontal = isHorizontal,
                    color = train.color,
                )
            }
            remaining -= length
        }
        error("Edge into ${train.edgeTo} has no segments")
    }

    fun startMotion(scope: CoroutineScope, onStep: (StepResult) -> Unit) {
        this.onStep = onStep
        isRunning = true
        launchLoop(scope)
    }

    override val isTimedPhaseActive: Boolean get() = isRunning

    override fun pauseTimedPhase() {
        animationJob?.cancel()
        animationJob = null
    }

    override fun resumeTimedPhase(scope: CoroutineScope, onChange: () -> Unit) {
        if (isRunning) launchLoop(scope)
    }

    override fun cancelTimedPhase() {
        isRunning = false
        animationJob?.cancel()
        animationJob = null
    }

    override fun isCorrect(input: String): Boolean = false

    override fun solution(): String = ""

    override fun toUiState(): RailYardUiState = RailYardUiState(
        layoutKey = layoutKey,
        tunnel = nodes[0].position,
        tracks = nodes.indices.drop(1).map { index ->
            val parent = nodes[index].parent
            RailYardUiState.Track(
                points = edgePaths.getValue(index).toImmutableList(),
                isOpen = !nodes[parent].isSwitch || nodes[parent].children[branchOf(parent)] == index,
            )
        }.toImmutableList(),
        switches = switchIndices().map { index ->
            RailYardUiState.Switch(
                nodeIndex = index,
                position = nodes[index].position,
                turnsRight = branchOf(index) == 1,
            )
        }.toImmutableList(),
        stations = stationIndices().map { index ->
            RailYardUiState.Station(
                position = nodes[index].position,
                color = nodes[index].stationColor!!,
                flash = flashUntilByStation[index]?.first ?: StationFlash.NONE,
            )
        }.toImmutableList(),
        trains = frames().toImmutableList(),
    )

    private fun launchLoop(scope: CoroutineScope) {
        animationJob?.cancel()
        animationJob = scope.launch {
            var lastFrameTime = currentTimeMillis()
            while (true) {
                delay(FRAME_DELAY)
                val now = currentTimeMillis()
                val delta = ((now - lastFrameTime) / 1000f).coerceAtMost(MAX_FRAME_SECONDS)
                lastFrameTime = now
                onStep?.invoke(step(delta))
            }
        }
    }

    private fun startStage() {
        buildLayout(stage.stations)
        correctThisStage = 0
        isSpawning = true
        stageBeatMs = null
        spawnCountdownMs = FIRST_SPAWN_DELAY_MS
        previousColor = null
        flashUntilByStation.clear()
        layoutKey++
    }

    private fun spawnTrain() {
        val colors = stationIndices().map { nodes[it].stationColor!! }
        // A repeat colour would let the player leave the switches as they are, so it is avoided
        // whenever there are enough stations for the next colour to stay unpredictable.
        val candidates = if (colors.size > 2) colors.filter { it != previousColor } else colors
        val color = candidates.random(random)
        previousColor = color
        movingTrains += Train(color = color, edgeTo = nodes[0].children.single(), distance = 0f)
    }

    private fun arrive(train: Train, station: Int, stationColor: GameColor): Boolean {
        val isCorrect = train.color == stationColor
        val flash = if (isCorrect) StationFlash.CORRECT else StationFlash.WRONG
        flashUntilByStation[station] = flash to clockMs + FLASH_MS
        if (isCorrect) {
            delivered++
            correctThisStage++
            if (correctThisStage >= stage.correctToAdvance) isSpawning = false
        } else {
            answeredAllCorrect = false
        }
        return isCorrect
    }

    private fun expireFlashes(): Boolean {
        val expired = flashUntilByStation.filterValues { (_, until) -> until <= clockMs }.keys
        expired.forEach { flashUntilByStation.remove(it) }
        return expired.isNotEmpty()
    }

    private sealed interface Shape {
        data object Leaf : Shape

        data class Split(val left: Shape, val right: Shape) : Shape
    }

    private fun buildLayout(stationCount: Int) {
        val maxDepth = minOf(MAX_DEPTH, ceilLog2(stationCount) + 1)
        val shape = randomShape(stationCount, maxDepth)
        val depth = depthOf(shape)
        val colors = STATION_COLORS.shuffled(random).take(stationCount)

        val built = mutableListOf<Node>()
        branchBySwitch.clear()
        edgePaths.clear()
        edgeLengths.clear()

        // Row 0 is the tunnel and the root switch sits on row 1, so the deepest station is on row depth + 1.
        fun rowY(row: Int): Float = TOP + row * (BOTTOM - TOP) / (depth + 1)

        // Children are placed before their parent so a switch can sit centred over them; indices
        // are reserved first so the tunnel stays node 0.
        val positions = mutableMapOf<Int, Point>()
        val parents = mutableMapOf<Int, Int>()
        val childrenOf = mutableMapOf<Int, List<Int>>()
        val colorOf = mutableMapOf<Int, GameColor>()
        var nextIndex = 1
        var nextLeaf = 0

        fun place(node: Shape, row: Int): Int {
            val index = nextIndex++
            when (node) {
                Shape.Leaf -> {
                    val slot = nextLeaf++
                    positions[index] = Point((slot + 0.5f) / stationCount, rowY(row))
                    colorOf[index] = colors[slot]
                    childrenOf[index] = emptyList()
                }
                is Shape.Split -> {
                    val left = place(node.left, row + 1)
                    val right = place(node.right, row + 1)
                    parents[left] = index
                    parents[right] = index
                    childrenOf[index] = listOf(left, right)
                    positions[index] = Point((positions.getValue(left).x + positions.getValue(right).x) / 2f, rowY(row))
                }
            }
            return index
        }

        val root = place(shape, row = 1)
        parents[root] = 0
        val rootPosition = positions.getValue(root)

        built += Node(position = Point(rootPosition.x, rowY(0)), parent = -1, children = listOf(root), stationColor = null)
        for (index in 1 until nextIndex) {
            built += Node(
                position = positions.getValue(index),
                parent = parents.getValue(index),
                children = childrenOf.getValue(index),
                stationColor = colorOf[index],
            )
        }
        nodes = built

        for (index in 1 until nodes.size) {
            val from = nodes[nodes[index].parent].position
            val to = nodes[index].position
            val corner = Point(to.x, from.y)
            val path = if (corner == from) listOf(from, to) else listOf(from, corner, to)
            edgePaths[index] = path
            edgeLengths[index] = path.zipWithNext { a, b -> segmentLength(a, b) }.sum()
        }
        for (index in switchIndices()) branchBySwitch[index] = random.nextInt(2)
    }

    /**
     * A random full binary tree with [leaves] leaves and depth at most [maxDepth]. Splits lean
     * towards even but not always, so a map can have a station high up beside a deep subtree.
     */
    private fun randomShape(leaves: Int, maxDepth: Int): Shape {
        if (leaves == 1) return Shape.Leaf
        val capacity = 1 shl (maxDepth - 1)
        val options = (1 until leaves).filter { it <= capacity && leaves - it <= capacity }
        val left = options.random(random)
        return Shape.Split(
            left = randomShape(left, maxDepth - 1),
            right = randomShape(leaves - left, maxDepth - 1),
        )
    }

    private fun depthOf(shape: Shape): Int = when (shape) {
        Shape.Leaf -> 0
        is Shape.Split -> 1 + maxOf(depthOf(shape.left), depthOf(shape.right))
    }

    private fun ceilLog2(n: Int): Int {
        var depth = 0
        while ((1 shl depth) < n) depth++
        return depth
    }

    private fun segmentLength(a: Point, b: Point): Float = abs(b.x - a.x) + abs(b.y - a.y)

    companion object {
        // Trains travel slowly next to how often they leave, so that several are on the board at
        // once; that, not speed, is what the game asks of the player.
        private val STAGES = listOf(
            Stage(stations = 3, spawnIntervalMs = 2400L, speed = 0.20f, correctToAdvance = 3),
            Stage(stations = 4, spawnIntervalMs = 2200L, speed = 0.21f, correctToAdvance = 4),
            Stage(stations = 5, spawnIntervalMs = 2000L, speed = 0.22f, correctToAdvance = 5),
            Stage(stations = 6, spawnIntervalMs = 1850L, speed = 0.23f, correctToAdvance = 5),
            Stage(stations = 7, spawnIntervalMs = 1700L, speed = 0.24f, correctToAdvance = 5),
            Stage(stations = 8, spawnIntervalMs = 1600L, speed = 0.25f, correctToAdvance = Int.MAX_VALUE),
        )

        val STATION_COLORS = listOf(
            GameColor.RED,
            GameColor.GREEN,
            GameColor.BLUE,
            GameColor.PURPLE,
            GameColor.YELLOW,
            GameColor.ORANGE,
            GameColor.TURQUOISE,
            GameColor.ROSA,
        )

        private const val MAX_DEPTH = 4
        private const val TOP = 0.06f
        private const val BOTTOM = 0.9f

        const val FIRST_SPAWN_DELAY_MS = 600L
        const val STAGE_BEAT_MS = 900L
        const val FLASH_MS = 700L

        private val FRAME_DELAY = 16.milliseconds
        private const val MAX_FRAME_SECONDS = 0.05f
    }
}
