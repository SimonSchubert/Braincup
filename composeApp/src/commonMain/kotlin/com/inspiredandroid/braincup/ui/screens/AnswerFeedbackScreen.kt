package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.ic_delivery
import braincup.composeapp.generated.resources.ic_success
import com.inspiredandroid.braincup.ui.components.GameScaffold
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnswerFeedbackScreen(
    isCorrect: Boolean,
    message: String?,
) {
    GameScaffold {
        if (isCorrect) {
            Image(
                painterResource(Res.drawable.ic_success),
                contentDescription = null,
                modifier = Modifier
                    .size(360.dp),
            )
        } else {
            Image(
                painterResource(Res.drawable.ic_delivery),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp),
            )
            Text(
                text = "Solution: $message",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
            )
        }
    }
}
