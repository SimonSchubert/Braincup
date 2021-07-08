package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.games.getScoreTable
import com.inspiredandroid.braincup.getAndroidMedalResource

@Composable
fun ScoreboardScreen(
    game: GameType,
    highscore: Int,
    scores: List<Pair<String, List<Int>>>,
    gameMaster: NavigationController
) {
    BaseScrollApp(title = "${game.getName()} - Scores", back = {
        Handler().post {
            gameMaster.start()
        }
        Unit
    }) {
        Spacer(Modifier.height(16.dp))
        Headline6(
            text = "Highscore: $highscore",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))
        val table = game.getScoreTable()
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            ScoreboardLegend(
                "> 0",
                R.drawable.ic_icons8_medal_third_place
            )
            Spacer(Modifier.height(8.dp))
            ScoreboardLegend(
                "> ${table[1] - 1}",
                R.drawable.ic_icons8_medal_second_place
            )
            Spacer(Modifier.height(8.dp))
            ScoreboardLegend(
                "> ${table[0] - 1}",
                R.drawable.ic_icons8_medal_first_place
            )
        }
        scores.forEach {
            Spacer(Modifier.height(16.dp))
            Headline6(
                text = it.first,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            val pointSize = 15
            it.second.forEach { score ->
                var width = (score * pointSize).dp
                if (width < 36.dp) {
                    width = 36.dp
                }
                Box(
                    modifier = Modifier.size(
                        width = width,
                        height = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFED7354)),
                ) {
                    Row {
                        Text(
                            score.toString(),
                            style = MaterialTheme.typography.subtitle1.merge(
                                ParagraphStyle(
                                    textAlign = TextAlign.Left
                                )
                            )
                        )
                        VectorImage(id = game.getAndroidMedalResource(score))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ScoreboardLegend(text: String, @DrawableRes drawable: Int) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle1.merge(ParagraphStyle(textAlign = TextAlign.Left))
    )
    VectorImage(id = drawable)
}