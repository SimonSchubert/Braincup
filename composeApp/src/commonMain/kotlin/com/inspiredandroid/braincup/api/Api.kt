package com.inspiredandroid.braincup.api

/**
 * Webservice calls
 */
object Api {

    internal fun postScore(gameId: String, score: Int, callback: (String, Boolean) -> Unit) {
        val storage = UserStorage()
        val newHighscore = storage.putScore(gameId, score)
        callback(score.toString(), newHighscore)
    }
}