package com.inspiredandroid.braincup.games.matrix

import com.inspiredandroid.braincup.games.tools.GameColor
import com.inspiredandroid.braincup.games.tools.Shape
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlin.random.Random

/**
 * Procedural generator for 3x3 matrix reasoning items.
 *
 * Four rules — Constant, Progression, Arithmetic and Distribute-Three — act on entity attributes
 * (type, colour, rotation, size) and layout attributes (number, position). Distractors are
 * balanced so that no attribute value is held by a majority of the options; without that
 * balancing the item is solvable from the options alone, by picking whichever one looks most
 * like the others.
 *
 * Rules run row-wise. Every attribute is either held constant across all nine panels, carrying no
 * information, or governed by a rule, in which case the answer's value for it is fully determined.
 * Nothing is ever left free, so every generated item has exactly one valid answer.
 */
class MatrixGenerator(private val random: Random) {

    fun generate(difficulty: Int): MatrixProblem {
        val tier = difficulty.coerceIn(0, MAX_DIFFICULTY)
        var unbalanced: MatrixProblem? = null
        repeat(MAX_ATTEMPTS) {
            val problem = tryBuild(tier)
            if (problem != null) {
                if (isBalanced(problem)) return problem
                if (unbalanced == null) unbalanced = problem
            }
        }
        return unbalanced ?: buildFallback()
    }

    /** Whether no governed attribute has the answer's value in more than half the options. */
    fun isBalanced(problem: MatrixProblem): Boolean {
        val answer = problem.answer.spec
        val limit = problem.options.size / 2
        return problem.governedAttributes.none { attribute ->
            problem.options.count { it.spec.valueOf(attribute) == answer.valueOf(attribute) } > limit
        }
    }

    private fun tryBuild(tier: Int): MatrixProblem? {
        val plan = planFor(tier) ?: return null
        val grids = plan.governed.associate { governed ->
            governed.attribute to (ruleGrid(governed, plan) ?: return null)
        }
        val specs = List(9) { index ->
            var spec = plan.baseSpec
            for ((attribute, grid) in grids) {
                spec = spec.withValue(attribute, grid[index / 3][index % 3])
            }
            spec
        }
        val rows = specs.chunked(3)
        if (rows[0] == rows[1] || rows[0] == rows[2] || rows[1] == rows[2]) return null
        if (tier > 0 && plan.governed.all { it.rule == MatrixRule.CONSTANT }) return null

        val attributes = plan.governed.map { it.attribute }
        val options = buildOptions(specs, plan, attributes) ?: return null
        val correctIndex = options.indexOf(specs[8])

        return MatrixProblem(
            matrix = specs.map { materialize(it, plan) }.toImmutableList(),
            options = options.map { materialize(it, plan) }.toImmutableList(),
            correctOptionIndex = correctIndex,
            optionColumns = optionColumnsFor(plan.optionCount),
            rules = plan.governed.associate { it.attribute to it.rule }.toImmutableMap(),
            ruleSummary = plan.governed.joinToString(", ") { it.summary() },
        )
    }

    // region planning

    private fun planFor(tier: Int): Plan? {
        val governed = pickGoverned(tier) ?: return null
        val attributes = governed.map { it.attribute }
        if (MatrixAttribute.SIZE in attributes && MatrixAttribute.NUMBER in attributes) return null
        if (MatrixAttribute.POSITION in attributes && MatrixAttribute.NUMBER in attributes) return null

        val shapeSource = if (MatrixAttribute.ROTATION in attributes) ROTATABLE_SHAPES else ALL_SHAPES
        val shapes = shapeSource.shuffled(random).take(VALUE_POOL_SIZE)
        val colors = ALL_COLORS.shuffled(random).take(VALUE_POOL_SIZE)
        val fixedNumber = when {
            MatrixAttribute.POSITION in attributes -> listOf(2, 3).random(random)
            MatrixAttribute.NUMBER in attributes -> 1
            else -> 1
        }
        return Plan(
            governed = governed,
            shapes = shapes,
            colors = colors,
            fixedNumber = fixedNumber,
            optionCount = optionCountFor(tier),
            positionGoverned = MatrixAttribute.POSITION in attributes,
        )
    }

