package com.inspiredandroid.braincup.composables.screens

import android.content.Context
import android.os.Handler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.app.NavigationController
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.Headline5
import com.inspiredandroid.braincup.composables.Subtitle2
import com.inspiredandroid.braincup.composables.TextImageButton
import com.inspiredandroid.braincup.composables.VectorImage

@Composable
fun WrongChallengeAnswerScreen(
    context: Context, url: String,
    gameMaster: NavigationController
) {
    BaseApp {
        Headline5(
            text = "Unsolved",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        Subtitle2(
            text = "The challenge will stay unsolved for now.",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        VectorImage(
            id = R.drawable.ic_searching,
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
            onClick = {
                Handler().post {
                    gameMaster.start()
                }
                Unit
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}