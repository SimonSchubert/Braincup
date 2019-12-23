package com.inspiredandroid.braincup.composables

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
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
    BaseScrollApp(title = "${game.getName()} - Scores", back = { gameMaster.start() }) {
        HeightSpacer(16.dp)
        Headline6(text = "Hightscore: $highscore", modifier = Gravity.Center)
        HeightSpacer(8.dp)
        val table = game.getScoreTable()
        Row(modifier = Gravity.Center) {
            ScoreboardLegend(
                "> 0",
                R.drawable.ic_icons8_medal_third_place
            )
            WidthSpacer(8.dp)
            ScoreboardLegend(
                "> ${table[1] - 1}",
                R.drawable.ic_icons8_medal_second_place
            )
            WidthSpacer(8.dp)
            ScoreboardLegend(
                "> ${table[0] - 1}",
                R.drawable.ic_icons8_medal_first_place
            )
        }
        scores.forEach {
            HeightSpacer(16.dp)
            Headline6(text = it.first, modifier = Gravity.Center)
            HeightSpacer(8.dp)
            val pointSize = 15
            it.second.forEach { score ->
                var width = (score * pointSize).dp
                if (width < 36.dp) {
                    width = 36.dp
                }
                Container(
                    width = width,
                    height = 24.dp,
                    modifier = Gravity.Center
                ) {
                    DrawShape(
                        RectangleShape,
                        color = Color(0xFFED7354)
                    )
                    Row {
                        Text(
                            score.toString(),
                            style = (+MaterialTheme.typography()).subtitle1,
                            paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
                        )
                        VectorImage(id = game.getAndroidMedalResource(score))
                    }
                }
                HeightSpacer(16.dp)
            }
        }
    }
}

@Composable
fun ScoreboardLegend(text: String, @DrawableRes drawable: Int) {
    Text(
        text,
        style = (+MaterialTheme.typography()).subtitle1
        , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
    )
    VectorImage(id = drawable)
}