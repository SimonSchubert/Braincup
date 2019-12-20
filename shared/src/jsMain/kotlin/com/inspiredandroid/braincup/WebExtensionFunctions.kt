package com.inspiredandroid.braincup

import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.games.tools.Figure
import com.inspiredandroid.braincup.games.tools.getHex
import com.inspiredandroid.braincup.games.tools.getPaths
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
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
    val te = document.createElement("textarea") as HTMLTextAreaElement
    te.value = data
    document.body?.appendChild(te)
    te.select()
    document.execCommand("copy")
    document.body?.removeChild(te)
}

fun GameType.openGameHtml() {
    window.open("${this.getName().toLowerCase().removeWhitespaces()}.html", target = "_self")
}

fun GameType.openScoreboardHtml() {
    window.open("${this.getName().toLowerCase().removeWhitespaces()}_score.html", target = "_self")
}

fun randomString(): String {
    return Random.nextInt(100, 10000).toString()
}