    private fun pickGoverned(tier: Int): List<Governed>? {
        val attributes = when (tier) {
            0 -> listOf(MatrixAttribute.TYPE, MatrixAttribute.COLOR).shuffled(random).take(1)
            1 -> ENTITY_ATTRIBUTES.shuffled(random).take(1)
            2 -> ENTITY_ATTRIBUTES.shuffled(random).take(2)
            3 -> {
                val anchor = listOf(MatrixAttribute.SIZE, MatrixAttribute.ROTATION).random(random)
                listOf(anchor) + ENTITY_ATTRIBUTES.filter { it != anchor }.shuffled(random).take(1)
            }
            4 -> listOf(MatrixAttribute.NUMBER) +
                listOf(MatrixAttribute.TYPE, MatrixAttribute.COLOR, MatrixAttribute.ROTATION)
                    .shuffled(random).take(2)
            5 -> (ENTITY_ATTRIBUTES + MatrixAttribute.NUMBER + MatrixAttribute.POSITION)
                .shuffled(random).take(3)
            else -> (ENTITY_ATTRIBUTES + MatrixAttribute.NUMBER + MatrixAttribute.POSITION)
                .shuffled(random).take(4)
        }
        if (attributes.isEmpty()) return null
        val rules = attributes.map { attribute ->
            val allowed = allowedRules(attribute, tier)
            Governed(attribute, allowed.randomOrNull(random) ?: return null)
        }
        return rules
    }

    private fun allowedRules(attribute: MatrixAttribute, tier: Int): List<MatrixRule> {
        if (tier == 0) return listOf(MatrixRule.CONSTANT, MatrixRule.DISTRIBUTE_THREE)
        return when (attribute) {
            // Shapes and hues have no order a player can read, so progression and arithmetic
            // would only be guessable on them.
            MatrixAttribute.TYPE, MatrixAttribute.COLOR, MatrixAttribute.POSITION ->
                listOf(MatrixRule.CONSTANT, MatrixRule.DISTRIBUTE_THREE)
            MatrixAttribute.ROTATION, MatrixAttribute.SIZE ->
                listOf(MatrixRule.CONSTANT, MatrixRule.PROGRESSION, MatrixRule.DISTRIBUTE_THREE)
            MatrixAttribute.NUMBER ->
                listOf(MatrixRule.PROGRESSION, MatrixRule.ARITHMETIC, MatrixRule.DISTRIBUTE_THREE)
        }
    }

    // endregion

    // region rule grids

    private fun ruleGrid(governed: Governed, plan: Plan): List<List<Int>>? {
        val domain = domainOf(governed.attribute, plan)
        return when (governed.rule) {
            MatrixRule.CONSTANT -> {
                val values = domain.shuffled(random).take(3)
                if (values.size < 3) null else values.map { value -> List(3) { value } }
            }
            MatrixRule.DISTRIBUTE_THREE -> {
                val values = domain.shuffled(random).take(3)
                if (values.size < 3) {
                    null
                } else {
                    List(3) { row -> List(3) { column -> values[(row + column) % 3] } }
                }
            }
            MatrixRule.PROGRESSION -> progressionGrid(governed.attribute, domain)
            MatrixRule.ARITHMETIC -> arithmeticGrid()
        }
    }

    private fun progressionGrid(attribute: MatrixAttribute, domain: List<Int>): List<List<Int>>? {
        val step = listOf(-1, 1).random(random)
        if (attribute == MatrixAttribute.ROTATION) {
            // Rotation wraps, so any start is valid and all three rows can start differently.
            val starts = domain.shuffled(random).take(3)
            if (starts.size < 3) return null
            return starts.map { start -> List(3) { column -> (start + step * column).mod(4) } }
        }
        // Stepping over domain indices rather than raw values keeps this correct when the domain
        // skips a step, which it does for size on the sub-grid. Identical to walking the values
        // whenever the domain is contiguous.
        val starts = domain.indices.filter { start ->
            (0..2).all { column -> start + step * column in domain.indices }
        }
        if (starts.isEmpty()) return null
        // Fewer than three valid starts is normal for a four-value range, so rows may repeat a
        // start; the caller rejects the board only if two whole rows come out identical.
        val chosen = List(3) { starts.random(random) }
        return chosen.map { start -> List(3) { column -> domain[start + step * column] } }
    }

