package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.games.tools.Color
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.Shape

class PatternSequenceGame : Game() {
    enum class PatternType {
        SHAPE_CYCLE,
        COLOR_CYCLE,
        ALTERNATING_PAIR,
        INDEPENDENT_CYCLES,
        ROTATION,
        TRIPLE_CYCLE,
        PALINDROME,
        ROTATION_WITH_COLOR,
        INTERLEAVED,
    }

    val sequence = mutableListOf<Figure>()
    val options = mutableListOf<Figure>()
    var correctOptionIndex = 0

    private val asymmetricShapes = listOf(
        Shape.TRIANGLE,
        Shape.ARROW,
        Shape.L,
        Shape.T,
        Shape.ABSTRACT_TRIANGLE,
    )

    private val allShapes = listOf(
        Shape.SQUARE,
        Shape.TRIANGLE,
        Shape.CIRCLE,
        Shape.HEART,
        Shape.STAR,
        Shape.T,
        Shape.L,
        Shape.DIAMOND,
        Shape.HOUSE,
        Shape.ABSTRACT_TRIANGLE,
        Shape.ARROW,
    )

    private val allColors = listOf(
        Color.GREEN,
        Color.BLUE,
        Color.PURPLE,
        Color.RED,
        Color.YELLOW,
        Color.ORANGE,
        Color.TURQUOISE,
        Color.ROSA,
    )

    override fun isCorrect(input: String): Boolean = input.toIntOrNull() == correctOptionIndex

    override fun nextRound() {
        sequence.clear()
        options.clear()

        val patternType = selectPatternType()
        val seqLength = getSequenceLength()

        when (patternType) {
            PatternType.SHAPE_CYCLE -> generateShapeCycle(seqLength)
            PatternType.COLOR_CYCLE -> generateColorCycle(seqLength)
            PatternType.ALTERNATING_PAIR -> generateAlternatingPair(seqLength)
            PatternType.INDEPENDENT_CYCLES -> generateIndependentCycles(seqLength)
            PatternType.ROTATION -> generateRotation(seqLength)
            PatternType.TRIPLE_CYCLE -> generateTripleCycle(seqLength)
            PatternType.PALINDROME -> generatePalindrome(seqLength)
            PatternType.ROTATION_WITH_COLOR -> generateRotationWithColor(seqLength)
            PatternType.INTERLEAVED -> generateInterleaved(seqLength)
        }
    }

    override fun solution(): String {
        val correct = options[correctOptionIndex]
        val rotationInfo = if (correct.rotation != 0) {
            " pointing ${correct.getRotationString()}"
        } else {
            ""
        }
        return "${correct.color.displayName} ${correct.shape.displayName}$rotationInfo"
    }

    override fun hint(): String? = null

    override fun getGameType(): GameType = GameType.PATTERN_SEQUENCE

    private fun selectPatternType(): PatternType {
        val available = when {
            round >= 8 -> listOf(
                PatternType.PALINDROME,
                PatternType.ROTATION_WITH_COLOR,
                PatternType.INTERLEAVED,
            )
            round >= 6 -> listOf(
                PatternType.INDEPENDENT_CYCLES,
                PatternType.TRIPLE_CYCLE,
                PatternType.PALINDROME,
                PatternType.ROTATION_WITH_COLOR,
                PatternType.INTERLEAVED,
            )
            round >= 4 -> listOf(
                PatternType.ALTERNATING_PAIR,
                PatternType.INDEPENDENT_CYCLES,
                PatternType.TRIPLE_CYCLE,
                PatternType.PALINDROME,
                PatternType.ROTATION,
                PatternType.ROTATION_WITH_COLOR,
            )
            round >= 2 -> listOf(
                PatternType.SHAPE_CYCLE,
                PatternType.COLOR_CYCLE,
                PatternType.ALTERNATING_PAIR,
                PatternType.INDEPENDENT_CYCLES,
                PatternType.TRIPLE_CYCLE,
            )
            else -> listOf(
                PatternType.SHAPE_CYCLE,
                PatternType.COLOR_CYCLE,
                PatternType.ALTERNATING_PAIR,
            )
        }
        return available.random()
    }

    private fun getSequenceLength(): Int = when {
        round >= 6 -> 6
        round >= 2 -> 5
        else -> 4
    }

    private fun getPoolSize(): Int = when {
        round >= 6 -> allShapes.size
        round >= 4 -> 5
        round >= 2 -> 4
        else -> 3
    }

    private fun generateShapeCycle(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val color = allColors.random()
        val period = if (shapes.size >= 3) (2..minOf(3, shapes.size)).random() else 2
        val cycleShapes = shapes.shuffled().take(period)

        for (i in 0 until seqLength) {
            sequence.add(Figure(cycleShapes[i % period], color))
        }
        val answer = Figure(cycleShapes[seqLength % period], color)
        generateOptions(answer, shapes, listOf(color))
    }

