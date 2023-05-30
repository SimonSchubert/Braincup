package com.inspiredandroid.braincup.composables.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.R
import com.inspiredandroid.braincup.composables.BaseApp
import com.inspiredandroid.braincup.composables.VectorImage

@Composable
fun CorrectAnswerScreen(hint: String?) {
    BaseApp {
        VectorImage(id = R.drawable.ic_success)
        if (hint != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                hint,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
