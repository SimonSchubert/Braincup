package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R

@Composable
fun WrongAnswerScreen(solution: String) {
    BaseApp {
        VectorImage(id = R.drawable.ic_searching)
        Spacer(Modifier.height(16.dp))
        Text(
            "Correct was: $solution"
        )
    }
}