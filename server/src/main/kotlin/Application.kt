/*
 * Copyright 2019 Simon Schubert Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package com.inspiredandroid.braincupserver

import com.google.cloud.Timestamp
import com.google.cloud.datastore.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title
import org.apache.http.auth.InvalidCredentialsException
import kotlin.math.round

var lastScoreCache = 0L
// The key is the user score and the value is the ranking in %
var scorePercentages = mutableMapOf<Long, Float>()

/**
 * Entry point
 */
fun Application.main() {
    val datastore = DatastoreOptions.getDefaultInstance().service
    val keyFactory = datastore.newKeyFactory().setKind("Score")

    install(DefaultHeaders)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        allowCredentials = true
        anyHost()
    }
    install(CallLogging)
    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Braincup API" }
                }
                body {
                    h1 {
                        +"Braincup is up :)"
                    }
                }
            }
        }
        route("/api/v1") {
            get("game/{id}/score") {
                val gameId = call.parameters["id"]?.toLongOrNull() ?: 0L

                call.respond(scorePercentages)
            }
            post("game/{id}/score") {
                val score = call.parameters["score"]?.toLongOrNull() ?: 0L
                val gameId = call.parameters["id"]?.toLongOrNull() ?: 0L

                datastore.addScore(keyFactory, score, gameId)

                if(scorePercentages.isEmpty() || System.currentTimeMillis() - lastScoreCache > 120 * 60 * 1000) {
                    scorePercentages = datastore.getScores()
                    lastScoreCache = System.currentTimeMillis()
                }

                val ranking = scorePercentages[score] ?: 50f
                val rankingRounded = round(ranking * 100) / 100
                call.respond(rankingRounded)
            }
        }
    }
}

/**
 * Get all scores from database and map to cached score table.
 */
fun Datastore.getScores(): MutableMap<Long, Float> {
    val scorePercentages = mutableMapOf<Long, Float>()

    val query = Query.newEntityQueryBuilder()
            .setKind("Score")
            .build()

    var totalScoresCount = 1L

    val scores = mutableMapOf<Long, Long>()
    val entities = this.run(query)
    entities?.forEach { entity ->
        val score = entity.getLong("score")
        scores[score] = 1 + (scores[score] ?: 0L)
        totalScoresCount++
    }

    var currentScoresCount = 1L
    for (i in 0..100) {
        currentScoresCount += (scores[i.toLong()] ?: 0L)
        scorePercentages[i.toLong()] = (currentScoresCount.toFloat() / totalScoresCount) * 100f
    }

    return scorePercentages
}

/**
 * Add score to database.
 */
fun Datastore.addScore(keyFactory: KeyFactory, score: Long, gameId: Long) {
    val key = this.allocateId(keyFactory.newKey())
    val task = Entity.newBuilder(key)
            .set("score", score)
            .set("game", gameId)
            .set("created", Timestamp.now())
            .build()
    this.put(task)
}