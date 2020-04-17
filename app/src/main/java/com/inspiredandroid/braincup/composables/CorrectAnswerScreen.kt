package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.unit.dp
import com.inspiredandroid.braincup.R

@Composable
fun CorrectAnswerScreen(hint: String?) {
    BaseApp {
        VectorImage(id = R.drawable.ic_success)
        if (hint != null) {
            Spacer(Modifier.preferredHeight(16.dp))
            Text(
                hint,
                modifier = Modifier.gravity(align = Alignment.CenterHorizontally)
            )
        }
    }
}
