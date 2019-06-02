package com.inspiredandroid.braincup

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal expect val ApplicationDispatcher: CoroutineDispatcher

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