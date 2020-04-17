package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R

@Composable
fun WrongAnswerScreen(solution: String) {
    BaseApp {
        VectorImage(id = R.drawable.ic_searching)
        Spacer(Modifier.preferredHeight(16.dp))
        Text(
            "Correct was: $solution"
        )
    }
}