    private fun arithmeticGrid(): List<List<Int>>? {
        val plus = random.nextBoolean()
        val triples = mutableListOf<List<Int>>()
        for (first in 1..MAX_ENTITIES) {
            for (second in 1..MAX_ENTITIES) {
                val third = if (plus) first + second else first - second
                if (third in 1..MAX_ENTITIES) triples.add(listOf(first, second, third))
            }
        }
        val chosen = triples.shuffled(random).take(3)
        return if (chosen.size < 3) null else chosen
    }

    private fun domainOf(attribute: MatrixAttribute, plan: Plan): List<Int> = when (attribute) {
        MatrixAttribute.TYPE -> plan.shapes.indices.toList()
        MatrixAttribute.COLOR -> plan.colors.indices.toList()
        MatrixAttribute.ROTATION -> (0 until 4).toList()
        // A governed position forces the 2x2 sub-grid, halving every slot, so the steps a rule
        // picks from have to be spaced further apart to survive at that scale.
        MatrixAttribute.SIZE -> if (plan.positionGoverned) SPREAD_SIZE_STEPS else (0 until SIZE_STEPS).toList()
        MatrixAttribute.NUMBER -> (1..MAX_ENTITIES).toList()
        MatrixAttribute.POSITION -> masksWith(plan.fixedNumber)
    }

    // endregion

    // region options

    private fun buildOptions(
        specs: List<MatrixPanelSpec>,
        plan: Plan,
        attributes: List<MatrixAttribute>,
    ): List<MatrixPanelSpec>? {
        val answer = specs[8]
        val candidates = LinkedHashSet<MatrixPanelSpec>()

        // The two mistakes a human actually makes: carrying the previous cell straight over, and
        // stopping the rule one step short.
        candidates.add(specs[7])
        for (attribute in attributes) {
            candidates.add(answer.withValue(attribute, specs[7].valueOf(attribute)))
            candidates.add(answer.withValue(attribute, specs[6].valueOf(attribute)))
        }
        repeat(CANDIDATE_SAMPLES) {
            val perturbed = attributes.shuffled(random).take(random.nextInt(1, attributes.size + 1))
            var spec = answer
            for (attribute in perturbed) {
                val others = domainOf(attribute, plan).filter { it != answer.valueOf(attribute) }
                spec = spec.withValue(attribute, others.randomOrNull(random) ?: return@repeat)
            }
            candidates.add(spec)
        }
        candidates.remove(answer)

        val pool = candidates.toMutableList().also { it.shuffle(random) }
        if (pool.size < plan.optionCount - 1) return null

        val limit = plan.optionCount / 2
        val matches = attributes.associateWith { 1 }.toMutableMap()
        val chosen = mutableListOf<MatrixPanelSpec>()
        repeat(plan.optionCount - 1) {
            // Take the most plausible distractor - the one that changes the fewest attributes -
            // and only reach for a wilder one when the modest ones would tip an attribute over
            // half the option set and hand the player a context-blind shortcut.
            val best = pool.minWithOrNull(
                compareBy(
                    { candidate ->
                        if (resultingMaxMatch(candidate, answer, attributes, matches) > limit) 1 else 0
                    },
                    { candidate -> attributes.count { candidate.valueOf(it) != answer.valueOf(it) } },
                    { candidate -> resultingMaxMatch(candidate, answer, attributes, matches) },
                ),
            ) ?: return null
            pool.remove(best)
            chosen.add(best)
            for (attribute in attributes) {
                if (best.valueOf(attribute) == answer.valueOf(attribute)) {
                    matches[attribute] = matches.getValue(attribute) + 1
                }
            }
        }
        return (chosen + answer).shuffled(random)
    }

    private fun resultingMaxMatch(
        candidate: MatrixPanelSpec,
        answer: MatrixPanelSpec,
        attributes: List<MatrixAttribute>,
        matches: Map<MatrixAttribute, Int>,
    ): Int = attributes.maxOf { attribute ->
        val current = matches.getValue(attribute)
        if (candidate.valueOf(attribute) == answer.valueOf(attribute)) current + 1 else current
    }

    // endregion

    private fun materialize(spec: MatrixPanelSpec, plan: Plan): MatrixPanel {
        val shape = plan.shapes[spec.typeIndex]
        val color = plan.colors[spec.colorIndex]
        val rotation = spec.rotationStep * 90
        val entities = if (spec.number == 1 && !plan.positionGoverned) {
            listOf(MatrixEntity(shape, color, rotation, spec.sizeStep, CENTERED_SLOT))
        } else {
            (0 until MAX_ENTITIES)
                .filter { slot -> spec.positionMask and (1 shl slot) != 0 }
                .map { slot -> MatrixEntity(shape, color, rotation, spec.sizeStep, slot) }
        }
        return MatrixPanel(entities.toImmutableList(), spec)
    }

