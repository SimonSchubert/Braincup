package com.inspiredandroid.braincup.games.tools

import kotlin.math.sqrt
import kotlin.random.Random

/**
 * A ball a physics step can move: position and velocity in arena units.
 *
 * Bubble Sum and Orbit Tracker each carry their own payload on top of this (a value and a blink
 * phase, or a target flag), so they implement this rather than sharing one ball type.
 */
interface MovingBall {
    var x: Float
    var y: Float
    var vx: Float
    var vy: Float
}

/**
 * Advances every ball by [deltaSeconds]: move, bounce off the arena walls, resolve overlaps
 * elastically, then pin every speed back to [speed].
 *
 * Renormalizing at the end is what keeps the round readable. The collision response conserves
 * momentum but not each ball's own speed, so without it a few exchanges leave some balls crawling
 * and others racing, and the difficulty a round was generated for stops holding.
 *
 * Orbit Tracker works in a unit square and passes 1f for both bounds; Bubble Sum passes the arena
 * the screen measured for it.
 */
fun stepBalls(
    balls: List<MovingBall>,
    deltaSeconds: Float,
    radius: Float,
    width: Float,
    height: Float,
    speed: Float,
) {
    for (ball in balls) {
        ball.x += ball.vx * deltaSeconds
        ball.y += ball.vy * deltaSeconds

        if (ball.x - radius < 0f) {
            ball.x = radius
            ball.vx = -ball.vx
        }
        if (ball.x + radius > width) {
            ball.x = width - radius
            ball.vx = -ball.vx
        }
        if (ball.y - radius < 0f) {
            ball.y = radius
            ball.vy = -ball.vy
        }
        if (ball.y + radius > height) {
            ball.y = height - radius
            ball.vy = -ball.vy
        }
    }

    for (i in balls.indices) {
        for (j in i + 1 until balls.size) {
            val a = balls[i]
            val b = balls[j]
            val dx = b.x - a.x
            val dy = b.y - a.y
            val dist = sqrt(dx * dx + dy * dy)
            val minDist = radius * 2

            if (dist < minDist && dist > 0.0001f) {
                val nx = dx / dist
                val ny = dy / dist
                val dvx = a.vx - b.vx
                val dvy = a.vy - b.vy
                val dvDotN = dvx * nx + dvy * ny
                // Only resolve a pair that is still closing; already-separating balls would be
                // yanked back together.
                if (dvDotN > 0) {
                    a.vx -= dvDotN * nx
                    a.vy -= dvDotN * ny
                    b.vx += dvDotN * nx
                    b.vy += dvDotN * ny
                }
                val overlap = (minDist - dist) / 2f
                a.x -= overlap * nx
                a.y -= overlap * ny
                b.x += overlap * nx
                b.y += overlap * ny
            }
        }
    }

    for (ball in balls) {
        val currentSpeed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy)
        if (currentSpeed > 0.0001f) {
            ball.vx = ball.vx / currentSpeed * speed
            ball.vy = ball.vy / currentSpeed * speed
        }
    }
}

/**
 * A start position at least [radius] * 3 from every ball in [placed], inside a [width] x [height]
 * arena kept [radius] * 2 clear of the edges.
 *
 * Rejection sampling with a cap rather than a real packing: at the ball counts these games use a
 * free spot is found in a handful of tries, and after [MAX_SPAWN_ATTEMPTS] the last candidate is
 * taken anyway so a crowded arena can still start. The overlap that leaves is resolved by the
 * first [stepBalls] call.
 */
fun spawnPosition(
    placed: List<MovingBall>,
    radius: Float,
    width: Float,
    height: Float,
    random: Random = Random,
): Pair<Float, Float> {
    val margin = radius * 2
    var x: Float
    var y: Float
    var attempts = 0
    do {
        x = margin + (random.nextFloat() * (width - 2 * margin))
        y = margin + (random.nextFloat() * (height - 2 * margin))
        attempts++
    } while (
        attempts < MAX_SPAWN_ATTEMPTS &&
        placed.any { existing ->
            val dx = existing.x - x
            val dy = existing.y - y
            sqrt(dx * dx + dy * dy) < radius * 3
        }
    )
    return x to y
}

private const val MAX_SPAWN_ATTEMPTS = 100
