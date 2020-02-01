package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R

@Composable
fun WrongAnswerScreen(solution: String) {
    BaseApp {
        VectorImage(id = R.drawable.ic_searching)
        Spacer(LayoutHeight(16.dp))
        Text(
            "Correct was: $solution",
            modifier = LayoutGravity.Center
        )
    }
}