    /**
     * The one item shape that cannot fail: three shapes distributed across three rows, with the
     * three unused pool entries standing in as distractors.
     */
    private fun buildFallback(): MatrixProblem {
        val shapes = ALL_SHAPES.shuffled(random).take(VALUE_POOL_SIZE)
        val colors = ALL_COLORS.shuffled(random).take(VALUE_POOL_SIZE)
        val plan = Plan(
            governed = listOf(Governed(MatrixAttribute.TYPE, MatrixRule.DISTRIBUTE_THREE)),
            shapes = shapes,
            colors = colors,
            fixedNumber = 1,
            optionCount = 4,
            positionGoverned = false,
        )
        val specs = List(9) { index ->
            plan.baseSpec.withValue(MatrixAttribute.TYPE, (index / 3 + index % 3) % 3)
        }
        val answer = specs[8]
        val distractors = shapes.indices.filter { it != answer.typeIndex }
            .map { answer.withValue(MatrixAttribute.TYPE, it) }
            .take(3)
        val options = (distractors + answer).shuffled(random)
        return MatrixProblem(
            matrix = specs.map { materialize(it, plan) }.toImmutableList(),
            options = options.map { materialize(it, plan) }.toImmutableList(),
            correctOptionIndex = options.indexOf(answer),
            optionColumns = optionColumnsFor(4),
            rules = mapOf(MatrixAttribute.TYPE to MatrixRule.DISTRIBUTE_THREE).toImmutableMap(),
            ruleSummary = "type: distribute three",
        )
    }

    private data class Governed(val attribute: MatrixAttribute, val rule: MatrixRule) {
        fun summary(): String = "${attribute.name.lowercase()}: ${rule.name.lowercase().replace('_', ' ')}"
    }

    private class Plan(
        val governed: List<Governed>,
        val shapes: List<Shape>,
        val colors: List<GameColor>,
        val fixedNumber: Int,
        val optionCount: Int,
        val positionGoverned: Boolean,
    ) {
        /** Values every ungoverned attribute keeps for the whole matrix. */
        val baseSpec = MatrixPanelSpec(
            typeIndex = 0,
            colorIndex = 0,
            rotationStep = 0,
            sizeStep = SIZE_STEPS - 1,
            number = fixedNumber,
            positionMask = MatrixPanelSpec.canonicalMask(fixedNumber),
        )
    }

    companion object {
        const val MAX_DIFFICULTY = 6

        private const val MAX_ATTEMPTS = 200
        private const val CANDIDATE_SAMPLES = 40
        private const val VALUE_POOL_SIZE = 4

        private val ENTITY_ATTRIBUTES = listOf(
            MatrixAttribute.TYPE,
            MatrixAttribute.COLOR,
            MatrixAttribute.ROTATION,
            MatrixAttribute.SIZE,
        )

        private val ALL_SHAPES = listOf(
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

        /** Shapes whose quarter turns are visible, so a rotation rule stays readable. */
        private val ROTATABLE_SHAPES = listOf(
            Shape.TRIANGLE,
            Shape.HEART,
            Shape.T,
            Shape.L,
            Shape.HOUSE,
            Shape.ABSTRACT_TRIANGLE,
            Shape.ARROW,
        )

        private val ALL_COLORS = listOf(
            GameColor.GREEN,
            GameColor.BLUE,
            GameColor.PURPLE,
            GameColor.RED,
            GameColor.YELLOW,
            GameColor.ORANGE,
            GameColor.TURQUOISE,
            GameColor.ROSA,
        )

        fun optionCountFor(tier: Int): Int = when {
            tier <= 1 -> 4
            tier <= 4 -> 6
            else -> 8
        }

        fun optionColumnsFor(optionCount: Int): Int = when (optionCount) {
            4 -> 2
            6 -> 3
            else -> 4
        }

        private fun masksWith(bits: Int): List<Int> = (0 until (1 shl MAX_ENTITIES)).filter { it.countOneBits() == bits }
    }
}
