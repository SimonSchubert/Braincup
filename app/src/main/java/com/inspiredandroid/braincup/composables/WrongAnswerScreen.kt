package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Gravity
import androidx.ui.layout.HeightSpacer
import com.inspiredandroid.braincup.R

@Composable
fun WrongAnswerScreen(solution: String) {
    BaseApp {
        VectorImage(id = R.drawable.ic_searching)
        HeightSpacer(16.dp)
        Text(
            "Correct was: $solution",
            modifier = Gravity.Center
        )
    }
}