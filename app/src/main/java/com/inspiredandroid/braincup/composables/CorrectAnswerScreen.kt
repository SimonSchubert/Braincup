package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R

@Composable
fun CorrectAnswerScreen(hint: String?) {
    BaseApp {
        VectorImage(id = R.drawable.ic_success)
        if (hint != null) {
            Spacer(LayoutHeight(16.dp))
            Text(
                hint,
                modifier = LayoutGravity.Center
            )
        }
    }
}
