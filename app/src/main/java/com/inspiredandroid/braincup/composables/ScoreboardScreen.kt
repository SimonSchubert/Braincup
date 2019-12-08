package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.AppController
import com.inspiredandroid.braincup.games.GameType
import com.inspiredandroid.braincup.games.getName
import com.inspiredandroid.braincup.games.getScoreTable
import com.inspiredandroid.braincup.getAndroidMedalResource

@Composable
fun ScoreboardScreen(
    game: GameType,
    highscore: Int,
    scores: List<Pair<String, List<Int>>>,
    gameMaster: AppController
) {
    BaseScrollApp(title = "${game.getName()} - Scores", back = { gameMaster.start() }) {
        HeightSpacer(16.dp)
        Text(
            "Hightscore: $highscore",
            style = (+MaterialTheme.typography()).h6,
            modifier = Gravity.Center
        )
        HeightSpacer(8.dp)
        val table = game.getScoreTable()
        Row(modifier = Gravity.Center) {
            ScoreboardLegend(
                "> 0",
                R.drawable.ic_icons8_medal_third_place
            )
            WidthSpacer(8.dp)
            ScoreboardLegend(
                ">= ${table[1]}",
                R.drawable.ic_icons8_medal_second_place
            )
            WidthSpacer(8.dp)
            ScoreboardLegend(
                ">= ${table[0]}",
                R.drawable.ic_icons8_medal_first_place
            )
        }
        scores.forEach {
            HeightSpacer(16.dp)
            Text(
                it.first,
                style = (+MaterialTheme.typography()).h6,
                modifier = Gravity.Center
            )
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
                            style = (+MaterialTheme.typography()).subtitle1
                            , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
                        )
                        val vectorAsset = +vectorResource(
                            game.getAndroidMedalResource(score)
                        )
                        Container(width = 24.dp, height = 24.dp) {
                            DrawVector(vectorAsset)
                        }
                    }
                }
                HeightSpacer(16.dp)
            }
        }
    }
}

@Composable
fun ScoreboardLegend(text: String, vector: Int) {
    Text(
        text,
        style = (+MaterialTheme.typography()).subtitle1
        , paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left)
    )
    val vectorAsset = +vectorResource(
        vector
    )
    Container(width = 24.dp, height = 24.dp) {
        DrawVector(vectorAsset)
    }
}