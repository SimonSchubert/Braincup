package com.inspiredandroid.braincup.api

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher
internal expect val settings: Settings

/**
 * Webservice calls
 */
object Api {
    private val client = HttpClient {
    }

    internal fun postScore(gameId: String, score: Int, callback: (String, Boolean) -> Unit) {
        val storage = UserStorage()
        val newHighscore = storage.putScore(gameId, score)
        callback(score.toString(), newHighscore)
        /*
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.post {
                    url("https://braincup.appspot.com/api/v1/game/$gameId/score")
                    body = "{\"score\": $score}"
                }

                callback(result)
            }
        }
        */
    }
}