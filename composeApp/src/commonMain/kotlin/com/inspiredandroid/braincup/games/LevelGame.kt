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
}
