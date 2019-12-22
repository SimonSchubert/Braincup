package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getScoreTable
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getHex
import com.inspiredandroid.braincup.games.tools.getPaths
import org.w3c.dom.*
import kotlin.random.Random

internal fun HTMLCanvasElement.drawFigure(figure: Figure, width: Int, height: Int) {
    val context = this.getContext("2d") as CanvasRenderingContext2D
    context.canvas.width = width
    context.canvas.height = height

    val path2D = Path2D()
    context.fillStyle = figure.color.getHex()
    figure.shape.getPaths().forEachIndexed { index, pair ->
        val x = (width * pair.first).toDouble()
        val y = (height * pair.second).toDouble()
        if (index == 0) {
            path2D.moveTo(x, y)
        } else {
            path2D.lineTo(x, y)
        }
    }

    if (figure.rotation != 0) {
        context.translate(
            (width / 2f).toDouble(),
            (height / 2f).toDouble()
        )
        context.rotate(figure.rotation * kotlin.math.PI / 180f)
        context.translate(
            (-width / 2f).toDouble(),
            (-height / 2f).toDouble()
        )
    }
    context.fill(path2D)
}

fun Document.copyToClipboard(data: String) {
    val te = this.createElement("textarea") as HTMLTextAreaElement
    te.value = data
    this.body?.appendChild(te)
    te.select()
    this.execCommand("copy")
    this.body?.removeChild(te)
}

fun GameType.getMedalResource(score: Int): String {
    val scoreTable = this.getScoreTable()
    return when {
        score >= scoreTable[0] -> MEDAL_FIRST_RESOURCE
        score >= scoreTable[1] -> MEDAL_SECOND_RESOURCE
        else -> MEDAL_THIRD_RESOURCE
    }
}

const val MEDAL_FIRST_RESOURCE = "icons8-medal_first_place.svg"
const val MEDAL_SECOND_RESOURCE = "icons8-medal_second_place.svg"
const val MEDAL_THIRD_RESOURCE = "icons8-medal_third_place.svg"

fun randomString(): String {
    return Random.nextInt(100, 10000).toString()
}
