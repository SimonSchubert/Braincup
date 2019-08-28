package com.inspiredandroid.braincup.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal expect val ApplicationDispatcher: CoroutineDispatcher

/**
 * Webservice calls
 */
object Api {
    private val client = HttpClient {
    }

    internal fun postScore(gameId: Int, score: Int, callback: (String) -> Unit) {
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.post {
                    url("https://braincup.appspot.com/api/v1/game/$gameId/score")
                    body = "{\"score\": $score}"
                }

                callback(result)
            }
        }
    }
}