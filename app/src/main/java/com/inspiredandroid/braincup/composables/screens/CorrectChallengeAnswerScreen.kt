package com.inspiredandroid.braincup.composables.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.composables.BaseScrollApp
import com.inspiredandroid.braincup.composables.Headline5
import com.inspiredandroid.braincup.composables.Headline6
import com.inspiredandroid.braincup.composables.Subtitle2
import com.inspiredandroid.braincup.composables.TextImageButton
import com.inspiredandroid.braincup.composables.VectorImage

@Composable
fun CorrectChallengeAnswerScreen(
    context: Context, solution: String?, secret: String, url: String,
    gameMaster: NavigationController
) {
    BaseScrollApp {
        Headline5(
            text = "Congratulation",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        Subtitle2(
            text = "Your solution '$solution' solved the challenge.",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (secret.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Headline6(
                text = "Secret unveiled: $secret",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        VectorImage(
            id = R.drawable.ic_delivery,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        TextImageButton(
            text = "Share challenge",
            drawableResource = R.drawable.ic_icons8_copy_link,
            onClick = { shareText(context, url) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        TextImageButton(
            text = "Menu",
            drawableResource = R.drawable.ic_icons8_menu,
            onClick = { gameMaster.start() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