    private fun generateColorCycle(seqLength: Int) {
        val poolSize = getPoolSize()
        val colors = allColors.shuffled().take(poolSize)
        val shape = allShapes.random()
        val period = if (colors.size >= 3) (2..minOf(3, colors.size)).random() else 2
        val cycleColors = colors.shuffled().take(period)

        for (i in 0 until seqLength) {
            sequence.add(Figure(shape, cycleColors[i % period]))
        }
        val answer = Figure(shape, cycleColors[seqLength % period])
        generateOptions(answer, listOf(shape), colors)
    }

    private fun generateAlternatingPair(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val colors = allColors.shuffled().take(poolSize)

        val fig1 = Figure(shapes[0], colors[0])
        val fig2 = Figure(shapes[1], colors[1])

        for (i in 0 until seqLength) {
            val src = if (i % 2 == 0) fig1 else fig2
            sequence.add(Figure(src.shape, src.color))
        }
        val answerSrc = if (seqLength % 2 == 0) fig1 else fig2
        val answer = Figure(answerSrc.shape, answerSrc.color)
        generateOptions(answer, shapes, colors)
    }

    private fun generateIndependentCycles(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val colors = allColors.shuffled().take(poolSize)

        val shapePeriod = 2
        val colorPeriod = 3.coerceAtMost(colors.size)
        val cycleShapes = shapes.shuffled().take(shapePeriod)
        val cycleColors = colors.shuffled().take(colorPeriod)

        for (i in 0 until seqLength) {
            sequence.add(Figure(cycleShapes[i % shapePeriod], cycleColors[i % colorPeriod]))
        }
        val answer = Figure(
            cycleShapes[seqLength % shapePeriod],
            cycleColors[seqLength % colorPeriod],
        )
        generateOptions(answer, shapes, colors)
    }

    private fun generateRotation(seqLength: Int) {
        val shape = asymmetricShapes.random()
        val color = allColors.random()
        val rotations = listOf(0, 90, 180, 270)
        val step = listOf(90, 180).random()
        val startRotation = rotations.random()

        for (i in 0 until seqLength) {
            val rotation = (startRotation + step * i) % 360
            sequence.add(Figure(shape, color, rotation))
        }
        val answerRotation = (startRotation + step * seqLength) % 360
        val answer = Figure(shape, color, answerRotation)
        generateRotationOptions(answer, shape, color)
    }

    private fun generateTripleCycle(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val colors = allColors.shuffled().take(poolSize)

        val figures = (0 until 3).map { Figure(shapes[it], colors[it]) }

        for (i in 0 until seqLength) {
            val f = figures[i % 3]
            sequence.add(Figure(f.shape, f.color))
        }
        val answerFig = figures[seqLength % 3]
        val answer = Figure(answerFig.shape, answerFig.color)
        generateOptions(answer, shapes, colors)
    }

    private fun generatePalindrome(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val colors = allColors.shuffled().take(poolSize)

        val figures = (0 until 3).map { Figure(shapes[it], colors[it]) }
        // Bounce pattern: 0,1,2,1,0,1,2,1,... period = 4
        val bouncePattern = listOf(0, 1, 2, 1)

        for (i in 0 until seqLength) {
            val f = figures[bouncePattern[i % 4]]
            sequence.add(Figure(f.shape, f.color))
        }
        val answerFig = figures[bouncePattern[seqLength % 4]]
        val answer = Figure(answerFig.shape, answerFig.color)
        generateOptions(answer, shapes, colors)
    }

    private fun generateRotationWithColor(seqLength: Int) {
        val shape = asymmetricShapes.random()
        val rotations = listOf(0, 90, 180, 270)
        val rotationStep = listOf(90, 180).random()
        val startRotation = rotations.random()

        val colorPeriod = listOf(2, 3).random()
        val cycleColors = allColors.shuffled().take(colorPeriod)

        for (i in 0 until seqLength) {
            val rotation = (startRotation + rotationStep * i) % 360
            val color = cycleColors[i % colorPeriod]
            sequence.add(Figure(shape, color, rotation))
        }
        val answerRotation = (startRotation + rotationStep * seqLength) % 360
        val answerColor = cycleColors[seqLength % colorPeriod]
        val answer = Figure(shape, answerColor, answerRotation)
        generateRotationWithColorOptions(answer, shape, cycleColors)
    }

    private fun generateInterleaved(seqLength: Int) {
        val poolSize = getPoolSize()
        val shapes = allShapes.shuffled().take(poolSize)
        val colors = allColors.shuffled().take(poolSize)

        // Even-position sub-sequence: fixed color, shapes cycle with period 2
        val evenColor = colors[0]
        val evenShapes = shapes.shuffled().take(2)

        // Odd-position sub-sequence: fixed shape, colors cycle with period 2
        val oddShape = shapes.filter { it !in evenShapes }.randomOrNull() ?: shapes.last()
        val oddColors = colors.filter { it != evenColor }.shuffled().take(2)

        for (i in 0 until seqLength) {
            if (i % 2 == 0) {
                sequence.add(Figure(evenShapes[(i / 2) % evenShapes.size], evenColor))
            } else {
                sequence.add(Figure(oddShape, oddColors[(i / 2) % oddColors.size]))
            }
        }
        val answer = if (seqLength % 2 == 0) {
            Figure(evenShapes[(seqLength / 2) % evenShapes.size], evenColor)
        } else {
            Figure(oddShape, oddColors[(seqLength / 2) % oddColors.size])
        }
        generateOptions(answer, shapes, colors)
    }

