package com.inspiredandroid.braincup.checkmate

import braincup.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CheckmatePuzzles {
    const val PATH = "files/checkmate/puzzles.txt"

    // Kept in step with the data file so the main menu can show progress without reading it.
    const val COUNT = 60

    var cached: List<CheckmatePuzzle>? = null
        private set

    suspend fun load(): List<CheckmatePuzzle> {
        cached?.let { return it }
        val text = Res.readBytes(PATH).decodeToString()
        val puzzles = withContext(Dispatchers.Default) { CheckmatePuzzle.parse(text) }
        cached = puzzles
        return puzzles
    }
}
