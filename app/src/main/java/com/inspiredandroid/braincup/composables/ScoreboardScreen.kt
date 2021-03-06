package com.inspiredandroid.braincup.composables

import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.dp
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
        Spacer(Modifier.preferredHeight(16.dp))
        Headline6(
            text = "Highscore: $highscore",
            modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.preferredHeight(8.dp))
        val table = game.getScoreTable()
        Row(modifier = Modifier.gravity(align = Alignment.CenterHorizontally)) {
            ScoreboardLegend(
                "> 0",
                R.drawable.ic_icons8_medal_third_place
            )
            Spacer(Modifier.preferredWidth(8.dp))
            ScoreboardLegend(
                "> ${table[1] - 1}",
                R.drawable.ic_icons8_medal_second_place
            )
            Spacer(Modifier.preferredWidth(8.dp))
            ScoreboardLegend(
                "> ${table[0] - 1}",
                R.drawable.ic_icons8_medal_first_place
            )
        }
        scores.forEach {
            Spacer(Modifier.preferredHeight(16.dp))
            Headline6(
                text = it.first,
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
            Spacer(Modifier.preferredHeight(8.dp))
            val pointSize = 15
            it.second.forEach { score ->
                var width = (score * pointSize).dp
                if (width < 36.dp) {
                    width = 36.dp
                }
                Box(
                    modifier = Modifier.preferredSize(
                        width = width,
                        height = 24.dp
                    ) + Modifier.gravity(align = Alignment.CenterHorizontally),
                    backgroundColor = Color(0xFFED7354)
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
                Spacer(Modifier.preferredHeight(16.dp))
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