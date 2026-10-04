package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.rail_yard_demo_caption_colour
import braincup.composeapp.generated.resources.rail_yard_demo_caption_many
import braincup.composeapp.generated.resources.rail_yard_demo_caption_switch
import braincup.composeapp.generated.resources.rail_yard_demo_title
import com.inspiredandroid.braincup.games.RailYardGame
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random

private val Captions = persistentListOf(
    Res.string.rail_yard_demo_caption_colour,
    Res.string.rail_yard_demo_caption_switch,
    Res.string.rail_yard_demo_caption_many,
)

/** How close a train gets to a wrongly set switch before the demo taps it, in board units. */
private const val TapLookahead = 0.1f
private const val PressMillis = 220L
private const val CaptionHoldMillis = 2200L

/**
 * Animated tutorial for Rail Yard, played by the real game on its first map: trains leave the
 * tunnel, and a switch set the wrong way is tapped just before a train reaches it, so the viewer
 * sees it heading wrong first. The yard starts over on a fresh map before it would grow.
 */
@Composable
fun RailYardDemo(modifier: Modifier = Modifier) {
    var seed by remember { mutableStateOf(1L) }
    val game = remember(seed) { RailYardGame(Random(seed)).apply { nextRound() } }
    var uiState by remember(game) { mutableStateOf(game.toUiState()) }
    var trains by remember(game) { mutableStateOf(game.frames()) }
    var pressedSwitch by remember { mutableStateOf<Int?>(null) }
    var caption by remember { mutableStateOf(Captions[0]) }

    LaunchedEffect(game) {
        var last = withFrameMillis { it }
        var pressedUntil = 0L
        var captionSince = last
        while (true) {
            val now = withFrameMillis { it }
            val result = game.step(((now - last) / 1000f).coerceAtMost(0.05f))
            last = now

            if (now >= pressedUntil) pressedSwitch = null
            val flip = game.switchToFlip(TapLookahead)
            val wanted = when {
                flip != null -> Res.string.rail_yard_demo_caption_switch
                game.trains.size >= 2 -> Res.string.rail_yard_demo_caption_many
                else -> Res.string.rail_yard_demo_caption_colour
            }
            if (flip != null) {
                game.toggleSwitch(flip)
                pressedSwitch = flip
                pressedUntil = now + PressMillis
            }
            if (wanted != caption && (flip != null || now - captionSince >= CaptionHoldMillis)) {
                caption = wanted
                captionSince = now
            }

            if (game.stageIndex > 0) {
                seed++
                return@LaunchedEffect
            }
            if (flip != null || result.boardChanged) uiState = game.toUiState()
            trains = game.frames()
        }
    }

    val compact = LocalIsCompactHeight.current

    DemoScaffold(title = Res.string.rail_yard_demo_title, modifier = modifier) {
        RailYardBoard(
            uiState = uiState,
            trains = { trains },
            pressedSwitch = pressedSwitch,
            modifier = if (compact) Modifier.size(200.dp, 160.dp) else Modifier.size(260.dp, 240.dp),
        )

        Spacer(Modifier.height(12.dp))

        DemoCaption(current = caption, all = Captions)
    }
}