    private fun generateOptions(
        answer: Figure,
        shapePool: List<Shape>,
        colorPool: List<Color>,
    ) {
        // Ensure pools are large enough for 3 unique distractors
        val shapes = if (shapePool.size * colorPool.size < 4) {
            (shapePool + allShapes.filter { it !in shapePool }.shuffled()).distinct().take(4)
        } else {
            shapePool
        }
        val colors = if (shapes.size * colorPool.size < 4) {
            (colorPool + allColors.filter { it !in colorPool }.shuffled()).distinct().take(4)
        } else {
            colorPool
        }

        val distractors = mutableListOf<Figure>()

        // Distractor 1: right shape, wrong color
        val wrongColor = colors.filter { it != answer.color }.randomOrNull()
        if (wrongColor != null) {
            distractors.add(Figure(answer.shape, wrongColor))
        }

        // Distractor 2: wrong shape, right color
        val wrongShape = shapes.filter { it != answer.shape }.randomOrNull()
        if (wrongShape != null) {
            distractors.add(Figure(wrongShape, answer.color))
        }

        // Distractor 3: wrong shape, wrong color
        val wrongShape2 = shapes.filter { it != answer.shape }.randomOrNull()
        val wrongColor2 = colors.filter { it != answer.color }.randomOrNull()
        if (wrongShape2 != null && wrongColor2 != null) {
            distractors.add(Figure(wrongShape2, wrongColor2))
        }

        // Fill remaining distractors if needed
        var attempts = 0
        while (distractors.size < 3 && attempts < 50) {
            attempts++
            val s = shapes.random()
            val c = colors.random()
            if (!figuresMatch(Figure(s, c), answer) &&
                distractors.none { figuresMatch(it, Figure(s, c)) }
            ) {
                distractors.add(Figure(s, c))
            }
        }

        val allOptions = (distractors.take(3) + answer).shuffled()
        options.addAll(allOptions)
        correctOptionIndex = allOptions.indexOfFirst { figuresMatch(it, answer) }
    }

    private fun generateRotationOptions(
        answer: Figure,
        shape: Shape,
        color: Color,
    ) {
        val rotations = listOf(0, 90, 180, 270)
        val wrongRotations = rotations.filter { it != answer.rotation }.shuffled().take(3)
        val distractors = wrongRotations.map { Figure(shape, color, it) }

        val allOptions = (distractors + answer).shuffled()
        options.addAll(allOptions)
        correctOptionIndex = allOptions.indexOfFirst {
            figuresMatch(it, answer) && it.rotation == answer.rotation
        }
    }

    private fun generateRotationWithColorOptions(
        answer: Figure,
        shape: Shape,
        colorPool: List<Color>,
    ) {
        val rotations = listOf(0, 90, 180, 270)
        val wrongRotations = rotations.filter { it != answer.rotation }.shuffled()
        val wrongColors = colorPool.filter { it != answer.color }

        val distractors = mutableListOf<Figure>()

        // Distractor 1: right rotation, wrong color
        if (wrongColors.isNotEmpty()) {
            distractors.add(Figure(shape, wrongColors.random(), answer.rotation))
        }

        // Distractor 2: wrong rotation, right color
        if (wrongRotations.isNotEmpty()) {
            distractors.add(Figure(shape, answer.color, wrongRotations[0]))
        }

        // Distractor 3: wrong rotation, wrong color
        if (wrongRotations.size >= 2 && wrongColors.isNotEmpty()) {
            distractors.add(Figure(shape, wrongColors.random(), wrongRotations[1]))
        }

        // Fill remaining distractors if needed (cap at max possible unique figures minus answer)
        val maxDistractors = (rotations.size * colorPool.size - 1).coerceAtMost(3)
        var attempts = 0
        while (distractors.size < maxDistractors && attempts < 50) {
            attempts++
            val r = rotations.random()
            val c = colorPool.random()
            val candidate = Figure(shape, c, r)
            if (!figuresMatch(candidate, answer) && distractors.none { figuresMatch(it, candidate) }) {
                distractors.add(candidate)
            }
        }

        val allOptions = (distractors.take(3) + answer).shuffled()
        options.addAll(allOptions)
        correctOptionIndex = allOptions.indexOfFirst { figuresMatch(it, answer) }
    }

    companion object {
        fun figuresMatch(a: Figure, b: Figure): Boolean = a.shape == b.shape && a.color == b.color && a.rotation == b.rotation
    }
}
