package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.components.GameScaffold
import com.inspiredandroid.braincup.ui.theme.ErrorRed
import com.inspiredandroid.braincup.ui.theme.SuccessGreen

@Composable
fun AnswerFeedbackScreen(
    isCorrect: Boolean,
    message: String?,
) {
    GameScaffold {
        val color = if (isCorrect) SuccessGreen else ErrorRed
        val title = if (isCorrect) "Correct!" else "Wrong"

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = color,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        if (message != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isCorrect) message else "Solution: $message",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }
    }
}
