package com.inspiredandroid.braincup.games

/**
 * A puzzle whose score is the highest level reached rather than a count of correct answers:
 * one puzzle per attempt, no timer, difficulty derived from [level].
 *
 * [maxLevel] caps games backed by a finite level catalog. It is a constructor parameter rather
 * than an overridable member because [level] is initialized here, before any subclass
 * initializer has run.
 */
abstract class LevelGame(level: Int, maxLevel: Int? = null) : Game() {
    override val adaptiveDifficulty: Boolean = false

    var level: Int = maxLevel?.let { level.coerceIn(1, it) } ?: level.coerceAtLeast(1)
        protected set

    /**
     * Whether the board has reached its finished state.
     *
     * These puzzles are answered on the board rather than by typing, so [isCorrect] asks this and
     * [solution] has nothing to show. N-Back is the one level game that really does answer by
     * input, which is why both stay open. Some games expose it to their tests, so it is public.
     */
    abstract fun isSolved(): Boolean

    override fun isCorrect(input: String): Boolean = isSolved()

    override fun solution(): String = ""
}
